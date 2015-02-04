package com.bibsmobile.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import au.com.bytecode.opencsv.CSVReader;

import com.bibsmobile.controller.ResultsFileMappingController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.UserProfile;

public final class ResultsFileUtil {
    private static final String RESULT_FILE_LOCATION = "/data/";

    private ResultsFileUtil() {
        super();
    }

    /**
     * guesses the mime type of a given file currently supported: - text/csv
     * TODO needs to support: - text/xls
     */
    public static String guessMimeType(File file) {
        String filePath = file.getAbsolutePath();
        String fileExtension = FilenameUtils.getExtension(filePath);
        boolean csvEnding = (fileExtension.equalsIgnoreCase("csv") || fileExtension.equalsIgnoreCase("txt"));

        if (csvEnding)
            return "text/csv";
        return null;
    }

    /**
     * calculates the SHA1 checksum of a given file
     */
    public static String getSha1Checksum(File file) throws IOException {
        // calculate checksum
        try (InputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = in.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            return null; // this should not open with some snaity checks for the
                         // constant algorithm above
        }
    }

    /**
     * imports a file from dropbox for the given user
     *
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static ResultsImport importDropbox(UserProfile user, Event event, String dropboxPath, List<String> map, boolean skipHeaders) throws IOException, InvalidFormatException {
        if (user == null || event == null || dropboxPath == null || map == null)
            throw new IllegalArgumentException("No null arguments permitted");

        // calculate where file will be stored
        String filename = new File(dropboxPath).getName();
        File destFile = new File(RESULT_FILE_LOCATION + filename);

        // get dropbox credentials
        String accessToken = user.getDropboxAccessToken();
        if (accessToken == null) {
            throw new IllegalArgumentException("User is not authenticated with Dropbox");
        }

        // get file from dropbox and move it to destination
        File tmpFile = DropboxUtil.getDropboxFile(accessToken, dropboxPath);
        FileUtils.copyFile(tmpFile, destFile);
        tmpFile.delete();

        // disable automatic updates on all other files associated with this
        // event
        for (ResultsFile rf : ResultsFile.findResultsFilesByEvent(event).getResultList()) {
            if (rf.getAutomaticUpdates()) {
                rf.setAutomaticUpdates(false);
                rf.persist();
            }
        }

        // save to database
        ResultsFile file = new ResultsFile();
        file.setName(destFile.getName());
        file.setContentType(ResultsFileUtil.guessMimeType(destFile));
        file.setEvent(event);
        file.setCreated(new Date());
        file.setFilesize(destFile.length());
        file.setFilePath(destFile.getAbsolutePath());
        file.setSha1Checksum(ResultsFileUtil.getSha1Checksum(destFile));
        file.setImportUser(user);
        file.setDropboxPath(dropboxPath);
        file.setAutomaticUpdates(true);
        file.persist();
        // create mapping for file in database
        ResultsFileMapping mapping = new ResultsFileMapping();
        mapping.setName(file.getName());
        mapping.setResultsFile(file);
        mapping.setSkipFirstRow(skipHeaders);
        mapping.setMap(StringUtils.join(map, ","));
        mapping.persist();

        // do the import
        ResultsImport rimport = new ResultsImport();
        rimport.setRunDate(new Date());
        rimport.setResultsFile(file);
        rimport.setResultsFileMapping(mapping);
        rimport.setRowsProcessed(0);
        rimport.setErrors(0);
        rimport.setErrorRows("");
        if (!map.isEmpty()) {
            //ResultsFileMappingController.doImport(rimport);
        }
        rimport.persist();

        return rimport;
    }

    /**
     * returns an iterator over the lines in an imported ResultsFile
     */
    public static ResultsFileIterator getResultsFileIterator(ResultsFile resultsFile) {
        return ResultsFileIteratorFactory.build(resultsFile);
    }

    /**
     * returns the first row/line of an imported ResultsFile. generally used to
     * check the headers in the file
     */
    public static List<String> getFirstRow(ResultsFile resultsFile) {
        return ResultsFileUtil.getResultsFileIterator(resultsFile).next();
    }

    private static final class ResultsFileIteratorFactory {
        public static ResultsFileIterator build(ResultsFile resultsFile) {
            try {
                File file = new File(resultsFile.getFilePath());
                String mimeType = ResultsFileUtil.guessMimeType(file);
                if (mimeType.equalsIgnoreCase("text/csv")) {
                    return new ResultsFileCSVIterator(file);
                }
                throw new UnsupportedOperationException("Unsupported ResultsFile mime type: " + file.getAbsolutePath() + ": " + mimeType);
            } catch (FileNotFoundException e) {
                // if this really happens, we lost data
                throw new RuntimeException(e);
            }
        }
    }

    // general interface for a ResultsFile iterator, closing needs to be in
    // there since we are dealing with files
    public static interface ResultsFileIterator extends Closeable, Iterator<List<String>> {
        @Override
        public void close() throws IOException;
    }

    // CSV implementation of a ResultsFile iterator
    public static class ResultsFileCSVIterator implements ResultsFileIterator {
        private final CSVReader reader;
        // CSV reader can't tell us if there is more in the file
        // so we need to try and figure it out.
        // this member saves one pre-read line which wasn't used yet.
        private String[] nextLine;

        protected ResultsFileCSVIterator(File file) throws FileNotFoundException {
            super();
            // not using the csvreader-iterator because it reads whole file at
            // once according to doc
            this.reader = new CSVReader(new FileReader(file));
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }

        @Override
        public boolean hasNext() {
            if (this.nextLine != null)
                return true;
            String[] tmp;
            try {
                tmp = this.reader.readNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (tmp == null)
                return false;
            this.nextLine = tmp;
            return true;
        }

        @Override
        public List<String> next() {
            String[] line;
            if (this.nextLine != null) {
                line = this.nextLine;
                this.nextLine = null;
            } else {
                try {
                    line = this.reader.readNext();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (line == null)
                return null;
            return Arrays.asList(line);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("ResultsFile can't be modified through iterator.");
        }
    }
}
