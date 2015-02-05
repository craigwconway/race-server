package com.bibsmobile.controller;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.DropboxUtil;
import com.bibsmobile.util.MailgunUtil;
import com.bibsmobile.util.ResultsFileUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/dropbox")
@Controller
public class DropBoxController {
    private static final Logger log = LoggerFactory.getLogger(DropBoxController.class);
    private static final String START_URL = "authorize";
    private static final String REDIRECT_URL = "authorized";
    private static final String DEAUTH_URL = "deauth";
    private static final String IMPORT_URL = "importFile";
    private static final String PICKER_URL = "filepicker";
    private static final String PICKER_CONTENT_URL = "directory";
    private static final String MAPPING_URL = "resultsfilemappings";
    @Value("${dropbox.com.api.key}")
    private String appKey;
    @Value("${dropbox.com.api.secret}")
    private String appSecret;
    @Value("${dropbox.com.sessionkey}")
    private String sessionKey;

    private static final DbxRequestConfig appConfig = new DbxRequestConfig("Bibs", Locale.getDefault().toString());

    public static DbxRequestConfig getAppConfig() {
        return DropBoxController.appConfig;
    }

    private String getRootUrl(HttpServletRequest request, String scheme) {
        String serverName = request.getServerName();
        String serverPort = String.valueOf(request.getServerPort());
        if (request.getHeader("X-Forwarded-Scheme") != null)
            scheme = request.getHeader("X-Forwarded-Scheme");
        if (request.getHeader("X-Forwarded-Host") != null)
            serverName = request.getHeader("X-Forwarded-Host");
        if (request.getHeader("X-Forwarded-Port") != null)
            serverPort = request.getHeader("X-Forwarded-Port");
        return scheme + "://" + serverName + ":" + serverPort + request.getContextPath() + "/";
    }

    private String getUrl(HttpServletRequest request, String path, String scheme) {
        return this.getRootUrl(request, scheme) + path;
    }

    private String getUrl(HttpServletRequest request, String path) {
        return this.getUrl(request, path, request.getScheme());
    }

    private String getDropboxUrl(HttpServletRequest request, String path) {
        return this.getUrl(request, "dropbox/" + path, (request.getServerName().equals("localhost") ? "http" : "https"));
    }

