package com.base.SpringBoot.base;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@RestController
public class Controlador {


    private static String CLIENT_SECRETS = "/client_secrets.json";
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "1ZAZpVQXq9RNGXtdWU0FI9UajiOhGFXA3i8kFxVNFKFc";
    @GetMapping
    public String hola(){
        return "Hola we";
    }

    @GetMapping("prueba")
    public String prueba() throws GeneralSecurityException, IOException {

        pruebaAdd();

        return "Se ejecuto";
    }


    public void pruebaAdd () throws GeneralSecurityException, IOException {

        Sheets sheetsService = SheetsServiceUtil.getSheetsService();

        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("Expenses January"),
                        Arrays.asList("books", "30"),
                        Arrays.asList("pens", "10"),
                        Arrays.asList("Expenses February"),
                        Arrays.asList("clothes", "20"),
                        Arrays.asList("shoes", "5")));
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }


    @RequestMapping(value = "/ask", method = RequestMethod.GET)
    public void ask(HttpServletResponse response) throws IOException, GeneralSecurityException {

        // Carga el archivo client_secrets.json
        InputStream in = GoogleAuthorizeUtil.class.getResourceAsStream(CLIENT_SECRETS);

        // JacksonFactory - se utiliza para interpretar el contenido del flujo de entrada (InputStream) como un objeto JSON
        // InputStreamReader - Se utiliza para leer contenido del flujo de entrada, conmvirtiendo bytes en caracteres
        // Crea un objeto GoogleClientSecrets a partir del JSON interpretado
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);



        String url = new GoogleAuthorizationCodeRequestUrl(
                clientSecrets,
                clientSecrets.getDetails().getRedirectUris().get(0),
                scopes)
//				.setApprovalPrompt("auto")
                .setApprovalPrompt("force")
                .build();

        System.out.println("Go to the following link in your browser: ");
        System.out.println(url);

        response.sendRedirect(url);

        // a partir de aqui todo son pruebas

        //pruebaAdd();

    }

}
