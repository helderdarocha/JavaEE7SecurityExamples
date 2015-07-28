/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.sam;

import br.com.argonavis.jaspictut.service.AuthenticationException;
import br.com.argonavis.jaspictut.service.AuthenticationService;
import br.com.argonavis.jaspictut.service.AuthenticationServiceFactory;
import br.com.argonavis.jaspictut.service.UserData;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a simple SAM which authenticates with a remote service.
 * The atual authentication is delegated to a class which deals with the
 * service-specific aspects of authentication in a class which
 * implements br.com.argonavis.jaspictut.service.AuthenticationService.
 * Include your authentication data in /WEB-INF/auth.properties and
 * use them in the AuthenticationService implementation and login
 * pages & javascript. Use the Google SignIn implementation as an example.
 * 
 * @author helderdarocha
 */
public class ExampleServerAuthModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Class<?>[] supportedMessageTypes = new Class[]{HttpServletRequest.class, HttpServletResponse.class};

    public ExampleServerAuthModule() {
    }
    
    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    /**
     * This is where we will authenticate the subject. The actual authentication is delegated
     * to the classes and interfaces in br.com.argonavis.jaspictut.service for 
     * service-specific authentication.
     * 
     * @param messageInfo
     * @param clientSubject
     * @param serviceSubject
     * @return
     * @throws AuthException 
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

        final HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        // 1) Check for Signout flag
        String signoutKey = request.getParameter("signout"); // signout from current service
        if (signoutKey != null) {
            System.out.println("LOGGING OUT!");
            try {
                request.logout(); // should null principal, roles and remote user
                request.getSession().invalidate(); // remove session data
            } catch (ServletException ex) {
                Logger.getLogger(ExampleServerAuthModule.class.getName()).log(Level.SEVERE, null, ex);
            }
            return AuthStatus.SUCCESS; // should be FAILURE (but its not really a FAILURE)
        }

        // 2) Check if principal exists and reuse it (JASPIC is stateless)
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal != null) { // active principal, use current data
            try {
                handler.handle(new Callback[]{
                    new CallerPrincipalCallback(clientSubject, userPrincipal)
                });
                return AuthStatus.SUCCESS;
            } catch (IOException | UnsupportedCallbackException ex) {
                Logger.getLogger(ExampleServerAuthModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // 3) Select service: "google", "facebook", etc.
        String serviceKey = request.getParameter("service");
        System.out.println("Service: " + serviceKey);
        if (serviceKey == null) { // try to read service name
            return AuthStatus.SUCCESS; // keep trying - request data still did not arrive  (only SUCCESS OR FAILURE work)
        }

        // 4) Setup auth service 
        // This is a simple implementation. See br.com.argonavis.jaspictut.service classes and interfaces.
        // For new services, implement AuthenticationService
        // and call init(), setCredentials() and then authenticate()
        AuthenticationService service = AuthenticationServiceFactory.getFactory().getService(serviceKey);
        service.init(request.getServletContext());
        
        // 5) Read the tokens from the request and store in the credentials map
        Map<String, String> credentials = new HashMap<>();
        if (!service.setCredentials(credentials, request)) {
            return AuthStatus.FAILURE;
        }
        System.out.println("Credential keys: " + Arrays.toString(credentials.keySet().toArray(new String[]{})));

        // 6) Try to authenticate with credentials map
        UserData data = null;
        try {
            data = service.authenticate(credentials, request, response);
        } catch (AuthenticationException ex) {
            Logger.getLogger(ExampleServerAuthModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (data == null) { // not authenticated
            System.out.println("Failed authentication in " + serviceKey);
            return AuthStatus.FAILURE;
        }

        String userid = data.getUserid();
        System.out.println("Authenticated user ID: " + userid);

        // 7) Set principal (using authenticated userid/email)
        // Map a principal to the privileged-user role in glassfish-web.xml to have full privileges
        CallerPrincipalCallback callerPCB = new CallerPrincipalCallback(clientSubject, userid);

        // 8) Set groups. Role-mapping depends on vendor-specific configuration in glassfish-web.xml
        // All unknown remote users will belong to the GUESTS group which is mapped to the "remote-user" role
        // in glassfish-web.xml and has limited privileges
        GroupPrincipalCallback groupPCB = new GroupPrincipalCallback(clientSubject, new String[]{"GUESTS"});

        // 9) Register callbacks with the container
        try {
            handler.handle(new Callback[]{callerPCB, groupPCB});
        } catch (IOException | UnsupportedCallbackException ex) {
            Logger.getLogger(ExampleServerAuthModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 10) Place login data in a session attribute so that the views can use them
        UserData d = (UserData)request.getSession().getAttribute("userData");
        System.out.println(d.getAvatar());
        
        // 11) Register a login session
        messageInfo.getMap().put("javax.servlet.http.registerSession", Boolean.TRUE.toString());
        
        // 12) Return login successful
        return AuthStatus.SUCCESS;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
    }
}
