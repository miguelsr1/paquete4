/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov.modulo;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.apps.utilitario.Herramientas;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.model.Anho;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.DetRubroMuestraInteres;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.PreciosRefRubroEmp;
import sv.gob.mined.paquescolar.model.ProcesoAdquisicion;
import sv.gob.mined.paquescolar.model.pojos.OfertaGlobal;

/**
 *
 * @author MISanchez
 */
@Named
@ViewScoped
public class DeclaracionMB implements Serializable {

    private Boolean aceptarCondiciones = false;
    private Boolean aceptarDeclaracion = false;
    private String idGestion = "";
    private String cabecera;
    private String detalle;
    private Empresa empresa = new Empresa();
    private DetalleProcesoAdq detalleProcesoAdq = new DetalleProcesoAdq();
    private CapaInstPorRubro capacidadInst = new CapaInstPorRubro();

    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private ReportesEJB reportesEJB;

    private static final ResourceBundle UTIL_CORREO = ResourceBundle.getBundle("Bundle");

    @PostConstruct
    public void ini() {
        if (VarSession.isVariableSession("idEmpresa")) {
            ProveedorMB proveedorMB = ((ProveedorMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                    getValue(FacesContext.getCurrentInstance().getELContext(), null, "proveedorMB"));
            empresa = proveedorMB.getEmpresa();
            Anho anho = proveedorMB.getAnho();
            ProcesoAdquisicion proceso = anho.getProcesoAdquisicionList().get(0);

            if (proceso == null || proceso.getIdProcesoAdq() == null) {
                JsfUtil.mensajeAlerta("Debe seleccionar un proceso de contratación");
            } else {
                if (proceso.getPadreIdProcesoAdq() != null) {
                    proceso = proceso.getPadreIdProcesoAdq();
                }
                DetRubroMuestraInteres detRubro = proveedorEJB.findDetRubroByAnhoAndRubro(anho.getIdAnho(), empresa.getIdEmpresa());
                capacidadInst = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), anho.getIdAnho(), empresa, CapaInstPorRubro.class);
                if (capacidadInst != null) {
                    detalleProcesoAdq = JsfUtil.findDetalleByRubroAndAnho(proceso,
                            capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres(),
                            capacidadInst.getIdMuestraInteres().getIdAnho().getIdAnho());
                    aceptarCondiciones = (capacidadInst.getIdMuestraInteres().getDatosVerificados() == 1);
                    aceptarDeclaracion = (capacidadInst.getIdMuestraInteres().getAceptacionTerminos() == 1);

                    cabecera = MessageFormat.format(UTIL_CORREO.getString("prov.declaracion.cabecera"),
                            capacidadInst.getIdMuestraInteres().getIdRubroInteres().getDescripcionRubro(),
                            anho.getAnho());
                    detalle = MessageFormat.format(UTIL_CORREO.getString("prov.declaracion.detalle"),
                            capacidadInst.getIdMuestraInteres().getIdRubroInteres().getDescripcionRubro(),
                            empresa.getIdPersona().getEmail(),
                            empresa.getDireccionCompleta().concat(", ").concat(empresa.getIdMunicipio().getNombreMunicipio()).concat(", ").concat(empresa.getIdMunicipio().getCodigoDepartamento().getNombreDepartamento()),
                            capacidadInst.getIdMuestraInteres().getIdAnho().getAnho());
                }
            }
        }
    }

    public String getCabecera() {
        return cabecera;
    }

    public String getDetalle() {
        return detalle;
    }

    public Boolean getAceptarDeclaracion() {
        return aceptarDeclaracion;
    }

    public void setAceptarDeclaracion(Boolean aceptarDeclaracion) {
        this.aceptarDeclaracion = aceptarDeclaracion;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Boolean getAceptarCondiciones() {
        return aceptarCondiciones;
    }

    public void setAceptarCondiciones(Boolean aceptarCondiciones) {
        this.aceptarCondiciones = aceptarCondiciones;
    }

    public void guardarAceptarCondiciones() {
        if (aceptarCondiciones && aceptarDeclaracion) {
            Boolean todoBien = true;
            if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() > 8) { // anho mayor de 2020
                //validación de ingreso de todos los item para el rubro de uniforme
                if (detalleProcesoAdq.getIdRubroAdq().getIdRubroUniforme().intValue() == 1) {
                    List<PreciosRefRubroEmp> lstPreciosReferencia = proveedorEJB.findPreciosRefRubroEmpRubro(getEmpresa(), detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres(), detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho());
                    if (lstPreciosReferencia.size() < 12) {
                        todoBien = false;
                    }
                }
            }

            if (todoBien) {
                idGestion = proveedorEJB.datosConfirmados(capacidadInst.getIdMuestraInteres().getIdMuestraInteres(),
                        empresa.getIdEmpresa(),
                        VarSession.getVariableSessionUsuario());
                enviarNotificacionModProv();
            } else {
                JsfUtil.mensajeAlerta("No se puede confirmar su Oferta Global debido a que no ha ingresado los precios de referencias de todos los ITEMS disponibles.");
            }
        } else {
            JsfUtil.mensajeAlerta("Debe de aceptar la Declaració Jurada y Aceptación de Presentación de Oferta.");
        }
    }

    public void impOfertaGlobal() {
        try {
            String lugar = empresa.getIdMunicipio().getNombreMunicipio().concat(", ").concat(empresa.getIdMunicipio().getCodigoDepartamento().getNombreDepartamento());
            if (idGestion.isEmpty()) {
                idGestion = proveedorEJB.datosConfirmados(capacidadInst.getIdMuestraInteres().getIdMuestraInteres(),
                        empresa.getIdEmpresa(),
                        VarSession.getVariableSessionUsuario());
            }

            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            HashMap param = new HashMap();
            param.put("ubicacionImagenes", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
            param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
            param.put("usuarioInsercion", VarSession.getVariableSessionUsuario());
            param.put("pLugar", lugar);
            param.put("pRubro", JsfUtil.getNombreRubroById(capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres()));
            param.put("pIdRubro", capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres().intValue());
            param.put("pCorreoPersona", capacidadInst.getIdMuestraInteres().getIdEmpresa().getIdPersona().getEmail());
            param.put("pIdGestion", idGestion);

            List<OfertaGlobal> lstDatos = reportesEJB.getLstOfertaGlobal(empresa.getNumeroNit(), detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres(), detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho());
            lstDatos.get(0).setRubro(JsfUtil.getNombreRubroById(capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres()));
            if (lstDatos.get(0).getDepartamento().contains("TODO EL PAIS")) {
                param.put("productor", Boolean.TRUE);
            } else {
                param.put("productor", Boolean.FALSE);
            }

            List<JasperPrint> jasperPrintList = new ArrayList();

            jasperPrintList.add(JasperFillManager.fillReport(
                    Reportes.getPathReporte("sv/gob/mined/apps/reportes/oferta" + File.separator + "rptOfertaGlobalProv" + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho() + ".jasper"),
                    param, new JRBeanCollectionDataSource(lstDatos)));

            String muni = VarSession.getNombreMunicipioSession();

            param.put("pLugar", empresa.getIdMunicipio().getCodigoDepartamento().getNombreDepartamento());

            if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) {
                jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurAceptacionPerProvNat" + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho(), param, new JRBeanCollectionDataSource(reportesEJB.getDeclaracionJurada(empresa, detalleProcesoAdq, muni))));

            } else {
                jasperPrintList.add(Reportes.getReporteAImprimir("sv/gob/mined/apps/reportes/declaracion" + File.separator + "rptDeclaracionJurAceptacionPerProvJur" + detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho(), param, new JRBeanCollectionDataSource(reportesEJB.getDeclaracionJurada(empresa, detalleProcesoAdq, muni))));

            }

            Reportes.generarReporte(jasperPrintList, "oferta_global_" + getEmpresa().getNumeroNit());

        } catch (IOException | JRException ex) {
            Logger.getLogger(DeclaracionMB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarNotificacionModProv() {
        String titulo;
        String mensaje;
        Date fecha = new Date();
        List<String> to = new ArrayList();
        List<String> cc = new ArrayList();
        List<String> bcc = new ArrayList();

        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

        cc.add("carlos.villegas@mined.gob.sv");
        cc.add("rene.brizuela@mined.gob.sv");

        bcc.add("rafael.arias@mined.gob.sv");
        to.add(empresa.getIdPersona().getEmail());

        titulo = MessageFormat.format(UTIL_CORREO.getString("prov_notif_inscripcion.email.titulo"), capacidadInst.getIdMuestraInteres().getIdAnho().getAnho());
        mensaje = MessageFormat.format(UTIL_CORREO.getString("prov_notif_inscripcion.email.mensaje"),
                sdfHora.format(fecha).split(":")[0], sdfHora.format(fecha).split(":")[1],
                Herramientas.getNumDia(fecha), Herramientas.getNomMes(fecha), Herramientas.getNumAnyo(fecha),
                empresa.getIdPersona().getEmail(), empresa.getIdPersona().getNombreCompleto(), empresa.getIdPersona().getNumeroDui(), idGestion,
                sdfHora.format(fecha).split(":")[0], sdfHora.format(fecha).split(":")[1],
                Herramientas.getNumDia(fecha), Herramientas.getNomMes(fecha), Herramientas.getNumAnyo(fecha));

        proveedorEJB.enviarNotificacionModProv("carlos.villegas@mined.gob.sv", titulo, mensaje, to, cc, bcc);
    }
}
