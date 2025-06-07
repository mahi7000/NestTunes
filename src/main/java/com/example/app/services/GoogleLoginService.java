//package com.example.app.services;
//
//
//
//import java.io.File;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.file.Paths;
//import java.util.List;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.oauth2.Oauth2;
//import com.google.api.services.oauth2.model.Userinfo;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//
//
//
//public class GoogleLoginService {
//    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";  // Should be in resources root
//    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = List.of(
//        "https://www.googleapis.com/auth/userinfo.profile",
//        "https://www.googleapis.com/auth/userinfo.email"
//    );
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//
//    public static Userinfo authenticateUser() throws Exception {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//
//        // Load credentials.json file from resources folder
//        InputStream in = GoogleLoginService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new Exception("Resource not found: " + CREDENTIALS_FILE_PATH + "\nMake sure the file is inside your resources folder.");
//        }
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // ✅ Delete old tokens before each login to force re-authentication
//    File tokensDir = Paths.get(TOKENS_DIRECTORY_PATH).toFile();
//    if (tokensDir.exists()) {
//        for (File file : tokensDir.listFiles()) {
//            file.delete();
//        }
//    }
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY_PATH).toFile()))
//                .setAccessType("offline")
//                .build();
//
//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//
//        // Build OAuth2 client to get user info
//        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName("JavaFX Login")
//                .build();
//
//        return oauth2.userinfo().get().execute();
//    }
//}
