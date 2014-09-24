package com.bibsmobile.util;

import com.bibsmobile.model.ResultsFile;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.io.FilenameUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;


public class ResultsFileUtil {

  public static ResultsFileIterator getResultsFileIterator(ResultsFile resultsFile) {
    return ResultsFileIteratorFactory.build(resultsFile);
  }

  public static List<String> getFirstRow(ResultsFile resultsFile) {
    return ResultsFileUtil.getResultsFileIterator(resultsFile).next();
  }

  public static class ResultsFileIteratorFactory {
    public static ResultsFileIterator build(ResultsFile resultsFile) {
      String filePath = resultsFile.getFilePath();
      String fileExtension = FilenameUtils.getExtension(filePath);
      String contentType = resultsFile.getContentType();
      File file = new File(filePath);
      boolean csvEnding = (fileExtension.equalsIgnoreCase(".csv") || fileExtension.equalsIgnoreCase(".txt"));

      try {
        // TODO more file types
        if (contentType.equalsIgnoreCase("text/csv") || csvEnding) {
          return new ResultsFileCSVIterator(file);
        } else {
          throw new UnsupportedOperationException("Unsupported ResultsFile type: " + filePath + ", " + fileExtension + ", " + contentType);
        }
      } catch(FileNotFoundException e) {
        // if this really happens, we lost data
        throw new RuntimeException(e);
      }
    }
  }

  // general interface for a ResultsFile iterator, closing needs to be in there since we are dealing with files
  public static interface ResultsFileIterator extends Closeable, Iterator<List<String>> {
    public void close() throws IOException;
  }

  // CSV implementation of a ResultsFile iterator
  public static class ResultsFileCSVIterator implements ResultsFileIterator {
    private CSVReader reader;
    // CSV reader can't tell us if there is more in the file
    // so we need to try and figure it out.
    // this member saves one pre-read line which wasn't used yet.
    private String[] nextLine;

    protected ResultsFileCSVIterator(File file) throws FileNotFoundException {
      // not using the csvreader-iterator because it reads whole file at once according to doc
      this.reader = new CSVReader(new FileReader(file));
    }

    public void close() throws IOException {
      this.reader.close();
    }

    public boolean hasNext() {
      if (this.nextLine != null) return true;
      String tmp[];
      try {
        tmp = this.reader.readNext();
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
      if (tmp == null) return false;
      this.nextLine = tmp;
      return true;
    }

    public List<String> next() {
      String line[];
      if (this.nextLine != null) {
        line = this.nextLine;
        this.nextLine = null;
      } else {
        try {
          line = this.reader.readNext();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
      if (line == null) return null;
      return Arrays.asList(line);
    }

    public void remove() {
      throw new UnsupportedOperationException("ResultsFile can't be modified through iterator.");
    }
  }
}
