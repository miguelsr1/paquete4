/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.paquescolar.ejb.PagoProveedoresEJB;
import sv.gob.mined.paquescolar.model.ListaNotificacionPago;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class NotificacionPagoMB implements Serializable {

    private Boolean activo = false;
    private String codigoDepartamento = "01";
    private ListaNotificacionPago notificacionPago = new ListaNotificacionPago();
    private List<ListaNotificacionPago> lstNotificacionPagos = new ArrayList();

    @Inject
    private PagoProveedoresEJB pagoProveedoresEJB;

    public NotificacionPagoMB() {
    }

    @PostConstruct
    public void init() {
        actualizarListaNotificacion();
    }

    public ListaNotificacionPago getNotificacionPago() {
        return notificacionPago;
    }

    public void setNotificacionPago(ListaNotificacionPago notificacionPago) {
        if (notificacionPago != null) {
            this.notificacionPago = notificacionPago;
        }
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<ListaNotificacionPago> getLstNotificacionPagos() {
        return lstNotificacionPagos;
    }

    public void actualizarListaNotificacion() {
        lstNotificacionPagos = pagoProveedoresEJB.getLstNotificacionPagosByCodDepa(codigoDepartamento);
    }

    public void guardar() {
        notificacionPago.setCodigoDepartamento(codigoDepartamento);
        notificacionPago.setActivo(activo ? (short) 1 : 0);
        switch (pagoProveedoresEJB.guardarNotificacionPago(notificacionPago)) {
            case 1:
                JsfUtil.mensajeInsert();
                break;
            case 2:
                JsfUtil.mensajeUpdate();
                break;
            default:
                JsfUtil.mensajeError("Se ha producido un error, favor comuniquese con el administrador.");
                break;
        }
        actualizarListaNotificacion();
        notificacionPago = new ListaNotificacionPago();
    }

    public void onRowSelect(SelectEvent event) {
        notificacionPago = (ListaNotificacionPago) event.getObject();
        activo = (notificacionPago.getActivo() == 1);
        codigoDepartamento = notificacionPago.getCodigoDepartamento();
    }
}
