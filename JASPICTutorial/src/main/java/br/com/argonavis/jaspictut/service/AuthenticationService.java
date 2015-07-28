/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.service;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for a basic stateless authentication service.
 * @author helderdarocha
 */
public interface AuthenticationService {
    void init(ServletContext ctx);
    boolean setCredentials(Map<String, String> credentials, HttpServletRequest request);
    UserData authenticate(Map<String, String> credentials, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
}
