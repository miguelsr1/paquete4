/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.contratacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.OfertaBienesServiciosEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.CatalogoProducto;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.NivelEducativo;
import sv.gob.mined.paquescolar.model.OfertaBienesServicios;
import sv.gob.mined.paquescolar.model.ResguardoBienes;
import sv.gob.mined.paquescolar.model.pojos.contratacion.DetalleContratadoPorComponenteDto;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class ResguardoMB extends RecuperarProcesoUtil implements Serializable {

    private int rowEdit = 0;
    private String codigoEntidad;
    private BigDecimal rubro = BigDecimal.ZERO;
    private BigDecimal idParticipante = BigDecimal.ZERO;

    private ResguardoBienes resguardoBienes = new ResguardoBienes();
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private OfertaBienesServicios current = new OfertaBienesServicios();
    private VwCatalogoEntidadEducativa entidadEducativa = new VwCatalogoEntidadEducativa();

    private List<ResguardoBienes> lstResguardoBienes = new ArrayList();
    private List<DetalleContratadoPorComponenteDto> lstDetalle = new ArrayList();
    private List<DetalleContratadoPorComponenteDto> lstDetalleSelect = new ArrayList();

    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private OfertaBienesServiciosEJB ofertaBienesServiciosEJB;
    @Inject
    private UtilEJB utilEJB;

    public List<DetalleContratadoPorComponenteDto> getLstDetalleSelect() {
        return lstDetalleSelect;
    }

    public void setLstDetalleSelect(List<DetalleContratadoPorComponenteDto> lstDetalleSelect) {
        this.lstDetalleSelect = lstDetalleSelect;
    }

    public List<DetalleContratadoPorComponenteDto> getLstDetalle() {
        return lstDetalle;
    }

    public void setLstDetalle(List<DetalleContratadoPorComponenteDto> lstDetalle) {
        this.lstDetalle = lstDetalle;
    }

    public int getRowEdit() {
        return rowEdit;
    }

    public void setRowEdit(int rowEdit) {
        this.rowEdit = rowEdit;
    }

    public BigDecimal getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(BigDecimal idParticipante) {
        this.idParticipante = idParticipante;
    }

    public BigDecimal getRubro() {
        return rubro;
    }

    public void setRubro(BigDecimal rubro) {
        this.rubro = rubro;
    }

    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        return entidadEducativa;
    }

    public void setEntidadEducativa(VwCatalogoEntidadEducativa entidadEducativa) {
        this.entidadEducativa = entidadEducativa;
    }

    public List<ResguardoBienes> getLstResguardoBienes() {
        return lstResguardoBienes;
    }

    public void setLstResguardoBienes(List<ResguardoBienes> lstResguardoBienes) {
        this.lstResguardoBienes = lstResguardoBienes;
    }

    public OfertaBienesServicios getCurrent() {
        return current;
    }

    public void setCurrent(OfertaBienesServicios current) {
        this.current = current;
    }

    public ResguardoBienes getResguardoBienes() {
        return resguardoBienes;
    }

    public void setResguardoBienes(ResguardoBienes resguardoBienes) {
        this.resguardoBienes = resguardoBienes;
    }

    public void limpiarFiltros() {
        codigoEntidad = "";
    }

    public void buscarEntidadEducativa() {
        if (codigoEntidad.length() == 5) {
            if (getRecuperarProceso().getProcesoAdquisicion() == null) {
                JsfUtil.mensajeAlerta("Debe de seleccionar un año y proceso de contratación.");
            } else {
                detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);

                entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);

                if (entidadEducativa == null) {
                    JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
                } else {
                    if (VarSession.getDepartamentoUsuarioSession() != null) {
                        String dep = getRecuperarProceso().getDepartamento();
                        if (entidadEducativa.getCodigoDepartamento().getCodigoDepartamento().equals(dep) || (Integer) VarSession.getVariableSession("idTipoUsuario") == 1) {
                            current = ofertaBienesServiciosEJB.getOfertaByProcesoCodigoEntidad(codigoEntidad, detalleProceso);
                            if (current != null) {
                                lstResguardoBienes = ofertaBienesServiciosEJB.getLstResguardoBienesByCodEntAndIdDetPro(codigoEntidad, detalleProceso.getIdDetProcesoAdq());
                            } else {
                                JsfUtil.mensajeError("No existe un proceso de contratación para este centro escolar.");
                            }
                        } else {
                            JsfUtil.mensajeAlerta("El codigo del centro escolar no pertenece al departamento " + JsfUtil.getNombreDepartamentoByCodigo(dep) + "<br/>"
                                    + "Departamento del CE: " + entidadEducativa.getCodigoEntidad() + " es " + entidadEducativa.getCodigoDepartamento().getNombreDepartamento());
                        }
                    } else {
                        JsfUtil.mensajeAlerta("Debe de seleccionar un departamento y municipio.");
                    }
                }
            }
        } else {
            if (current != null && current.getParticipantesList() != null) {
                current.getParticipantesList().clear();
            }
            entidadEducativa = null;
        }
    }

    public void limpiar() {
        current = new OfertaBienesServicios();
        codigoEntidad = "";
    }

    public void guardar() {
        for (ResguardoBienes resguardo : lstResguardoBienes) {
            ofertaBienesServiciosEJB.guardarResguardo(resguardo, VarSession.getVariableSessionUsuario());
        }

        lstResguardoBienes = ofertaBienesServiciosEJB.getLstResguardoBienesByCodEntAndIdDetPro(codigoEntidad, detalleProceso.getIdDetProcesoAdq());

        JsfUtil.mensajeInsert();
    }

    public void eliminarDetalle() {
        if (resguardoBienes != null) {
            if (resguardoBienes.getEstadoEliminacion().compareTo((short) 0) == 0) {
                if (resguardoBienes.getIdResguardo() != null) {
                    resguardoBienes.setEstadoEliminacion((short) 1);
                } else {
                    lstResguardoBienes.remove(rowEdit);
                }
            } else {
                resguardoBienes.setEstadoEliminacion((short) 0);
            }

            resguardoBienes = null;
        } else {
            JsfUtil.mensajeAlerta("Debe seleccionar un detalle para poder eliminarlo.");
        }
    }

    public void buscarItem() {
        lstDetalle = ofertaBienesServiciosEJB.getLstDetalleContratado(codigoEntidad, detalleProceso.getIdDetProcesoAdq());
        PrimeFaces.current().executeScript("PF('dlgDetalle').show();");
    }

    public void agregarDetalle() {
        for (DetalleContratadoPorComponenteDto detContratado : lstDetalleSelect) {
            ResguardoBienes res = new ResguardoBienes();
            res.setCantidad(BigInteger.ZERO);
            res.setCodigoEntidad(codigoEntidad);
            res.setEstadoEliminacion((short) 0);
            res.setFechaInsercion(new Date());
            res.setIdDetProcesoAdq(detalleProceso);
            res.setIdNivelEducativo(utilEJB.find(NivelEducativo.class, detContratado.getIdNivelEducativo()));
            res.setIdProducto(utilEJB.find(CatalogoProducto.class, detContratado.getIdProducto()));
            res.setUsuarioInsercion(VarSession.getVariableSessionUsuario());

            lstResguardoBienes.add(res);
        }
    }
}
