/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.pojos.DeclaracionJurada;
import sv.gob.mined.paquescolar.model.pojos.OfertaGlobal;

/**
 *
 * @author misanchez
 */
public class Reportes {

    public static final String PATH_REPORTES = File.separator + "sv" + File.separator + "gob" + File.separator + "mined" + File.separator + "apps" + File.separator + "reportes" + File.separator;
    public static final String PATH_REPORTES_PAGO_PROVEEDORES = File.separator + "sv" + File.separator + "gob" + File.separator + "mined" + File.separator + "apps" + File.separator + "reportes" + File.separator + "pagoproveedor" + File.separator;
    public static final String PATH_IMAGENES = File.separator + "resources" + File.separator + "images" + File.separator;

    public static InputStream getPathReporte(String pathReporte) {
        String tmpPath;
        try {
            if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
                tmpPath = JsfUtil.getValorFromBundleByKey("path_reportes_win");
                pathReporte = pathReporte.replaceAll("/", "\\\\");
            } else {
                tmpPath = JsfUtil.getValorFromBundleByKey("path_reportes_linux");
            }
            return new FileInputStream(tmpPath.concat(pathReporte));
        } catch (FileNotFoundException ex) {
            try {
                tmpPath = JsfUtil.getValorFromBundleByKey("path_reportes_linux2");

                return new FileInputStream(tmpPath.concat(pathReporte));
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, "ERROR REPORTES: No se encontro el reporte en la ubicación " + pathReporte, ex);
                return null;
            }

        }
    }

    /**
     *
     * @param lst
     * @param param
     * @param paqueteRpt
     * @param nombreRpt
     * @param nombrePdfGenerado
     */
    public static void generarRptBeanConnection(List lst, HashMap param, String paqueteRpt, String nombreRpt, String nombrePdfGenerado) {
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            param.put("ubicacionImagenes", ctx.getRealPath(PATH_IMAGENES));
            JasperPrint jasperPrint = JasperFillManager.fillReport(Reportes.getPathReporte(paqueteRpt + File.separator + nombreRpt), param, new JRBeanCollectionDataSource(lst));
            responseRptPdf(jasperPrint, nombrePdfGenerado);
        } catch (IOException | JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JasperPrint generarRptBeanConnection(List lst, HashMap param, String paqueteRpt, String nombreRpt) {
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            param.put("ubicacionImagenes", ctx.getRealPath(PATH_IMAGENES));
            JasperPrint jasperPrint = JasperFillManager.fillReport(Reportes.getPathReporte(paqueteRpt + File.separator + nombreRpt), param, new JRBeanCollectionDataSource(lst));
            return jasperPrint;
        } catch (JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param reportesEJB
     * @param param
     * @param paqueteRpt
     * @param nombreRpt
     * @param nombrePdfGenerado
     */
    public static void generarRptSQLConnection(ReportesEJB reportesEJB, HashMap param, String paqueteRpt, String nombreRpt, String nombrePdfGenerado) {
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            param.put("ubicacionImagenes", ctx.getRealPath(PATH_IMAGENES));
            JasperPrint jasperPrint = reportesEJB.getRpt(param, Reportes.getPathReporte((paqueteRpt + File.separator + nombreRpt + ".jasper")));
            responseRptPdf(jasperPrint, nombrePdfGenerado);
        } catch (IOException | JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JasperPrint getRptSQLConnection(ReportesEJB reportesEJB, HashMap param, String paqueteRpt, String nombreRpt) {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        param.put("ubicacionImagenes", ctx.getRealPath(PATH_IMAGENES));
        return reportesEJB.getRpt(param, Reportes.getPathReporte((paqueteRpt + File.separator + nombreRpt)));
    }

    /**
     *
     * @param jp
     * @param nombrePdfGenerado
     * @throws JRException
     * @throws IOException
     */
    private static void responseRptPdf(JasperPrint jp, String nombrePdfGenerado) throws JRException, IOException {
        byte[] content = JasperExportManager.exportReportToPdf(jp);
        UtilFile.downloadFileBytes(content, nombrePdfGenerado, UtilFile.CONTENIDO_PDF, UtilFile.EXTENSION_PDF);
    }

    /**
     *
     * @param lst
     * @param param
     * @param codigoEntidad
     * @param sobredemanda
     * @param reportesEJB
     * @param anho
     * @param lstRpts
     */
    public static void generarRptsContractuales(List<?> lst, HashMap param, String codigoEntidad, Boolean sobredemanda, ReportesEJB reportesEJB, String anho, String... lstRpts) {
        try {
            List<JasperPrint> jasperPrintList = new ArrayList();
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            param.put("ubicacionImagenes", ctx.getRealPath(PATH_IMAGENES));
            for (String rpt : lstRpts) {
                if (rpt.contains("Uni")) {
                    param.put("descripcionRubro", (sobredemanda ? "SOBREDEMANDA DE " : "") + "SERVICIOS DE CONFECCION DE UNIFORMES");
                } else if (rpt.contains("Uti")) {
                    param.put("descripcionRubro", (sobredemanda ? "SOBREDEMANDA DE " : "") + "SUMINISTRO DE PAQUETES DE UTILES ESCOLARES");
                } else {
                    param.put("descripcionRubro", (sobredemanda ? "SOBREDEMANDA DE " : "") + "PRODUCCION DE ZAPATOS");
                }

                if (rpt.contains("rptCertUti_2017")) {
                    jasperPrintList.add(reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt)));
                } else if (rpt.contains("rptCertUti2021")) {
                    jasperPrintList.add(getRptSQLConnection(reportesEJB, param, "sv/gob/mined/apps/sispaqescolar/reporte/", rpt));
                } else {
                    if (rpt.contains("rptCertUni")) {
                        if (Integer.parseInt(anho) > 2017) {
                            param.put("descripcionRubro", "SERVICIOS DE CONFECCION DEL PRIMER UNIFORME");
                            jasperPrintList.add(JasperFillManager.fillReport(Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt), param, new JRBeanCollectionDataSource(lst)));
                            param.put("descripcionRubro", "SERVICIOS DE CONFECCION DEL SEGUNDO UNIFORME");
                            jasperPrintList.add(JasperFillManager.fillReport(Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt), param, new JRBeanCollectionDataSource(lst)));
                        } else {
                            jasperPrintList.add(JasperFillManager.fillReport(Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt), param, new JRBeanCollectionDataSource(lst)));
                        }
                    } else if (rpt.contains("rptDeclaracionJuradaMatricula")) {
                        jasperPrintList.add(reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt)));
                    } else {
                        jasperPrintList.add(JasperFillManager.fillReport(Reportes.getPathReporte("sv/gob/mined/apps/sispaqescolar/reporte" + File.separator + rpt), param, new JRBeanCollectionDataSource(lst)));
                    }
                }
            }
            generarReporte(jasperPrintList, codigoEntidad);
        } catch (JRException | IOException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.WARNING, "Error en la impresion de los documentos contractuales {0} anho {1}", new Object[]{codigoEntidad, anho});
        }
    }

    /**
     * buscar las exceciones de este metodo y controlar los errores.
     *
     * @param jasperPrintList
     * @param nombreRpt
     * @throws IOException
     * @throws JRException
     */
    public static void generarReporte(List<JasperPrint> jasperPrintList, String nombreRpt) throws IOException, JRException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
        exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.FALSE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.exportReport();

        UtilFile.downloadFileBytes(baos.toByteArray(), nombreRpt, UtilFile.CONTENIDO_PDF, UtilFile.EXTENSION_PDF);
    }

    public static JasperPrint getReporteAImprimir(String path, Map<String, Object> param, JRDataSource ds) throws JRException {
        return JasperFillManager.fillReport(Reportes.getPathReporte(path + ".jasper"), param, ds);
    }

    public static List<JasperPrint> getReporteOfertaDeProveedor(CapaInstPorRubro capacidadInst, Empresa empresa,
            DetalleProcesoAdq detalleProcesoAdq, List<OfertaGlobal> lstDatos, List<DeclaracionJurada> lstDeclaracion) {

        Boolean imprimirRpt = false;
        List<JasperPrint> jasperPrintList = new ArrayList();
        try {
            String lugar = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                    getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getUbicacion();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            HashMap param = new HashMap();
            param.put("ubicacionImagenes", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
            param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
            param.put("usuarioInsercion", VarSession.getVariableSessionUsuario());
            param.put("pLugar", lugar + ", " + sdf.format(new Date()));
            param.put("pRubro", JsfUtil.getNombreRubroById(capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres()));
            param.put("pIdRubro", capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres().intValue());

            lstDatos.get(0).setRubro(JsfUtil.getNombreRubroById(capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres()));
            if (lstDatos.get(0).getDepartamento().contains("TODO EL PAIS")) {
                param.put("productor", Boolean.TRUE);
            } else {
                param.put("productor", Boolean.FALSE);
            }

            String tmp = "";
            if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() > 6 && detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue() == 2) {
                tmp = "2019";
            }

            //String muni = VarSession.getNombreMunicipioSession();
            String nombreRpt;

            switch (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue()) {
                case 8:
                case 9:
                    imprimirRpt = ((capacidadInst.getIdMuestraInteres().getAceptacionTerminos() == 1) && (capacidadInst.getIdMuestraInteres().getDatosVerificados() == 1));

                    if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() > 8) {
                        if (imprimirRpt) {
                            String idGestion = capacidadInst.getIdMuestraInteres().getIdGestion();
                            lugar = empresa.getIdMunicipio().getNombreMunicipio().concat(", ").concat(empresa.getIdMunicipio().getCodigoDepartamento().getNombreDepartamento());
                            param.put("pCorreoPersona", capacidadInst.getIdMuestraInteres().getIdEmpresa().getIdPersona().getEmail());
                            param.put("pIdGestion", idGestion);
                            param.put("pLugar", lugar);

                            jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/oferta" + File.separator + "rptOfertaGlobalProv" + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho(), param, new JRBeanCollectionDataSource(lstDatos)));
                            nombreRpt = "rptDeclaracionJurAceptacionPerProv" + ((empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) ? "Nat" : "Jur");
                            jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + nombreRpt + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho(), param, new JRBeanCollectionDataSource(lstDeclaracion)));
                        } else {
                            JsfUtil.mensajeAlerta("Este proveedor no ha actualizado la oferta para el año " + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho());
                        }
                    } else {
                        imprimirRpt = true;
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/oferta" + File.separator + "rptOfertaGlobal" + tmp, param, new JRBeanCollectionDataSource(lstDatos)));
                        nombreRpt = "rptDeclaracionJurAceptacionPer" + ((empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) ? "Nat" : "Jur");
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + nombreRpt + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho(), param, new JRBeanCollectionDataSource(lstDeclaracion)));
                    }

                    break;
                default:
                    imprimirRpt = true;
                    if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) {
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurCumplimientoPerNat", param, new JRBeanCollectionDataSource(lstDeclaracion)));
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurAceptacionPerNat2", param, new JRBeanCollectionDataSource(lstDeclaracion)));
                    } else {
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurCumplimientoPerJur", param, new JRBeanCollectionDataSource(lstDeclaracion)));
                        jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurAceptacionPerJur2", param, new JRBeanCollectionDataSource(lstDeclaracion)));
                    }
                    break;
            }
        } catch (JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (imprimirRpt) {
            return jasperPrintList;
        } else {
            return new ArrayList();
        }
    }
}