    /**
     * returns the DbxWebAuth to be used for dropbox authentication
     */
    public DbxWebAuth getDbxWebAuth(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session, this.sessionKey);
        String redirectUrl = this.getDropboxUrl(request, REDIRECT_URL);
        return new DbxWebAuth(DropBoxController.appConfig, new DbxAppInfo(this.appKey, this.appSecret), redirectUrl, csrfTokenStore);
    }

    // this method assumes a successful import already happened with the file
    // i.e. a previous mapping exists
    private void doActualImport(ResultsFile file, List<String> prevHeaders) throws IOException, InvalidFormatException {
        log.info("DropBoxController.doActualImport with results file id " + file.getId());
        ResultsImport newImport = new ResultsImport();
        // get latest file mapping
        ResultsImport latestImport = ResultsImport.findResultsImportsByResultsFile(file, "run_date", "DESC").getResultList().get(0);
        ResultsFileMapping latestMapping = latestImport.getResultsFileMapping();

        // check if headers changed
        List<String> newHeaders = ResultsFileUtil.getFirstRow(file);
        boolean headerOrderChanged = false;
        boolean headerNumChanged = false;
        if (latestMapping.isSkipFirstRow() && prevHeaders.size() != newHeaders.size()) {
            headerNumChanged = true;
            newImport.setErrors(1);
            newImport.setErrorRows(StringUtils.join(newHeaders, ",") + newImport.getErrorRows());
        } else if (latestMapping.isSkipFirstRow() && !prevHeaders.equals(newHeaders)) {
            headerOrderChanged = true;
            newImport.setErrors(1);
            newImport.setErrorRows(StringUtils.join(newHeaders, ",") + newImport.getErrorRows());
        }

        newImport.setResultsFile(file);
        newImport.setResultsFileMapping(latestMapping);
        // do the actual import
        if (newImport.getErrors() == 0) {
            new ResultsFileMappingController().doImport(newImport);
        } else {
            newImport.setRowsProcessed(1);
            newImport.setRunDate(new Date());
        }
        newImport.persist();

        // send import email if necessary
        String mailTo = newImport.getResultsFile().getImportUser().getEmail();
        String mailSubject = "Automatic results file import failed";
        String mailMessage = "The automatic import of your changes to " + newImport.getResultsFile().getName() + " failed with " + newImport.getErrors() + " errors";
        if (headerNumChanged) {
            mailMessage = "The automatic import of your changes to " + newImport.getResultsFile().getName() + " failed because the number of columns changed.";
        } else if (headerOrderChanged) {
            mailMessage = "The automatic import of your changes to " + newImport.getResultsFile().getName() + " failed because the headers changed.";
        }
        if (newImport.getErrors() > 0) {
            if (mailTo != null) {
                boolean res = MailgunUtil.send(mailTo, mailSubject, mailMessage);
                if (res) {
                    log.info("Automatic import email notification sent");
                } else {
                    log.error("Automatic import email notification sending failed");
                }
            } else {
                log.warn("Automatic import notification skipped because user " + newImport.getResultsFile().getImportUser().getId() + " has no email");
            }
        }
    }

    private int updateFile(ResultsFile file) throws IOException, InvalidFormatException {
        String dropboxPath = file.getDropboxPath();
        if (dropboxPath == null)
            return 0; // can't update, not a dropbox import
        if (file.getImportUser() == null)
            return -1; // no user associated with file
        String userAccessToken = file.getImportUser().getDropboxAccessToken();
        if (userAccessToken == null)
            return -1; // user has no access token to use
        if (!file.getAutomaticUpdates())
            return 0;

        // get file from dropbox
        File tmpFile = DropboxUtil.getDropboxFile(userAccessToken, dropboxPath);
        String chksum = ResultsFileUtil.getSha1Checksum(tmpFile);

        if (!chksum.equals(file.getSha1Checksum())) {
            // the file changed => reimport
            // get the previous headers before we override them
            List<String> prevHeaders = ResultsFileUtil.getFirstRow(file);
            // update file & checksum
            File destFile = new File(file.getFilePath());
            FileUtils.copyFile(tmpFile, destFile);
            tmpFile.delete();
            file.setSha1Checksum(chksum);
            file.persist();
            // reimport
            this.doActualImport(file, prevHeaders);
            return 1;
        }
        log.info("Results file " + file.getId() + ": checksum " + chksum + " didn't change.");
        return 0;
    }

    @RequestMapping(value = "/" + START_URL, method = RequestMethod.GET)
    public void authorize(@RequestParam(value = "eventId", required = false) Long eventId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserProfile up = UserProfileUtil.getLoggedInUserProfile();
        if (up == null) {
            response.sendError(401);
            return;
        }
        request.getSession().setAttribute("dropboxEventId", eventId);
        if (up.getDropboxAccessToken() != null) {
            response.sendRedirect(this.getDropboxUrl(request, DropBoxController.REDIRECT_URL));
            return;
        }
        response.sendRedirect(this.getDbxWebAuth(request).start()); // redirect
                                                                    // user to
                                                                    // authorization
    }

    @ResponseBody
    @RequestMapping(value = "/" + REDIRECT_URL, method = RequestMethod.GET)
    public ResponseEntity<String> authorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserProfile up = UserProfileUtil.getLoggedInUserProfile();
        if (up == null) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        if (up.getDropboxAccessToken() == null) {
            DbxAuthFinish authFinish;
            try {
                authFinish = this.getDbxWebAuth(request).finish(request.getParameterMap());
            } catch (DbxWebAuth.BadRequestException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (DbxWebAuth.BadStateException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (DbxWebAuth.CsrfException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (DbxWebAuth.NotApprovedException ex) {
                // When Dropbox asked "Do you want to allow this app to access
                // your
                // Dropbox account?", the user clicked "No".
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (DbxWebAuth.ProviderException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (DbxException ex) {
                return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            up.setDropboxId(authFinish.userId);
            up.setDropboxAccessToken(authFinish.accessToken);
            up.persist();
        }

        Long eventId = (Long) request.getSession().getAttribute("dropboxEventId");
        String redirectUrl = null;
        if (eventId != null)
            redirectUrl = this.getDropboxUrl(request, PICKER_URL + "?eventId=" + eventId);
        else
            redirectUrl = this.getUrl(request, "");
        response.sendRedirect(redirectUrl);
        return new ResponseEntity<>(redirectUrl, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/" + PICKER_CONTENT_URL, method = RequestMethod.GET)
    public ResponseEntity<String> filepickerContent(@RequestParam(value = "dropboxPath", required = false) String dropboxPath, HttpServletRequest request, HttpServletResponse response)
            throws DbxException, IOException {
        String dbToken = UserProfileUtil.getLoggedInDropboxAccessToken();
        if (dbToken == null)
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        if (dropboxPath == null || dropboxPath.isEmpty())
            dropboxPath = "/";
        DbxEntry.WithChildren listing = DropboxUtil.getDbxClient(dbToken).getMetadataWithChildren(dropboxPath);
        List<DirectoryListEntry> dirEntries = new LinkedList<>();
        for (DbxEntry child : listing.children) {
            dirEntries.add(new DirectoryListEntry(child.name, child.isFolder(), child.path));
        }
        DirectoryListRoot dirRoot = new DirectoryListRoot(dropboxPath, dirEntries);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter strW = new StringWriter();
        mapper.writeValue(strW, dirRoot);
        return new ResponseEntity<>(strW.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/" + PICKER_URL, method = RequestMethod.GET)
    public String filepicker(@RequestParam("eventId") Long eventId, @RequestParam(value = "dropboxPath", required = false) String dropboxPath, Model uiModel,
            HttpServletRequest request, HttpServletResponse response) {
        if (UserProfileUtil.getLoggedInDropboxAccessToken() == null) {
            return "redirect:" + this.getDropboxUrl(request, START_URL + "?eventId=" + eventId);
        }
        // check access token
        try {
            DropboxUtil.getDbxClient(UserProfileUtil.getLoggedInDropboxAccessToken()).getAccountInfo();
        } catch (DbxException e) {
            // access token invalid, reauthorize
            UserProfile up = UserProfileUtil.getLoggedInUserProfile();
            up.setDropboxId(null);
            up.setDropboxAccessToken(null);
            up.persist();
            return "redirect:" + this.getDropboxUrl(request, START_URL + "?eventId=" + eventId);
        }
        // render view
        uiModel.addAttribute("eventId", eventId);
        uiModel.addAttribute("path", ((dropboxPath == null || dropboxPath.isEmpty()) ? "/" : dropboxPath));
        return "dropbox/filepicker";
    }

    @ResponseBody
    @RequestMapping(value = "/" + DEAUTH_URL, method = RequestMethod.POST)
    public ResponseEntity<String> deauth(HttpServletRequest request, HttpServletResponse response) {
        UserProfile up = UserProfileUtil.getLoggedInUserProfile();
        if (up == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        try {
            if (up.getDropboxAccessToken() != null)
                DropboxUtil.getDbxClient(up.getDropboxAccessToken()).disableAccessToken();
        } catch (DbxException ex) {
            // ignoring this, since we're deleting the token anyways
        }
        up.setDropboxId(null);
        up.setDropboxAccessToken(null);
        up.persist();
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/" + IMPORT_URL, method = RequestMethod.POST)
    public ResponseEntity<String> importFile(@RequestParam("eventId") Long eventId, @RequestParam("dropboxPath") String dropboxPath, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // sanity check arguments
        Event event = Event.findEvent(eventId);
        if (event == null)
            return new ResponseEntity<>("unknown event", HttpStatus.BAD_REQUEST);
        if (dropboxPath.isEmpty())
            return new ResponseEntity<>("dropboxPath empty", HttpStatus.BAD_REQUEST);

        // get dropbox credentials
        String accessToken = UserProfileUtil.getLoggedInDropboxAccessToken();
        if (accessToken == null) {
            response.sendRedirect(this.getDropboxUrl(request, START_URL + "?eventId=" + eventId));
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        List<String> emptyMap = Collections.emptyList();
        ResultsImport tmpImport;
        try {
            tmpImport = ResultsFileUtil.importDropbox(UserProfileUtil.getLoggedInUserProfile(), Event.findEvent(eventId), dropboxPath, emptyMap, false);
        } catch (InvalidFormatException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        response.sendRedirect(this.getUrl(request, MAPPING_URL + "/" + tmpImport.getResultsFileMapping().getId() + "?form"));
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public ResponseEntity<String> webhookVerify(@RequestParam("challenge") String challenge, HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(challenge, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> webhookReceive(@RequestBody String plainJson, HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidFormatException {
        // check validity of request
        SecretKeySpec keySpec = new SecretKeySpec(this.appSecret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(plainJson.getBytes());
        String strHmac = Hex.encodeHexString(rawHmac);
        String dbxHmac = request.getHeader("X-Dropbox-Signature");
        if (!strHmac.equalsIgnoreCase(dbxHmac)) {
            return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
        }

        // parse json and update files
        ObjectMapper mapper = new ObjectMapper();
        WebhookRoot parsedJson = null;
        parsedJson = mapper.readValue(plainJson, WebhookRoot.class);
        List<String> deltaUserIds = parsedJson.getDelta().getUsers();
        int filesUpdated = 0;
        for (String userId : deltaUserIds) {
            List<UserProfile> ups = UserProfile.findUserProfilesByDropboxIdEquals(userId).getResultList();
            for (UserProfile up : ups) {
                // TODO can be done more efficiently, by getting /delta for the
                // user and only try to update changed files
                for (ResultsFile rf : up.getResultsFiles()) {
                    filesUpdated += this.updateFile(rf);
                }
            }
        }
        return new ResponseEntity<>("" + filesUpdated, HttpStatus.OK);
    }

    /*
     * classes representing the json for the filepicker
     */
    private static class DirectoryListRoot {
        private final String fullPath;
        private final List<DirectoryListEntry> entries;

        protected DirectoryListRoot(String fullPath, List<DirectoryListEntry> entries) {
            super();
            this.fullPath = ((fullPath == null || fullPath.isEmpty()) ? "/" : fullPath);
            this.entries = entries;
        }

        public String getFullPath() {
            return this.fullPath;
        }

        public List<DirectoryListEntry> getEntries() {
            return this.entries;
        }
    }

    protected static class DirectoryListEntry {
        private final String name;
        private final boolean directory;
        private final String fullPath;

        public DirectoryListEntry(String name, boolean directory, String fullPath) {
            super();
            this.name = name;
            this.directory = directory;
            this.fullPath = fullPath;
        }

        public String getName() {
            return this.name;
        }

        public String getFullPath() {
            return this.fullPath;
        }

        public boolean isDirectory() {
            return this.directory;
        }
    }

    /*
     * classes representing the json the dropbox webhook delivers
     */
    protected static class WebhookRoot {
        private WebhookDelta delta;

        public WebhookDelta getDelta() {
            return this.delta;
        }

        public void setDelta(WebhookDelta d) {
            this.delta = d;
        }

        public List<String> getUserIds() {
            return this.delta.getUsers();
        }
    }

    protected static class WebhookDelta {
        private List<String> users;

        public List<String> getUsers() {
            return this.users;
        }

        public void setUsers(List<String> u) {
            this.users = u;
        }
    }
}
