/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.javaeesecurity;

import br.com.argonavis.javaeesecurity.entity.Grupo;
import br.com.argonavis.javaeesecurity.entity.Usuario;
import br.com.argonavis.javaeesecurity.facade.GrupoFacade;
import br.com.argonavis.javaeesecurity.facade.UsuarioFacade;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author helderdarocha
 */
@WebServlet(name = "JDBCRealmSetup", urlPatterns = {"/JDBCRealmSetup"})
public class JDBCRealmSetup extends HttpServlet {

    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private GrupoFacade grupoFacade;

    private final String[] userids = {"spock", "marvin", "cerebro", "vini", "masha", "niquel", "kusko", "helder.darocha"};
    private final String[] grupos = {"megalomaniacos", "masha", "terraqueos", "ratos", "russos", "alienigenas"};

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(JDBCRealmSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
        String pass = "12345"; // same password for everybody
        md.update(pass.getBytes());
        String md5pass = convertByteToHex(md.digest());

        if (usuarioFacade.count() == 0) { // não faz nada se houver dados na tabela Usuario

            for (String userid : userids) { // Cria usuarios
                Usuario user = new Usuario();
                user.setPassword(md5pass);
                user.setUserid(userid);
                usuarioFacade.create(user);
            }

            for (String groupid : grupos) { // Cria usuarios
                Grupo group = new Grupo();
                group.setNome(groupid);
                grupoFacade.create(group);

                if (groupid.equals("terraqueos")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"masha", "vini", "kusko", "cerebro", "niquel"});
                    group.setUsers(membros);
                }
                if (groupid.equals("megalomaniacos")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"marvin", "cerebro"});
                    group.setUsers(membros);
                }
                if (groupid.equals("masha")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"masha"});
                    group.setUsers(membros);
                }
                if (groupid.equals("ratos")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"cerebro", "niquel"});
                    group.setUsers(membros);
                }
                if (groupid.equals("russos")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"masha", "vini"});
                    group.setUsers(membros);
                }
                if (groupid.equals("alienigenas")) {
                    List<Usuario> membros = usuarioFacade.findByNames(new String[]{"spock", "marvin", "helder.darocha"});
                    group.setUsers(membros);
                }

                grupoFacade.edit(group);
                for (Usuario u : group.getUsers()) {
                    u.addGroup(group);
                    usuarioFacade.edit(u);
                }

            }

            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html><head>");
                out.println("<title>Servlet JDBCRealmSetup</title>");
                out.println("</head><body>");
                out.println("<h1>Tabelas de usuários e grupos criadas e preenchidas com sucesso!</h1>");
out.println("<p><a href='javascript:history.back()'>Voltar</a></a>");
                printContents(out);
out.println("<p><a href='javascript:history.back()'>Voltar</a></a>");
                out.println("</body></html>");
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html><head>");
                out.println("<title>Servlet JDBCRealmSetup</title>");
                out.println("</head><body>");
                out.println("<h1>Tabela Usuario já existe e contém dados.</h1><h3>Setup não foi executado!</h3>");
                out.println("<p><a href='javascript:history.back()'>Voltar</a></a>");
                printContents(out);

                out.println("<p><a href='javascript:history.back()'>Voltar</a></a>");
                out.println("</body></html>");
            }
        }

    }
    private static final Logger LOG = Logger.getLogger(JDBCRealmSetup.class.getName());

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

    private void printContents(PrintWriter out) {
        List<Usuario> users = usuarioFacade.findAll();
        List<Grupo> groups = grupoFacade.findAll();

        out.println("<h3>Usuarios</h3><table border='1'><tr><th>userid</th><th>MD5 hashed password</th><th>grupos</th></tr>");
        for (Usuario u : users) {
            out.println("<tr><td>" + u.getUserid() + "</td><td>" + u.getPassword() + "</td><td>" + printList(u.getGroups()) + "</td></tr>");
        }
        out.println("</table>");

        out.println("<h3>Grupos</h3><table border='1'><tr><th>nome</th><th>usuarios</th></tr>");
        for (Grupo g : groups) {
            out.println("<tr><td>" + g.getNome() + "</td><td>" + printList(g.getUsers()) + "</td></tr>");
        }
        out.println("</table>");
    }

    private <T> String printList(List<T> objects) {
        StringBuilder builder = new StringBuilder();
        for (T object : objects) {
            builder.append(object.toString()).append(", ");
        }
        String list = builder.toString();
        if (list.length() > 2) {
            return list.substring(0, list.length() - 2);
        } else {
            return "";
        }
    }

    private String convertByteToHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buffer.toString();
    }
}
