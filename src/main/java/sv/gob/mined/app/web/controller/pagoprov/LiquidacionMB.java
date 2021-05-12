/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.LoginEJB;
import sv.gob.mined.paquescolar.ejb.OfertaBienesServiciosEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.ejb.ResolucionAdjudicativaEJB;
import sv.gob.mined.paquescolar.model.ContratosOrdenesCompras;
import sv.gob.mined.paquescolar.model.DetalleLiquidacion;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.Liquidacion;
import sv.gob.mined.paquescolar.model.RecepcionBienesServicios;
import sv.gob.mined.paquescolar.model.ResolucionesModificativas;
import sv.gob.mined.paquescolar.model.pojos.contratacion.ParticipanteConContratoDto;
import sv.gob.mined.paquescolar.model.pojos.liquidacion.DatosContratoDto;
import sv.gob.mined.paquescolar.model.pojos.liquidacion.DatosLiquidacionDto;
import sv.gob.mined.paquescolar.model.pojos.liquidacion.DatosModificativaDto;
import sv.gob.mined.paquescolar.model.pojos.liquidacion.DatosRecepcionDto;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;

/**
 *
 * @author MISanchez
 */
@Named
@ViewScoped
public class LiquidacionMB extends RecuperarProcesoUtil implements Serializable {

    private Boolean modificativa = false;
    private String codigoEntidad;
    private String numeroContrato;
    private String observacion;
    private BigDecimal cantidadOriginal;
    private BigDecimal montoOriginal;
    private BigDecimal cantidadModificativa;
    private BigDecimal montoModificativa;
    private BigDecimal cantidadRecepcion;
    private BigDecimal idRubro;
    private BigDecimal idParticipante = BigDecimal.ZERO;

    private ContratosOrdenesCompras contrato = new ContratosOrdenesCompras();
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private Liquidacion liquidacion = new Liquidacion();
    private RecepcionBienesServicios recepcion = new RecepcionBienesServicios();
    private ResolucionesModificativas resModificativa;

    private List<DatosContratoDto> datosContratoDto;
    private List<DatosModificativaDto> datosModificativaDto;
    private List<DatosRecepcionDto> datosRecepcionDto;

    private List<DatosLiquidacionDto> datosLiquidacionDtos;

    private List<Liquidacion> lstLiquidaciones = new ArrayList();
    private List<ParticipanteConContratoDto> lstParticipantes = new ArrayList();

    private VwCatalogoEntidadEducativa entidadEducativa = new VwCatalogoEntidadEducativa();

    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private OfertaBienesServiciosEJB ofertaBienesServiciosEJB;
    @Inject
    private ResolucionAdjudicativaEJB resolucionAdjudicativaEJB;
    @Inject
    private LoginEJB loginEJB;
    @Inject
    private ReportesEJB reportesEJB;

    public LiquidacionMB() {
    }

    public List<ParticipanteConContratoDto> getLstParticipantes() {
        return lstParticipantes;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public List<Liquidacion> getLstLiquidaciones() {
        return lstLiquidaciones;
    }

    public void setLstLiquidaciones(List<Liquidacion> lstLiquidaciones) {
        this.lstLiquidaciones = lstLiquidaciones;
    }

    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal idRubro) {
        this.idRubro = idRubro;
    }

    public BigDecimal getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(BigDecimal idParticipante) {
        this.idParticipante = idParticipante;
    }

