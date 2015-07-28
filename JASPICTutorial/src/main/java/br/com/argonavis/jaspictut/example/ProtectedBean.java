/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.example;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;

/**
 * A backing bean for privileged.xhtml to demonstrate authentication,]
 * authorization and security context propagation. Access Exceptions thrown
 * by the EJB are logged (see yoru stack trace) and captured in this class
 * (which sends an accessdenied.jpg image back to the client).
 * 
 * @author helderdarocha
 */
@Named("protectedBean")
@RequestScoped
public class ProtectedBean implements Serializable {
    
    @Inject java.security.Principal principal;

    @EJB
    private ImageBean imageBean;
    private String userid;
    private final String accessDenied = "accessdenied.jpg";

    @PostConstruct
    public void init() {
        userid = principal.getName();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImage1() {
        try {
            return imageBean.publicDomain();
        } catch (Exception e) {
            return accessDenied;
        }
    }

    public String getImage2() {
        try {
            return imageBean.restricted();
        } catch (EJBAccessException e) {
            return accessDenied;
        }
    }

    public String getImage3() {
        try {
            return imageBean.confidential();
        } catch (EJBAccessException e) {
            return accessDenied;
        }
    }
}
