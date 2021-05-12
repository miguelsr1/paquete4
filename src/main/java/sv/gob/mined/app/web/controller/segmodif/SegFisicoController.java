/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.segmodif;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.RecepcionEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.ContratosOrdenesCompras;
import sv.gob.mined.paquescolar.model.DetalleOfertas;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.DetalleRecepcion;
import sv.gob.mined.paquescolar.model.EstadoSeguimiento;
import sv.gob.mined.paquescolar.model.OfertaBienesServicios;
import sv.gob.mined.paquescolar.model.RecepcionBienesServicios;
import sv.gob.mined.paquescolar.model.pojos.modificativa.VwBusquedaContratos;
import sv.gob.mined.paquescolar.model.view.VwBusquedaSeguimientos;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;

/**
 *
 * @author DesarrolloPc
 */
@Named
@ViewScoped
public class SegFisicoController extends RecuperarProcesoUtil implements Serializable {
    
    @Dependent
    private RecepcionEJB recepcionEJB;
    @Dependent
    private EntidadEducativaEJB entidadEducativaEJB;
    @Dependent
    private UtilEJB utilEJB;
    @Dependent
    private ReportesEJB reportesEJB;
    
    private BigDecimal rubro = BigDecimal.ZERO;
    private String codigoDepartamento;
    private DetalleRecepcion detalleRecepcion = new DetalleRecepcion();
    private RecepcionBienesServicios recepcion = new RecepcionBienesServicios();
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private ContratosOrdenesCompras contratoOrden = new ContratosOrdenesCompras();
    private OfertaBienesServicios current = new OfertaBienesServicios();
    private VwCatalogoEntidadEducativa entidadEducativa = new VwCatalogoEntidadEducativa();
    private VwBusquedaContratos contratoSelecionado = new VwBusquedaContratos();
    private List<VwBusquedaContratos> lstContratos = new ArrayList();
    private List<VwBusquedaSeguimientos> lstSeguimientos = new ArrayList();
    private List<RecepcionBienesServicios> recepciones = new ArrayList();
    private List<DetalleOfertas> listaOfertas = new ArrayList();
    private List<DetalleOfertas> listaOfertasOriginal = new ArrayList();
    private List<DetalleRecepcion> lstDetalleRecepcion = new ArrayList();
    private BigInteger totalEntregado = BigInteger.ZERO;
    private BigInteger cantidadPendiente = BigInteger.ZERO;
    private BigInteger cantidadTotalDetalle = BigInteger.ZERO;
    private BigInteger totalEntregado1 = BigInteger.ZERO;
    private BigDecimal idRecepcion = BigDecimal.ZERO;
    private Boolean mensajeAlerta = false;
    private Boolean disabledCantidad = true;
    private Boolean disableBtnTotal;
    private Boolean esDosUniformes = false;
    private Boolean showPnlNewSeguimiento = true;
    private Date fechaEntregaUno;
    private Date fechaEntregaDos;
    
    private String detalleItem = "";
    private String tipoentrega = "1";
    private String tipoentregaEdt = "1";
    private String codigoEntidad = "";
    private String numeroNit = "";
    private String razonSocial = "";
    private String numeroContrato = "";
    private String numeroItem;
    private String detalleItemEs;

    /**
     * Creates a new instance of SegFisicoController
     */
    public SegFisicoController() {
    }
    
    @PostConstruct
    public void ini() {
        String idContrato = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idContrato");
        
        if (idContrato != null && !idContrato.isEmpty()) {
            if (recepcionEJB.existeModificativaByIdContrato(new BigDecimal(idContrato))) {
                JsfUtil.mensajeInformacion("Debe de aplicar la modificativa que tiene registrada este contrato");
            } else {
                showPnlNewSeguimiento = false;
                inicializarValores(idContrato);
                cargarContrato(recepcionEJB.getContratosById(new BigDecimal(idContrato)));
                verificarEntregaCompleta();
            }
        }
        rubro = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getRubro();
        esDosUniformes = (rubro.intValue() == 1);
    }
    
    public void nuevoSeguimiento() {
        recepcion = new RecepcionBienesServicios();
        VarSession.removeVariableSession("idContrato");
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public List<DetalleRecepcion> getLstDetalleRecepcion() {
        return lstDetalleRecepcion;
    }
    
    public void setLstDetalleRecepcion(List<DetalleRecepcion> lstDetalleRecepcion) {
        this.lstDetalleRecepcion = lstDetalleRecepcion;
    }
    
    public BigDecimal getIdRecepcion() {
        return idRecepcion;
    }
    
    public void setIdRecepcion(BigDecimal idRecepcion) {
        this.idRecepcion = idRecepcion;
    }
    
    public Boolean getShowPnlNewSeguimiento() {
        return showPnlNewSeguimiento;
    }

    /**
     * Devuelve el listado de item actual del contrato/modificativa vigente
     *
     * @return
     */
    public List<DetalleOfertas> getListaOfertasOriginal() {
        return listaOfertasOriginal;
    }
    
    public void setShowPnlNewSeguimiento(Boolean showPnlNewSeguimiento) {
        this.showPnlNewSeguimiento = showPnlNewSeguimiento;
    }
    
    public void buscarProceso() {
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
    }
    
    public String getCodigoEntidad() {
        return codigoEntidad;
    }
    
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }
    
