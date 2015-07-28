/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This listener will setup JASPIC and store the authentication
 * properties from /WEB-INF/auth.properties in the "authconfig"
 * servlet context attribute. 
 * 
 * JASPIC must also be setup in the server. 
 * 
 * In Glassfish 4.1 (to set up the Google SignIn Provider):
 * 1) In the Admin Console go to
 *   Configurations/server-config/Security/Message Security/HttpServlet/Providers
 * 2) Click [New...]
 * 3) Fill in the fields:
 *   Provider ID: [SocialLoginProvider]
 *   Provider Type: [server]
 *   Class Name: [br.com.argonavis.jaspictut.sam.ExampleServerAuthModule]
 * 
 * The Provider ID must be the same one used in /WEB-INF/glassfish-web.xml.
 * The Class Name is the fully qualified name of the SAM.
 * Restart the server.
 * 
 * You can retrieve the properties in
 * JSF using: 
 *    #{authconfig.PROPERTY-NAME}" 
 * and from a ServletContext object (ex: ctx) using: 
 *    ((Properties)ctx.getAttribute("authconfig")).getProperty("PROPERTY-NAME")
 * 
 * @author helderdarocha
 */
@WebListener
public class AuthConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // JASPIC setup
        AuthConfigProvider provider = new AuthConfigProviderImpl();
        String layer = "HttpServlet";
        String appContext = sce.getServletContext().getVirtualServerName()
                          + " " 
                          + sce.getServletContext().getContextPath();
        String description = "Exemplo Jaspic";
        
        AuthConfigFactory factory = AuthConfigFactory.getFactory();
        factory.registerConfigProvider(provider, layer, appContext, description);
        
        // Load properties from auth.properties file with SSO data
        InputStream authProps = sce.getServletContext().getResourceAsStream("/WEB-INF/auth.properties");
        Properties props = new Properties();
        try {
            props.load(authProps);
            sce.getServletContext().setAttribute("authconfig", props);
        } catch (IOException ex) {
            Logger.getLogger(AuthConfigListener.class.getName()).log(Level.SEVERE, null, "Unable to load auth.properties file: " + ex);
        } 
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
