package com.bibsmobile.util;

import com.bibsmobile.controller.DropBoxController;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;

public final class DropboxUtil {

    private DropboxUtil() {
        super();
    }

    /**
     * returns the DbxClient instance to be used for API requests
     */
    public static DbxClient getDbxClient(String token) {
        return new DbxClient(DropBoxController.getAppConfig(), token);
    }

    /**
     * gets a file from a user's dropbox
     */
    public static File getDropboxFile(String token, String path) throws IOException {
        File tmpFile = File.createTempFile("dropboximport", FilenameUtils.getExtension(path));
        tmpFile.deleteOnExit(); // this file will be destroyed if vm exits at
                                // latest
        DbxEntry.File md;
        try (OutputStream out = new FileOutputStream(tmpFile)) {
            md = DropboxUtil.getDbxClient(token).getFile(path, null, out);
        } catch (DbxException ex) {
            return null; // not found
        }
        if (md == null)
            return null;
        return tmpFile;
    }
}