    public BigDecimal getRubro() {
        if (rubro == null) {
            if (VarSession.isCookie("rubro")) {
                rubro = new BigDecimal((VarSession.getCookieValue("rubro")));
            }
        }
        return rubro;
    }
    
    public void setRubro(BigDecimal rubro) {
        if (rubro != null) {
            VarSession.crearCookie("rubro", rubro.toString());
            /*ParametrosMB controller = (ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                    getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB");
            controller.setRubro(rubro);
            controller.findDetalleProcesoAdq();*/
            
            this.rubro = rubro;
        }
    }
    
    public String getNumeroNit() {
        return numeroNit;
    }
    
    public void setNumeroNit(String numeroNit) {
        this.numeroNit = numeroNit;
    }
    
    public String getRazonSocial() {
        return razonSocial;
    }
    
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    
    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }
    
    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }
    
    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        return entidadEducativa;
    }
    
    public void setEntidadEducativa(VwCatalogoEntidadEducativa entidadEducativa) {
        this.entidadEducativa = entidadEducativa;
    }
    
    public String getNumeroContrato() {
        return numeroContrato;
    }
    
    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }
    
    public List<VwBusquedaContratos> getLstContratos() {
        return lstContratos;
    }
    
    public void setLstContratos(List<VwBusquedaContratos> lstContratos) {
        this.lstContratos = lstContratos;
    }
    
    public List<RecepcionBienesServicios> getRecepciones() {
        return recepciones;
    }
    
    public void setRecepciones(List<RecepcionBienesServicios> recepciones) {
        this.recepciones = recepciones;
    }
    
    public VwBusquedaContratos getContratoSelecionado() {
        if (contratoSelecionado != null) {
            return contratoSelecionado;
        } else {
            contratoSelecionado = new VwBusquedaContratos();
            return contratoSelecionado;
        }
    }
    
    public void setContratoSelecionado(VwBusquedaContratos contratoSelecionado) {
        this.contratoSelecionado = contratoSelecionado;
    }
    
    public BigDecimal getRubroSeg() {
        return rubro;
    }
    
    public void setRubroSeg(BigDecimal rubro) {
        this.rubro = rubro;
    }
    
    public List<VwBusquedaSeguimientos> getLstSeguimientos() {
        return lstSeguimientos;
    }
    
    public void setLstSeguimientos(List<VwBusquedaSeguimientos> lstSeguimientos) {
        this.lstSeguimientos = lstSeguimientos;
    }
    
    public ContratosOrdenesCompras getContratoOrden() {
        contratoOrden = recepcionEJB.getContratoOrdenCompra(contratoSelecionado.getIdContrato());
        return contratoOrden;
    }
    
    public void setContratoOrden(ContratosOrdenesCompras contratoOrden) {
        this.contratoOrden = contratoOrden;
    }
    
    public String getDetalleItem() {
        return detalleItem;
    }
    
    public void setDetalleItem(String detalleItem) {
        this.detalleItem = detalleItem;
    }
    
    public BigInteger getTotalEntregado() {
        return totalEntregado;
    }
    
    public void setTotalEntregado(BigInteger totalEntregado) {
        this.totalEntregado = totalEntregado;
    }
    
    public BigInteger getCantidadPendiente() {
        return cantidadPendiente;
    }
    
    public void setCantidadPendiente(BigInteger cantidadPendiente) {
        this.cantidadPendiente = cantidadPendiente;
    }
    
    public BigInteger getCantidadTotalDetalle() {
        return cantidadTotalDetalle;
    }
    
    public void setCantidadTotalDetalle(BigInteger cantidadTotalDetalle) {
        this.cantidadTotalDetalle = cantidadTotalDetalle;
    }
    
    public String getEstadoSeguimiento() {
        if (recepcion.getIdRecepcion() == null || recepcion.getIdEstadoSeguimiento().getIdEstadoSeguimiento() == null) {
            return "EN PROCESO";
        } else {
            return recepcion.getIdEstadoSeguimiento().getDescripcionEstado();
        }
    }
    
    public Boolean getMensajeAlerta() {
        return mensajeAlerta;
    }
    
    public void setMensajeAlerta(Boolean mensajeAlerta) {
        this.mensajeAlerta = mensajeAlerta;
    }
    
    public BigInteger getTotalEntregado1() {
        return totalEntregado1;
    }
    
    public void setTotalEntregado1(BigInteger totalEntregado1) {
        this.totalEntregado1 = totalEntregado1;
    }
    
    public boolean isDisabledCantidad() {
        return disabledCantidad;
    }
    
    public void setDisabledCantidad(boolean disabledCantidad) {
        this.disabledCantidad = disabledCantidad;
    }
    
    public void setTipoentrega(String tipoentrega) {
        this.tipoentrega = tipoentrega;
    }
    
    public Date getFechaEntregaUno() {
        if (recepcion.getFechaOrdenInicioEntrega1() != null) {
            fechaEntregaUno = recepcion.getFechaOrdenInicioEntrega1();
        }
        return fechaEntregaUno;
    }
    
    public void setFechaEntregaUno(Date fechaEntregaUno) {
        this.fechaEntregaUno = fechaEntregaUno;
    }
    
    public Date getFechaEntregaDos() {
        if (recepcion.getFechaOrdenInicioEntrega1() != null) {
            fechaEntregaDos = recepcion.getFechaOrdenInicioEntrega2();
        }
        return fechaEntregaDos;
    }
    
    public void setFechaEntregaDos(Date fechaEntregaDos) {
        this.fechaEntregaDos = fechaEntregaDos;
    }
    
    public void setListaOfertas(List<DetalleOfertas> listaOfertas) {
        this.listaOfertas = listaOfertas;
    }
    
    public void setDisableBtnTotal(Boolean disableBtnTotal) {
        this.disableBtnTotal = disableBtnTotal;
    }
    
    public RecepcionBienesServicios getRecepcion() {
        return recepcion;
    }

    // </editor-fold>
    public Boolean getUniforme() {
        return contratoSelecionado != null && contratoSelecionado.getIdRubroAdq() != null && (contratoSelecionado.getIdRubroAdq().intValue() == 4 || contratoSelecionado.getIdRubroAdq().intValue() == 5);
    }
    
    private void inicializarValores(String idContrato) {
        contratoSelecionado = recepcionEJB.getContratosById(new BigDecimal(idContrato));
        recepcion = recepcionEJB.getRecepcion(contratoSelecionado.getIdContrato());
        lstDetalleRecepcion = recepcionEJB.getLstDetalleRecepcionByFk(recepcion.getIdRecepcion());
        listaOfertas = recepcionEJB.getItemDeContratoVigente(contratoSelecionado.getIdContrato());
        
        fechaEntregaUno = recepcion.getIdContrato().getFechaOrdenInicio();
        
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            detalleItem = utilEJB.find(DetalleRecepcion.class, VarSession.getVariableSession("idDetalleSeguimiento")).getNoItem();
            calcularTotalEDit();
            calcularTotalporItem();
            calcularPendiente();
        }
    }
    
    public void buscarContrato() {
        lstContratos.clear();
        if (rubro == null) {
            JsfUtil.mensajeAlerta("El campo Rubro de adquisicion es obligatorio");
        } else if (rubro == null) {
            JsfUtil.mensajeAlerta("El campo Rubro de adquisicion es obligatorio");
        } else {
            buscarProceso();
            lstContratos = recepcionEJB.getLstBusquedaContratosFisico(detalleProceso, codigoEntidad, codigoDepartamento, numeroNit, numeroContrato, razonSocial);
        }
    }
    
    public void buscarSeguimiento() {
        lstSeguimientos = new ArrayList();
        if (rubro == null) {
            JsfUtil.mensajeAlerta("El campo Rubro de adquisicion es obligatorio");
        } else {
            buscarProceso();
            try {
                lstSeguimientos = recepcionEJB.getLstBusquedaSeguimientos(detalleProceso.getIdDetProcesoAdq(), codigoEntidad, codigoDepartamento, numeroNit, numeroContrato, razonSocial);
                if (lstSeguimientos.isEmpty()) {
                    JsfUtil.mensajeInformacion("No se ha creado el seguimiento para el centro escolar " + codigoEntidad);
                }
            } catch (Exception e) {
                Logger.getLogger(SegFisicoController.class.getName()).log(Level.WARNING, "ERROR\n=====================================");
                Logger.getLogger(SegFisicoController.class.getName()).log(Level.WARNING, "detProcesoAdq {0}", detalleProceso);
                Logger.getLogger(SegFisicoController.class.getName()).log(Level.WARNING, "codigoEntidad {0} codigoDepartamento {1} numeroNit {2} numeroContrato {3} razonSocial {4}", new Object[]{codigoEntidad, codigoDepartamento, numeroNit, numeroContrato, razonSocial});
                Logger.getLogger(SegFisicoController.class.getName()).log(Level.WARNING, "USUARIO {0}", VarSession.getUsuarioSession().getIdPersona().getUsuario());
            }
        }
    }
    
    public void editSeguimiento() {
        Boolean existe;
        if (contratoSelecionado.getIdContrato() != null) {
            existe = recepcionEJB.existeModificativaByIdContrato(contratoSelecionado.getIdContrato());
            
            if (existe) {
                JsfUtil.mensajeAlerta("Este contrato tiene una modificativa en estado DIGITADA, debe de APLICAR la modificativa antes de registrar las entregas.");
            } else {
                existe = validarSiSeguimientoExiste();
                if (!existe) {
                    crearSeguimiento();
                }
                
                if (existe) {
                    contratoSelecionado = new VwBusquedaContratos();
                    JsfUtil.mensajeAlerta("Este contrato ya esta en seguimiento");
                } else {
                    PrimeFaces.current().dialog().closeDynamic(contratoSelecionado);
                }
            }
        }
    }
    
    private boolean validarSiSeguimientoExiste() {
        return this.recepcionEJB.ifExisteRecepcion(contratoSelecionado.getIdContrato());
    }
    
    public void crearSeguimiento() {
        ContratosOrdenesCompras contrato = recepcionEJB.getContratoOrdenCompra(contratoSelecionado.getIdContrato());
        
        if (contrato != null) {
            recepcion.setEstadoEliminacion(BigInteger.ZERO);
            recepcion.setIdEstadoSeguimiento(new EstadoSeguimiento(1));
            recepcion.setFechaInsercion(new Date());
            recepcion.setIdContrato(contrato);
            recepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            if (contrato.getFechaOrdenInicio() != null) {
                recepcion.setFechaOrdenInicioEntrega1(contrato.getFechaOrdenInicio());
            }
            this.recepcionEJB.guardarRecepcion(recepcion);
        } else {
            JsfUtil.mensajeAlerta("Ah ocurrido un error. Por favor vuelva a seleccionar el contrato.");
        }
    }
    
    public void showDlgEntregaTotal() {
        boolean validarAdicionDetalle = true;
        if (contratoSelecionado.getIdContrato() != null) {
            VarSession.setVariableSession("idContrato", contratoSelecionado.getIdContrato());
            VarSession.setVariableSession("op", "1");
            VarSession.setVariableSession("esDosUniformes", esDosUniformes);
            VarSession.setVariableSession("total", 1);
            if (esDosUniformes) {
                boolean cuadrarPrimeraEntrega = cuadraPrimeraEntregaUniformes();
                if ((cuadrarPrimeraEntrega && segundaEntregaEscero()) || sumEntregaTotal1UniformeEntregas().equals(BigInteger.ZERO)) {
                    VarSession.setVariableSession("cuadradaPrimeraEntrega", cuadrarPrimeraEntrega);
                    
                    Map<String, Object> opt = new HashMap();
                    opt.put("modal", true);
                    opt.put("draggable", true);
                    opt.put("resizable", false);
                    opt.put("contentHeight", 540);
                    opt.put("contentWidth", 400);
                    PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/seguimiento/fisico/dlgDetalleSeguimientoEntregaTotal.mined", opt, null);
                } else {
                    JsfUtil.mensajeAlerta("Ya existen detalles no se puede hacer entrega completa");
                }
            } else if (fechaEntregaUno == null) {
                JsfUtil.mensajeAlerta("Debe de ingresar la fecha de orden de inicio");
                validarAdicionDetalle = false;
            }
            
            if (validarAdicionDetalle) {
                Map<String, Object> opt = new HashMap();
                opt.put("modal", true);
                opt.put("draggable", true);
                opt.put("resizable", false);
                opt.put("contentHeight", 340);
                opt.put("contentWidth", 500);
                PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/seguimiento/fisico/dlgDetalleSeguimientoEntregaTotal.mined", opt, null);
                
            }
        } else {
            JsfUtil.mensajeAlerta("No ha seleccionado contrato");
        }
        
    }
    
    public void showDlgAgregarRecepcion() {
        boolean validarAdicionDetalle = true;
        if (esDosUniformes) {
            if (cuadraPrimeraEntregaUniformes()) {
                if (fechaEntregaDos == null) {
                    JsfUtil.mensajeAlerta("Debe de ingresar la fecha de orden de inicio de la segunda entrega");
                    validarAdicionDetalle = false;
                }
            } else if (fechaEntregaUno == null) {
                JsfUtil.mensajeAlerta("Debe de ingresar la fecha de orden de inicio de la primera entrega");
                validarAdicionDetalle = false;
            }
        } else if (fechaEntregaUno == null) {
            JsfUtil.mensajeAlerta("Debe de ingresar la fecha de orden de inicio");
            validarAdicionDetalle = false;
        } else if (contratoSelecionado.getIdContrato() == null) {
            JsfUtil.mensajeAlerta("No ha seleccionado contrato");
            validarAdicionDetalle = false;
        }
        
        if (validarAdicionDetalle) {
            String op = JsfUtil.getRequestParameter("nuevo");
            /*
         1 - Nuevo
         0 - Modificar
         2 - Eliminar
             */
            if (op.equals("1")) {//nuevo
                detalleRecepcion = new DetalleRecepcion();
            }
            
            VarSession.setVariableSession("op", op);
            VarSession.setVariableSession("idDetalleSeguimiento", detalleRecepcion.getIdDetalleRecepcion());
            VarSession.setVariableSession("fechaOrdenInicio", recepcion.getFechaOrdenInicioEntrega1());
            
            VarSession.setVariableSession("esDosUniformes", esDosUniformes);
            VarSession.setVariableSession("idContrato", contratoSelecionado.getIdContrato());
            if (esDosUniformes) {
                VarSession.setVariableSession("cuadradaPrimeraEntrega", cuadraPrimeraEntregaUniformes());
                VarSession.setVariableSession("dividirCantidad", recepcion.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres().intValue() == 1);
            }
            Map<String, Object> opt = new HashMap();
            opt.put("modal", true);
            opt.put("draggable", true);
            opt.put("resizable", false);
            opt.put("contentHeight", 332);
            opt.put("contentWidth", 500);
            PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/seguimiento/fisico/dlgDetalleSeguimientoNuevo.mined", opt, null);
        }
    }
    
    public void eliminarDetalleSeguimientoSeg() throws IOException {
        if (detalleRecepcion.getIdRecepcion() == null) {
            detalleRecepcion = new DetalleRecepcion();
        } else {
            VarSession.setVariableSession("idDetalleSeguimiento", detalleRecepcion.getIdDetalleRecepcion());
        }
        if (contratoSelecionado.getIdContrato() != null) {
            Map<String, Object> opt = new HashMap();
            opt.put("modal", true);
            opt.put("draggable", true);
            opt.put("resizable", false);
            opt.put("contentHeight", 150);
            opt.put("contentWidth", 300);
            PrimeFaces.current().dialog().openDynamic("/app/comunes/msgConfirmacion.mined", opt, null);
        }
    }
    
    public void actualizarsaldoEdit() {
        totalEntregado1 = totalEntregado;
        totalEntregado = totalEntregado.add(detalleRecepcion.getCantidadEntregada());
        totalEntregado = totalEntregado.subtract(totalEntregado1);
        cantidadPendiente = cantidadTotalDetalle.subtract(totalEntregado);
        
        mensajeAlerta = false;
    }
    
    public void buscarEntidadEducativa() {
        if (codigoEntidad.length() == 5) {
            detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
            entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);
            if (entidadEducativa == null) {
                JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
            }
        } else {
            entidadEducativa = null;
        }
    }
    
    public OfertaBienesServicios getSelected() {
        if (current == null) {
            current = new OfertaBienesServicios();
        }
        return current;
    }
    
    public void onEntidadChosen(SelectEvent event) {
        entidadEducativa = (VwCatalogoEntidadEducativa) event.getObject();
        getSelected().getCodigoEntidad().setCodigoEntidad(entidadEducativa.getCodigoEntidad());
        buscarEntidadEducativa();
    }

    /**
     * Luego de seleccionar el contrato a darle seguimiento, se refresca la
     * informacion del contrato junto con el detalle del mimso
     *
     * @param event
     */
    public void onContratoChosen(SelectEvent event) {
        if (event.getObject() instanceof VwBusquedaContratos) {
            cargarContrato((VwBusquedaContratos) event.getObject());
        }
    }
    
    private void cargarContrato(VwBusquedaContratos contrato) {
        contratoSelecionado = contrato;
        recepcion = recepcionEJB.getRecepcion(contratoSelecionado.getIdContrato());
        if (recepcion.getIdRecepcion() == null) {
            recepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        }
        fechaEntregaUno = recepcion.getIdContrato().getFechaOrdenInicio();
        listaOfertas = recepcionEJB.getItemDeContratoVigente(recepcion.getIdContrato().getIdContrato());
    }
    
    public void actualizarTblDetalleRecepcion(SelectEvent event) {
        lstDetalleRecepcion = recepcionEJB.getLstDetalleRecepcionByFk(recepcion.getIdRecepcion());
        verificarEntregaCompleta();
        
        if (disableBtnTotal) {
            recepcion.setIdEstadoSeguimiento(utilEJB.find(EstadoSeguimiento.class, 2));
            recepcionEJB.guardarRecepcion(recepcion);
        }
    }
    
    public void showDlgFiltroContrato() {
        Map<String, Object> opt = new HashMap();
        opt.put("modal", true);
        opt.put("draggable", true);
        opt.put("resizable", false);
        opt.put("contentHeight", 400);
        opt.put("contentWidth", 900);
        PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/seguimiento/fisico/filtroContratoSeguimiento", opt, null);
    }
    
    public void findItemBydetalle() {
        contratoOrden = recepcionEJB.getContratoOrdenCompra(contratoSelecionado.getIdContrato());
        contratoOrden.getIdResolucionAdj().getIdParticipante().getDetalleOfertasList().forEach((detalle) -> {
            if ((detalle.getNoItem() + " " + detalle.getConsolidadoEspTec()).equals(detalleItem)) {
                numeroItem = detalle.getNoItem();
                detalleItemEs = detalle.getConsolidadoEspTec();
            }
        });
    }
    
    public void modificardetalleContratoEdt() {
        detalleRecepcion.setTipoEntrega(tipoentregaEdt);
        detalleRecepcion.setNoItem(detalleItem);
        
        Boolean escero = siContratoSaldoCero();
        
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            detalleRecepcion.setFechaModificacion(new Date());
            detalleRecepcion.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        } else {
            detalleRecepcion.setEstadoEliminacion(BigInteger.ZERO);
            detalleRecepcion.setFechaInsercion(new Date());
            detalleRecepcion.setIdRecepcion(recepcion);
            detalleRecepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        }
        recepcionEJB.guardarDetalleRecepcion(detalleRecepcion, escero);
        VarSession.removeVariableSession("idDetalleSeguimiento");
        detalleRecepcion = new DetalleRecepcion();
        
        PrimeFaces.current().dialog().closeDynamic(detalleRecepcion);
    }
    
    public void modificardetalleContrato() {
        findItemBydetalle();
        if (esDosUniformes) {
            detalleRecepcion.setTipoEntrega(tipoentrega);
        }
        detalleRecepcion.setNoItem(numeroItem);
        detalleRecepcion.setConsolidadoEspTec(detalleItemEs);
        Boolean escero = siContratoSaldoCero();
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            detalleRecepcion.setFechaModificacion(new Date());
            detalleRecepcion.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        } else {
            detalleRecepcion.setEstadoEliminacion(BigInteger.ZERO);
            detalleRecepcion.setFechaInsercion(new Date());
            detalleRecepcion.setIdRecepcion(recepcion);
            detalleRecepcion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        }
        recepcionEJB.guardarDetalleRecepcion(detalleRecepcion, escero);
        VarSession.removeVariableSession("idDetalleSeguimiento");
        detalleRecepcion = new DetalleRecepcion();
        
        PrimeFaces.current().dialog().closeDynamic(detalleRecepcion);
    }
    
    public void eliminardetalleContrato() {
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            BigDecimal idDetalleRecepcion = (BigDecimal) VarSession.getVariableSession("idDetalleSeguimiento");
            detalleRecepcion = this.recepcionEJB.getDetalleRecepcionById(idDetalleRecepcion);
            detalleRecepcion.setEstadoEliminacion(BigInteger.ONE);
            recepcionEJB.guardarDetalleRecepcion(detalleRecepcion, false);
            this.detalleRecepcion = new DetalleRecepcion();
            VarSession.removeVariableSession("idDetalleSeguimiento");
            
        }
        PrimeFaces.current().dialog().closeDynamic(detalleRecepcion);
        
    }
    
    public void cancelareliminardetalleContrato() {
        PrimeFaces.current().dialog().closeDynamic(detalleRecepcion);
        
    }

    /*public void onDateSelect(SelectEvent event) {
        if (fechaEntregaUno != null) {
            recepcion.setFechaOrdenInicioEntrega1(fechaEntregaUno);
        }
        if (fechaEntregaDos != null) {
            recepcion.setFechaOrdenInicioEntrega2(fechaEntregaDos);
        }

        this.recepcionEJB.guardarRecepcion(recepcion);
    }*/
    public DetalleRecepcion getDetalleRecepcion() {
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            if (VarSession.isVariableSession("nuevo")) {
                String valor = (String) VarSession.getVariableSession("nuevo");
                if (valor.equals("0")) {
                    BigDecimal iddetalleRecepcion = (BigDecimal) VarSession.getVariableSession("idDetalleSeguimiento");
                    detalleRecepcion = recepcionEJB.getDetalleRecepcionById(iddetalleRecepcion);
                    this.tipoentregaEdt = detalleRecepcion.getTipoEntrega();
                    VarSession.removeVariableSession("nuevo");
                }
            }
        }
        return detalleRecepcion;
    }
    
    public void setDetalleRecepcion(DetalleRecepcion detalleRecepcion) {
        this.detalleRecepcion = detalleRecepcion;
    }
    
    private boolean cuadraPrimeraEntregaUniformes() {
        return sumEntregaTotal1UniformeContrato().equals(sumEntregaTotal1UniformeEntregas());
    }
    
    private boolean segundaEntregaEscero() {
        return sumEntregaTotal2Uniforme().equals(BigInteger.ZERO);
    }
    
    private boolean cuadraentregaUniformes() {
        return sumEntregaTotal2UniformeContrato().equals(sumEntregaTotal2UniformeEntregas());
    }
    
    private boolean cuadraentregaOtrosRubros() {
        return sumEntregaTotal2UniformeContrato().equals(sumEntregaTotal2UniformeEntregas());
    }
    
    private BigInteger sumEntregaTotal2UniformeContrato() {
        BigInteger total = BigInteger.ZERO;
        for (DetalleOfertas detalle : listaOfertas) {
            total = total.add(detalle.getCantidad());
        }
        return total;
    }
    
    private BigInteger sumEntregaTotal2UniformeEntregas() {
        BigInteger total = BigInteger.ZERO;
        for (DetalleRecepcion detRecepcion : lstDetalleRecepcion) {
            total = total.add(detRecepcion.getCantidadEntregada());
        }
        return total;
    }
    
    private BigInteger sumEntregaTotal1UniformeContrato() {
        BigInteger total = BigInteger.ZERO;
        for (DetalleOfertas detalle : listaOfertas) {
            if (esDosUniformes) {
                total = total.add(detalle.getCantidad().divide(BigInteger.valueOf(2)));
            } else {
                total = total.add(detalle.getCantidad());
            }
        }
        return total;
    }
    
    private BigInteger sumEntregaTotal1UniformeEntregas() {
        BigInteger total = BigInteger.ZERO;
        for (DetalleRecepcion detRecepcion : lstDetalleRecepcion) {
            if (esDosUniformes) {
                if (detRecepcion.getTipoEntrega().equals("1")) {
                    total = total.add(detRecepcion.getCantidadEntregada());
                }
            } else {
                total = total.add(detRecepcion.getCantidadEntregada());
            }
        }
        return total;
    }
    
    private BigInteger sumEntregaTotal2Uniforme() {
        BigInteger total = BigInteger.ZERO;
        for (DetalleRecepcion detRecepcion : lstDetalleRecepcion) {
            if (detRecepcion.getTipoEntrega().equals("2")) {
                total = total.add(detRecepcion.getCantidadEntregada());
            }
        }
        return total;
    }
    
    public void calcularTotalporItemDet() {
        contratoOrden = recepcionEJB.getContratoOrdenCompra(contratoSelecionado.getIdContrato());
        contratoOrden.getIdResolucionAdj().getIdParticipante().getDetalleOfertasList().forEach((detalle) -> {
            if ((detalle.getNoItem() + " " + detalle.getConsolidadoEspTec()).equals(detalleItem)) {
                
                cantidadTotalDetalle = detalle.getCantidad();
                if (VarSession.isVariableSession("rubroConfeccion")) {
                    Boolean rubroConfeccion = (Boolean) VarSession.getVariableSession("rubroConfeccion");
                    
                    if (rubroConfeccion) {
                        if (contratoOrden.getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdProcesoAdq().getPadreIdProcesoAdq() == null) {
                            cantidadTotalDetalle = BigInteger.valueOf(cantidadTotalDetalle.intValue() / 2);
                        } else {
                            cantidadTotalDetalle = BigInteger.valueOf(cantidadTotalDetalle.intValue());
                        }
                    }
                }
            }
        });
    }
    
    public void calcularTotalporItem() {
        contratoOrden = recepcionEJB.getContratoOrdenCompra(contratoSelecionado.getIdContrato());
        contratoOrden.getIdResolucionAdj().getIdParticipante().getDetalleOfertasList().forEach((detalle) -> {
            if (detalle.getNoItem().equals(detalleItem)) {
                cantidadTotalDetalle = detalle.getCantidad();
                if (VarSession.isVariableSession("rubroConfeccion")) {
                    Boolean rubroConfeccion = (Boolean) VarSession.getVariableSession("rubroConfeccion");
                    
                    if (rubroConfeccion) {
                        if (contratoOrden.getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdProcesoAdq().getPadreIdProcesoAdq() == null) {
                            cantidadTotalDetalle = BigInteger.valueOf(cantidadTotalDetalle.intValue() / 2);
                        } else {
                            cantidadTotalDetalle = BigInteger.valueOf(cantidadTotalDetalle.intValue());
                        }
                    }
                }
            }
        });
    }
    
    public void calcularTotalEDit() {
        totalEntregado = BigInteger.ZERO;
        recepcion.getDetalleRecepcionList().forEach((detalle) -> {
            if (detalleItem != null) {
                if (detalleItem.equals(detalle.getNoItem())) {
                    BigInteger valor = detalle.getCantidadEntregada();
                    if (esDosUniformes) {
                        if (detalle.getTipoEntrega().equals(tipoentrega)) {
                            totalEntregado = totalEntregado.add(valor);
                        }
                    } else {
                        totalEntregado = totalEntregado.add(valor);
                    }
                }
            }
        });
    }
    
    public void calcularPendiente() {
        Integer valor;
        valor = cantidadTotalDetalle.intValue();
        Integer valor2 = totalEntregado.intValue();
        Integer total = valor - valor2;
        cantidadPendiente = BigInteger.valueOf(total);
    }
    
    public Boolean siContratoSaldoCero() {
        Boolean escero = false;
        BigInteger totaldetalles = BigInteger.ZERO;
        String totalModi = (String) VarSession.getVariableSession("nuevoSum");
        if (totalModi.equals("0")) {
            BigDecimal idDetalleRecepcion = (BigDecimal) VarSession.getVariableSession("idDetalleSum");
            for (DetalleRecepcion detalle : recepcion.getDetalleRecepcionList()) {
                BigInteger valor = detalle.getCantidadEntregada();
                if (detalle.getIdDetalleRecepcion() != idDetalleRecepcion) {
                    totaldetalles = totaldetalles.add(valor);
                }
            }
        } else {
            for (DetalleRecepcion detalle : recepcion.getDetalleRecepcionList()) {
                BigInteger valor = detalle.getCantidadEntregada();
                totaldetalles = totaldetalles.add(valor);
            }
        }
        
        BigDecimal cantidadTotalContrato = contratoSelecionado.getCantidad();
        BigInteger cantidadDetalle = detalleRecepcion.getCantidadEntregada();
        Long resultado = cantidadTotalContrato.longValue() - (totaldetalles.longValue() + cantidadDetalle.longValue());
        if (resultado == 0) {
            escero = true;
        }
        
        return escero;
    }
    
    public String getTipoentrega() {
        if (VarSession.isVariableSession("segundaEntrega")) {
            Boolean segundaentrega = (Boolean) VarSession.getVariableSession("segundaEntrega");
            if (segundaentrega) {
                if (VarSession.isVariableSession("cuadraPrimera")) {
                    Boolean cuadraPrimeraEntrega = (Boolean) VarSession.getVariableSession("cuadraPrimera");
                    if (cuadraPrimeraEntrega) {
                        tipoentrega = "2";
                    } else {
                        tipoentrega = "1";
                    }
                } else {
                    tipoentrega = "1";
                }
                
            } else {
                tipoentrega = "1";
            }
        }
        return tipoentrega;
    }
    
    public List<DetalleOfertas> getListaOfertas() {
        return listaOfertas;
    }
    
    public Boolean getDisableBtnTotal() {
        return disableBtnTotal;
        
    }
    
    private void verificarEntregaCompleta() {
        if (recepcion.getDetalleRecepcionList() != null && !recepcion.getDetalleRecepcionList().isEmpty()) {
            if (!esDosUniformes) {
                disableBtnTotal = cuadraentregaOtrosRubros();
                
            } else {
                
                disableBtnTotal = cuadraentregaUniformes();
            }
        } else {
            disableBtnTotal = false;
        }
    }
    
    public String getRubroAdquisicion() {
        if (contratoSelecionado != null && contratoSelecionado.getIdContrato() != null) {
            switch (contratoSelecionado.getIdRubroAdq().intValue()) {
                case 1:
                    return "SERVICIOS DE CONFECCION DE UNIFORMES";
                case 2:
                    return "SUMINISTRO DE PAQUETES DE UTILES ESCOLARES";
                case 3:
                    return "PRODUCCION DE ZAPATOS";
                case 4:
                    return "SERVICIOS DE CONFECCION DEL PRIMER UNIFORME";
                case 5:
                    return "SERVICIOS DE CONFECCION DEL SEGUNDO UNIFORME";
            }
        }
        return "";
    }
    
    public Boolean getEsDosUniformes() {
        return esDosUniformes;
    }
    
    public String getTipoentregaEdt() {
        if (VarSession.isVariableSession("idDetalleSeguimiento")) {
            BigDecimal iddetalleRecepcion = (BigDecimal) VarSession.getVariableSession("idDetalleSeguimiento");
            detalleRecepcion = recepcionEJB.getDetalleRecepcionById(iddetalleRecepcion);
            this.tipoentregaEdt = detalleRecepcion.getTipoEntrega();
        }
        return tipoentregaEdt;
    }
    
    public void setTipoentregaEdt(String tipoentregaEdt) {
        this.tipoentregaEdt = tipoentregaEdt;
    }
    
    public void eliminarRecepcionBienesYServicios() {
        recepcionEJB.eliminarRecepcion(idRecepcion, VarSession.getVariableSessionUsuario());
        buscarSeguimiento();
    }
    
    public void onDateSelect(SelectEvent event) {
        if (fechaEntregaUno != null) {
            ContratosOrdenesCompras contrato = recepcion.getIdContrato();
            contrato.setFechaOrdenInicio(fechaEntregaUno);
            recepcion.setFechaOrdenInicioEntrega1(fechaEntregaUno);
            
            this.utilEJB.updateEntity(contrato);
            this.recepcionEJB.guardarRecepcion(recepcion);
        }
    }
    
    public void imprimir() {
        String nombreReporte = "rptActaRecepcionPer";
        if (recepcion.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdEmpresa().getIdPersoneria().getIdPersoneria().intValue() == 1) {
            nombreReporte += "Nat";
        } else {
            nombreReporte += "Jur";
        }
        
        HashMap param = new HashMap();
        param.put("idContrato", recepcion.getIdContrato().getIdContrato().intValue());
        
        Reportes.generarRptSQLConnection(reportesEJB, param, "sv/gob/mined/apps/reportes/pagoproveedor" + File.separator, nombreReporte, "rptActaRecepcion_");
    }
    
}
