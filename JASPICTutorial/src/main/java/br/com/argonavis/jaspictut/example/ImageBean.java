/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.example;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 * A simple EJB to demonstrate authorization and security context
 * propagation. This component is used by ProtectedBean and privileged.xhtml
 * 
 * @author helderdarocha
 */
@Stateless
public class ImageBean implements Serializable {
    
    @Resource 
    SessionContext ctx;

    public String publicDomain() {
        return "minipeixe.jpg";
    }

    @RolesAllowed({"remote-user", "privileged-user"})
    public String restricted() {
        return "lupita.jpg";
    }
    
    @RolesAllowed("privileged-user")
    public String confidential() {
        return "armenia.jpg";
    }
}
