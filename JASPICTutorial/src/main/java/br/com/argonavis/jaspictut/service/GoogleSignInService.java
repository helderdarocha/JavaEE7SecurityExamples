/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.service;

import br.com.argonavis.jaspictut.sam.ExampleServerAuthModule;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Example AuthenticationService for Google SignIn.
 * See: https://developers.google.com/identity/sign-in/web/
 * 
 * @author helderdarocha
 */
public class GoogleSignInService implements AuthenticationService {

    private static final String ISS = "accounts.google.com";
    
    private String googleClientID;

    public GoogleSignInService() {}
    
    @Override
    public void init(ServletContext ctx) {
        Properties authconfig = (Properties)ctx.getAttribute("authconfig");
        googleClientID = authconfig.getProperty("googleClientID");
    }
    
    @Override
    public boolean setCredentials(Map<String, String> credentials, HttpServletRequest request) {
        String idToken = request.getParameter("idtoken");
        System.out.println("Google ID Token: " + idToken);
        if (idToken != null) {
            credentials.put("idToken", idToken);
            return true;
        }
        return false;
    }

    @Override
    public UserData authenticate(Map<String, String> credentials, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException  {

        if (googleClientID == null) {
            throw new AuthenticationException("Configuration error: missing Google Client ID - call init() and check auth.properties file.");
        }
        
        String idToken = credentials.get("idToken");
        if (idToken == null) {
            throw new AuthenticationException("NULL Token. Google ID Token cannot be null!");
        }

        String tokenCheckUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken;

        URL url = null;
        try {
            url = new URL(tokenCheckUrl);
        } catch (MalformedURLException ex) {
            throw new AuthenticationException("Malformed URL: " + tokenCheckUrl);
        }
        
        System.out.println("tokenCheckUrl: " + tokenCheckUrl);

        // Now try to read the authentication data
        try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            String iss = obj.getString("iss");
            String sub = obj.getString("sub"); // user ID
            String exp = obj.getString("exp"); // expires
            String email = obj.getString("email");
            String emailVerified = obj.getString("email_verified");
            String aud = obj.getString("aud");
            String name = obj.getString("name");
            String picture = obj.getString("picture");
            
            System.out.println("ISS: "+iss);
            System.out.println("Email verified: "+emailVerified);
            System.out.println("AUD: "+aud);
            System.out.println("Name: "+name);
            System.out.println("Expires: " + new Date(Long.parseLong(exp)*1000));

            // authenticate - check if provider is google, if email is verified, if client id matches and message is not expired
            if (iss.equals(ISS) 
                    && emailVerified.equals("true") 
                    && aud.equals(googleClientID)
                    && Long.parseLong(exp) * 1000 > new Date().getTime()   // expires in the future
                    ) {
                
                // authentication done - save the data somewhere
                
                UserData userData = new UserData();
                
                userData.setUserid(email); // in our app we are using the email as the principal
                userData.setEmail(email);
                userData.setAvatar(picture);
                userData.setName(name);
                
                request.getSession().setAttribute("userData", userData);

                return userData;
            } else {
               throw new AuthenticationException("Authentication failed. Bad credentials.");
            }

        } catch (IOException ex) {
            Logger.getLogger(ExampleServerAuthModule.class.getName()).log(Level.SEVERE, null, ex);
            throw new AuthenticationException("Unable to read response from Google authentication service.");
        }
    }
}
