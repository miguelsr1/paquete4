/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.dialogos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.RecepcionEJB;
import sv.gob.mined.paquescolar.model.DetalleRecepcion;
import sv.gob.mined.paquescolar.model.RecepcionBienesServicios;
import sv.gob.mined.paquescolar.model.pojos.recepcion.DetalleItems;

/**
 *
 * @author misanchez
 */
@Named
@javax.faces.view.ViewScoped
public class EdicionSeguimientoMB implements Serializable {

    @javax.inject.Inject
    private RecepcionEJB recepcionEJB;

    private boolean esDosUniformes = false;
    private boolean mostrarEntregas = false;
    private boolean disabledCantidad = true;
    private boolean cuadradaPrimeraEntrega = false;
    private boolean errorValidacion = false;

    //private int dividendo = 1;
    private int idRubro;

    /**
     * 1 - Nuevo 0 - Modificar 2 - Eliminar
     */
    private String op = "";
    private String noItem = "";
    private String detalleItemEs = "";
    private String tipoEntrega = "1";
    private String observacionesTotal;
    private String mesageTiempoEntregaSatisfactorio = "";
    private String mesageTiempoEntregaInsatisfactorio = "";

    private Date fechaRecepcionTotal;
    private Date fechaInicio;

    private BigInteger cantidadRecibidaItem = BigInteger.ZERO;
    private BigInteger cantidadPendienteItem = BigInteger.ZERO;
    private BigInteger cantidadTotalItem = BigInteger.ZERO;
    private BigInteger cantidadTotalRecibidaItem = BigInteger.ZERO;
    private BigInteger cantidadTotal = BigInteger.ZERO;

    private DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
    private RecepcionBienesServicios recepcion = new RecepcionBienesServicios();
    private List<DetalleRecepcion> lstDetalleRecepcion = new ArrayList();
    private List<DetalleItems> lstDetalleOfertaPendiente = new ArrayList();

    public EdicionSeguimientoMB() {
    }

