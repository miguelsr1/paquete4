/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author misanchez
 */
public class UtilFile {

    public static final String EXTENSION_XLS = "xls";
    public static final String EXTENSION_PDF = "pdf";
    public static final String EXTENSION_CSV = "csv";
    public static final String EXTENSION_TXT = "txt";
    public static final String CONTENIDO_XLS = "application/ms-excel";
    public static final String CONTENIDO_PDF = "application/pdf";
    public static final String CONTENIDO_TEXTPLANO = "application/octet-stream";

    private static final DateFormat FORMAT_DATE_RPT = new SimpleDateFormat("ddMMMyy_HHmmss");

    public static void downloadFileTextoPlano(String contenido, String nombreFile, String extension) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        response.reset();
        response.setContentType(CONTENIDO_TEXTPLANO);
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreFile + getFechaGeneracionReporte() + "." + extension);
        OutputStream out = response.getOutputStream();
        Writer writer = new OutputStreamWriter(out, "ISO-8859-15");
        try {
            writer.write(contenido);
        } finally {
            writer.close();
        }
        facesContext.responseComplete();
    }

    public static void downloadFileBytes(byte[] outArray, String nombreFile, String tipoDeContenido, String extension) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        response.setContentType(tipoDeContenido);
        response.setContentLength(outArray.length);
        response.setHeader("Content-disposition", "attachment; filename=" + nombreFile + "-" + getFechaGeneracionReporte() + "." + extension);
        OutputStream outStream = response.getOutputStream();
        outStream.write(outArray);
        outStream.flush();
        outStream.close();
        facesContext.responseComplete();
        facesContext.renderResponse();
    }

    public static String getFechaGeneracionReporte() {
        return FORMAT_DATE_RPT.format(new Date());
    }
}
