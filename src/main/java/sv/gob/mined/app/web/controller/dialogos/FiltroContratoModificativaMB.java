/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.dialogos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.ModificativaEJB;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.pojos.modificativa.VwBusquedaContratos;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;
import sv.gob.mined.paquescolar.model.pojos.modificativa.VwContratoModificatoria;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class FiltroContratoModificativaMB extends RecuperarProcesoUtil implements Serializable {

    private int op = 0;
    private Boolean deshabilitar = true;
    private String codigoEntidad;
    private String codigoDepartamento;
    private String numeroNit;
    private String numeroContrato;
    private BigDecimal idRubro = BigDecimal.ZERO;
    private Date fecha1;
    private Date fecha2;
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private VwCatalogoEntidadEducativa entidadEducativa;

    private VwContratoModificatoria vwContratoModificatoria = new VwContratoModificatoria();
    private List<VwBusquedaContratos> lstContratos = new ArrayList();
    private List<VwContratoModificatoria> lstContratoModificatorias = new ArrayList();

    @Inject
    private ModificativaEJB modificativaEJB;
    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;

    /**
     * Creates a new instance of FiltroContratoModificativaMB
     */
    public FiltroContratoModificativaMB() {
    }

    @PostConstruct
    public void ini() {
        if (VarSession.isVariableSession("op")) {
            op = Integer.parseInt(VarSession.getVariableSession("op").toString());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public List<VwContratoModificatoria> getLstContratoModificatorias() {
        return lstContratoModificatorias;
    }

    public void setLstContratoModificatorias(List<VwContratoModificatoria> lstContratoModificatorias) {
        this.lstContratoModificatorias = lstContratoModificatorias;
    }

    public Boolean getDeshabilitar() {
        return deshabilitar;
    }

    public void setDeshabilitar(Boolean deshabilitar) {
        this.deshabilitar = deshabilitar;
    }

    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public String getNumeroNit() {
        return numeroNit;
    }

    public void setNumeroNit(String numeroNit) {
        this.numeroNit = numeroNit;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal idRubro) {
        this.idRubro = idRubro;
    }

    public Date getFecha1() {
        return fecha1;
    }

    public void setFecha1(Date fecha1) {
        this.fecha1 = fecha1;
    }

    public Date getFecha2() {
        return fecha2;
    }

    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

    public DetalleProcesoAdq getDetalleProceso() {
        return detalleProceso;
    }

    public void setDetalleProceso(DetalleProcesoAdq detalleProceso) {
        this.detalleProceso = detalleProceso;
    }

    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        return entidadEducativa;
    }

    public void setEntidadEducativa(VwCatalogoEntidadEducativa entidadEducativa) {
        this.entidadEducativa = entidadEducativa;
    }

    public VwContratoModificatoria getVwContratoModificatoria() {
        return vwContratoModificatoria;
    }

    public void setVwContratoModificatoria(VwContratoModificatoria vwContratoModificatoria) {
        this.vwContratoModificatoria = vwContratoModificatoria;
    }

    public List<VwBusquedaContratos> getLstContratos() {
        return lstContratos;
    }

    public void setLstContratos(List<VwBusquedaContratos> lstContratos) {
        this.lstContratos = lstContratos;
    }
    // </editor-fold>

    public void buscarProceso() {
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
    }

    public void buscarContratos() {
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        if (idRubro == null) {
            JsfUtil.mensajeAlerta("El campo Rubro de adquisicion es obligatorio");
        } else if (idRubro == null || idRubro.compareTo(BigDecimal.ZERO) == 0) {
            JsfUtil.mensajeAlerta("El campo Rubro de adquisicion es obligatorio");
        } else {
            lstContratos = modificativaEJB.getLstBusquedaContrato(detalleProceso, codigoEntidad, null, null, null, null, null, op);
            if (lstContratos.isEmpty()) {
                JsfUtil.mensajeInformacion("No se encontrarón coincidencias.");
            }
        }
    }

    public void selectContrato() {
        if (vwContratoModificatoria == null || vwContratoModificatoria.getIdContrato() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un contrato");
        } else {
            switch (op) {
                case 1:
                    if (vwContratoModificatoria.getIdEstadoReserva().intValue() == 2) {
                        PrimeFaces.current().dialog().closeDynamic(vwContratoModificatoria);
                    } else {
                        JsfUtil.mensajeAlerta("El contrato seleccionado no tiene la RESERVA DE FONDOS en estado APLICADA");
                    }
                    break;
                case 2:
                    if (VarSession.getVariableSessionUsuario().equals("MSANCHEZ")) {
                        if (vwContratoModificatoria.getIdEstadoReserva().intValue() == 1 || vwContratoModificatoria.getIdEstadoReserva().intValue() == 2 || vwContratoModificatoria.getIdEstadoReserva().intValue() == 5 || vwContratoModificatoria.getIdEstadoReserva().intValue() == 3) {
                            PrimeFaces.current().dialog().closeDynamic(vwContratoModificatoria);
                        } else {
                            JsfUtil.mensajeAlerta("El contrato seleccionado no tiene la RESERVA DE FONDOS en estado DIGITADA");
                        }
                    } else {
                        if (vwContratoModificatoria.getIdEstadoReserva().intValue() == 1 || vwContratoModificatoria.getIdEstadoReserva().intValue() == 2 || vwContratoModificatoria.getIdEstadoReserva().intValue() == 5) {
                            PrimeFaces.current().dialog().closeDynamic(vwContratoModificatoria);
                        } else {
                            JsfUtil.mensajeAlerta("El contrato seleccionado no tiene la RESERVA DE FONDOS en estado DIGITADA");
                        }
                    }
                    break;
                case 3:
                    if (vwContratoModificatoria.getIdEstadoReserva().intValue() == 4) {
                        PrimeFaces.current().dialog().closeDynamic(vwContratoModificatoria);
                    } else {
                        JsfUtil.mensajeAlerta("El contrato seleccionado no tiene la RESERVA DE FONDOS en estado ANULADA");
                    }
                    break;
            }
        }
    }

    public void onRowToggle(ToggleEvent event) {
        lstContratoModificatorias = modificativaEJB.getContratoModificatoria(((VwBusquedaContratos) event.getData()).getIdContrato());
    }

    public void onContratoChosen(SelectEvent event) {
        if (event.getObject() instanceof VwContratoModificatoria) {
            //si la consulta del contrato proviene de la pagina reg04AprobacionModificatoria se debe dejar cargar la información
            if ((Boolean) VarSession.getVariableSession("aprobacion")) {
                vwContratoModificatoria = (VwContratoModificatoria) event.getObject();
            } else //verificar existencia de recepcion
            if (modificativaEJB.existeRecepcionDeContrato(((VwContratoModificatoria) event.getObject()).getIdContrato())) {
                vwContratoModificatoria = null;
                PrimeFaces.current().executeScript("frmDialog:tblContratos");
                JsfUtil.mensajeAlerta("El contrato seleccionado posee registro de entrega, por favor borrar la recepcion registrada antes de continuar.");
            } else {
                vwContratoModificatoria = (VwContratoModificatoria) event.getObject();
            }
        }
    }

    public void onSelectedContrato(SelectEvent event) {
        onContratoChosen(event);
        if (vwContratoModificatoria != null) {
            selectContrato();
        }
    }

    public void buscarEntidadEducativa() {
        entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);
        if (entidadEducativa == null) {
            JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
        }
    }
}
