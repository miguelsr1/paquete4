/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author RCeron
 */
public class FilterWeb implements Filter {

    public static final String OUTCOME_FALLO = "/index.mined";
    private FilterConfig filterConfig = null;
    //private String recurso = "java:comp/env/jdbc/PaqueteDS";
    //private String recurso = "java:/PaqueteDS";

    public FilterWeb() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        doBeforeProcessing(request, response);
        Throwable problem = null;
        try {
            String user = (String) session.getAttribute("Usuario");
            if (user != null) {
                chain.doFilter(request, response);
            } else {
                session.setAttribute("msg", "Error Autenticacion");
                session.setAttribute("errorPag", req.getRequestURI());
                String uri = res.encodeRedirectURL(req.getContextPath() + OUTCOME_FALLO);
                res.sendRedirect(uri);
            }
        } catch (Exception ex) {
            Logger.getLogger(FilterWeb.class.getName()).log(Level.WARNING, "Ah ocurrido un error: {0} peticion de {1}", new Object[]{ex.getMessage(), req.getContextPath()});
        }
        doAfterProcessing(request, response);
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FilterWeb(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n");
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
                System.out.println("Error en sendProcessingError 1, " + ex.getMessage());
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
                System.out.println("Error en sendProcessingError 1, " + ex.getMessage());
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
            System.out.println("Error en Stact Trace Filter, " + ex.getMessage());
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    /* public Boolean verificarOpcion(String user, String urlPag) throws NamingException {
        Boolean autorizado = false;
        String query = "SELECT OPCION_MENU.NOMBRE_PANEL,PERSONA.USUARIO FROM OPCION_MENU "
                + "INNER JOIN USUARIO_OPC_MENU ON OPCION_MENU.ID_OPC_MENU = USUARIO_OPC_MENU.ID_OPC_MENU "
                + "INNER JOIN USUARIO ON USUARIO.ID_USUARIO = USUARIO_OPC_MENU.ID_USUARIO "
                + "INNER JOIN PERSONA ON USUARIO.ID_PERSONA = PERSONA.ID_PERSONA "
                + "WHERE PERSONA.USUARIO=?1 AND opcion_menu.nombre_panel=?2";
        /*ResultSet rs = null;
         PreparedStatement pstm = null;
         Context ctx = new InitialContext();
         Connection conn = null;*/
    //EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    //DataSource origendatos = (DataSource) ctx.lookup(recurso);
    //try {
    /*synchronized (origendatos) {
             conn = origendatos.getConnection();
             }
             if (conn != null) {
             pstm = conn.prepareStatement(query);
             pstm.setString(1, user);
             pstm.setString(2, urlPag);
             rs = pstm.executeQuery();
             while (rs.next()) {
             autorizado=true; 
             }
             } else {
             System.out.println("Error al obtener la conexion");
             }*/
 /* Query q = em.createNativeQuery(query);
            q.setParameter(1, user);
            q.setParameter(2, urlPag);

            if(q.getResultList().isEmpty()){
                autorizado = false;
            }else{
                autorizado = true;
            }
        } finally {
            em.close();
        }
        if (urlPag.equals("/app/principal.mined")) {
            autorizado = true;
        }
        return autorizado;
    }*/
}
