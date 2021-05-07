/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.contratacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.ResolucionAdjudicativaEJB;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.EstadoReserva;
import sv.gob.mined.paquescolar.model.ResolucionesAdjudicativas;
import sv.gob.mined.paquescolar.model.TechoRubroEntEdu;
import sv.gob.mined.paquescolar.model.pojos.contratacion.SaldoProveedorDto;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class ResolucionesAdjudicativasController implements Serializable {

    private String codigoEntidad;
    private String comentarioReversion;
    private Boolean deshabilitar = true;
    private Boolean negativo = false;
    private Boolean positivo = false;
    private BigInteger saldoAdjudicacion;
    private BigDecimal saldoActual = BigDecimal.ZERO;
    private BigDecimal idParticipante = BigDecimal.ZERO;
    private BigDecimal idEstadoReserva = BigDecimal.ZERO;

    private SaldoProveedorDto saldoPro = new SaldoProveedorDto();
    private ResolucionesAdjudicativas current = new ResolucionesAdjudicativas();

    private TechoRubroEntEdu techoCE = null;
    private List<CapaInstPorRubro> lstCapaInstalada = new ArrayList();
    @Inject
    private ResolucionAdjudicativaEJB resolucionAdjudicativaEJB;

    /**
     * Creates a new instance of ResolucionesAdjudicativasController
     */
    public ResolucionesAdjudicativasController() {
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        if (lstCapaInstalada != null) {
            lstCapaInstalada.clear();
        }

        if (techoCE == null) {
            techoCE = new TechoRubroEntEdu();
            techoCE.setMontoAdjudicado(BigDecimal.ZERO);
            techoCE.setMontoDisponible(BigDecimal.ZERO);
            techoCE.setMontoPresupuestado(BigDecimal.ZERO);
        }
        if (params.containsKey("txtCodigoEntidad") && params.containsKey("idParticipante")) {
            VarSession.setVariableSessionED("2");
            current = resolucionAdjudicativaEJB.findResolucionesAdjudicativasByIdParticipante(new BigDecimal(params.get("idParticipante")));
            codigoEntidad = params.get("txtCodigoEntidad");
            idParticipante = current.getIdParticipante().getIdParticipante();
            deshabilitar = false;
            idEstadoReserva = current.getIdEstadoReserva().getIdEstadoReserva();
            buscarSaldoPresupuestoCE(current.getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdDetProcesoAdq());
            saldoPro = resolucionAdjudicativaEJB.getSaldoProveedor(current);
        } else {
            VarSession.setVariableSessionED("0");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public SaldoProveedorDto getSaldoPro() {
        return saldoPro;
    }

    public void setSaldoPro(SaldoProveedorDto saldoPro) {
        this.saldoPro = saldoPro;
    }

    public BigDecimal getIdResolucionAdj() {
        return current.getIdResolucionAdj();
    }

    public void setIdResolucionAdj(BigDecimal idResolucionAdj) {

    }

    public BigDecimal getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(BigDecimal idParticipante) {
        this.idParticipante = idParticipante;
    }

    public boolean getEEModificar() {
        return VarSession.getVariableSessionED() == 2;
    }

    public Boolean getDeshabilitar() {
        return deshabilitar;
    }

    public void setDeshabilitar(Boolean deshabilitar) {
        this.deshabilitar = deshabilitar;
    }

    public Boolean getNegativo() {
        return negativo;
    }

    public void setNegativo(Boolean negativo) {
        this.negativo = negativo;
    }

    public Boolean getPositivo() {
        return positivo;
    }

    public void setPositivo(Boolean positivo) {
        this.positivo = positivo;
    }

    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public String getComentarioReversion() {
        return comentarioReversion;
    }

    public void setComentarioReversion(String comentarioReversion) {
        this.comentarioReversion = comentarioReversion;
    }

    public void setSaldoAdjudicacion(BigInteger saldoAdjudicacion) {
        this.saldoAdjudicacion = saldoAdjudicacion;
    }

    public BigDecimal getIdEstadoReserva() {
        return idEstadoReserva;
    }

    public void setIdEstadoReserva(BigDecimal estadoReserva) {
        this.idEstadoReserva = estadoReserva;
    }

    public List<CapaInstPorRubro> getLstCapaInstalada() {
        return lstCapaInstalada;
    }

    public void setLstCapaInstalada(List<CapaInstPorRubro> lstCapaInstalada) {
        this.lstCapaInstalada = lstCapaInstalada;
    }

    public List<EstadoReserva> getLstEstadoReservaModif() {
        if (VarSession.getVariableSessionUsuario().equals("MSANCHEZ")) {
            return resolucionAdjudicativaEJB.findEstadoReservaEntities();
        } else {
            return resolucionAdjudicativaEJB.getLstEstadoReservaModif();
        }
    }

    //</editor-fold>
    public void prepareEdit() {
        VarSession.setVariableSessionED("2");
        deshabilitar = false;
        codigoEntidad = "";
        idParticipante = BigDecimal.ZERO;
        idEstadoReserva = null;
        techoCE = null;
        if (lstCapaInstalada != null) {
            lstCapaInstalada.clear();
        }
        current = new ResolucionesAdjudicativas();
    }

    public void limpiarDatos() {
        current = null;
        saldoAdjudicacion = BigInteger.ZERO;
        idEstadoReserva = null;
        OfertaMB controller = (OfertaMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "ofertaMB");
        controller.limpiarFiltros();
    }

    public BigInteger getSaldoAdjudicacion() {
        try {
            saldoAdjudicacion = saldoPro.getCapacidadCalificada().add(saldoPro.getCapacidadAdjudicada().negate());

            if (saldoAdjudicacion != null) {
                if (idEstadoReserva.intValue() != 2) {
                    saldoAdjudicacion = saldoAdjudicacion.add(saldoPro.getAdjudicadaActual().negate());
                }

                if (saldoAdjudicacion.compareTo(BigInteger.ZERO) == -1 && idEstadoReserva.intValue() == 1 && idEstadoReserva.intValue() == 3) {
                    JsfUtil.mensajeAlerta("Este proveedor ya no posee capacidad para proveer a este centro escolar.");
                }
            } else {
                saldoAdjudicacion = BigInteger.ZERO;
            }
        } catch (Exception e) {

        }

        return saldoAdjudicacion;
    }

    public String getMontoPresupuestado() {
        if (techoCE != null) {
            return JsfUtil.getFormatoNum(techoCE.getMontoPresupuestado(), false);
        } else {
            return "0.00";
        }
    }

    public String getMontoAdjudicado() {
        if (techoCE != null) {
            return JsfUtil.getFormatoNum(techoCE.getMontoAdjudicado(), false);
        } else {
            return "0.00";
        }
    }

    public String getMontoAdjActual() {
        if (techoCE != null && current != null && current.getIdResolucionAdj() != null) {
            return JsfUtil.getFormatoNum(current.getValor(), false);
        } else {
            return "0.00";
        }
    }

    public String getMontoSaldo() {
        if (idParticipante == null || idParticipante.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        }
        if (techoCE != null) {
            if (current == null || current.getIdResolucionAdj() == null || current.getIdEstadoReserva().getIdEstadoReserva().compareTo(new BigDecimal("2")) == 0) {
                saldoActual = techoCE.getMontoDisponible();
            } else {
                saldoActual = techoCE.getMontoDisponible().add(current.getValor().negate());
            }

            negativo = saldoActual.compareTo(BigDecimal.ZERO) == -1;
            positivo = saldoActual.compareTo(BigDecimal.ZERO) == 1;
            return JsfUtil.getFormatoNum(saldoActual, false);
        } else {
            return "0.00";
        }
    }

    public String update() {
        try {
            String url = null;

            if (current != null && current.getIdResolucionAdj() != null) {
                if (!resolucionAdjudicativaEJB.validarCambioEstado(current, idEstadoReserva)) {
                    JsfUtil.mensajeAlerta("Cambio de estado inválido");
                } else {
                    switch (idEstadoReserva.intValue()) {
                        case 2:
                            if (aplicarCambiosReserva()) {
                                VarSession.setVariableSession("idRes", current.getIdResolucionAdj());
                                url = "reg02Contrato.mined?includeViewParams=true";
                            }
                            break;
                        case 3:
                            PrimeFaces.current().executeScript("PF('dlgReversion').show();");
                            break;
                    }
                }
            } else {
                JsfUtil.mensajeInformacion("Primero debe de seleccionar un proveedor");
            }
            return url;
        } catch (Exception e) {
            JsfUtil.mensajeError("Error en el cambio de estado de la reseva.<br/>" + e.getMessage());
            return null;
        }
    }

    private boolean aplicarCambiosReserva() {
        HashMap<String, Object> param = resolucionAdjudicativaEJB.aplicarReservaDeFondos(current, idEstadoReserva, codigoEntidad, comentarioReversion, VarSession.getVariableSessionUsuario());
        Boolean exito = !param.containsKey("error");
        if (VarSession.getVariableSessionUsuario().equals("RMINERO")
                || VarSession.getVariableSessionUsuario().equals("RAFAARIAS")
                || VarSession.getVariableSessionUsuario().equals("CVILLEGAS") || exito) {
        } else {
            PrimeFaces.current().ajax().update("frmPrincipal");
            JsfUtil.mensajeAlerta(param.get("error").toString());
        }
        return exito;
    }

    public void reversionMasiva() {
        resolucionAdjudicativaEJB.reversionMasiva();
        JsfUtil.mensajeUpdate();
    }

    public void buscarSaldoPresupuestoCE(Integer idDetProcesoAdq) {
        if (codigoEntidad.length() == 5) {
            techoCE = resolucionAdjudicativaEJB.findTechoRubroEntEdu(codigoEntidad, idDetProcesoAdq);
        }
    }

    public void buscarResolucionAdjudicativa() {
        saldoPro = new SaldoProveedorDto();
        if (idParticipante != null && idParticipante.compareTo(BigDecimal.ZERO) != 0) {
            current = resolucionAdjudicativaEJB.findResolucionesAdjudicativasByIdParticipante(idParticipante);

            if (current == null) {
                JsfUtil.mensajeInformacion("El proveedor seleccionado no posee adjudicacion para este centro escolar");
                idEstadoReserva = null;
            } else {
                idEstadoReserva = current.getIdEstadoReserva().getIdEstadoReserva();
                saldoPro = resolucionAdjudicativaEJB.getSaldoProveedor(current);
            }
        } else {
            limpiarDatos();
        }
    }

    public String revertirReserva() {
        if (VarSession.getVariableSessionUsuario().equals("RMINERO")
                || VarSession.getVariableSessionUsuario().equals("RAFAARIAS")
                || VarSession.getVariableSessionUsuario().equals("CVILLEGAS")
                || VarSession.getVariableSessionUsuario().equals("MSANCHEZ")) {
            if (aplicarCambiosReserva()) {
                return "reg02DetalleOferta.mined"; //enviar parametros
            }
            return null;
        } else {
            JsfUtil.mensajeError("No posee privilegios para revertir reservas de fondos.");
            return "";
        }
    }
}
