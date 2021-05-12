/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.DonutChartModel;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.RptExcel;
import sv.gob.mined.app.web.util.UtilFile;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.CreditosEJB;
import sv.gob.mined.paquescolar.ejb.EMailEJB;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.PagoProveedoresEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.ejb.ServiciosJsonEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.ContratosOrdenesCompras;
import sv.gob.mined.paquescolar.model.DetalleDocPago;
import sv.gob.mined.paquescolar.model.DetalleModificativa;
import sv.gob.mined.paquescolar.model.DetallePlanilla;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.DetalleRequerimiento;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.EntidadFinanciera;
import sv.gob.mined.paquescolar.model.PlanillaPago;
import sv.gob.mined.paquescolar.model.PlanillaPagoCheque;
import sv.gob.mined.paquescolar.model.ReintegroRequerimiento;
import sv.gob.mined.paquescolar.model.RequerimientoFondos;
import sv.gob.mined.paquescolar.model.ResolucionesModificativas;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.ResumenRequerimientoDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.DatosBusquedaPlanillaDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.DatosProveDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.DatosResumenPagosDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.DatosResumenPagosPorReqYProveedorDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.InformeF14Dto;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class PagoProveedoresController extends RecuperarProcesoUtil implements Serializable {

    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private ServiciosJsonEJB serviciosEJB;
    @Inject
    private UtilEJB utilEJB;
    @Inject
    private CreditosEJB creditosEJB;
    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private ReportesEJB reportesEJB;
    @Inject
    private PagoProveedoresEJB pagoProveedoresEJB;
    @Inject
    private EMailEJB eMailEJB;

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("Bundle");

    private int indexTab = 0;
    private int rowEdit = 0;
    private int ajusteRenta = 0;
    private int idMes = 0;
    private int idTipoPlanilla = 0;
    private Integer[] tipoDocumentoImp;

    private Boolean dlgShowEntidadesFinancieras = false;
    private Boolean dlgShowTipoPlanilla = false;
    private Boolean dlgShowSeleccionProveedor = false;
    private Boolean contratoExtinguido = false;
    private Boolean showPnlCheques = false;
    private Boolean showChequeEntProv = false;
    private Boolean showChequeUsefi = false;
    private Boolean showChequeRenta = false;
    private Boolean cheque = false;
    private Boolean filtro = true;
    private Boolean seleccionRequerimiento = false;
    private Boolean seleccionPlanilla = false;
    private Boolean planilla = false;
    private Boolean detallePlanilla = false;
    private Boolean dlgDetallePlanilla = false;
    private Boolean dlgEdtDetPlanilla = false;
    private Boolean dlgEdtDetDocPago = false;
    private Boolean dlgDetPagoProveedor = false;
    private Boolean contratoModificado = false;
    private Boolean renderGrafico = false;
    private Boolean renderMontoRenta = false;
    private Boolean tipoPagoEntFinanciera = false;
    private Boolean isRubroUniforme = false;
    private Boolean isPlanillaLectura = false;
    private Boolean contratoSinActaRecepcion = false;

    private String anho;
    private String anhoRptAnual;
    private String anhoRptMensual;
    private String codigoEntidad = "";
    private String codigoDepartamento = "";
    private String numeroCheque = "";
    private String numeroRequerimiento = "";
    private String nombreEntFinanciera = "";
    private String nombreRubro = "";
    private String numeroNit = "";
    private String emailUnico;
    private String razonSocial;

    private Date fechaCheque;

    private BigDecimal idPlanilla;
    private BigDecimal idReq = BigDecimal.ZERO;
    private BigDecimal idRubro = BigDecimal.ZERO;
    private BigDecimal montoCheque = BigDecimal.ZERO;
    private BigDecimal cantidadCe = BigDecimal.ZERO;
    private BigDecimal cantidadPlanilla = BigDecimal.ZERO;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado = BigDecimal.ZERO;
    private BigDecimal montoPendiente = BigDecimal.ZERO;
    private BigDecimal montoReintegro = BigDecimal.ZERO;
    private BigDecimal montoSaldo = BigDecimal.ZERO;
    private BigDecimal montoSujetoRenta = BigDecimal.ZERO;
    private BigDecimal montoRenta = BigDecimal.ZERO;
    private BigDecimal montoContrato = BigDecimal.ZERO;
    private BigDecimal ceContratados = BigDecimal.ZERO;
    private BigDecimal totalContratado = BigDecimal.ZERO;
    private BigDecimal totalPagado = BigDecimal.ZERO;
    private BigDecimal totalPendiente = BigDecimal.ZERO;
    private BigDecimal totalReintegro = BigDecimal.ZERO;
    private BigInteger cantidadContrato = BigInteger.ZERO;

    private DonutChartModel donutModel = new DonutChartModel();

    private Empresa empresa = new Empresa();
    private DatosProveDto proveedor = new DatosProveDto();
    private PlanillaPagoCheque chequeFinanProve = new PlanillaPagoCheque();
    private PlanillaPagoCheque chequeUsefi = new PlanillaPagoCheque();
    private PlanillaPagoCheque chequeRenta = new PlanillaPagoCheque();
    private EntidadFinanciera entidadFinanciera = new EntidadFinanciera();
    private PlanillaPago planillaPago = new PlanillaPago();
    private DetalleRequerimiento detalleRequerimiento = new DetalleRequerimiento();
    private DetallePlanilla detPlanilla = new DetallePlanilla();
    private DetalleDocPago detalleDocPago = new DetalleDocPago();
    private DetalleProcesoAdq detalleProcesoAdq = new DetalleProcesoAdq();
    private RequerimientoFondos requerimientoFondos = new RequerimientoFondos();
    private ReintegroRequerimiento reintegroRequerimiento = new ReintegroRequerimiento();
    private VwCatalogoEntidadEducativa entidadEducativa = new VwCatalogoEntidadEducativa();

    private List<DatosProveDto> lstEmailProveeCredito = new ArrayList();
    private List<SelectItem> lstTipoDocImp = new ArrayList();

    private List<ResumenRequerimientoDto> lstResumenRequerimiento = new ArrayList();
    private List<DatosResumenPagosDto> lstResumenPago = new ArrayList();
    private List<DatosResumenPagosPorReqYProveedorDto> lstResumenPagoPorProveedor = new ArrayList();
    private List<EntidadFinanciera> lstEntFinRequerimiento = new ArrayList();

    private List<RequerimientoFondos> lstRequerimientoFondos = new ArrayList();
    private List<DetalleRequerimiento> lstDetalleRequerimiento = new ArrayList();
    private List<DetalleRequerimiento> lstDetalleRequerimientoSeleccionado = new ArrayList();
    private List<PlanillaPago> lstPlanillas = new ArrayList();
    private List<DetallePlanilla> lstDetallePlanilla = new ArrayList();
    private List<DatosProveDto> lstProveedores = new ArrayList();
    private List<InformeF14Dto> lstF14 = new ArrayList();
    private List<DatosBusquedaPlanillaDto> lstBusquedaPlanillas = new ArrayList();

    private StreamedContent file;

    public PagoProveedoresController() {
    }

    @PostConstruct
    public void ini() {
        codigoDepartamento = getRecuperarProceso().getDepartamento();
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public Boolean getContratoSinActaRecepcion() {
        return contratoSinActaRecepcion;
    }

    public void setContratoSinActaRecepcion(Boolean contratoSinActaRecepcion) {
        this.contratoSinActaRecepcion = contratoSinActaRecepcion;
    }

    public BigDecimal getMontoContrato() {
        return montoContrato;
    }

    public BigInteger getCantidadContrato() {
        return cantidadContrato;
    }

    public Boolean getDlgDetPagoProveedor() {
        return dlgDetPagoProveedor;
    }

    public void setDlgDetPagoProveedor(Boolean dlgDetPagoProveedor) {
        this.dlgDetPagoProveedor = dlgDetPagoProveedor;
    }

    public BigDecimal getCeContratados() {
        return ceContratados;
    }

    public void setCeContratados(BigDecimal ceContratados) {
        this.ceContratados = ceContratados;
    }

    public BigDecimal getTotalContratado() {
        return totalContratado;
    }

    public void setTotalContratado(BigDecimal totalContratado) {
        this.totalContratado = totalContratado;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public BigDecimal getTotalPendiente() {
        return totalPendiente;
    }

    public void setTotalPendiente(BigDecimal totalPendiente) {
        this.totalPendiente = totalPendiente;
    }

    public BigDecimal getTotalReintegro() {
        return totalReintegro;
    }

    public void setTotalReintegro(BigDecimal totalReintegro) {
        this.totalReintegro = totalReintegro;
    }

    public List<DatosResumenPagosPorReqYProveedorDto> getLstResumenPagoPorProveedor() {
        return lstResumenPagoPorProveedor;
    }

    public void setLstResumenPagoPorProveedor(List<DatosResumenPagosPorReqYProveedorDto> lstResumenPagoPorProveedor) {
        this.lstResumenPagoPorProveedor = lstResumenPagoPorProveedor;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public ReintegroRequerimiento getReintegroRequerimiento() {
        return reintegroRequerimiento;
    }

    public void setReintegroRequerimiento(ReintegroRequerimiento reintegroRequerimiento) {
        this.reintegroRequerimiento = reintegroRequerimiento;
    }

    public List<DetallePlanilla> getLstDetallePlanilla() {
        return lstDetallePlanilla;
    }

    public void setLstDetallePlanilla(List<DetallePlanilla> lstDetallePlanilla) {
        this.lstDetallePlanilla = lstDetallePlanilla;
    }

    public List<DatosBusquedaPlanillaDto> getLstBusquedaPlanillas() {
        return lstBusquedaPlanillas;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Boolean getUsuarioAdministrador() {
        return VarSession.getUsuarioSession().getIdTipoUsuario().getAdministrador().intValue() == 1;
    }

    public String getAnhoRptAnual() {
        return anhoRptAnual;
    }

    public void setAnhoRptAnual(String anhoRptAnual) {
        this.anhoRptAnual = anhoRptAnual;
    }

    public String getAnhoRptMensual() {
        return anhoRptMensual;
    }

    public void setAnhoRptMensual(String anhoRptMensual) {
        this.anhoRptMensual = anhoRptMensual;
    }

    public int getIndexTab() {
        return indexTab;
    }

    public void setIndexTab(int indexTab) {
        this.indexTab = indexTab;
    }

    public Boolean getIsPlanillaLectura() {
        return isPlanillaLectura;
    }

    public BigDecimal getIdPlanilla() {
        return idPlanilla;
    }

    public void setIdPlanilla(BigDecimal idPlanilla) {
        this.idPlanilla = idPlanilla;
    }

    public String getAnho() {
        return anho;
    }

    public void setAnho(String anho) {
        this.anho = anho;
    }

    public Boolean getContratoExtinguido() {
        return contratoExtinguido;
    }

    public void setContratoExtinguido(Boolean contratoExtinguido) {
        this.contratoExtinguido = contratoExtinguido;
    }

    public String getNombreEntFinanciera() {
        return nombreEntFinanciera;
    }

    public void setNombreEntFinanciera(String nombreEntFinanciera) {
        this.nombreEntFinanciera = nombreEntFinanciera;
    }

    public DatosProveDto getProveedor() {
        return proveedor;
    }

    public void setProveedor(DatosProveDto proveedor) {
        this.proveedor = proveedor;
    }

    public int getIdTipoPlanilla() {
        return idTipoPlanilla;
    }

    public void setIdTipoPlanilla(int idTipoPlanilla) {
        this.idTipoPlanilla = idTipoPlanilla;
    }

    public int getIdMes() {
        return idMes;
    }

    public void setIdMes(int idMes) {
        if (idMes != 0) {
            this.idMes = idMes;
        }
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<SelectItem> getLstTipoDocImp() {
        return lstTipoDocImp;
    }

    public void setLstTipoDocImp(List<SelectItem> lstTipoDocImp) {
        this.lstTipoDocImp = lstTipoDocImp;
    }

    public Integer[] getTipoDocumentoImp() {
        return tipoDocumentoImp;
    }

    public void setTipoDocumentoImp(Integer[] tipoDocumentoImp) {
        if (tipoDocumentoImp != null) {
            this.tipoDocumentoImp = tipoDocumentoImp;
        }
    }

    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        return entidadEducativa;
    }

    public void setEntidadEducativa(VwCatalogoEntidadEducativa entidadEducativa) {
        this.entidadEducativa = entidadEducativa;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    public BigDecimal getIdReq() {
        return idReq;
    }

    public void setIdReq(BigDecimal idReq) {
        this.idReq = idReq;
    }

    public List<ResumenRequerimientoDto> getLstResumenRequerimiento() {
        return lstResumenRequerimiento;
    }

    public DetalleRequerimiento getDetalleRequerimiento() {
        return detalleRequerimiento;
    }

    public void setDetalleRequerimiento(DetalleRequerimiento detalleRequerimiento) {
        this.detalleRequerimiento = detalleRequerimiento;
    }

    public Boolean getDlgShowTipoPlanilla() {
        return dlgShowTipoPlanilla;
    }

    public void setDlgShowTipoPlanilla(Boolean dlgShowTipoPlanilla) {
        this.dlgShowTipoPlanilla = dlgShowTipoPlanilla;
    }

    public Boolean getDlgShowSeleccionProveedor() {
        return dlgShowSeleccionProveedor;
    }

    public void setDlgShowSeleccionProveedor(Boolean dlgShowSeleccionProveedor) {
        this.dlgShowSeleccionProveedor = dlgShowSeleccionProveedor;
    }

    public Boolean getDlgEdtDetDocPago() {
        return dlgEdtDetDocPago;
    }

    public void setDlgEdtDetDocPago(Boolean dlgEdtDetDocPago) {
        this.dlgEdtDetDocPago = dlgEdtDetDocPago;
    }

    public DetalleDocPago getDetalleDocPago() {
        return detalleDocPago;
    }

    public void setDetalleDocPago(DetalleDocPago detalleDocPago) {
        if (detalleDocPago != null) {
            this.detalleDocPago = detalleDocPago;
        }
    }

    public String getNumeroRequerimiento() {
        return numeroRequerimiento;
    }

    public void setNumeroRequerimiento(String numeroRequerimiento) {
        this.numeroRequerimiento = numeroRequerimiento;
    }

    public PlanillaPagoCheque getChequeFinanciera() {
        return chequeFinanProve;
    }

    public void setChequeFinanciera(PlanillaPagoCheque planillaPagoCheque) {
        this.chequeFinanProve = planillaPagoCheque;
    }

    public PlanillaPagoCheque getChequeUsefi() {
        return chequeUsefi;
    }

    public void setChequeUsefi(PlanillaPagoCheque chequeUsefi) {
        this.chequeUsefi = chequeUsefi;
    }

    public PlanillaPagoCheque getChequeRenta() {
        return chequeRenta;
    }

    public void setChequeRenta(PlanillaPagoCheque chequeRenta) {
        this.chequeRenta = chequeRenta;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public BigDecimal getMontoCheque() {
        return montoCheque;
    }

    public void setMontoCheque(BigDecimal montoCheque) {
        this.montoCheque = montoCheque;
    }

    public Date getFechaCheque() {
        return fechaCheque;
    }

    public void setFechaCheque(Date fechaCheque) {
        this.fechaCheque = fechaCheque;
    }

    public int getNumeroDetalle() {
        return lstDetalleRequerimiento.size();
    }

    public Boolean getCheque() {
        return cheque;
    }

    public void setCheque(Boolean cheque) {
        this.cheque = cheque;
    }

    public Boolean getShowChequeCredito() {
        return showChequeEntProv;
    }

    public EntidadFinanciera getEntidadFinanciera() {
        return entidadFinanciera;
    }

    public void setEntidadFinanciera(EntidadFinanciera entidadFinanciera) {
        if (entidadFinanciera != null && entidadFinanciera.getCodEntFinanciera() != null) {
            this.entidadFinanciera = entidadFinanciera;
        }
    }

    public List<EntidadFinanciera> getLstEntFinRequerimiento() {
        return lstEntFinRequerimiento;
    }

    public void setLstEntFinRequerimiento(List<EntidadFinanciera> lstEntFinRequerimiento) {
        this.lstEntFinRequerimiento = lstEntFinRequerimiento;
    }

    public Boolean getDlgEdtDetPlanilla() {
        return dlgEdtDetPlanilla;
    }

    public void setDlgEdtDetPlanilla(Boolean dlgEdtDetPlanilla) {
        this.dlgEdtDetPlanilla = dlgEdtDetPlanilla;
    }

    public Boolean getContratoModificado() {
        return contratoModificado;
    }

    public void setContratoModificado(Boolean contratoModificado) {
        this.contratoModificado = contratoModificado;
    }

    public DetallePlanilla getDetPlanilla() {
        return detPlanilla;
    }

    public void setDetPlanilla(DetallePlanilla detPlanilla) {
        this.detPlanilla = detPlanilla;
    }

    public List<DetalleRequerimiento> getLstDetalleRequerimientoSeleccionado() {
        return lstDetalleRequerimientoSeleccionado;
    }

    public void setLstDetalleRequerimientoSeleccionado(List<DetalleRequerimiento> lstDetalleRequerimientoSeleccionado) {
        this.lstDetalleRequerimientoSeleccionado = lstDetalleRequerimientoSeleccionado;
    }

    public List<DetalleRequerimiento> getLstDetalleRequerimiento() {
        return lstDetalleRequerimiento;
    }

    public void setLstDetalleRequerimiento(List<DetalleRequerimiento> lstDetalleRequerimiento) {
        this.lstDetalleRequerimiento = lstDetalleRequerimiento;
    }

    public int getCantidadCeSeleccionados() {
        return lstDetalleRequerimientoSeleccionado.size();
    }

    public Boolean getDlgDetallePlanilla() {
        return dlgDetallePlanilla;
    }

    public int getRowEdit() {
        return rowEdit;
    }

    public void setRowEdit(int rowEdit) {
        this.rowEdit = rowEdit;
    }

    public void setDlgDetallePlanilla(Boolean dlgDetallePlanilla) {
        this.dlgDetallePlanilla = dlgDetallePlanilla;
    }

    public Boolean getDetallePlanilla() {
        return detallePlanilla;
    }

    public void setDetallePlanilla(Boolean detallePlanilla) {
        this.detallePlanilla = detallePlanilla;
    }

    public Boolean getSeleccionRequerimiento() {
        return seleccionRequerimiento;
    }

    public void setSeleccionRequerimiento(Boolean seleccionRequerimiento) {
        this.seleccionRequerimiento = seleccionRequerimiento;
    }

    public Boolean getSeleccionPlanilla() {
        return seleccionPlanilla;
    }

    public void setSeleccionPlanilla(Boolean seleccionPlanilla) {
        this.seleccionPlanilla = seleccionPlanilla;
    }

    public Boolean getPlanilla() {
        return planilla;
    }

    public void setPlanilla(Boolean planilla) {
        this.planilla = planilla;
    }

    public List<RequerimientoFondos> getLstReq() {
        return lstRequerimientoFondos;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        if (codigoDepartamento != null) {
            this.codigoDepartamento = codigoDepartamento;
        }
    }

    public String getNombreRubro() {
        return nombreRubro;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal rubro) {
        this.idRubro = rubro;
    }

    public List<RequerimientoFondos> getLstRequerimientoFondos() {
        return lstRequerimientoFondos;
    }

    public void setLstRequerimientoFondos(List<RequerimientoFondos> lstRequerimientoFondos) {
        this.lstRequerimientoFondos = lstRequerimientoFondos;
    }

    public RequerimientoFondos getRequerimientoFondos() {
        return requerimientoFondos;
    }

    public void setRequerimientoFondos(RequerimientoFondos requerimientoFondos) {
        if (requerimientoFondos != null) {
            this.requerimientoFondos = requerimientoFondos;
        }
    }

    public PlanillaPago getPlanillaPago() {
        return planillaPago;
    }

    public void setPlanillaPago(PlanillaPago planillaPago) {
        this.planillaPago = planillaPago;
    }

    public Boolean getRenderGrafico() {
        return renderGrafico;
    }

    public DonutChartModel getDonutModel() {
        return donutModel;
    }

    public Boolean getShowChequeUsefi() {
        return showChequeUsefi;
    }

    public void setShowChequeUsefi(Boolean showChequeUsefi) {
        this.showChequeUsefi = showChequeUsefi;
    }

    public Boolean getShowChequeRenta() {
        return showChequeRenta;
    }

    public void setShowChequeRenta(Boolean showChequeRenta) {
        this.showChequeRenta = showChequeRenta;
    }

    public List<DatosResumenPagosDto> getLstResumenPago() {
        return lstResumenPago;
    }

    public BigDecimal getCantidadCe() {
        return cantidadCe;
    }

    public void setCantidadCe(BigDecimal cantidadCe) {
        this.cantidadCe = cantidadCe;
    }

    public BigDecimal getCantidadPlanilla() {
        return cantidadPlanilla;
    }

    public void setCantidadPlanilla(BigDecimal cantidadPlanilla) {
        this.cantidadPlanilla = cantidadPlanilla;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getMontoPendiente() {
        return montoPendiente;
    }

    public void setMontoPendiente(BigDecimal montoPendiente) {
        this.montoPendiente = montoPendiente;
    }

    public BigDecimal getMontoReintegro() {
        return montoReintegro;
    }

    public void setMontoReintegro(BigDecimal montoReintegro) {
        this.montoReintegro = montoReintegro;
    }

    public BigDecimal getMontoSaldo() {
        return montoSaldo;
    }

    public void setMontoSaldo(BigDecimal montoSaldo) {
        this.montoSaldo = montoSaldo;
    }

    public Boolean getRenderMontoRenta() {
        return renderMontoRenta;
    }

    public Boolean getDlgShowEntidadesFinancieras() {
        return dlgShowEntidadesFinancieras;
    }

    public void setDlgShowEntidadesFinancieras(Boolean dlgShowEntidadesFinancieras) {
        this.dlgShowEntidadesFinancieras = dlgShowEntidadesFinancieras;
    }

    public Boolean getTipoPagoEntFinanciera() {
        return tipoPagoEntFinanciera;
    }

    public List<PlanillaPago> getLstPlanillas() {
        return lstPlanillas;
    }

    public void setLstPlanillas(List<PlanillaPago> lstPlanillas) {
        this.lstPlanillas = lstPlanillas;
    }

    public Boolean getFiltro() {
        return filtro;
    }

    public void setFiltro(Boolean filtro) {
        this.filtro = filtro;
    }

    /**
     * Tipo de pago a la entidad financiera
     *
     * @param tipoPagoEntFinanciera true transferencia :: false cheque
     */
    public void setTipoPagoEntFinanciera(Boolean tipoPagoEntFinanciera) {
        this.tipoPagoEntFinanciera = tipoPagoEntFinanciera;
    }

    public Boolean getShowPnlCheques() {
        return showPnlCheques;
    }

    public void setShowPnlCheques(Boolean showPnlCheques) {
        this.showPnlCheques = showPnlCheques;
    }

    public int getAjusteRenta() {
        return ajusteRenta;
    }

    public void setAjusteRenta(int ajusteRenta) {
        this.ajusteRenta = ajusteRenta;
    }

    public String getNumeroNit() {
        return numeroNit;
    }

    public void setNumeroNit(String numeroNit) {
        this.numeroNit = numeroNit;
    }

    public List<DatosProveDto> getLstRentaProve() {
        return lstProveedores;
    }

    public BigDecimal getMontoSujetoRenta() {
        return montoSujetoRenta;
    }

    public BigDecimal getMontoRenta() {
        return montoRenta;
    }

    public List<DatosProveDto> getLstProveedores() {
        return lstProveedores;
    }

    public String getEmailUnico() {
        return emailUnico;
    }

    public List<DatosProveDto> getLstEmailProveeCredito() {
        return lstEmailProveeCredito;
    }
    // </editor-fold>

    public void asignarAnhoDeReporte(Boolean anual) {
        if (anual) {
            anho = anhoRptAnual;
        } else {
            anho = anhoRptMensual;
        }
    }

    public void pagarCheque() {
        if (!contratoModificado && cheque) {
            detPlanilla.setMontoCheque(detPlanilla.getMontoOriginal());
        } else if (contratoModificado && cheque) {
            detPlanilla.setMontoCheque(detPlanilla.getIdDetalleDocPago().getMontoActual());
        }
    }

    public BigInteger getCantidadOriginalTotal() {
        BigInteger total = BigInteger.ZERO;
        for (DetallePlanilla detallePlanilla1 : lstDetallePlanilla) {
            if (detallePlanilla1.getCantidadOriginal() != null) {
                total = total.add(detallePlanilla1.getCantidadOriginal());
            }
        }
        return total;
    }

    public BigDecimal getMontoOriginalTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePlanilla detPla : lstDetallePlanilla) {
            if (detPla.getCantidadOriginal() != null) {
                total = total.add(detPla.getMontoOriginal());
            }
        }
        return total;
    }

    public BigInteger getCantidadActualTotal() {
        BigInteger total = BigInteger.ZERO;
        for (DetallePlanilla detPla : lstDetallePlanilla) {
            if (detPla.getCantidadActual() != null) {
                total = total.add(detPla.getCantidadActual());
            }
        }
        return total;
    }

    public BigDecimal getMontoActualTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePlanilla detPla : lstDetallePlanilla) {
            if (detPla.getCantidadActual() != null && detPla.getEstadoEliminacion() == 0) {
                total = total.add(detPla.getMontoActual());
            }
        }
        return total;
    }

    public BigDecimal getMontoRentaTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePlanilla detPla : lstDetallePlanilla) {
            if (detPla.getCantidadActual() != null && detPla.getIdDetalleDocPago().getMontoRenta() != null) {
                total = total.add(detPla.getIdDetalleDocPago().getMontoRenta());
            }
        }
        return total;
    }

    public void addCeSeleccionadosADetallePlanilla() {
        for (DetalleRequerimiento detalleReq : lstDetalleRequerimientoSeleccionado) {
            boolean isRepetido = false;

            for (DetallePlanilla detPla : lstDetallePlanilla) {
                if (detPla.getIdDetalleDocPago().getIdDetRequerimiento().getIdContrato().intValue()
                        == detalleReq.getIdContrato().intValue()) {
                    isRepetido = true;
                    break;
                }
            }
            if (!isRepetido) {
                addDetPlanilla(detalleReq);
            }
        }
        closeDlgDetallePlanilla();
    }

    private void addDetPlanilla(DetalleRequerimiento detalleReq) {
        DetallePlanilla detPla = new DetallePlanilla();
        detPla.setCantidadOriginal(new BigInteger(detalleReq.getCantidadTotal().toString()));
        detPla.setMontoOriginal(detalleReq.getMontoTotal());
        detPla.setCantidadActual(BigInteger.ZERO);
        detPla.setMontoActual(BigDecimal.ZERO);
        detPla
                .setCodigoEntidad(utilEJB.find(VwCatalogoEntidadEducativa.class,
                        detalleReq.getCodigoEntidad()));
        detPla.setIdPlanilla(planillaPago);
        detPla.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        detPla.setFechaInsercion(new Date());
        detPla.setEstadoEliminacion((short) 0);
        lstDetallePlanilla.add(detPla);

        DetalleDocPago detDocPago = detalleReq.getRegCompleto() ? detalleReq.getDetalleDocPagoList().get(0) : null;
        if (detDocPago != null && detDocPago.getIdDetalleDocPago() != null) {

            if (detDocPago.getContratoModif() == 1) {
                detPla.setCantidadActual(detDocPago.getCantidadActual());
                detPla.setMontoActual(detDocPago.getMontoActual());
            } else {
                detPla.setCantidadActual(detPla.getCantidadOriginal());
                detPla.setMontoActual(detPla.getMontoOriginal());
            }

            detPla.setIdDetalleDocPago(detDocPago);
        }
    }

    public void guardarPlanilla() {
        boolean guardarNuevo = (planillaPago.getIdPlanilla() == null);

        //Verificar que los montos (actual y original) sean diferentes para ingreso de los datos del cheque USEFI
        //Esta validacion se realiza unicamente cuando la planilla se esta creando
        if (!showChequeUsefi) {
            for (DetallePlanilla detPla : lstDetallePlanilla) {
                if (detPla.getIdDetalleDocPago().getMontoActual() != null && detPla.getIdDetalleDocPago().getContratoModif() == 1) {
                    showChequeUsefi = (detPla.getIdDetalleDocPago().getMontoActual().compareTo(detPla.getMontoOriginal()) == -1);
                    if (showChequeUsefi) {
                        break;
                    }
                }
            }
        }

        //guardar datos de fecha y numero de cheque en el detalle de planilla
        if (isRubroUniforme && chequeFinanProve.getFechaCheque() != null && chequeFinanProve.getNumeroCheque() != null) {
            lstDetallePlanilla.forEach((detPla) -> {
                detPla.setFechaCheque(chequeFinanProve.getFechaCheque());
                detPla.setNumCheque(chequeFinanProve.getNumeroCheque());
            });
        }

        if (planillaPago.getIdPlanilla() == null) {
            planillaPago.setFechaInsercion(new Date());
            planillaPago.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            planillaPago.setEstadoEliminacion((short) 0);
        } else {
            planillaPago.setFechaModificacion(new Date());
            planillaPago.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        }

        //Habilitar visibilidad de cheques
        if (guardarNuevo) {
            showChequeRenta = isRubroUniforme;
            showPnlCheques = (showChequeEntProv || showChequeRenta || showChequeUsefi);
        }

        planillaPago = pagoProveedoresEJB.guardarPlanillaPago(planillaPago);
        lstDetallePlanilla.forEach((detPla) -> {
            pagoProveedoresEJB.guardarDetallePlanilla(detPla);
        });

        planillaPago = utilEJB.find(PlanillaPago.class, planillaPago.getIdPlanilla());
        lstDetallePlanilla = planillaPago.getDetallePlanillaList();

        if (planillaPago.getIdPlanilla() != null) {
            JsfUtil.mensajeInformacion("Datos almacenados satisfactoriamente.");

            if (guardarNuevo) {
                indexTab = 1;
                JsfUtil.mensajeAlerta("Debe de completar el registro de la información de los Cheques.");
                PrimeFaces.current().ajax().update("tabView");
            }

            //Persistiendo los datos de los cheques
            guardarCheques();
            PrimeFaces.current().ajax().update("tbDetallePlanilla");
        } else {
            JsfUtil.mensajeError("Ocurrio un error en la operación.");

        }
    }

    /**
     * Almacenar datos de los cheques necesarios
     */
    private void guardarCheques() {
        planillaPago = utilEJB.find(PlanillaPago.class,
                planillaPago.getIdPlanilla());
        BigDecimal mRenta = BigDecimal.ZERO;
        BigDecimal montoTotalActual;

        if (isRubroUniforme) {
            for (DetallePlanilla detPla : lstDetallePlanilla) {
                if (detPla.getIdDetalleDocPago().getMontoRenta() != null) {
                    mRenta = mRenta.add(detPla.getIdDetalleDocPago().getMontoRenta());
                }
            }
            montoTotalActual = getMontoActualTotal().add(mRenta.negate());
        } else {
            montoTotalActual = getMontoActualTotal();
        }

        if (showChequeEntProv) {
            chequeFinanProve.setMontoCheque(montoTotalActual);
            if (chequeFinanProve.getIdPlanillaCheque() == null) {
                chequeFinanProve.setaFavorDe((short) (idTipoPlanilla == 1 ? 3 : 0));
                chequeFinanProve.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                chequeFinanProve.setFechaInsercion(new Date());
                chequeFinanProve.setEstadoEliminacion((short) 0);
                chequeFinanProve.setTransferencia((short) 0);
                chequeFinanProve.setIdPlanilla(planillaPago);
            } else {
                chequeFinanProve.setTransferencia(tipoPagoEntFinanciera ? 1 : (short) 0);
                chequeFinanProve.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                chequeFinanProve.setFechaModificacion(new Date());
            }

            chequeFinanProve = pagoProveedoresEJB.guardarPlanillaPagoCheque(chequeFinanProve);
        }

        if (showChequeUsefi) {
            chequeUsefi.setMontoCheque(getMontoOriginalTotal().add(montoTotalActual.negate()).add(mRenta.negate()));
            if (chequeUsefi.getIdPlanillaCheque() == null) {
                chequeUsefi.setaFavorDe((short) 1);
                chequeUsefi.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                chequeUsefi.setFechaInsercion(new Date());
                chequeUsefi.setEstadoEliminacion((short) 0);
                chequeUsefi.setIdPlanilla(planillaPago);
            } else {
                chequeUsefi.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                chequeUsefi.setFechaModificacion(new Date());
            }
            chequeUsefi = pagoProveedoresEJB.guardarPlanillaPagoCheque(chequeUsefi);
        }

        if (showChequeRenta) {
            if (chequeRenta.getIdPlanillaCheque() == null) {
                chequeRenta.setaFavorDe((short) 2);
                chequeRenta.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                chequeRenta.setFechaInsercion(new Date());
                chequeRenta.setEstadoEliminacion((short) 0);
                chequeRenta.setIdPlanilla(planillaPago);
            } else {
                chequeRenta.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                chequeRenta.setFechaModificacion(new Date());
            }

            chequeRenta.setMontoCheque(mRenta);
            chequeRenta = pagoProveedoresEJB.guardarPlanillaPagoCheque(chequeRenta);
        }
    }

    public void guardarReintegro() {
        if (reintegroRequerimiento.getIdReintegro() == null) {

        } else if (reintegroRequerimiento.getMontoCheque().compareTo(montoReintegro) != 0) {
            reintegroRequerimiento.setFechaModificacion(new Date());
            reintegroRequerimiento.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
            reintegroRequerimiento.setMontoCheque(montoReintegro);
        }

        pagoProveedoresEJB.guardarReintegro(reintegroRequerimiento);
        if (reintegroRequerimiento.getIdReintegro() != null) {
            JsfUtil.mensajeInformacion("Datos almacenados correctamente");
        } else {
            JsfUtil.mensajeError("Ha ocurrido un error al momento de completar la operación.");
        }
    }

    public void editEdtDetPlanilla() {
        Boolean montoValidado = true;
        detPlanilla.setIdBanco(proveedorEJB.getLstBancos().get(0));
        detPlanilla.setCheque(cheque ? (short) 1 : 0);
        detPlanilla.getIdDetalleDocPago().setContratoModif((short) (contratoModificado ? 1 : 0));
        if (contratoModificado) {
            montoValidado = (detPlanilla.getMontoActual().compareTo(detPlanilla.getIdDetalleDocPago().getMontoActual()) != -1);
            if (montoValidado) {
                detPlanilla.setCantidadActual(detPlanilla.getIdDetalleDocPago().getCantidadActual());
                detPlanilla.setMontoActual(detPlanilla.getIdDetalleDocPago().getMontoActual());
            } else {
                JsfUtil.mensajeAlerta("El monto actual no puede recuperarProcesoar al monto original!");
            }
        } else {
            detPlanilla.setCantidadActual(detPlanilla.getCantidadOriginal());
            detPlanilla.setMontoActual(detPlanilla.getMontoOriginal());

        }

        if (!montoValidado) {
            if (showChequeEntProv) {
                detPlanilla.setCheque((short) 1);
                detPlanilla.setNumCheque(chequeFinanProve.getNumeroCheque());
                detPlanilla.setMontoCheque(chequeFinanProve.getMontoCheque());
                detPlanilla.setFechaCheque(chequeFinanProve.getFechaCheque());
            }
            detPlanilla.setCheque(cheque ? (short) 1 : 0);
            contratoModificado = false;
            cheque = false;
            dlgEdtDetPlanilla = false;
        }
    }

    public void editEdtDetDocPago() {
        String msj = "";
        boolean montoValidado = true;
        boolean correcto = false;
        if (contratoExtinguido) {
            correcto = pagoProveedoresEJB.isDetalleRequerimeintoEnPlanilla(detalleRequerimiento.getIdDetRequerimiento());
            if (correcto) {
                JsfUtil.mensajeAlerta("No se puede registrar la extinsión de este contrato debido a que esta asociado a una planilla de pago.");
            } else {
                correcto = true;
                detalleRequerimiento.setActivo((short) 1);
                utilEJB.updateEntity(detalleRequerimiento);
                PrimeFaces.current().executeScript("PF('dlgEdtDetDocPago').hide();");
                PrimeFaces.current().ajax().update("tblDetRequerimiento");
            }
        } else if (detalleRequerimiento.getActivo() == 1) {
            detalleRequerimiento.setActivo((short) 0);
            utilEJB.updateEntity(detalleRequerimiento);
            PrimeFaces.current().ajax().update("tblDetRequerimiento");
        }

        if (!correcto) {
            if (detalleDocPago.getNoDocPago().trim().isEmpty()) {
                msj += " - El número de documento.<br/>";
            }
            if (detalleDocPago.getFechaDocPago() == null) {
                msj += " - La fecha de emisión del documento.<br/>";
            }

            if (contratoModificado) {
                if (detalleDocPago.getNoResModificativa().trim().isEmpty()) {
                    msj += " - El No Resolución modificativa.<br/>";
                }
                if (detalleDocPago.getFechaModificativa() == null) {
                    msj += " - Fecha de Resolución.<br/>";
                }
                if (detalleDocPago.getCantidadActual() == null || (detalleDocPago.getCantidadActual() != null && detalleDocPago.getCantidadActual().intValue() < 0)) {
                    msj += " - Cantidad actual del contrato.<br/>";
                }
                if (detalleDocPago.getMontoActual() == null || (detalleDocPago.getMontoActual() != null && detalleDocPago.getMontoActual().intValue() < 0)) {
                    msj += " - Monto ($) actual del contrato.<br/>";
                }
            }

            if (msj.isEmpty()) {
                //validar, si existe modificación, que el monto actual no recuperarProcesoe el monto original del contrato
                if (contratoModificado) {
                    if (detalleDocPago.getCantidadActual() != null && detalleDocPago.getMontoActual() != null) {
                        montoValidado = pagoProveedoresEJB.validarMontoRequerido(codigoEntidad,
                                detalleDocPago.getIdDetRequerimiento().getIdRequerimiento().getIdRequerimiento(),
                                detalleDocPago.getIdDetRequerimiento().getIdDetRequerimiento(),
                                detalleDocPago.getMontoActual());
                        msj = montoValidado ? "" : "El monto actual no puede recuperarProcesoar al monto original!";
                    } else {
                        msj = "La cantidad actual o monto actual deben de tener un valor mayor que 0";
                    }
                }
                if (montoValidado) {
                    detalleDocPago.setContratoModif(contratoModificado ? (short) 1 : (short) 0);

                    if (detalleDocPago.getIdDetalleDocPago() == null) {
                        detalleDocPago.setFechaInsercion(new Date());
                        detalleDocPago.setEstadoEliminacion((short) 0);
                        detalleDocPago.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                    } else {
                        detalleDocPago.setFechaModificacion(new Date());
                        detalleDocPago.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                    }

                    pagoProveedoresEJB.guardarDetalleDocPago(detalleDocPago);

                    contratoModificado = false;
                    dlgEdtDetDocPago = false;

                    if (!numeroRequerimiento.trim().isEmpty()) {
                        requerimientoFondos = utilEJB.find(RequerimientoFondos.class,
                                detalleRequerimiento.getIdRequerimiento().getIdRequerimiento());
                    } else if (!codigoEntidad.trim().isEmpty()) {
                        lstDetalleRequerimiento = proveedorEJB.getLstDetalleReqByCodEntidadAndProceso(codigoEntidad,
                                getRecuperarProceso().getProcesoAdquisicion(),
                                VarSession.isVariableSession("departamentoUsuario") ? getRecuperarProceso().getDepartamento() : null,
                                numeroRequerimiento.isEmpty() ? null : numeroRequerimiento);
                    }

                    PrimeFaces.current().executeScript("PF('dlgEdtDetDocPago').hide();");
                    PrimeFaces.current().ajax().update("tblDetRequerimiento");
                    JsfUtil.mensajeInsert();
                } else {
                    JsfUtil.mensajeAlerta(msj);
                }
            } else {
                msj = "Los campos siguientes son requeridos:<br/>" + msj;
                JsfUtil.mensajeAlerta(msj);
            }
        }
    }

    public void calculoDeRenta() {
        if (renderMontoRenta) {
            BigDecimal montoTotalContrato = BigDecimal.ZERO;
            BigDecimal mRenta = BigDecimal.ZERO;

            //CALCULA DE LA RENTA
            if (contratoModificado) {
                if (detalleDocPago.getMontoActual() != null) {
                    montoTotalContrato = detalleDocPago.getMontoActual();
                }
            } else {
                detalleDocPago.setMontoActual(null);
                montoTotalContrato = detalleDocPago.getIdDetRequerimiento().getMontoTotal();
            }
            switch (detalleDocPago.getIdTipoDocPago()) {
                case 1://FACTURA :: RENTA = (MONTO_TOTAL/1.13) * 0.10
                    mRenta = (montoTotalContrato.divide(new BigDecimal(1.13), 2, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(0.10)).setScale(2, RoundingMode.HALF_DOWN);
                    break;
                case 2://RECIBO :: RENTA = MONTO_TOTAL * 0.10
                    mRenta = montoTotalContrato.multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.HALF_DOWN);
                    break;
            }

            switch (ajusteRenta) {
                case 0:
                    break;
                case 1:
                    mRenta = mRenta.add(new BigDecimal(0.01).negate());
                    break;
                case 2:
                    mRenta = mRenta.add(new BigDecimal(0.01));
                    break;
            }

            detalleDocPago.setMontoRenta(mRenta);
        } else {
            detalleDocPago.setMontoRenta(BigDecimal.ZERO);
        }
    }

    public BigDecimal getMontoCeSeleccionados() {
        BigDecimal monto = BigDecimal.ZERO;
        for (DetalleRequerimiento detalleReq : lstDetalleRequerimientoSeleccionado) {
            monto = monto.add(detalleReq.getMontoTotal());
        }

        return monto;
    }

    public void nuevoPlanilla() {
        inicializacionVariables(true);
        planillaPago.setEstadoEliminacion((short) 0);
        planillaPago.setFechaInsercion(new Date());
        planillaPago.setIdRequerimiento(requerimientoFondos);
        planillaPago.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
    }

    public void editarPlanilla() {
        inicializacionVariables(false);
    }

    private void inicializacionVariables(Boolean valor) {
        seleccionRequerimiento = valor;
        seleccionPlanilla = !valor;

        idTipoPlanilla = 0;

        planilla = false;
        filtro = true;
        showPnlCheques = false;
        showChequeUsefi = false;
        showChequeRenta = false;
        showChequeEntProv = false;
        dlgDetallePlanilla = false;
        contratoModificado = false;

        requerimientoFondos = new RequerimientoFondos();
        planillaPago = new PlanillaPago();
        chequeFinanProve = new PlanillaPagoCheque();
        chequeUsefi = new PlanillaPagoCheque();
        chequeRenta = new PlanillaPagoCheque();
        detPlanilla = new DetallePlanilla();
        entidadFinanciera = new EntidadFinanciera();

        proveedor = new DatosProveDto();

        idRubro = BigDecimal.ZERO;
        idReq = BigDecimal.ZERO;
        lstRequerimientoFondos.clear();
        lstPlanillas.clear();
        lstDetalleRequerimiento.clear();
        lstEntFinRequerimiento.clear();
        lstProveedores.clear();
        lstTipoDocImp.clear();
        lstDetalleRequerimientoSeleccionado.clear();
    }

    public void selectRequerimiento() {
        requerimientoFondos = utilEJB.find(RequerimientoFondos.class,
                idReq); //recuperacion del requerimiento de fondos

        dlgShowTipoPlanilla = false;
        dlgShowEntidadesFinancieras = false;
        lstEntFinRequerimiento.clear(); //limpiando listado de entidades financieras del requerimiento previamente seleccionado
        lstProveedores.clear(); //limpiando listado de proveedores del requerimiento previamente seleccionado

        showChequeEntProv = (requerimientoFondos.getCredito().intValue() == 1);
        if (showChequeEntProv) {
            idTipoPlanilla = 3;
            List<String> lstNombre = proveedorEJB.getEntidadesPorRequerimiento(requerimientoFondos.getIdRequerimiento());

            if (!lstNombre.isEmpty()) {
                //si el requerimiento es de credito, se desplegan las entidades financieras asociados a dicho requerimiento
                lstEntFinRequerimiento = creditosEJB.findEntidadFinancieraEntitiesByName(proveedorEJB.getEntidadesPorRequerimiento(requerimientoFondos.getIdRequerimiento()));
                dlgShowEntidadesFinancieras = true;
            } else {
                JsfUtil.mensajeInformacion("Ya se ha completado el pago de este requerimiento.");
            }
        } else {
            dlgShowTipoPlanilla = true;
        }
    }

    private void recuperarContratosByEntidadFinanciera(BigDecimal idRequerimiento, String nombreEntFinan) {
        lstDetalleRequerimiento = pagoProveedoresEJB.getDetRequerimientoPendienteByEntFinan(idRequerimiento, nombreEntFinan);
    }

    private void recuperarContratosByProveedor(BigDecimal idRequerimiento, String numeroNit) {
        lstDetalleRequerimiento = pagoProveedoresEJB.getLstProveedorByIdRequerimiento(idRequerimiento, numeroNit);
    }

    private void recuperarContratosByIdRequerimiento(BigDecimal idRequerimiento) {
        lstDetalleRequerimiento = pagoProveedoresEJB.getDetRequerimientoPendiente(idRequerimiento);
    }

    public void cerrarDlgTipoPlanilla() {
        PrimeFaces.current().executeScript("PF('dlgTipoPlanilla').hide()");
        switch (idTipoPlanilla) {
            case 1: //Planilla con un solo proveedor
                showDlgSeleccionProveedor();
                break;
            case 2: //Planilla con más de 2 proveedores
                showChequeEntProv = false;
                dlgShowSeleccionProveedor = false;
                crearPlanillaDePago();
                break;
        }
    }

    private void showDlgSeleccionProveedor() {
        showChequeEntProv = true;
        dlgShowSeleccionProveedor = true;
        //Se recuperar el listado de proveedores que conforman el requerimiento seleccionado y que estan pendientes de 
        //ser asociados a una planilla de pago
        lstProveedores = pagoProveedoresEJB.getProveedoresPorIdRequerimiento(idReq);
    }

    public void cerrarDlgSeleccioneProveedor() {
        if (proveedor == null || proveedor.getNumeroNit() == null || proveedor.getNumeroNit().isEmpty()) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proveedor");
        } else {
            empresa = proveedorEJB.findEmpresaByNit(proveedor.getNumeroNit(), true);
            recuperarContratosByProveedor(idReq, proveedor.getNumeroNit());
            mostrarEditDePlanilla();
        }
    }

    public void crearPlanillaDePago() {
        showPnlCheques = false;
        //validacion de requerimiento con credito y seleccion de entidad financiera
        if (showChequeEntProv && entidadFinanciera.getCodEntFinanciera() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar una entidad financiera");
        } else {
            mostrarEditDePlanilla();
            if (showChequeEntProv) {
                recuperarContratosByEntidadFinanciera(requerimientoFondos.getIdRequerimiento(), entidadFinanciera.getNombreEntFinan());
            } else {
                recuperarContratosByIdRequerimiento(requerimientoFondos.getIdRequerimiento());
            }
            PrimeFaces.current().executeScript("PF('dlgEntidadesFinancieras').hide()");
        }
    }

    private void mostrarEditDePlanilla() {
        showPnlCheques = true;
        seleccionPlanilla = false;
        seleccionRequerimiento = false;
        filtro = false;
        planilla = true;
        detallePlanilla = true;
        planillaPago.setIdRequerimiento(requerimientoFondos);
        planillaPago.setIdTipoPlanilla((short) idTipoPlanilla);
        planillaPago.setIdEstadoPlanilla((short) 1);
    }

    public void selectPlanilla() {
        seleccionPlanilla = false;
        seleccionRequerimiento = false;
        planilla = true;
        detallePlanilla = true;
        filtro = false;
        requerimientoFondos = planillaPago.getIdRequerimiento();
        idTipoPlanilla = planillaPago.getIdTipoPlanilla().intValue();
        lstDetallePlanilla = planillaPago.getDetallePlanillaList();

        switch (planillaPago.getIdTipoPlanilla()) {
            case 1:
                showChequeEntProv = true;
                if (lstDetallePlanilla.isEmpty()) {
                    showDlgSeleccionProveedor();
                    PrimeFaces.current().ajax().update("dlgSeleccionProveedor");
                } else {
                    empresa = proveedorEJB.findEmpresaByNit(lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getNumeroNit(), true);
                    recuperarContratosByProveedor(requerimientoFondos.getIdRequerimiento(), lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getNumeroNit());
                }
                chequeFinanProve = cheque(3); //recuperar cheque para proveedor

                //Si la planilla seleccionada fue creada en el proceso anterior, se debe de recuperar el dato del cheque
                if (chequeFinanProve.getIdPlanillaCheque() == null) {
                    chequeFinanProve.setMontoCheque(BigDecimal.ZERO);
                    for (DetallePlanilla detPla : lstDetallePlanilla) {
                        chequeFinanProve.setNumeroCheque(detPla.getNumCheque());
                        chequeFinanProve.setFechaCheque(detPla.getFechaCheque());
                        if (detPla.getMontoCheque() != null) {
                            chequeFinanProve.setMontoCheque(chequeFinanProve.getMontoCheque().add(detPla.getMontoCheque()));
                        }
                    }
                }
                tipoPagoEntFinanciera = (chequeFinanProve.getTransferencia() == 1);
                break;
            case 2:
                recuperarContratosByIdRequerimiento(requerimientoFondos.getIdRequerimiento());
                break;
            case 3:
                showChequeEntProv = (requerimientoFondos.getCredito() == 1);
                if (!lstDetallePlanilla.isEmpty()) {
                    nombreEntFinanciera = lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getNombreEntFinan();
                    entidadFinanciera = proveedorEJB.getEntidadByNombre(nombreEntFinanciera);
                    recuperarContratosByEntidadFinanciera(requerimientoFondos.getIdRequerimiento(), nombreEntFinanciera);
                }
                chequeFinanProve = cheque(0); //recuperar cheque para financiera
                tipoPagoEntFinanciera = (chequeFinanProve.getTransferencia() == 1);
                break;
        }

        //verificación de reintegros en la planilla seleccionada
        showChequeUsefi = pagoProveedoresEJB.isPlanillaConReintegro(planillaPago.getIdPlanilla());
        if (showChequeUsefi) {
            chequeUsefi = cheque(1);
        }

        if (isRubroUniforme) {
            for (DetallePlanilla detPla : lstDetallePlanilla) {
                showChequeRenta = proveedorEJB.isPersonaNatural(detPla.getIdDetalleDocPago().getIdDetRequerimiento().getNumeroNit());
                if (showChequeRenta) {
                    chequeRenta = cheque(2);
                    break;
                }
            }
        }

        showPnlCheques = !(showChequeEntProv == false && showChequeRenta == false && showChequeUsefi == false);
    }

    /**
     * Metodo que crea el cheque para la financiera, pagaduría USEFI, Renta o
     * Proveedor
     *
     * @param isFinanciera si es 1 devuelve el cheque de la financiera, si es 2
     * el cheque de USEFI, si es 3 cheque de Renta y si es 4 el cheque pera el
     * Proveedor(Planilla de tipo 1)
     */
    private PlanillaPagoCheque cheque(int tipoCheque) {
        PlanillaPagoCheque pagoCheque = pagoProveedoresEJB.getPlanillaPagoCheque(planillaPago, (short) tipoCheque);
        if (pagoCheque.getIdPlanillaCheque() == null) {
            pagoCheque.setEstadoEliminacion((short) 0);
            pagoCheque.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            pagoCheque.setFechaInsercion(new Date());
            pagoCheque.setaFavorDe((short) tipoCheque);
        }
        return pagoCheque;
    }

    public void showDlgDetallePlanilla() {
        dlgDetallePlanilla = true;
        switch (idRubro.intValue()) {
            case 1:
                nombreRubro = "SERVICIOS DE CONFECCION DE UNIFORMES";
                break;
            case 2:
                nombreRubro = "SUMINISTRO DE PAQUETES DE UTILES ESCOLARES";
                break;
            case 3:
                nombreRubro = "PRODUCCION DE ZAPATOS";
                break;
            case 4:
                nombreRubro = "SERVICIOS DE CONFECCION DEL 1er UNIFORMES";
                break;
            case 5:
                nombreRubro = "SERVICIOS DE CONFECCION DEL 2do UNIFORMES";
                break;
        }
        PrimeFaces.current().ajax().update("dlgDetallePlanilla");
    }

    public void closeDlgDetallePlanilla() {
        cheque = false;
        contratoModificado = false;
        dlgDetallePlanilla = false;
        lstDetalleRequerimientoSeleccionado.clear();
    }

    public void showDlgEdtDetPlanilla() {
        dlgEdtDetPlanilla = true;
        contratoModificado = false;

        if (detPlanilla.getIdDetalleDocPago().getIdDetalleDocPago() != null) {
            contratoModificado = detPlanilla.getIdDetalleDocPago().getContratoModif() == 1;
            detPlanilla.setMontoCheque(detPlanilla.getIdDetalleDocPago().getMontoActual());
            cheque = (detPlanilla.getCheque() != null && detPlanilla.getCheque() == 1);
            pagarCheque();
            PrimeFaces.current().ajax().update("dlgEdtDetPlanilla");
            PrimeFaces.current().ajax().update("pngEdit");
        }
    }

    public void closeDlgEdtDetPlanilla() {
        contratoModificado = false;
        cheque = false;
        dlgEdtDetPlanilla = false;
    }

    //docPago
    public void showDlgEdtDetDocPago() {
        contratoSinActaRecepcion = pagoProveedoresEJB.contratoConActaDeRecepcion(new BigDecimal(detalleRequerimiento.getIdContrato()));
        if (contratoSinActaRecepcion) {
            ajusteRenta = 0;
            contratoModificado = false;
            contratoExtinguido = (detalleRequerimiento.getActivo() == 1);
            renderMontoRenta = false;

            detalleDocPago = proveedorEJB.getDetalleDocPago(detalleRequerimiento);

            if (detalleDocPago.getIdDetalleDocPago() == null) {
                detalleDocPago.setIdDetRequerimiento(detalleRequerimiento);
                detalleDocPago.setIdTipoDocPago(1);
            } else {
                contratoModificado = (detalleDocPago.getContratoModif() == (short) 1);
            }
            isRubroUniforme = (detalleRequerimiento.getIdRequerimiento().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres().intValue() == 1)
                    || (detalleRequerimiento.getIdRequerimiento().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres().intValue() == 4)
                    || (detalleRequerimiento.getIdRequerimiento().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres().intValue() == 5);

            //verificar si ha existido modificativa al contrato original
            if (!contratoModificado) {
                contratoModificado = pagoProveedoresEJB.isContratoConModificativa(new BigDecimal(detalleRequerimiento.getIdContrato()));
            }
            if (contratoModificado) {
                ResolucionesModificativas resModif = pagoProveedoresEJB.getUltimaModificativa(new BigDecimal(detalleRequerimiento.getIdContrato()));

                if (resModif != null) {

                    montoContrato = resModif.getIdContrato().getIdResolucionAdj().getIdParticipante().getMonto();
                    cantidadContrato = resModif.getIdContrato().getIdResolucionAdj().getIdParticipante().getCantidad();

                    if (resModif.getIdEstadoReserva().intValue() == 1) {
                        JsfUtil.mensajeAlerta("Este contrato tiene una modificativa en estado de DIGITACIÓN");
                    } else {
                        detalleDocPago.setFechaModificativa(resModif.getFechaResolucion());
                        detalleDocPago.setCantidadActual(BigInteger.ZERO);
                        detalleDocPago.setMontoActual(BigDecimal.ZERO);
                        for (DetalleModificativa detalle : resModif.getDetalleModificativaList()) {
                            if (detalle.getEstadoEliminacion() == 0) {
                                if (detalle.getIdProducto().intValue() != 1) {
                                    detalleDocPago.setCantidadActual(detalleDocPago.getCantidadActual().add(new BigInteger(detalle.getCantidadNew().toString())));
                                }
                                detalleDocPago.setMontoActual(detalleDocPago.getMontoActual().add(detalle.getPrecioUnitarioNew().multiply(new BigDecimal(detalle.getCantidadNew()))));
                            }
                        }
                    }
                }
            } else {
                ContratosOrdenesCompras contrato = utilEJB.find(ContratosOrdenesCompras.class, new BigDecimal(detalleRequerimiento.getIdContrato()));
                montoContrato = contrato.getIdResolucionAdj().getIdParticipante().getMonto();
                cantidadContrato = contrato.getIdResolucionAdj().getIdParticipante().getCantidad();
            }

            //verificación del tipo de rubro y personeria natural para determinar si aplica o no el calculo de renta
            renderMontoRenta = (isRubroUniforme
                    && proveedorEJB.isPersonaNatural(detalleDocPago.getIdDetRequerimiento().getNumeroNit()));
            //Si aplica, se realiza el calculo del monto de renta
            calculoDeRenta();
        }
        dlgEdtDetDocPago = true;
    }

    public void closeDlgEdtDetDocPago() {
        contratoModificado = false;
        contratoExtinguido = false;
        dlgEdtDetDocPago = false;
        detalleDocPago = new DetalleDocPago();
        ajusteRenta = 0;
        buscarRequerimiento();
    }

    public void eliminarDetalle() {
        if (detPlanilla != null) {
            if (detPlanilla.getEstadoEliminacion().intValue() == 0) {
                if (detPlanilla.getIdDetallePlanilla() != null) {
                    detPlanilla.setEstadoEliminacion((short) 1);
                } else {
                    lstDetallePlanilla.remove(rowEdit);
                }
            } else {
                detPlanilla.setEstadoEliminacion((short) 0);
            }

            detPlanilla = new DetallePlanilla();
        } else {
            JsfUtil.mensajeAlerta("Debe seleccionar un detalle para poder eliminarlo.");
        }
    }

    public void eliminarPlanilla() {
        pagoProveedoresEJB.eliminarPlanilla(idPlanilla, VarSession.getVariableSessionUsuario());
        buscarPlanillas();
    }

    private void documentosAImprimir() {
        lstTipoDocImp.clear();
        lstTipoDocImp.add(new SelectItem(1, "Planilla de Pago"));
        lstTipoDocImp.add(new SelectItem(2, "Matriz de Pago"));
        lstTipoDocImp.add(new SelectItem(3, "Formato de Entrega Cheque"));
        lstTipoDocImp.add(new SelectItem(4, "Planilla de Reintegro"));
        switch (idRubro.intValue()) {
            case 1:
            case 4:
            case 5:
                lstTipoDocImp.add(new SelectItem(5, "Planilla de Renta"));
                break;
        }
    }

    public void reiniciarVisibilidadCheques() {
        showChequeEntProv = false;
        showChequeRenta = false;
        showChequeUsefi = false;
        showPnlCheques = false;
    }

    public void buscarRequerimientosImp() {
        detalleProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        if (idRubro != null) {
            lstRequerimientoFondos = proveedorEJB.getLstRequerimientos(codigoDepartamento, detalleProcesoAdq.getIdDetProcesoAdq());
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un rubro de adquisición.");
        }
    }

    public void generarRptLiquidacion() {
        anho = getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho().substring(2);
        lstEmailProveeCredito = pagoProveedoresEJB.getDatosRptLiquidacion(codigoDepartamento, anho, getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq(), codigoEntidad);
    }

    public void buscarRequerimientos() {
        if (idRubro != null && idRubro.intValue() > 0) {
            buscarReuerimientoqOrPlanilla();
            lstResumenRequerimiento = proveedorEJB.getLstResumenRequerimiento(codigoDepartamento, detalleProcesoAdq.getIdDetProcesoAdq());
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un rubro de adquisición.");
        }
    }

    public void buscarReintegro() {
        if (idRubro != null) {
            if (idReq != null) {
                lstProveedores = pagoProveedoresEJB.getDatosRptReintegroByIdReq(idReq);
                if (lstProveedores.isEmpty()) {
                    JsfUtil.mensajeInformacion("El requerimiento seleccionado no tienen reintegro de fondos");

                } else {
                    requerimientoFondos = utilEJB.find(RequerimientoFondos.class, idReq);
                    montoReintegro = BigDecimal.ZERO;
                    lstProveedores.forEach((datosProveDto) -> {
                        montoReintegro = montoReintegro.add(datosProveDto.getMontoReintegro());
                    });
                    reintegroRequerimiento = pagoProveedoresEJB.getReintegroByIdReq(idReq);
                    if (reintegroRequerimiento.getIdReintegro() == null) {
                        reintegroRequerimiento.setEstadoEliminacion((short) 0);
                        reintegroRequerimiento.setFechaInsercion(new Date());
                        reintegroRequerimiento.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                        reintegroRequerimiento.setMontoCheque(montoReintegro);
                        reintegroRequerimiento.setIdRequerimiento(requerimientoFondos);
                    } else if (reintegroRequerimiento.getMontoCheque().compareTo(montoReintegro) != 0) {
                        reintegroRequerimiento.setMontoCheque(montoReintegro);
                    }
                }
            } else {
                JsfUtil.mensajeInformacion("Debe de seleccionar un requerimiento.");
            }
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un rubro de adquisición.");
        }
    }

    public void clearRubroAndRequerimiento() {
        idRubro = BigDecimal.ZERO;
        idReq = BigDecimal.ZERO;
    }

    public void recuperarRequerimientos() {
        detalleProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        anho = detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho().substring(2);
        isRubroUniforme = idRubro.intValue() == 1 || idRubro.intValue() == 4 || idRubro.intValue() == 5;
        lstRequerimientoFondos = proveedorEJB.getLstRequerimientos(codigoDepartamento, detalleProcesoAdq.getIdDetProcesoAdq());
    }

    public void buscarPlanillas() {
        if (idRubro != null) {
            if (idReq != null) {
                buscarReuerimientoqOrPlanilla();
                lstPlanillas = proveedorEJB.getLstPlanillaPagos(idReq);
                if (lstPlanillas.isEmpty()) {
                    JsfUtil.mensajeInformacion("El requerimiento seleccionado no tienen planillas registradas");
                }
                documentosAImprimir();
            } else {
                JsfUtil.mensajeInformacion("Debe de seleccionar un requerimiento.");
            }
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un rubro de adquisición.");
        }
    }

    private void buscarReuerimientoqOrPlanilla() {
        reiniciarVisibilidadCheques();
        isRubroUniforme = ((idRubro.intValue() == 1) || (idRubro.intValue() == 4) || (idRubro.intValue() == 5));
        detalleProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
    }

    public void buscarRequerimiento() {
        if (!numeroRequerimiento.trim().isEmpty()) {
            requerimientoFondos = proveedorEJB.getRequerimientoByNumero(numeroRequerimiento, (VarSession.isVariableSession("departamentoUsuario") ? getRecuperarProceso().getDepartamento() : null), getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq());
            if (requerimientoFondos == null) {
                JsfUtil.mensajeInformacion("No se encontro el requerimiento con número: " + numeroRequerimiento);
            } else if (!codigoEntidad.trim().isEmpty()) {
                lstDetalleRequerimiento = proveedorEJB.getLstDetalleReqByCodEntidadAndProceso(codigoEntidad, getRecuperarProceso().getProcesoAdquisicion(),
                        VarSession.isVariableSession("departamentoUsuario") ? getRecuperarProceso().getDepartamento() : null,
                        numeroRequerimiento);
            } else {
                lstDetalleRequerimiento = requerimientoFondos.getDetalleRequerimientoList();
            }
        } else if (!codigoEntidad.trim().isEmpty()) {
            lstDetalleRequerimiento = proveedorEJB.getLstDetalleReqByCodEntidadAndProceso(codigoEntidad, getRecuperarProceso().getProcesoAdquisicion(), VarSession.isVariableSession("departamentoUsuario") ? getRecuperarProceso().getDepartamento() : null, null);
        }

        if (lstDetalleRequerimiento.isEmpty()) {
            JsfUtil.mensajeAlerta("No se encontrarón resultados.");
        }
    }

    public void imprimirRequerimiento() {
        try {
            List<JasperPrint> jasperPrintList = new ArrayList();

            jasperPrintList.add(imprimirRpt(requerimientoFondos, JsfUtil.getNombreDepartamentoByCodigo(codigoDepartamento), "rptRequerimientoFondos.jasper", "requerimientoFondosDet"));
            jasperPrintList.add(imprimirRpt(requerimientoFondos, JsfUtil.getNombreDepartamentoByCodigo(codigoDepartamento), "rptResumenRequerimiento.jasper", "resumenRequerimientoFondos"));

            Reportes.generarReporte(jasperPrintList, "requerimiento_" + codigoDepartamento.replace(" ", ""));
        } catch (IOException | JRException ex) {
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.WARNING, "Error en la generacion del requerimiento {0}", requerimientoFondos.getFormatoRequerimiento());
        }
    }

    public void impRptPagoProve() {
        try {
            List<JasperPrint> jasperPrintList = new ArrayList();

            jasperPrintList.add(imprimirRptPagoProve(JsfUtil.getNombreDepartamentoByCodigo(getRecuperarProceso().getDepartamento())));

            Reportes.generarReporte(jasperPrintList, "rptPagoProve_" + codigoDepartamento.replace(" ", ""));
        } catch (IOException | JRException ex) {
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void impRptReintegro() {
        if (reintegroRequerimiento.getIdReintegro() == null) {
            JsfUtil.mensajeAlerta("Primero debe de guardar los datos del cheque.");
        } else {
            try {
                List<JasperPrint> jasperPrintList = new ArrayList();

                jasperPrintList.add(imprimirRptReintegro(JsfUtil.getNombreDepartamentoByCodigo(getRecuperarProceso().getDepartamento())));

                Reportes.generarReporte(jasperPrintList, "rptReintegro_" + codigoDepartamento.replace(" ", ""));
            } catch (IOException | JRException ex) {
                Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.WARNING, "Error en el reporte de Reintegro {0}", reintegroRequerimiento);
            }
        }
    }

    public void imprimirDocumentos() {
        try {

            boolean tempChequeEntPro = false;
            String rpt = "";
            String pNombreCheque = "";

            List<JasperPrint> jasperPrintList = new ArrayList();
            //artificio para impresion de planillas creadas previo a la tipificación de planillas

            if (planillaPago == null || planillaPago.getIdEstadoPlanilla() == null) {
                Logger.getLogger(PagoProveedoresController.class
                        .getName()).log(Level.INFO, "Error en el estado de la planilla {0}", planillaPago);
            } else if (planillaPago.getIdEstadoPlanilla() == 0) {
                tempChequeEntPro = planillaPago.getIdRequerimiento().getCredito() == 1;
                pNombreCheque = nombreEntFinanciera;
            } else {
                tempChequeEntPro = showChequeEntProv;
                switch (planillaPago.getIdTipoPlanilla()) {
                    case 1:
                        pNombreCheque = lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getRazonSocial();
                        break;
                    case 3:
                        pNombreCheque = nombreEntFinanciera;
                        break;
                    default:
                        pNombreCheque = "";
                        break;
                }
            }

            for (Integer idRpt : tipoDocumentoImp) {
                switch (idRpt) {
                    case 1: //Planilla de Pago
                        rpt = tempChequeEntPro ? "rptTransferenciaCrediCheque" : "rptTransferenciaCheque";
                        break;
                    case 2: //Matriz de Pago
                        rpt = tempChequeEntPro ? "rptMatrizPagoCredito" : "rptMatrizPago";
                        break;
                    case 3: //Formato Entrega de Cheques
                        rpt = tempChequeEntPro ? "rptFormatoEntregaChequeCredito" : "rptFormatoEntregaCheque";
                        break;
                    case 4: //Planilla de Reintegro
                        rpt = "rptFormatoReintegro";
                        break;
                    case 5: //Planilla Renta
                        rpt = "rptTransferenciaRenta";
                        break;
                }
                jasperPrintList.add(imprimirRptPlanilla(rpt, pNombreCheque));
            }
            Reportes.generarReporte(jasperPrintList, "rptsPlanilla-" + planillaPago.getIdPlanilla());
        } catch (IOException | JRException e) {
            if (planillaPago != null & planillaPago.getIdPlanilla() != null) {
                JsfUtil.mensajeError("Ah ocurrido un error en la generacion de los documentos contractuales. Id Planilla: " + planillaPago.getIdPlanilla());
            } else {
                JsfUtil.mensajeError("Ah ocurrido un error en la generacion de los documentos contractuales.");
            }
        }
    }

    public JasperPrint imprimirRptPlanilla(String rpt, String pNombreCheque) {
        JasperPrint jp;
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        HashMap param = new HashMap();
        param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
        param.put("pDepartamento", JsfUtil.getNombreDepartamentoByCodigo(codigoDepartamento));
        param.put("pIdPlanilla", planillaPago.getIdPlanilla().intValue());
        param.put("pAFavorDe", (short) (planillaPago.getIdTipoPlanilla() == 1 ? 3 : 0));
        param.put("pNombreCheque", pNombreCheque);
        jp = reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/reportes/pagoproveedor" + File.separator + rpt + ".jasper"));
        return jp;
    }

    public JasperPrint imprimirRpt(RequerimientoFondos req, String nombreDepartamento, String nombreRpt, String nombrePdf) {
        JasperPrint jp;
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        HashMap param = new HashMap();
        param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
        param.put("pDepartamental", nombreDepartamento);
        param.put("pUniforme", detalleProcesoAdq.getIdDetProcesoAdq() == 25 ? 1 : 0);
        param.put("pIdRequerimiento", req.getIdRequerimiento().intValue());
        param.put("pAnho", "20" + anho);
        jp = reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/reportes/pagoproveedor" + File.separator + nombreRpt));
        return jp;
    }

    public JasperPrint imprimirRptPagoProve(String nombreDepartamento) {
        JasperPrint jp;
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        HashMap param = new HashMap();
        param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
        param.put("pNombreDepartamento", nombreDepartamento);
        param.put("pAnho", "20" + anho);
        param.put("pUsuario", VarSession.getVariableSessionUsuario());
        param.put("pPagadorDepartamental", pagoProveedoresEJB.getNombrePagadorByCodDepa(getRecuperarProceso().getDepartamento()));
        DatosProveDto datos = new DatosProveDto();
        datos.setLstDetalle(lstEmailProveeCredito);
        List<DatosProveDto> lst = new ArrayList();
        lst.add(datos);
        jp = reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/reportes/pagoproveedor/rptPagoProvee.jasper"), lst);
        return jp;
    }

    public JasperPrint imprimirRptReintegro(String nombreDepartamento) {
        JasperPrint jp;
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        HashMap param = new HashMap();
        param.put("pEscudo", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
        param.put("pNombreDepartamento", nombreDepartamento);
        param.put("pAnho", "20" + anho);
        param.put("pUsuario", VarSession.getVariableSessionUsuario());
        param.put("pPagadorDepartamental", pagoProveedoresEJB.getNombrePagadorByCodDepa(getRecuperarProceso().getDepartamento()));

        param.put("pConcepto", requerimientoFondos.getConcepto());
        param.put("pFuenteFinanciamiento", requerimientoFondos.getFuenteFinanciamiento());
        param.put("pFormatoRequerimiento", requerimientoFondos.getFormatoRequerimiento());
        param.put("pNumeroCheque", reintegroRequerimiento.getNumeroCheque());
        param.put("pFechaCheque", reintegroRequerimiento.getFechaCheque());
        param.put("pMontoCheque", reintegroRequerimiento.getMontoCheque());

        DatosProveDto datos = new DatosProveDto();
        datos.setLstDetalle(lstProveedores);
        List<DatosProveDto> lst = new ArrayList();
        lst.add(datos);
        jp = reportesEJB.getRpt(param, Reportes.getPathReporte("sv/gob/mined/apps/reportes/pagoproveedor/rptPagoReintegro.jasper"), lst);
        return jp;
    }

    public void validarDetalleRequerimiento(SelectEvent event) {
        if (!((DetalleRequerimiento) event.getObject()).getRegCompleto()) {
            lstDetalleRequerimientoSeleccionado.remove((DetalleRequerimiento) event.getObject());
            JsfUtil.mensajeAlerta("Este contrato no tiene ingresada la información del documento de pago. Por favor ingresarlo");
        }
    }

    public void buscarEntidadEducativa() {
        if (codigoEntidad.length() == 5) {
            entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);

            if (entidadEducativa == null) {
                JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
            } else {
                if (entidadEducativa.getCodigoDepartamento().getCodigoDepartamento().equals(getRecuperarProceso().getDepartamento())) {

                } else {
                    if (getRecuperarProceso().getDepartamento() != null) {
                        JsfUtil.mensajeAlerta("El codigo del centro escolar no pertenece al departamento " + JsfUtil.getNombreDepartamentoByCodigo(getRecuperarProceso().getDepartamento()) + "<br/>"
                                + "Departamento del CE: " + entidadEducativa.getCodigoEntidad() + " es " + entidadEducativa.getCodigoDepartamento().getNombreDepartamento());
                    }
                }
            }
        } else {
            entidadEducativa = null;
        }
    }

    public void notificacion() {
        if (planillaPago.getIdTipoPlanilla() == 3) {
            emailUnico = pagoProveedoresEJB.getEMailEntidadFinancieraById(lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getCodEntFinanciera());
            lstEmailProveeCredito = pagoProveedoresEJB.getLstNitProveeByIdPlanilla(planillaPago.getIdPlanilla());

            //Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Email Entidad {0}", emailUnico);
            //lstEmailProveeCredito.forEach((datosProveDto) -> { Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Email {0} Proveedor {1} {2}", new String[]{datosProveDto.getCorreoElectronico(), datosProveDto.getRazonSocial(), datosProveDto.getNumeroNit()});});
        } else if (planillaPago.getIdTipoPlanilla() == 1) {
            emailUnico = pagoProveedoresEJB.getEMailProveedorByNit(lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getNumeroNit());
            //Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Email Entidad {0}", emailUnico);
        }

        if (emailUnico == null || emailUnico.isEmpty()) {
            JsfUtil.mensajeAlerta("No se puede enviar la notificación debido a que el proveedor/entidad financiera no posee registro de correo electrónico");
        } else {
            PrimeFaces.current().ajax().update("dlgEMailNotificacion");
        }
    }

    public void enviarCorreos() {
        String correoNotificacionAmbiente = utilEJB.getValorDeParametro("CORREO_NOTIFICACION_AMBIENTE");
        String emailTemp = utilEJB.getValorDeParametro("CORREO_NOTIFICACION");

        if (planillaPago.getIdTipoPlanilla() == null) {
        } else {
            switch (planillaPago.getIdTipoPlanilla()) {
                case 1:
                case 3:
                    //envio de notificacion a Entidad/Proveedor
                    if (correoNotificacionAmbiente.equals("PRODUCCION")) {
                        emailTemp = emailUnico;
                    }

                    eMailEJB.enviarMail("Paquete Escolar - Notificación de Pago ", emailTemp, getMensajeDeNotificacion(planillaPago, lstDetallePlanilla, false), codigoDepartamento);

                    //Si es planilla tipo credito, enviar notificacion a proveedores incluidos en la planilla de pago
                    if (planillaPago.getIdTipoPlanilla() == 3) {
                        for (DatosProveDto datosProveDto : lstEmailProveeCredito) {
                            if (correoNotificacionAmbiente.equals("PRODUCCION")) {
                                emailTemp = datosProveDto.getCorreoElectronico();
                            }
                            if (datosProveDto.getCorreoElectronico() != null && !datosProveDto.getCorreoElectronico().isEmpty()) {
                                eMailEJB.enviarMail("Paquete Escolar - Notificación de Pago ", emailTemp, getMensajeDeNotificacion(planillaPago, pagoProveedoresEJB.getLstDetallePlanillaByIdPlanillaAndNit(planillaPago.getIdPlanilla(), datosProveDto.getNumeroNit()), true), codigoDepartamento);
                            }
                        }
                    }

                    pagoProveedoresEJB.planillaNotificada(planillaPago.getIdPlanilla());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Método que devuelve el mensage a enviar a la entidad financiera o al
     * proveedor
     *
     * @param plaPago
     * @return
     */
    private String getMensajeDeNotificacion(PlanillaPago plaPago, List<DetallePlanilla> lstDetallePlanilla, boolean isProveedores) {
        StringBuilder sb = new StringBuilder("");
        if (isProveedores) {
            sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.cabeceraNotificacion"), lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getRazonSocial()));
        } else {
            if (plaPago.getIdTipoPlanilla() == 3) {
                sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.cabeceraNotificacion"), lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getNombreEntFinan()));
            } else if (plaPago.getIdTipoPlanilla() == 1) {
                sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.cabeceraNotificacion"), lstDetallePlanilla.get(0).getIdDetalleDocPago().getIdDetRequerimiento().getRazonSocial()));
            }
        }

        if (isProveedores) {
            sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.cabeceraMensajeProv"), nombreEntFinanciera));
        } else {
            sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.cabeceraMensaje"), planillaPago.getIdRequerimiento().getFormatoRequerimiento(), JsfUtil.getFormatoNum(planillaPago.getMontoTotal(), false)));
        }
        sb.append(RESOURCE_BUNDLE.getString("pagoprov.email.tablaDetalle.header"));
        lstDetallePlanilla.forEach((detalle) -> {
            sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.tablaDetalle.detalle"), detalle.getCodigoEntidad().getCodigoEntidad(), detalle.getCodigoEntidad().getNombre(), detalle.getIdDetalleDocPago().getIdDetRequerimiento().getNumeroNit(), detalle.getIdDetalleDocPago().getIdDetRequerimiento().getRazonSocial(), JsfUtil.getFormatoNum(detalle.getMontoActual(), false)));
        });
        //agregando fila de totales (unicamente si la planilla de pago contiene más de 1 contrato)
        if (lstDetallePlanilla.size() > 1) {
            sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.tablaDetalle.footer"),
                    JsfUtil.getFormatoNum(getMontoActualTotal(), false)));
        }
        sb.append(RESOURCE_BUNDLE.getString("pagoprov.email.tablaDetalle.fin"));
        sb.append(MessageFormat.format(RESOURCE_BUNDLE.getString("pagoprov.email.finNotificacion"), JsfUtil.getFechaString(planillaPago.getFechaInsercion()), JsfUtil.getNombreDepartamentoByCodigo(getRecuperarProceso().getDepartamento())));

        return sb.toString();
    }

    public void createDonutModel() {
        detalleProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        renderGrafico = true;

        generarResumenPagos();

        donutModel = new DonutChartModel();

        Map<String, Number> circle2 = new LinkedHashMap();
        circle2.put("Contratado", 0);
        circle2.put("Pagado", montoPagado);
        circle2.put("Pendiente", montoPendiente);
        circle2.put("A Reintegrar", montoReintegro);
        donutModel.addCircle(circle2);

        Map<String, Number> circle1 = new LinkedHashMap();
        circle1.put("Contratado", montoTotal);
        circle1.put("Pagado", 0);
        circle1.put("Pendiente", 0);
        circle1.put("A Reintegrar", 0);
        donutModel.addCircle(circle1);

        donutModel.setLegendPosition("e");
        donutModel.setSliceMargin(5);
        donutModel.setShowDataLabels(true);
        donutModel.setDataLabelFormatString("%'.2f");
        donutModel.setDataFormat("value");
        donutModel.setExtender("skinDonut");
        donutModel.setShadow(false);
    }

    private void generarResumenPagos() {
        if (codigoDepartamento.equals("00") && (Integer) VarSession.getVariableSession("idTipoUsuario") == 1) {
            lstResumenPago = serviciosEJB.getResumenPagoJsonByDetProcesoAdq(detalleProcesoAdq.getIdDetProcesoAdq());
        } else {
            lstResumenPago = serviciosEJB.getResumenPagoJsonByDepaAndDetProcesoAdq(codigoDepartamento, detalleProcesoAdq.getIdDetProcesoAdq());
        }

        cantidadCe = cantidadPlanilla = montoTotal = montoPagado = montoPendiente = montoReintegro = montoSaldo = BigDecimal.ZERO;
        lstResumenPago.forEach((resumenPagoJson) -> {
            cantidadCe = cantidadCe.add(resumenPagoJson.getCantidadCe());
            cantidadPlanilla = cantidadPlanilla.add(resumenPagoJson.getCantidadPlanilla());
            montoTotal = montoTotal.add(resumenPagoJson.getMontoTotal());
            montoPagado = montoPagado.add(resumenPagoJson.getMontoPagado());
            montoPendiente = montoPendiente.add(resumenPagoJson.getMontoPendiente());
            montoReintegro = montoReintegro.add(resumenPagoJson.getMontoReintegro());
            montoSaldo = montoSaldo.add(resumenPagoJson.getMontoSaldo());
        });
    }

    public void generarArchivoDePagoFinaciera() {
        if (showChequeEntProv) {
            StringBuilder sb = new StringBuilder();
            Boolean esCorrecto = true;
            chequeFinanProve.setTransferencia((short) 1);
            pagoProveedoresEJB.guardarPlanillaPagoCheque(chequeFinanProve);

            switch (planillaPago.getIdTipoPlanilla()) {
                case 1://un solo proveedor
                    if (empresa.getNumeroCuenta() == null || empresa.getNumeroCuenta().trim().isEmpty()) {
                        JsfUtil.mensajeError("El Proveedor/La Financiera, no tiene registrada la cuenta bancaria para efectuar la transferencia electrónica.");
                        esCorrecto = false;
                    }
                    break;
                case 3://credito
                    if (entidadFinanciera.getNumeroCuenta() == null || entidadFinanciera.getNumeroCuenta().trim().isEmpty()) {
                        JsfUtil.mensajeError("El Proveedor/La Financiera, no tiene registrada la cuenta bancaria para efectuar la transferencia electrónica.");
                        esCorrecto = false;
                    }
                    break;
            }

            if (esCorrecto) {
                try {
                    switch (idTipoPlanilla) {
                        case 1: //un solo proveedor
                            sb.append(empresa.getNumeroCuenta()).append(",").
                                    append(empresa.getRazonSocial()).append(",").
                                    append(" ").append(",").
                                    append(chequeFinanProve.getMontoCheque()).append(",").
                                    append("1").append(",").
                                    append("Abono por pago de contrato de paquete escolar");
                            break;
                        case 3: //entidad financiera
                            sb.append(entidadFinanciera.getNumeroCuenta()).append(";").
                                    append(entidadFinanciera.getNombreEntFinan()).append(";").
                                    append(" ").append(";").
                                    append(chequeFinanProve.getMontoCheque()).append(";").
                                    append("1").append(";").
                                    append("Abono por pago de contrato(s) de paquete(s) escolar(es) asociado(s) a un crédito otorgado");
                            break;
                    }
                    notificacion();
                    UtilFile.downloadFileTextoPlano(sb.toString(), "transferencia-" + planillaPago.getIdPlanilla(), UtilFile.EXTENSION_CSV);
                    Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Archivo: Genera: {0}\n======================================\n{1}", new Object[]{VarSession.getVariableSessionUsuario(), sb.toString()});

                } catch (IOException ex) {
                    Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void buscarLstRentaProve() {
        if (!numeroNit.isEmpty()) {
            empresa = proveedorEJB.findEmpresaByNit(numeroNit, true);
            lstProveedores = pagoProveedoresEJB.getLstDatosProveDto(anho, numeroNit, null, VarSession.getVariableSessionUsuario());
        } else {
            JsfUtil.mensajeInformacion("Debe de ingresar un NIT o un requerimiento de fondos");
        }
        if (lstProveedores.isEmpty()) {
            JsfUtil.mensajeInformacion("No se encontraro información segun los datos de busqueda");
        } else {
            montoTotal = BigDecimal.ZERO;
            montoSujetoRenta = BigDecimal.ZERO;
            montoRenta = BigDecimal.ZERO;
            lstProveedores.forEach((rentaProveDto) -> {
                montoTotal = montoTotal.add(rentaProveDto.getMontoActual());
                montoSujetoRenta = montoSujetoRenta.add(rentaProveDto.getMontoRetencion());
                montoRenta = montoRenta.add(rentaProveDto.getMontoRenta());
            });
        }
    }

    public void generarRptRentaMensual() {
        if (anho == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un año");
        } else if (idMes == 0) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un mes");
        } else {
            if (numeroRequerimiento.isEmpty()) {
                lstProveedores = pagoProveedoresEJB.getDatosRptRentaMensual(codigoDepartamento, idMes, Integer.parseInt(anho), VarSession.getVariableSessionUsuario());
            } else {
                lstProveedores = pagoProveedoresEJB.getDatosRptRentaMensualByRequerimiento(codigoDepartamento, idMes, Integer.parseInt(anho), numeroRequerimiento, VarSession.getVariableSessionUsuario());
            }
            if (lstProveedores.isEmpty()) {
                JsfUtil.mensajeInformacion("No se encontraron datos");

            } else {
                RptExcel.generarRptRentaMensual(lstProveedores);
                //RptExcel.generarRptGenerico(lstProveedores, "rentaMensual", 2);
            }
        }
    }

    public void generarArchivoF910() {
        anho = getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho();
        lstProveedores = pagoProveedoresEJB.getDatosF910(codigoDepartamento, Integer.parseInt(anho));
        if (lstProveedores.isEmpty()) {
            JsfUtil.mensajeInformacion("No se existen datos para el año seleccionado");

        } else {
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Generacion de archivo F910 ver. WEB");
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Departamento: {0}", codigoDepartamento);

            RptExcel.generarRptRentaAnual(lstProveedores, anho);
        }
    }

    public void generarArchivoF14() {
        lstF14 = pagoProveedoresEJB.getDatosF14(codigoDepartamento,
                (idMes < 10 ? "0".concat(String.valueOf(idMes)) : String.valueOf(idMes)).concat(getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho()));
        if (lstF14.isEmpty()) {
            JsfUtil.mensajeInformacion("No se existen datos para el año seleccionado");

        } else {
            try {
                Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Generacion de archivo F910 ver. WEB");
                Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.INFO, "Departamento: {0}", codigoDepartamento);

                StringBuilder sb = new StringBuilder();

                for (InformeF14Dto dato : lstF14) {
                    sb = sb.append(dato.getColA()).append(";").append(dato.getColB()).append(";").append(dato.getColC()).append(";").append(dato.getColD()).append(";").append(dato.getColE()).
                            append(";").append(dato.getColF()).append(";").append(dato.getColG()).append(";").append(dato.getColH()).append(";").append(dato.getColI()).append(";").append(dato.getColJ()).
                            append(";").append(dato.getColK()).append(";").append(dato.getColL()).append(";").append(dato.getColLl()).append(";").append(dato.getColM()).append(";").append(dato.getColN()).
                            append(";").append(dato.getColO()).append(";").append(dato.getColP()).append(";").append(dato.getColQ()).append("\n");
                }

                UtilFile.downloadFileTextoPlano(sb.toString(), "rptF14", UtilFile.EXTENSION_CSV);
            } catch (IOException ex) {
                Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void generarCertificacion() {
        if (lstProveedores.isEmpty()) {
            JsfUtil.mensajeAlerta("No hay datos para generar el reporte");
        } else {
            HashMap param = new HashMap();
            param.put("pAnho", Integer.parseInt(anho));
            param.put("pNumeroNit", numeroNit);
            param.put("pCiudad", VarSession.getNombreMunicipioSession());
            param.put("pUsuario", VarSession.getVariableSessionUsuario());

            param.put("pRazonSocial", empresa.getRazonSocial());
            param.put("pNumeroNitEmp", empresa.getNumeroNit());
            param.put("pMontoRetencion", getMontoSujetoRenta());
            param.put("pMontoRenta", getMontoRenta());
            param.put("pCodigoDepartamento", VarSession.getDepartamentoUsuarioSession());

            Reportes.generarRptSQLConnection(reportesEJB, param, "sv/gob/mined/apps/reportes/pagoproveedor/", "rptConstanciaRetencion", "contacionRentencion" + numeroNit.replace("-", ""));
        }
    }

    public void verPlanillaPago() {
        planillaPago = utilEJB.find(PlanillaPago.class,
                idPlanilla);
        idRubro = planillaPago.getIdRequerimiento().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres();

        reiniciarVisibilidadCheques();
        isRubroUniforme = ((idRubro.intValue() == 1) || (idRubro.intValue() == 4) || (idRubro.intValue() == 5));
        detalleProcesoAdq = planillaPago.getIdRequerimiento().getIdDetProcesoAdq();

        selectPlanilla();
        isPlanillaLectura = true;
        PrimeFaces.current().ajax().update("dlgPlanillaPago");
    }

    public void buscarPlanilla() {
        Integer idDet;
        if (montoTotal != null && montoTotal.intValue() == 0) {
            montoTotal = BigDecimal.ZERO;
        }
        if (idPlanilla != null && idPlanilla.intValue() == 0) {
            idPlanilla = BigDecimal.ZERO;
        }

        switch (idRubro.intValue()) {
            case 0:
                idDet = null;
                break;
            case 6:
                idDet = idRubro.intValue();
                break;
            default:
                idDet = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro).getIdDetProcesoAdq();
                break;
        }

        lstBusquedaPlanillas = pagoProveedoresEJB.buscarPlanillas(idPlanilla, montoTotal, numeroNit,
                nombreEntFinanciera, getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq(),
                numeroCheque, fechaCheque, codigoDepartamento, idDet);
    }

    public void postProcessXLS(Object document) {
        if (lstBusquedaPlanillas.isEmpty()) {
            JsfUtil.mensajeInformacion("No hay datos para exportar");
        } else {
            HSSFWorkbook wb = (HSSFWorkbook) document;
            HSSFSheet sheet = wb.getSheetAt(0);

            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                HSSFRow row = sheet.getRow(j);
                if (j != 0) {
                    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                        if (i == 7 || i == 8 || i == 11) {
                            String valor = row.getCell(i).getRichStringCellValue().getString();
                            if (!valor.isEmpty()) {
                                HSSFCell celda = row.createCell(i);
                                celda.setCellType(CellType.NUMERIC);
                                celda.setCellValue(new Double(valor.replace(",", "")));
                            }
                        }
                    }
                }
            }
        }
    }

    public String getFormatoFechaReporte() {
        return UtilFile.getFechaGeneracionReporte();
    }

    public void editarReintegro() {

    }

    public void showDetalleRequerimientoPorProveedor() {
        ceContratados = BigDecimal.ZERO;
        totalContratado = BigDecimal.ZERO;
        totalPagado = BigDecimal.ZERO;
        totalPendiente = BigDecimal.ZERO;
        totalReintegro = BigDecimal.ZERO;

        nombreRubro = detalleProcesoAdq.getIdRubroAdq().getDescripcionRubro();
        lstResumenPagoPorProveedor = serviciosEJB.getResumenPagoJsonByDetProcesoAdqAndRequerimiento(detalleProcesoAdq.getIdDetProcesoAdq(), numeroRequerimiento);

        lstResumenPagoPorProveedor.forEach((dato) -> {
            ceContratados = ceContratados.add(dato.getCantidadTotalContratos());
            totalContratado = totalContratado.add(dato.getMontoTotalContratado());
            totalPagado = totalPagado.add(dato.getMontoTotalPagado());
            totalPendiente = totalPendiente.add(dato.getMontoTotalPendiente());
            totalReintegro = totalReintegro.add(dato.getMontoTotalReintegrar());
        });
        dlgDetPagoProveedor = true;
    }

    public void cerrarDlgDetPagoProvee() {
        dlgDetPagoProveedor = false;
    }
}