    @PostConstruct
    public void init() {
        recepcion = recepcionEJB.getRecepcion((BigDecimal) VarSession.getVariableSession("idContrato"));
        op = VarSession.getVariableSession("op").toString();
        esDosUniformes = (Boolean) VarSession.getVariableSession("esDosUniformes");
        if (esDosUniformes) {
            if (VarSession.isVariableSession("cuadradaPrimeraEntrega")) {
                cuadradaPrimeraEntrega = (Boolean) VarSession.getVariableSession("cuadradaPrimeraEntrega");
                if (cuadradaPrimeraEntrega) {
                    tipoEntrega = "2";
                }
            }
        }
        //si es el rubro 1, se deben de dividir las cantidades
        idRubro = recepcion.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres().intValue();

        mostrarEntregas = esDosUniformes;
        lstDetalleRecepcion = recepcion.getDetalleRecepcionList();

        switch (op) {
            case "0"://edición de detalle recepción
                detalleRecepcion = recepcionEJB.getDetalleRecepcionById((BigDecimal) VarSession.getVariableSession("idDetalleSeguimiento"));
                noItem = detalleRecepcion.getNoItem();

                if (detalleRecepcion.getCantidadEntregada() == null) {
                    detalleRecepcion.setCantidadEntregada(BigInteger.ZERO);
                }

                lstDetalleOfertaPendiente = recepcionEJB.getLstItemsPendienteEntrega(recepcion.getIdContrato().getIdContrato(), noItem);
                if (lstDetalleOfertaPendiente.isEmpty()) {
                    DetalleItems det = new DetalleItems();
                    det.setCantidad(cantidadRecibidaItem);
                    det.setConsolidadoEspTec(detalleRecepcion.getConsolidadoEspTec());
                    det.setNoItem(noItem);
                    lstDetalleOfertaPendiente.add(det);
                }
                
                cantidadTotalItem = lstDetalleOfertaPendiente.get(0).getCantidad().add(detalleRecepcion.getCantidadEntregada());
                cantidadRecibidaItem = detalleRecepcion.getCantidadEntregada();
                cantidadPendienteItem = cantidadTotalItem.subtract(cantidadRecibidaItem);

                break;
            default:
                lstDetalleOfertaPendiente = recepcionEJB.getLstItemsPendienteEntrega(recepcion.getIdContrato().getIdContrato());
                break;
        }

        for (DetalleItems detalleOfertas : lstDetalleOfertaPendiente) {
            cantidadTotal = cantidadTotal.add(detalleOfertas.getCantidad());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public boolean getErrorValidacion() {
        return errorValidacion;
    }

    public boolean isDividirEntreDos() {
        return esDosUniformes && !cuadradaPrimeraEntrega;
    }

    public boolean isEsDosUniformes() {
        return esDosUniformes;
    }

    public List<DetalleItems> getLstDetalleOfertaPendiente() {
        return lstDetalleOfertaPendiente;
    }

    public String getOp() {
        return op;
    }

    public DetalleRecepcion getDetalleRecepcion() {
        return detalleRecepcion;
    }

    public void setDetalleRecepcion(DetalleRecepcion detalleRecepcion) {
        this.detalleRecepcion = detalleRecepcion;
    }

    public boolean getMostrarEntregas() {
        return mostrarEntregas;
    }

    public void setMostrarEntregas(boolean mostrarEntregas) {
        this.mostrarEntregas = mostrarEntregas;
    }

    public String getObservacionesTotal() {
        return observacionesTotal;
    }

    public void setObservacionesTotal(String observacionesTotal) {
        this.observacionesTotal = observacionesTotal;
    }

    public RecepcionBienesServicios getRecepcionBienesServicios() {
        return recepcion;
    }

    public void setRecepcionBienesServicios(RecepcionBienesServicios recepcionBienesServicios) {
        this.recepcion = recepcionBienesServicios;
    }

    public Date getFechaRecepcionTotal() {
        return fechaRecepcionTotal;
    }

    public void setFechaRecepcionTotal(Date fechaRecepcionTotal) {
        this.fechaRecepcionTotal = fechaRecepcionTotal;
    }

    public void setTipoEntrega(String tipoentrega) {
        this.tipoEntrega = tipoentrega;
    }

    public String getMesageTiempoEntregaSatisfactorio() {
        return mesageTiempoEntregaSatisfactorio;
    }

    public void setMesageTiempoEntregaSatisfactorio(String mesageTiempoEntregaSatisfactorio) {
        this.mesageTiempoEntregaSatisfactorio = mesageTiempoEntregaSatisfactorio;
    }

    public String getMesageTiempoEntregaInsatisfactorio() {
        return mesageTiempoEntregaInsatisfactorio;
    }

    public void setMesageTiempoEntregaInsatisfactorio(String mesageTiempoEntregaInsatisfactorio) {
        this.mesageTiempoEntregaInsatisfactorio = mesageTiempoEntregaInsatisfactorio;
    }

    public BigInteger getCantidadTotalItem() {
        return cantidadTotalItem;
    }

    public BigInteger getCantidadRecibidaItem() {
        return cantidadRecibidaItem;
    }

    public BigInteger getCantidadPendienteItem() {
        return cantidadPendienteItem;
    }

    public boolean isDisabledCantidad() {
        return disabledCantidad;
    }

    public void setDisabledCantidad(boolean disabledCantidad) {
        this.disabledCantidad = disabledCantidad;
    }

    public String getNoItem() {
        return noItem;
    }

    public void setNoItem(String noItem) {
        this.noItem = noItem;
    }

    // </editor-fold>
    public void onDateActaSelect(SelectEvent event) {
        fechaInicio = null;
        //validar fecha de orden de inico dependiendo si es uniformes (1ra y 2da entrega)
        if (esDosUniformes) {
            if (tipoEntrega.equals("1")) {
                fechaInicio = esFechaDiferenteDeNull(recepcion.getFechaOrdenInicioEntrega1());
            } else if (tipoEntrega.equals("2")) {
                fechaInicio = esFechaDiferenteDeNull(recepcion.getFechaOrdenInicioEntrega2());
            }
        } else {
            fechaInicio = esFechaDiferenteDeNull(recepcion.getFechaOrdenInicioEntrega1());
        }

        if (fechaInicio != null) {
            if (detalleRecepcion.getFechaRecepcion().after(fechaInicio)
                    && detalleRecepcion.getFechaRecepcion().before(fechaInicioMasPlazoDeEntrega(fechaInicio, idRubro))) {
                JsfUtil.mensajeInformacion("Entrega en tiempo Satisfactoria");
            } else {//entrega en fuera de rango
                JsfUtil.mensajeAlerta("Entrega en fuera de tiempo");
            }
        }
    }

    /**
     * MISANCHEZ
     *
     * @param fecha
     * @return
     */
    private Date esFechaDiferenteDeNull(Date fecha) {
        if (fecha == null) {
            JsfUtil.mensajeError("Favor Ingrese fecha de orden de inicio");
            return null;
        } else {
            return fecha;
        }
    }

    /**
     * MISanchez
     *
     * @param fInicio
     * @param idRubro
     * @return
     */
    private Date fechaInicioMasPlazoDeEntrega(Date fInicio, Integer idRubro) {

        Calendar fechaTemp = Calendar.getInstance();
        fechaTemp.setTime(fInicio);
        switch (idRubro) {
            case 1:
            case 3:
            case 4:
            case 5:
                fechaTemp.add(Calendar.DATE, 60);
                break;
            case 2:
                fechaTemp.add(Calendar.DATE, 30);
                break;
        }

        return fechaTemp.getTime();
    }

    public void onDateActaSelectEntregaCompleta(SelectEvent event) {
        if (recepcion.getIdContrato().getFechaOrdenInicio() == null) {
            fechaRecepcionTotal = null;
            JsfUtil.mensajeError("Favor Ingrese fecha de orden de inicio entrega n");
        } else if (esFechaInicioMayorRecepcion1Total() && tipoEntrega.equals("1")) {
            fechaRecepcionTotal = null;
            JsfUtil.mensajeError("Fecha de orden de inicio entrega1 es mayor que fecha de acta");
        } else if (tipoEntrega.equals("2") && esFechaInicioMayorRecepcion2Total()) {
            detalleRecepcion.setFechaRecepcion(null);
            JsfUtil.mensajeError("Fecha de orden de inicio entrega 2 es mayor que fecha de acta");
        }
    }

    private boolean esFechaInicioMayorRecepcion1Total() {
        boolean salida = false;
        Date fechaInicio1 = null;
        Date fecharecepcion = null;
        if (recepcion.getIdContrato().getFechaOrdenInicio() != null) {
            fechaInicio1 = recepcion.getIdContrato().getFechaOrdenInicio();
        }
        if (fechaRecepcionTotal != null) {
            fecharecepcion = fechaRecepcionTotal;
        }
        if (fecharecepcion != null && fecharecepcion.before(fechaInicio1)) {
            salida = true;
        }

        return salida;
    }

    private boolean esFechaInicioMayorRecepcion2Total() {
        boolean salida = false;
        Date fechaInicio2 = null;
        Date fecharecepcion = null;
        try {
            if (recepcion.getIdContrato().getFechaOrdenInicio() != null) {
                fechaInicio2 = recepcion.getIdContrato().getFechaOrdenInicio();
            }
            if (fechaRecepcionTotal != null) {
                fecharecepcion = fechaRecepcionTotal;
            }
            if (fecharecepcion != null && fecharecepcion.before(fechaInicio2)) {
                salida = true;
            }
        } catch (Exception e) {
            /*System.out.println("Error compracion de fechas");
            System.out.println("FechaRecepcionTotal " + fechaRecepcionTotal);
            System.out.println("fechaInicio2 " + fechaInicio2);*/
        }

        return salida;
    }

    public String getTipoEntrega() {
        if (VarSession.isVariableSession("segundaEntrega")) {
            Boolean segundaentrega = (Boolean) VarSession.getVariableSession("segundaEntrega");
            if (segundaentrega) {
                if (VarSession.isVariableSession("cuadraPrimera")) {
                    Boolean cuadraPrimeraEntrega = (Boolean) VarSession.getVariableSession("cuadraPrimera");
                    if (cuadraPrimeraEntrega) {
                        tipoEntrega = "2";
                    } else {
                        tipoEntrega = "1";
                    }
                } else {
                    tipoEntrega = "1";
                }

            } else {
                tipoEntrega = "1";
            }
        }

        return tipoEntrega;
    }

    public void modificardetalleContratoTotal() {
        for (DetalleItems detOfe : lstDetalleOfertaPendiente) {
            detalleRecepcion = new DetalleRecepcion();
            detalleRecepcion.setFechaRecepcion(fechaRecepcionTotal);
            detalleRecepcion.setTipoEntrega(tipoEntrega);
            if (isDividirEntreDos()) {
                detalleRecepcion.setCantidadEntregada(detOfe.getCantidad().divide(BigInteger.valueOf(2)));
            } else {
                detalleRecepcion.setCantidadEntregada(detOfe.getCantidad());
            }
            detalleRecepcion.setNoItem(detOfe.getNoItem());
            detalleRecepcion.setConsolidadoEspTec(detOfe.getConsolidadoEspTec());
            detalleRecepcion.setEstadoEliminacion(BigInteger.ZERO);
            detalleRecepcion.setFechaInsercion(new Date());
            detalleRecepcion.setIdRecepcion(recepcion);
            detalleRecepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            detalleRecepcion.setObservaciones(observacionesTotal);
            recepcionEJB.guardarDetalleRecepcion(detalleRecepcion, true);
        }
        PrimeFaces.current().dialog().closeDynamic(this);
    }

    public void calcularTotales() {
        cantidadTotalItem = BigInteger.ZERO;
        cantidadTotalRecibidaItem = BigInteger.ZERO;
        cantidadPendienteItem = BigInteger.ZERO;

        calcularTotalItemPendienteDeEntrega();

        //calcularTotalRecibidoByItem();
        //cantidadRecibidaItem = cantidadTotalRecibidaItem;
        cantidadPendienteItem = cantidadTotalItem.subtract(cantidadTotalRecibidaItem);
        disabledCantidad = false;

        errorValidacion = cantidadPendienteItem.intValue() == 0;
        if (errorValidacion) {
            JsfUtil.mensajeInformacion("Este item ya ha sido entregado completamente");
        }
    }

    /**
     * Obtiene el total de piezas pendientes de entrega
     */
    private void calcularTotalItemPendienteDeEntrega() {
        for (DetalleItems detalle : lstDetalleOfertaPendiente) {
            if (detalle.getNoItem().equals(noItem)) {
                cantidadTotalItem = detalle.getCantidad();
                if (isDividirEntreDos()) {
                    cantidadTotalItem = BigInteger.valueOf(cantidadTotalItem.intValue() / 2);
                }
                break;
            }
        }
    }

    public void actualizarSaldo() {
        if (op.equals("0")) {
            cantidadRecibidaItem = (detalleRecepcion.getCantidadEntregada() == null ? BigInteger.ZERO : detalleRecepcion.getCantidadEntregada());
        } else {
            cantidadRecibidaItem = (detalleRecepcion.getCantidadEntregada() == null ? BigInteger.ZERO : cantidadTotalRecibidaItem.add(detalleRecepcion.getCantidadEntregada()));
        }
        cantidadPendienteItem = cantidadTotalItem.subtract(cantidadRecibidaItem);
        /*errorValidacion = cantidadPendienteItem.doubleValue() < 0;*/
        if (cantidadPendienteItem.doubleValue() < 0) {
            JsfUtil.mensajeError("Error cantidad excede monto para este item");
        }
    }

    public void modificardetalleContrato() {
        if (cantidadRecibidaItem != null || cantidadRecibidaItem.intValue() != 0) {
            findItemBydetalle();

            detalleRecepcion.setTipoEntrega(tipoEntrega);
            detalleRecepcion.setNoItem(noItem);
            detalleRecepcion.setConsolidadoEspTec(detalleItemEs);
            Boolean escero = siContratoSaldoCero();
            if (VarSession.isVariableSession("idDetalleSeguimiento")) {
                detalleRecepcion.setFechaModificacion(new Date());
                detalleRecepcion.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                VarSession.removeVariableSession("idDetalleSeguimiento");
            } else {
                detalleRecepcion.setEstadoEliminacion(BigInteger.ZERO);
                detalleRecepcion.setFechaInsercion(new Date());
                detalleRecepcion.setIdRecepcion(recepcion);
                detalleRecepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            }
            recepcionEJB.guardarDetalleRecepcion(detalleRecepcion, escero);
            detalleRecepcion = new DetalleRecepcion();

            PrimeFaces.current().dialog().closeDynamic(detalleRecepcion);
        } else {
            JsfUtil.mensajeAlerta("La cantidad recibida debe de mayor a 0");
        }
    }

    public void findItemBydetalle() {
        for (DetalleItems detalle : lstDetalleOfertaPendiente) {
            if (detalle.getNoItem().equals(noItem)) {
                detalleItemEs = detalle.getConsolidadoEspTec();
                break;
            }
        }
    }

    public Boolean siContratoSaldoCero() {
        BigInteger totalDetalles = BigInteger.ZERO;
        if (op.equals("0")) {
            for (DetalleRecepcion detalle : lstDetalleRecepcion) {
                BigInteger valor = detalle.getCantidadEntregada();
                if (detalle.getIdDetalleRecepcion() != detalleRecepcion.getIdDetalleRecepcion()) {
                    totalDetalles = totalDetalles.add(valor);
                }
            }
        } else {
            for (DetalleRecepcion detalle : lstDetalleRecepcion) {
                BigInteger valor = detalle.getCantidadEntregada();
                totalDetalles = totalDetalles.add(valor);
            }
        }
        return (cantidadTotal.add(totalDetalles.add(detalleRecepcion.getCantidadEntregada()).negate()).compareTo(BigInteger.ZERO) == 0);
    }
}