    /*public OfertaBienesServicios getOferta() {
        return oferta;
    }*/
    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        return entidadEducativa;
    }

    public ContratosOrdenesCompras getContrato() {
        return contrato;
    }

    public RecepcionBienesServicios getRecepcion() {
        return recepcion;
    }

    public Liquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Liquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Boolean getModificativa() {
        return modificativa;
    }

    public void setModificativa(Boolean modificativa) {
        this.modificativa = modificativa;
    }

    public BigDecimal getMontoOriginal() {
        return montoOriginal;
    }

    public void setMontoOriginal(BigDecimal montoOriginal) {
        this.montoOriginal = montoOriginal;
    }

    public BigDecimal getMontoModificativa() {
        return montoModificativa;
    }

    public void setMontoModificativa(BigDecimal montoModificativa) {
        this.montoModificativa = montoModificativa;
    }

    public ResolucionesModificativas getResModificativa() {
        return resModificativa;
    }

    public void setResModificativa(ResolucionesModificativas resModificativa) {
        this.resModificativa = resModificativa;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public BigDecimal getCantidadModificativa() {
        return cantidadModificativa;
    }

    public void setCantidadModificativa(BigDecimal cantidadModificativa) {
        this.cantidadModificativa = cantidadModificativa;
    }

    public BigDecimal getCantidadRecepcion() {
        return cantidadRecepcion;
    }

    public void setCantidadRecepcion(BigDecimal cantidadRecepcion) {
        this.cantidadRecepcion = cantidadRecepcion;
    }

    public List<DatosLiquidacionDto> getDatosLiquidacionDtos() {
        return datosLiquidacionDtos;
    }

    public void buscarEntidadEducativa() {
        if (codigoEntidad.length() == 5) {
            /**
             * Fecha: 30/08/2018 Comentario: Validación de seleccion del año y
             * proceso de adquisición
             */
            if (getRecuperarProceso().getProcesoAdquisicion() == null) {
                JsfUtil.mensajeAlerta("Debe de seleccionar un año y proceso de contratación.");
            } else {

                entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);
                if (entidadEducativa == null) {
                    JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
                } else {
                    detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
                    lstParticipantes = resolucionAdjudicativaEJB.findParticipantesConContratoByCodEntAndIdDetProcesoAdq(codigoEntidad, detalleProceso.getIdDetProcesoAdq());
                }
            }
        } else {
            entidadEducativa = null;
        }
    }

    public void agregarLista() {
        liquidacion = new Liquidacion();
        liquidacion.setFechaInsercion(new Date());
        liquidacion.setEstadoEliminacion((short) 0);
        liquidacion.setIdContrato(resolucionAdjudicativaEJB.findContratoByPk(datosContratoDto.get(0).getIdContrato()));
        liquidacion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());

        /**
         * faltaria detalle de diferencia de items contratados por modificativa
         * a contratos
         */
        datosLiquidacionDtos.forEach(dato -> {
            DetalleLiquidacion det = new DetalleLiquidacion();

            det.setNoItem(dato.getNoItem());
            det.setCantidad(dato.getCantidadContrato().longValue());
            det.setPrecioUnitario(dato.getPrecioUnitarioContrato());

            det.setCantidadModificativa(dato.getCantidadModificativa().longValue());
            det.setPrecioUnitarioModif(dato.getPrecioUnitarioModificativa());

            det.setCantidadEntregada(dato.getCantidadRecepcion().longValue());
            det.setCantidadResguardo(dato.getCantidadResguardo().longValue());

            det.setIdLiquidacion(liquidacion);

            liquidacion.getDetalleLiquidacionList().add(det);
        });

        resolucionAdjudicativaEJB.guardarLiquidacion(liquidacion);

        JsfUtil.mensajeInsert();

        liquidacion = new Liquidacion();
    }

    public void recuperarLstLiquidacionByCodEntAndIdDetPro() {
        lstLiquidaciones = resolucionAdjudicativaEJB.getLstLiquidacionByCodigoEntAndIdDetProcesoAdqAndIdParticipante(codigoEntidad, detalleProceso.getIdDetProcesoAdq(), idParticipante);
    }

    public void recuperarDatos() {
        datosLiquidacionDtos = new ArrayList();

        datosContratoDto = resolucionAdjudicativaEJB.getDatosContratoDto(codigoEntidad, detalleProceso.getIdDetProcesoAdq());
        if (datosContratoDto.get(0).getIdEstadoReserva().intValue() == 5) {
            datosModificativaDto = resolucionAdjudicativaEJB.getDatosModificativaDto(datosContratoDto.get(0).getIdContrato());
            modificativa = true;
        }

        datosRecepcionDto = resolucionAdjudicativaEJB.getDatosRecepcionDto(datosContratoDto.get(0).getIdContrato());

        switch (detalleProceso.getIdRubroAdq().getIdRubroInteres().intValue()) {
            case 1:
            case 4:
            case 5:
                crearTablaDeCompracion(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"));
                break;
            case 2:
                crearTablaDeCompracion(Arrays.asList("1", "2", "3", "4", "5"));
                break;
            case 3:
                crearTablaDeCompracion(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                break;
        }
    }

    private void crearTablaDeCompracion(List<String> listado) {
        listado.forEach(noItem -> {
            DatosLiquidacionDto datoLiquidacion = new DatosLiquidacionDto();
            datoLiquidacion = subCrearTabla(noItem, datoLiquidacion);
            if (datoLiquidacion != null) {
                datosLiquidacionDtos.add(datoLiquidacion);
            }
        });
    }

    private DatosLiquidacionDto subCrearTabla(String noItem, DatosLiquidacionDto datoLiquidacion) {
        Boolean noEstaItem = true;
        for (DatosContratoDto dato : datosContratoDto) {
            if (dato.getNoItem().equals(noItem)) {
                datoLiquidacion.setIdContrato(dato.getIdContrato());
                datoLiquidacion.setNoItem(noItem);
                datoLiquidacion.setCantidadContrato(dato.getCantidad());
                datoLiquidacion.setPrecioUnitarioContrato(dato.getPrecioUnitario());
                noEstaItem = false;
                break;
            }
        }

        for (DatosModificativaDto dato : datosModificativaDto) {
            if (dato.getNoItem().equals(noItem)) {
                datoLiquidacion.setIdContrato(dato.getIdContrato());
                datoLiquidacion.setNoItem(noItem);
                datoLiquidacion.setCantidadModificativa(dato.getCantidadNew());
                datoLiquidacion.setPrecioUnitarioModificativa(dato.getPrecioUnitarioNew());
                noEstaItem = false;
                break;
            }
        }

        for (DatosRecepcionDto dato : datosRecepcionDto) {
            if (dato.getNoItem().equals(noItem)) {
                datoLiquidacion.setIdContrato(dato.getIdContrato());
                datoLiquidacion.setNoItem(noItem);
                datoLiquidacion.setCantidadRecepcion(dato.getCantidadEntregada());
                noEstaItem = false;
                break;
            }
        }

        if (noEstaItem) {
            return null;
        } else {
            return datoLiquidacion;
        }
    }

    public void imprimirReporte() {
        String nombreUsuario = loginEJB.getNombreByUsername(liquidacion.getUsuarioInsercion());

        HashMap param = new HashMap();
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
        param.put("p_id_liquidacion", liquidacion.getIdLiquidacion());
        param.put("p_id_contrato", liquidacion.getIdContrato().getIdContrato());
        param.put("p_nombre_canton", "");
        param.put("p_nombre_cacerio", "");
        param.put("p_nombre_usuario", nombreUsuario);

        Reportes.generarRptSQLConnection(reportesEJB, param, "sv/gob/mined/apps/reportes/pagoproveedor/", "rptLiquidacion", "rptLiquidacion");
    }
}
