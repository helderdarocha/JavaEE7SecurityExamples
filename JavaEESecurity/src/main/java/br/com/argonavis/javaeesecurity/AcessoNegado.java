/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.security.jacc.WebRoleRefPermission;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author helderdarocha
 */
@WebServlet(name = "AcessoNegado", urlPatterns = {"/AcessoNegado"})
public class AcessoNegado extends HttpServlet {

    private static final String SUBJECT_HANDLER_KEY = "javax.security.auth.Subject.container";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            
            Subject s = (Subject) PolicyContext.getContext(SUBJECT_HANDLER_KEY);
            
            String principal = request.getUserPrincipal().getName();

            CodeSource cs = new CodeSource(null, (java.security.cert.Certificate[]) null);
            Principal principals[] = (s == null ? new Principal[0] : s.getPrincipals().toArray(new Principal[0]));
            ProtectionDomain pd = new ProtectionDomain(cs, null, null, principals);

            Policy policy = Policy.getPolicy();
            PermissionCollection pc = policy.getPermissions(pd);
            pc.implies(new WebRoleRefPermission(null, null));

            HashSet<String> roleSet = null;

            Enumeration<Permission> e = pc.elements();

            while (e.hasMoreElements()) {
                Permission p = e.nextElement();
                if (p instanceof WebRoleRefPermission) {
                    String roleRef = p.getActions();
                    if (roleSet == null) {
                        roleSet = new HashSet<>();
                    }
                    //confirm roleRef via isUserInRole to ensure proper scoping to Servlet Name
                    if (!roleSet.contains(roleRef) && request.isUserInRole(roleRef)) {
                        roleSet.add(p.getActions());
                    }
                }
            }
            
            
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Acesso Negado</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Acesso Negado</h1>");
                
                out.println("<p><b>User Principal</b>: "+request.getUserPrincipal()+"</p>");
                out.println("<p><b>administrador</b>: "+request.isUserInRole("administrador")+"<br>");
                out.println("<b>especial</b>: "+request.isUserInRole("especial")+"<br>");
                out.println("<b>amigo</b>: "+request.isUserInRole("amigo")+"<br>");
                out.println("<b>outro</b>: "+request.isUserInRole("outro")+"<br>");
                out.println("<b>**</b>: "+request.isUserInRole("**")+"<br>");
                
                
                out.println("</body>");
                out.println("</html>");
            }
        }   catch (PolicyContextException ex) {
            Logger.getLogger(AcessoNegado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
