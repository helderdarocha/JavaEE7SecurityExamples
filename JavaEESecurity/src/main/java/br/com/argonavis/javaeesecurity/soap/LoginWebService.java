/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity.soap;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author helderdarocha
 */
@WebService(serviceName = "LoginWebService")
public class LoginWebService {
    
    @Resource
    WebServiceContext webServiceContext;

    @WebMethod
    public String loginAndSayHello() {
        MessageContext messageContext = webServiceContext.getMessageContext();
        messageContext.get(BindingProvider.USERNAME_PROPERTY);
        messageContext.get(BindingProvider.PASSWORD_PROPERTY);
        
        return null;
        
    }
}
