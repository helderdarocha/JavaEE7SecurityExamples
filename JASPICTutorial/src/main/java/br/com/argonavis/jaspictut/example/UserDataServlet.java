/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.jaspictut.example;

import br.com.argonavis.jaspictut.service.UserData;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet which returns a JSON object containing the
 * data of the currently logged in user.
 * 
 * @author helderdarocha
 */
@WebServlet(name = "UserDataServlet", urlPatterns = {"/UserDataServlet"})
public class UserDataServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserData userData = (UserData)request.getSession().getAttribute("userData");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("{");
            out.println("\"userid\":\""+userData.getUserid()+"\",");
            out.println("\"email\":\""+userData.getEmail()+"\",");
            out.println("\"name\":\""+userData.getName()+"\",");            
            out.println("\"avatar\":\""+userData.getAvatar()+"\",");
            out.println("\"role\":{\"remote\":\""+request.isUserInRole("remote-user")+"\",");
            out.println(    "\"privileged\":\""+request.isUserInRole("privileged-user")+"\",");
            out.println(           "\"all\":\""+request.isUserInRole("**")+"\"}");
            out.println("}");
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
