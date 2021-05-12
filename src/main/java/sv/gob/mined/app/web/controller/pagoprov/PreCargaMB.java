/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.paquescolar.ejb.PagoProveedoresEJB;
import sv.gob.mined.paquescolar.model.DetallePreCarga;
import sv.gob.mined.paquescolar.model.PreCarga;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.PreCargaDto;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class PreCargaMB extends RecuperarProcesoUtil implements Serializable {

    private Boolean filtro = true;
    private Integer idDetProcesoAdq = 0;
    private BigDecimal idRubro = BigDecimal.ZERO;
    private List<PreCargaDto> lstPreCargaDto = new ArrayList();
    private List<PreCarga> lstPreCarga = new ArrayList();
    private List<DetallePreCarga> lstDetallePreCarga = new ArrayList();

    @Inject
    private PagoProveedoresEJB pagoProveedoresEJB;

    public PreCargaMB() {
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public Boolean getFiltro() {
        return filtro;
    }

    public void setFiltro(Boolean filtro) {
        this.filtro = filtro;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal idRubro) {
        this.idRubro = idRubro;
    }

    public List<PreCarga> getLstPreCarga() {
        return lstPreCarga;
    }

    public void setLstPreCarga(List<PreCarga> lstPreCarga) {
        this.lstPreCarga = lstPreCarga;
    }

    public List<PreCargaDto> getLstPreCargaDto() {
        return lstPreCargaDto;
    }

    public void setLstPreCargaDto(List<PreCargaDto> lstPreCargaDto) {
        this.lstPreCargaDto = lstPreCargaDto;
    }

    public List<DetallePreCarga> getLstDetallePreCarga() {
        return lstDetallePreCarga;
    }

    public void setLstDetallePreCarga(List<DetallePreCarga> lstDetallePreCarga) {
        this.lstDetallePreCarga = lstDetallePreCarga;
    }

    // </editor-fold>
    public void obtenerIdDetalleProcesoAdq() {
        idDetProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro).getIdDetProcesoAdq();
    }

    public void buscarContratos() {
        lstPreCargaDto = pagoProveedoresEJB.getLstPreCargaByIdDetProcesoAdq(idDetProcesoAdq);
    }

    public void buscarPreCargas() {
        lstPreCarga = pagoProveedoresEJB.findPreCargaByIdDetProcesoAdq(idDetProcesoAdq);
    }

    public void nuevo() {
        filtro = true;
    }

    public void modificar() {
        filtro = true;
    }

    public void editarPreCarga(BigDecimal idPrecarga) {
        filtro = false;
        lstDetallePreCarga = pagoProveedoresEJB.getLstDetallePreCargaByIdPreCarga(idPrecarga);
    }
}
