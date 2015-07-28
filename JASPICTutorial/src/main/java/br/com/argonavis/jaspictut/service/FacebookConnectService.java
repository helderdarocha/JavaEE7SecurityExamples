/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.service;

import br.com.argonavis.jaspictut.sam.ExampleServerAuthModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;

/**
 * Based on
 * http://javapapers.com/java/java-facebook-login-with-oauth-authentication/ Not
 * implemented.
 */
public class FacebookConnectService implements AuthenticationService {

    private String facebookAppID;
    private String facebookAppSecret;

    public FacebookConnectService() {
    }

    @Override
    public void init(ServletContext ctx) {
        Properties authconfig = (Properties) ctx.getAttribute("authconfig");
        facebookAppID = authconfig.getProperty("facebookAppID");
        facebookAppSecret = authconfig.getProperty("facebookAppSecret");
    }

    @Override
    public boolean setCredentials(Map<String, String> credentials, HttpServletRequest request) {
        String accessToken = request.getParameter("accessToken");
        System.out.println("Facebook accessToken: " + accessToken);
        if (accessToken != null) {
            credentials.put("accessToken", accessToken);
            return true;
        }
        return false;
    }

    @Override
    public UserData authenticate(Map<String, String> credentials, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String token = credentials.get("accessToken");
        
        // If already authenticated via client API -
        // just retrieve token to set data object to
        // set the principal and group-mapping
        String authDataUrl = "https://graph.facebook.com/me"
                + "?access_token=" + token
                + "&appsecret_proof=" + calculateAppSecretProof(token, facebookAppSecret)
                + "&fields=id,name,email,picture";

        HttpURLConnection connection = null;
        try {
            URL url = new URL(authDataUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException ex) {
            Logger.getLogger(FacebookConnectService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (InputStream is = connection.getInputStream();
             JsonReader rdr = Json.createReader(is)) {

            JsonObject data = rdr.readObject();
            String userid = data.getString("id");
            String email = data.getString("email");
            String name = data.getString("name");
            String picture = data.getJsonObject("picture").getJsonObject("data").getString("url");

            System.out.println("userid: " + userid);
            System.out.println("email: " + email);
            System.out.println("name: " + name);
            System.out.println("picture: " + picture);

            UserData userData = new UserData();

            userData.setUserid(email); // in our app we are using the email as the principal
            userData.setEmail(email);
            userData.setAvatar(picture);
            userData.setName(name);

            request.getSession().setAttribute("userData", userData);

            return userData;
        } catch (IOException ex) {
            Logger.getLogger(FacebookConnectService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Source: https://jira.spring.io/browse/SOCIALFB-148
     * @param token
     * @param appSecret
     * @return
     * @throws Exception 
     */
    private String calculateAppSecretProof(String token, String appSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKey);
            byte[] digest = mac.doFinal(token.getBytes());
            return new String(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException ex) {
            Logger.getLogger(FacebookConnectService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
