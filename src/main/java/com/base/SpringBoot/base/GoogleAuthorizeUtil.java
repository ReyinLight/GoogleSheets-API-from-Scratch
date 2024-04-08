package com.base.SpringBoot.base;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleAuthorizeUtil {

    static private final String CLIENT_SECRETS = "/client_secrets.json";

    static private final String TOKENS_DIRECTORY_PATH = "tokens";
    public static Credential authorize() throws IOException, GeneralSecurityException {

        // Carga el archivo client_secrets.json
        InputStream in = GoogleAuthorizeUtil.class.getResourceAsStream(CLIENT_SECRETS);

        // JacksonFactory - se utiliza para interpretar el contenido del flujo de entrada (InputStream) como un objeto JSON
        // InputStreamReader - Se utiliza para leer contenido del flujo de entrada, conmvirtiendo bytes en caracteres
        // Crea un objeto GoogleClientSecrets a partir del JSON interpretado
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));


        //List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);


        // Pruebas para abrir la interfaz grafica de autenticacion
/*
        String url = new GoogleAuthorizationCodeRequestUrl(
                clientSecrets,
                "localhost:8080",
                Arrays.asList("https://www.googleapis.com/auth/userinfo.email",
                        "https://www.googleapis.com/auth/userinfo.profile"))
                .setState("/profile")
                .build();


        String url = new GoogleAuthorizationCodeRequestUrl(
                clientSecrets,
                clientSecrets.getDetails().getRedirectUris().get(0),
                scopes)
//				.setApprovalPrompt("auto")
                .setApprovalPrompt("force")
                .build();
*/



        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JacksonFactory.getDefaultInstance(),
                        clientSecrets,
                        scopes)
//                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance() == null ? new MemoryDataStoreFactory() : MemoryDataStoreFactory.getDefaultInstance()) // Valida si en la ejecucion actual ya se autorizó
//                .setDataStoreFactory(new MemoryDataStoreFactory()) // Guarda el token en la memoria
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))) // Guarda el token en una carpeta para su uso posterior
                .setAccessType("offline")
                .build();


        String url = new GoogleAuthorizationCodeRequestUrl(
                clientSecrets,
                clientSecrets.getDetails().getRedirectUris().get(0),
                scopes)
//				.setApprovalPrompt("auto")
                .setApprovalPrompt("force")
                .build();




        // Callback Local
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        // LocalServerReceiver receiver = new LocalServerReceiver(); // Callback generico http://localhost:52359/Callback


        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");


        return credential;


/*
        // Callback en la nube
//        URI redirectUri = URI.create("https://googleapi-prueba-googlesheets.fly.dev");
        URI redirectUri = URI.create("http://localhost:8080/prueba");
        // Use the custom redirect URI instead of default one
        AuthorizationCodeInstalledApp authorizationApp = new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver.Builder()
                        .setPort(8888)
                        .setHost("0.0.0.0")
                        .setCallbackPath("/callback")
                        .build()) {

            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                authorizationUrl.setRedirectUri(redirectUri.toString());
                super.onAuthorization(authorizationUrl);
            }
        };

        return authorizationApp.authorize("user");
*/



    }
}
