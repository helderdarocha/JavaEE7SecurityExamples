/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.config;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ClientAuthContext;

/**
 * Based on http://arjan-tijms.omnifaces.org/2012/11/implementing-container-authentication.html
 */
class ClientAuthConfigImpl implements ClientAuthConfig {

    private String layer;
    private String appContext;
    private CallbackHandler handler;
    private Map<String, String> providerProperties;
    private String authContextID;

    public ClientAuthConfigImpl(String layer, String appContext, CallbackHandler handler, Map<String, String> providerProperties) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
        this.providerProperties = providerProperties;
        this.authContextID = appContext;
    }

    @Override
    public ClientAuthContext getAuthContext(String authContextID, Subject clientSubject, Map properties) throws AuthException {
        return null;
    }

    @Override
    public String getMessageLayer() {
        return layer;
    }

    @Override
    public String getAppContext() {
        return appContext;
    }

    @Override
    public String getAuthContextID(MessageInfo messageInfo) {
        return authContextID;
    }

    @Override
    public void refresh() {
    }

    @Override
    public boolean isProtected() {
        return false;
    }

}
