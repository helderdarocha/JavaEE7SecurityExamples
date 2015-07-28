/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.config;

import br.com.argonavis.jaspictut.sam.ExampleServerAuthModule;
import java.util.Collections;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

/**
 * Based on http://arjan-tijms.omnifaces.org/2012/11/implementing-container-authentication.html
 */
class ServerAuthContextImpl implements ServerAuthContext {
    
    private ServerAuthModule sam;

    public ServerAuthContextImpl(CallbackHandler handler) throws AuthException {
        MessagePolicy requestPolicy = null;
        MessagePolicy responsePolicy = null;
        Map<String, String> options = Collections.<String, String>emptyMap();
        
        // Our login module (SAM)
        sam = new ExampleServerAuthModule();
        sam.initialize(requestPolicy, responsePolicy, handler, options);
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        return sam.validateRequest(messageInfo, clientSubject, serviceSubject);
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return sam.secureResponse(messageInfo, serviceSubject);
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        sam.cleanSubject(messageInfo, subject);
    }
    
}
