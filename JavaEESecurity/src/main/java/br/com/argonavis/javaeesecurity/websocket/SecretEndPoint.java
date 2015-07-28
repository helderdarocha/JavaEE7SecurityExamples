/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.websocket;

import br.com.argonavis.javaeesecurity.entity.Usuario;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author helderdarocha
 */
@ApplicationScoped
@ServerEndpoint("/secretpoint")
public class SecretEndPoint {

    /**
     * Para poder usar um certificado auto-assinado (localhost)
     * Veja http://www.mkyong.com/webservices/jax-ws/java-security-cert-certificateexception-no-name-matching-localhost-found/
     */
    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
            new javax.net.ssl.HostnameVerifier() {
                public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                    if (hostname.equals("localhost")) {
                        return true;
                    }
                    return false;
                }
            });
    }

    @OnMessage
    public void onMessage(String message, Session client) {
        String[] clientData = message.split(";");
        String contextPath = clientData[0];
        String userid = clientData[1];

        try {
            Client restClient = ClientBuilder.newClient();

            // Resource protegido - apenas administrador e especial podem usar
            WebTarget target = restClient.target("https://" + contextPath);
            Usuario usuario = target.path("webapi/usuario/userid/" + userid)
                    .request(MediaType.APPLICATION_XML)
                    .get(Usuario.class);
            
            String user = client.getUserPrincipal().getName();
                    
            // Para verificar se o usuario esta em um role (Java EE 7), é preciso alterar o handshake
            // Veja: http://stackoverflow.com/questions/30729287/websocket-java-ee-how-to-get-role-of-current-user

            if (usuario == null) {
                client.getBasicRemote().sendText("User does not exist!");
            } else {
                String senha = usuario.getPassword();
                client.getBasicRemote().sendText("RESPOSTA enviada para "+ user +":\n" + senha);
            }
        } catch (IOException e) {
            Logger.getLogger(SecretEndPoint.class.getName()).log(Level.SEVERE, null, e);
        }

    }
}
