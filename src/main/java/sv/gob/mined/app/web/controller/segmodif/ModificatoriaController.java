
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.segmodif;

import sv.gob.mined.app.web.controller.contratacion.ContratosOrdenesComprasController;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.controller.contratacion.OfertaMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.ModificativaEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ResolucionAdjudicativaEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.CatalogoProducto;
import sv.gob.mined.paquescolar.model.ContratosOrdenesCompras;
import sv.gob.mined.paquescolar.model.DetalleModificativa;
import sv.gob.mined.paquescolar.model.DetalleOfertas;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.NivelEducativo;
import sv.gob.mined.paquescolar.model.PreciosRefRubroEmp;
import sv.gob.mined.paquescolar.model.ResolucionesModificativas;
import sv.gob.mined.paquescolar.model.RptDocumentos;
import sv.gob.mined.paquescolar.model.TechoRubroEntEdu;
import sv.gob.mined.paquescolar.model.TipoModifContrato;
import sv.gob.mined.paquescolar.model.pojos.contratacion.ContratoDto;
import sv.gob.mined.paquescolar.model.pojos.VwDepartamentoModificativas;
import sv.gob.mined.paquescolar.model.pojos.VwDetalleModificativas;
import sv.gob.mined.paquescolar.model.pojos.modificativa.VwBusquedaContratos;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;
import sv.gob.mined.paquescolar.model.pojos.modificativa.VwContratoModificatoria;

/**
 *
 * @author DesarrolloPc
 */
@Named
@ViewScoped
public class ModificatoriaController extends RecuperarProcesoUtil implements Serializable {

    private Boolean deshabilitar = true;
    private Boolean deshabilitarAgregar = false;
    private Boolean deshabilitarEliminar = false;
    private Boolean deshabilitarCantidad = false;
    private Boolean negativo = false;
    private Boolean positivo = false;
    /*
    ayuda a determinar la fila que se debe de actualizar en una datatable
     */
    private int rowEdit = 0;
    private int tipoConsulta = 0;
    private Integer cantidadTotalNew;
    private Integer cantidadTotalOld;

    private BigDecimal idContratoTemp = BigDecimal.ZERO;
    private String codigoEntidad;
    private String msjError = "";
    private String msjInformacion = "";
    private String descripcionTipoModif = "";

    private Date fechaOrdenInicio;

    private BigDecimal idRubro = BigDecimal.ZERO;
    private BigDecimal idEstadoReserva = BigDecimal.ZERO;
    private BigDecimal idTipoModif = BigDecimal.ZERO;
    private BigDecimal montoTotalNew;
    private BigDecimal montoTotalOld;
    private BigDecimal saldoActual = BigDecimal.ZERO;
    private BigDecimal montoAdjudicacionActual = BigDecimal.ZERO;
    private BigDecimal montoSaldo = BigDecimal.ZERO;

    private ResolucionesModificativas contratoExtinsion = new ResolucionesModificativas();
    private DetalleModificativa detalleSeleccionado = new DetalleModificativa();

    private ContratosOrdenesCompras contratoOriginal = new ContratosOrdenesCompras();
    private ResolucionesModificativas resolucionesModificativas = new ResolucionesModificativas();
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private TipoModifContrato tipoModifContrato = new TipoModifContrato();
    private TechoRubroEntEdu techoCE = new TechoRubroEntEdu();
    private VwBusquedaContratos vwBusquedaContrato = new VwBusquedaContratos();
    private VwContratoModificatoria vwContratoModificatoria = new VwContratoModificatoria();
    private VwCatalogoEntidadEducativa entidadEducativa;

    private List<TipoModifContrato> lstTipoModifContratos = new ArrayList();
    private List<DetalleModificativa> lstDetalleModificativas = new ArrayList();
    private List<CatalogoProducto> lstItem = new ArrayList();

    private List<VwDepartamentoModificativas> lstCeDetModificativas = new ArrayList();
    private List<VwDetalleModificativas> lstProDetModificativas = new ArrayList();

    @Dependent
    private ModificativaEJB modificativaEJB;
    @Dependent
    private ResolucionAdjudicativaEJB resolucionAdjudicativaEJB;
    @Dependent
    private UtilEJB utilEJB;
    @Dependent
    private ProveedorEJB proveedorEJB;

    /**
     * Creates a new instance of ModificatoriaController
     */
    public ModificatoriaController() {
    }

    @PostConstruct
    public void ini() {
        idRubro = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getRubro();
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        techoCE.setMontoAdjudicado(BigDecimal.ZERO);
        techoCE.setMontoDisponible(BigDecimal.ZERO);
        techoCE.setMontoPresupuestado(BigDecimal.ZERO);

        if (JsfUtil.isExisteParametroUrl("idContrato")) {
            idContratoTemp = new BigDecimal(JsfUtil.getParametroUrl("idContrato"));
            //vwBusquedaContrato = modificativaEJB.getContratosById(idContratoTemp);
            vwContratoModificatoria = modificativaEJB.getContratoModificatorioEnDigitacion(idContratoTemp);
            cargarModif(vwContratoModificatoria);
        }
    }

    public BigDecimal getIdContratoTemp() {
        return idContratoTemp;
    }

    public void setIdContratoTemp(BigDecimal idContratoTemp) {
        this.idContratoTemp = idContratoTemp;
    }

    public Date getFechaOrdenInicio() {
        return fechaOrdenInicio;
    }

    public void setFechaOrdenInicio(Date fechaOrdenInicio) {
        this.fechaOrdenInicio = fechaOrdenInicio;
    }

    public String getMsjInformacion() {
        return msjInformacion;
    }

    public BigDecimal getIdEstadoReserva() {
        return idEstadoReserva;
    }

    public void setIdEstadoReserva(BigDecimal idEstadoReserva) {
        this.idEstadoReserva = idEstadoReserva;
    }

    public BigDecimal getIdTipoModif() {
        return idTipoModif;
    }

    public void setIdTipoModif(BigDecimal idTipoModif) {
        this.idTipoModif = idTipoModif;
    }

    public VwBusquedaContratos getContratoSelecionado() {
        return vwBusquedaContrato;
    }

    public void setContratoSelecionado(VwBusquedaContratos contratoSelecionado) {
        this.vwBusquedaContrato = contratoSelecionado;
    }

    public void prepararCreacion() {
        VarSession.setVariableSession("op", 1);
        resolucionesModificativas = new ResolucionesModificativas();
        lstDetalleModificativas.clear();
    }

    public void prepararEdicion() {
        VarSession.setVariableSession("op", 2);
    }

    public boolean getEENuevo() {
        if (VarSession.isVariableSession("op")) {
            return VarSession.getVariableSession("op").toString().equals("1");
        } else {
            return false;
        }
    }

    public boolean getEEModificar() {
        if (VarSession.isVariableSession("op")) {
            return VarSession.getVariableSession("op").toString().equals("2");
        } else {
            return false;
        }
    }

    public String getDescripcionTipoModif() {
        if (resolucionesModificativas.getIdResolucionModif() != null) {
            return resolucionesModificativas.getIdModifContrato().getDescripcionModificativa();
        } else {
            return descripcionTipoModif;
        }
    }

    public Boolean getDeshabilitar() {
        return deshabilitar;
    }

    public void setDeshabilitar(Boolean deshabilitar) {
        this.deshabilitar = deshabilitar;
    }

    public Boolean getDeshabilitarAgregar() {
        return deshabilitarAgregar;
    }

    public void setDeshabilitarAgregar(Boolean deshabilitarAgregar) {
        this.deshabilitarAgregar = deshabilitarAgregar;
    }

    public Boolean getDeshabilitarEliminar() {
        return deshabilitarEliminar;
    }

    public void setDeshabilitarEliminar(Boolean deshabilitarEliminar) {
        this.deshabilitarEliminar = deshabilitarEliminar;
    }

    public Boolean getDeshabilitarCantidad() {
        return deshabilitarCantidad;
    }

    public void setDeshabilitarCantidad(Boolean deshabilitarCantidad) {
        this.deshabilitarCantidad = deshabilitarCantidad;
    }

    public void deshabilitarAcciones() {
        deshabilitarAgregar = false;
        deshabilitarEliminar = false;
        deshabilitarCantidad = false;
        resolucionesModificativas.setIdModifContrato(new TipoModifContrato(idTipoModif));
        switch (idTipoModif.intValue()) {
            case 1:
            case 4:
                deshabilitarAgregar = true;
                deshabilitarEliminar = true;
                deshabilitarCantidad = true;
                lstDetalleModificativas.forEach((detalle) -> {
                    detalle.setCantidadNew(detalle.getCantidadOld());
                    if (idTipoModif.intValue() == 4) {
                        detalle.setPrecioUnitarioNew(detalle.getPrecioUnitarioOld());
                    }
                });
                break;
            case 2:
            case 3:
                break;
            case 5:
            case 6:
                if (idTipoModif.intValue() == 6) {
                    lstDetalleModificativas.forEach((detalle) -> {
                        detalle.setCantidadNew(detalle.getCantidadOld());
                    });
                    msjInformacion = "No se permiten disminuciones <b>MENOR ▼</b> al 20% del monto total del contrato.";
                } else {
                    msjInformacion = "No se permiten incrementos <b>MAYOR ▲</b> al 20% del monto total del contrato.";
                }
                deshabilitarAgregar = true;
                deshabilitarEliminar = true;
                break;
            case 13:
                deshabilitarAgregar = true;
                deshabilitarEliminar = true;

                lstDetalleModificativas.forEach((detalle) -> {
                    detalle.setCantidadNew(detalle.getCantidadOld());
                    if (idTipoModif.intValue() == 13) {
                        detalle.setPrecioUnitarioNew(detalle.getPrecioUnitarioOld());
                    }
                });
                break;
        }
    }

    public ContratosOrdenesCompras getContratoOriginal() {
        return contratoOriginal;
    }

    public void setContratoOriginal(ContratosOrdenesCompras contratoOriginal) {
        this.contratoOriginal = contratoOriginal;
    }

    public ResolucionesModificativas getContratoEstado() {
        return contratoExtinsion;
    }

    public void setContratoEstado(ResolucionesModificativas contratoEstado) {
        this.contratoExtinsion = contratoEstado;
    }

    public DetalleModificativa getDetalleSeleccionado() {
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(DetalleModificativa detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }

    public ResolucionesModificativas getResolucionesModificativas() {
        return resolucionesModificativas;
    }

    public void setResolucionesModificativas(ResolucionesModificativas resolucionesModificativas) {
        this.resolucionesModificativas = resolucionesModificativas;
    }

    public TipoModifContrato getTipoModifContrato() {
        return tipoModifContrato;
    }

    public void setTipoModifContrato(TipoModifContrato tipoModifContrato) {
        this.tipoModifContrato = tipoModifContrato;
    }

    public int getRowEdit() {
        return rowEdit;
    }

    public void setRowEdit(int rowEdit) {
        this.rowEdit = rowEdit;
    }

    public Integer getCantidadTotalNew() {
        return cantidadTotalNew;
    }

    public void setCantidadTotalNew(Integer cantidadTotalNew) {
        this.cantidadTotalNew = cantidadTotalNew;
    }

    public Integer getCantidadTotalOld() {
        return cantidadTotalOld;
    }

    public void setCantidadTotalOld(Integer cantidadTotalOld) {
        this.cantidadTotalOld = cantidadTotalOld;
    }

    public BigDecimal getMontoTotalNew() {
        return montoTotalNew;
    }

    public void setMontoTotalNew(BigDecimal montoTotalNew) {
        this.montoTotalNew = montoTotalNew;
    }

    public BigDecimal getMontoTotalOld() {
        return montoTotalOld;
    }

    public void setMontoTotalOld(BigDecimal montoTotalOld) {
        this.montoTotalOld = montoTotalOld;
    }

    public List<DetalleModificativa> getLstDetalleModificativas() {
        return lstDetalleModificativas;
    }

    public void setLstDetalleModificativas(List<DetalleModificativa> lstDetalleModificativas) {
        this.lstDetalleModificativas = lstDetalleModificativas;
    }

    public List<TipoModifContrato> getLstTipoModifContratos() {
        return lstTipoModifContratos;
    }

    public void setLstTipoModifContratos(List<TipoModifContrato> lstTipoModifContratos) {
        this.lstTipoModifContratos = lstTipoModifContratos;
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

    public VwCatalogoEntidadEducativa getEntidadEducativa() {
        if (entidadEducativa == null) {
            entidadEducativa = new VwCatalogoEntidadEducativa();
        }
        return entidadEducativa;
    }

    public void setEntidadEducativa(VwCatalogoEntidadEducativa entidadEducativa) {
        this.entidadEducativa = entidadEducativa;
    }

    public void buscarProceso() {
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
    }

    public void buscarEntidadEducativa() {
        OfertaMB controller = (OfertaMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "ofertaMB");
        controller.setCodigoEntidad(codigoEntidad);
        controller.buscarEntidadEducativa();
        entidadEducativa = controller.getEntidadEducativa();
    }

    public void agregarItem() {
        if (!lstDetalleModificativas.isEmpty()) {
            DetalleModificativa detalle = new DetalleModificativa();
            detalle.setModificarItem(true);
            detalle.setCantidadNew(0);
            detalle.setCantidadOld(0);
            detalle.setPrecioUnitarioNew(BigDecimal.ZERO);
            detalle.setPrecioUnitarioOld(BigDecimal.ZERO);
            detalle.setEstadoEliminacion(Short.parseShort("0"));
            detalle.setFechaInsercion(new Date());
            detalle.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            detalle.setIdResolucionModif(resolucionesModificativas);
            lstDetalleModificativas.add(detalle);
        }
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        DetalleModificativa det = context.getApplication().evaluateExpressionGet(context, "#{detalle}", DetalleModificativa.class);

        if (event.getNewValue() != null) {
            rowEdit = event.getRowIndex();

            if (event.getColumn().getColumnKey().contains("item")) {
                editarNumeroDeItem(det, event.getRowIndex(), event.getNewValue().toString().trim());
                det.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                det.setFechaModificacion(new Date());
            }
        }
    }

    private void editarNumeroDeItem(DetalleModificativa det, int rowEdit, String numItem) {
        CatalogoProducto item;
        NivelEducativo nivel;

        msjError = "";
        HashMap<String, Object> param = proveedorEJB.validarItemProveedor(numItem, idRubro.intValue());

        if (param.containsKey("error")) {
            msjError = param.get("error").toString();
            det.setConsolidadoEspTec("");
        } else {
            item = (CatalogoProducto) param.get("item");
            nivel = (NivelEducativo) param.get("nivel");

            if (item != null && nivel != null && !validarItemDuplicado(det, rowEdit) && isProductoIsValid(item.getIdProducto())) {
                det.setConsolidadoEspTec(item.toString() + ", " + nivel.toString());
                det.setIdProducto(item.getIdProducto());
                det.setIdNivelEducativo(nivel.getIdNivelEducativo());
                PreciosRefRubroEmp precio = proveedorEJB.getPrecioRef(contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdEmpresa(), nivel.getIdNivelEducativo(), item.getIdProducto(),
                        detalleProceso.getIdRubroAdq().getIdRubroInteres(), detalleProceso.getIdProcesoAdq().getIdAnho().getIdAnho());
                if (precio != null) {
                    det.setPrecioUnitarioNew(precio.getPrecioReferencia());
                } else {
                    msjError = "El proveedor seleccionado no posee precios de referencia para el producto y nivel educativo seleccionado.";
                    limpiarDetalleModif(det);
                }
            } else {
                limpiarDetalleModif(det);
            }
        }
    }

    private void limpiarDetalleModif(DetalleModificativa det) {
        det.setNoItem("");
        det.setConsolidadoEspTec("");
        det.setPrecioUnitarioNew(BigDecimal.ZERO);
        det.setCantidadNew(0);
    }

    private boolean validarItemDuplicado(DetalleModificativa detalleNew, int rowEdit) {
        for (int i = 0; i < lstDetalleModificativas.size(); i++) {
            if (detalleNew.getIdDetalleModif() != null && lstDetalleModificativas.get(i).getIdDetalleModif() != null && lstDetalleModificativas.get(i).getIdDetalleModif().compareTo(detalleNew.getIdDetalleModif()) == 0 && i != rowEdit) {
            } else if (lstDetalleModificativas.get(i).getNoItem() != null && lstDetalleModificativas.get(i).getNoItem().equals(detalleNew.getNoItem()) && i != rowEdit) {
                msjError = "Este item ya fue agregado!";
                return true;
            }
        }
        return false;
    }

    private boolean isProductoIsValid(BigDecimal idProducto) {
        for (CatalogoProducto producto : lstItem) {
            if (producto.getIdProducto().intValue() == idProducto.intValue()) {
                return true;
            }
        }
        return false;
    }

    public void updateFilaDetalle() {
        PrimeFaces.current().ajax().update("tblDetalleItems:" + rowEdit + ":descripcionItem");
        PrimeFaces.current().ajax().update("tblDetalleItems:" + rowEdit + ":precioUnitario");
        PrimeFaces.current().ajax().update("tblDetalleItems:" + rowEdit + ":subTotal");
        PrimeFaces.current().ajax().update("tblDetalleItems:cantidadOld");
        PrimeFaces.current().ajax().update("tblDetalleItems:cantidadNew");
        PrimeFaces.current().ajax().update("tblDetalleItems:montoOld");
        PrimeFaces.current().ajax().update("tblDetalleItems:montoNew");
        if (!msjError.isEmpty()) {
            JsfUtil.mensajeAlerta(msjError);
        }
    }

    public String getMontoOld() {
        return JsfUtil.getFormatoNum(getMontoAdjudicadoOld(), false);
    }

    public String getCantidadOld() {
        return JsfUtil.getFormatoNum(getCantidadAdjudicadoOld(), true);
    }

    public String getMontoNew() {
        return JsfUtil.getFormatoNum(getMontoAdjudicadoNew(), false);
    }

    public String getCantidadNew() {
        return JsfUtil.getFormatoNum(getCantidadAdjudicadoNew(), true);
    }

    /**
     * Recuperar monto del contrato anterior. Si es la primer modificativa se
     * recupera el monto total del contrato original. Si es una modificativa de
     * otra modificativa previa, se recupera el monto de la modificativa padre.
     *
     * @return
     */
    private BigDecimal getMontoAdjudicadoOld() {
        if (resolucionesModificativas.getIdResolucionModif() == null) {
            BigDecimal total = BigDecimal.ZERO;

            if (resolucionesModificativas.getDetalleModificativaList() != null) {
                for (DetalleModificativa detalle : resolucionesModificativas.getDetalleModificativaList()) {
                    total = total.add(new BigDecimal(detalle.getCantidadOld()).multiply(detalle.getPrecioUnitarioOld()));
                }
            }

            return total;
        } else {
            return modificativaEJB.getMontoOldContrato(resolucionesModificativas.getIdResolucionModif());
        }
    }

    private Integer getCantidadAdjudicadoOld() {
        Integer total = 0;
        if (lstDetalleModificativas != null) {
            for (DetalleModificativa detalle : lstDetalleModificativas) {
                total += detalle.getCantidadOld();
            }
        }
        return total;
    }

    private BigDecimal getMontoAdjudicadoNew() {
        BigDecimal total = BigDecimal.ZERO;
        if (lstDetalleModificativas != null) {
            for (DetalleModificativa detalle : lstDetalleModificativas) {
                if (detalle.getCantidadNew() != null && detalle.getPrecioUnitarioNew() != null) {
                    total = total.add(new BigDecimal(detalle.getCantidadNew()).multiply(detalle.getPrecioUnitarioNew()));
                }
            }
        }
        return total;
    }

    private Integer getCantidadAdjudicadoNew() {
        Integer total = 0;
        if (lstDetalleModificativas != null) {
            for (DetalleModificativa detalle : lstDetalleModificativas) {
                if (detalle.getCantidadNew() != null) {
                    total += detalle.getCantidadNew();
                }
            }
        }
        return total;
    }

    public String guardar() {
        String urlRed = null;
        if (vwBusquedaContrato != null && vwBusquedaContrato.getIdContrato() != null) {
            if (resolucionesModificativas.getFechaNota() != null && resolucionesModificativas.getFechaResolucion() != null && resolucionesModificativas.getFechaSolicitud() != null) {
                if (idTipoModif == null || idTipoModif.compareTo(BigDecimal.ZERO) == 0) {
                    JsfUtil.mensajeAlerta("Debe de seleccionar el tipo de modificativa a realizar");
                } else {
                    if (resolucionesModificativas.getJustificacionModificativa().trim().isEmpty()) {
                        JsfUtil.mensajeAlerta("Debe de ingresar una breve justificación de la modificativa a realizar");
                    } else {
                        Boolean validacion = false;

                        switch (idTipoModif.intValue()) {
                            case 1: //CAMBIO DE PRECIO
                                validacion = validarCambioDePrecio();
                                break;
                            case 2: //ERROR DE DIGITACIÓN
                                validacion = true;
                                break;
                            case 3: //ERROR EN ADJUDICACIÓN
                                validacion = true;
                                break;
                            case 4: //POR PRORROGA EN PLAZO CONTRACTUAL
                                if (idRubro.intValue() == 2) {
                                    if (resolucionesModificativas.getDiasProrroga() > 0
                                            && resolucionesModificativas.getDiasProrroga() <= 30) {
                                        validacion = true;
                                    } else {
                                        JsfUtil.mensajeAlerta("Los días de prorroga deben de estar entre 1 y 30 días");
                                        validacion = false;
                                    }
                                } else if (resolucionesModificativas.getDiasProrroga() > 0 && resolucionesModificativas.getDiasProrroga() <= 60) {
                                    validacion = true;
                                } else {
                                    JsfUtil.mensajeAlerta("Los días de prorroga deben de estar entre 1 y 60 días");
                                    validacion = false;
                                }
                                break;
                            case 5: //INCREMENTO EN EL MONTO DEL CONTRATO
                                validacion = true;
                                break;
                            case 6: //VARIACIONES DE CANTIDAD SIN INCREMENTO DE MONTO
                                BigDecimal diferencia = getMontoAdjudicadoOld().subtract(getMontoAdjudicadoNew());
                                if (diferencia.compareTo(BigDecimal.ZERO) == -1) {
                                    JsfUtil.mensajeAlerta("No se permiten incremento en el monto total original del contrato.");
                                    validacion = false;
                                }

                                /**
                                 * 21-ENERO-2020 Se elimina esta restricción por
                                 * cambios realizados por la UNAC
                                 */
                                /*BigDecimal veintePorciento = getMontoAdjudicadoOld().multiply(new BigDecimal("0.20"));
                                if (veintePorciento.compareTo(diferencia) == -1) {
                                    JsfUtil.mensajeAlerta("No se permiten disminuciones menores al 20% del monto total del contrato.");
                                    validacion = false;
                                } else {
                                    validacion = true;
                                }*/
                                break;
                        }

                        if (validacion) {
                            switch (idRubro.intValue()) {
                                case 1:
                                case 4:
                                case 5:
                                    if (contratoOriginal.getFechaOrdenInicio() == null) {
                                        contratoOriginal.setFechaOrdenInicio(fechaOrdenInicio);
                                        contratoOriginal.setFechaModificacion(new Date());
                                        contratoOriginal.setUsuarioModificacion(VarSession.getVariableSessionUsuario());

                                        resolucionAdjudicativaEJB.editContrato(contratoOriginal);
                                    }
                                    break;
                            }

                            resolucionesModificativas.setIdModifContrato(new TipoModifContrato(idTipoModif));
                            resolucionesModificativas.setValor(getMontoAdjudicadoNew().add(getMontoAdjudicadoOld().negate()).negate());
                            if (modificativaEJB.guardarModificatoria(resolucionesModificativas, VarSession.getVariableSessionUsuario()) == true) {
                                JsfUtil.mensajeError("Ah ocurrido un error.");
                            } else {
                                //JsfUtil.mensajeInsert();
                                urlRed = "reg04AprobacionModificatoria.mined?includeViewParams=true&idContrato=" + resolucionesModificativas.getIdContrato().getIdContrato().intValue();
                            }
                        }
                    }
                }
            } else {
                JsfUtil.mensajeAlerta("Debe de ingresar las fechas de Nota, Resolución y Solicitud");
            }
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un contrato!");
        }

        return urlRed;
    }

    private Boolean validarCambioDePrecio() {
        Boolean preciosIguales = true;
        for (DetalleModificativa detalle : lstDetalleModificativas) {
            if (detalle.getPrecioUnitarioOld() != null) {
                if (detalle.getPrecioUnitarioOld().compareTo(detalle.getPrecioUnitarioNew()) != 0) {
                    preciosIguales = false;
                }
            } else {
                JsfUtil.mensajeAlerta("No se puede agregar nuevos items en este tipo de modificatoria, debe de borrarlo");
                return false;
            }
        }

        if (preciosIguales) {
            JsfUtil.mensajeAlerta("Debe de existir por lo menos un item con diferente precio unitario.");
            return false;
        } else {
            return true;
        }
    }

    public void aprobarModificatoria() {
        if (resolucionesModificativas.getIdResolucionModif() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar una modificativa valida.");
        } else {
            if (!modificativaEJB.validarCambioEstado(resolucionesModificativas, idEstadoReserva)) {
                JsfUtil.mensajeAlerta("Cambio de estado inválido");
            } else {
                //primer revertir la reserva aplicada
                HashMap<String, Object> param = modificativaEJB.aplicarReservaDeFondos(resolucionesModificativas, idEstadoReserva, VarSession.getVariableSessionUsuario());
                Boolean exito = !param.containsKey("error");
                if (exito) {
                    PrimeFaces.current().executeScript("PF('dlgPregunta').show()");
                } else {
                    PrimeFaces.current().ajax().update("frmPrincipal");
                    JsfUtil.mensajeAlerta(param.get("error").toString());
                }
            }
        }
    }

    public void redirigirAModEntregas() {
        JsfUtil.redireccionar("reg06Seguimiento.mined?faces-redirect=true&idContrato=" + resolucionesModificativas.getIdContrato().getIdContrato().intValue());
    }

    public void eliminarDetalle() {
        if (detalleSeleccionado != null) {
            if (detalleSeleccionado.getEstadoEliminacion() == 0) {
                detalleSeleccionado.setEstadoEliminacion(Short.parseShort("1"));
                detalleSeleccionado.setCantidadNew(0);
            } else {
                detalleSeleccionado.setEstadoEliminacion(Short.parseShort("0"));
            }

            detalleSeleccionado = null;
        } else {
            JsfUtil.mensajeAlerta("Debe seleccionar un detalle para poder eliminarlo.");
        }
    }

    public List<TipoModifContrato> getLstEstadoContrato() {
        return modificativaEJB.getLstTipoModifContrato(Short.parseShort("1"));
    }

    public List<TipoModifContrato> getLstTipoExtinsionContratos() {
        return modificativaEJB.getLstTipoModifContrato(Short.valueOf("1"));
    }

    public void validarProcesoAdquisicion() {
        if (getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de adquisición.");
        }
    }

    public void nuevaExtinsion() {
        VarSession.setVariableSession("op", "1");
    }

    public void consultarExtinsionContrato() {
        VarSession.setVariableSession("op", "3");
    }

    public void mostrarFiltroContrato(Boolean aprobacion) {
        if (getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq() != null) {
            Map<String, Object> opt = new HashMap();
            opt.put("modal", true);
            opt.put("draggable", true);
            opt.put("resizable", false);
            opt.put("contentHeight", 400);
            opt.put("contentWidth", 1000);
            VarSession.setVariableSession("aprobacion", aprobacion);
            PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/seguimiento/contrato/filtroContratoModificatoria", opt, null);
        }
    }

    public void onContratoModifChosen(SelectEvent event) {
        lstDetalleModificativas.clear();
        if (event.getObject() instanceof VwContratoModificatoria) {
            vwContratoModificatoria = (VwContratoModificatoria) event.getObject();
            idContratoTemp = vwContratoModificatoria.getIdContrato();

            if (VarSession.getVariableSession("op").toString().equals("1")) {
                vwBusquedaContrato = modificativaEJB.getContratosById(vwContratoModificatoria.getIdContrato());

                contratoOriginal = utilEJB.find(ContratosOrdenesCompras.class, vwContratoModificatoria.getIdContrato());

                if (contratoOriginal.getFechaOrdenInicio() != null) {
                    fechaOrdenInicio = contratoOriginal.getFechaOrdenInicio();
                }

                idRubro = contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres();
                detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);

                if (vwContratoModificatoria.getIdResolucionAdj() == null) {//modificación de una modificatoria
                    //for (DetalleModificativa detalle : utilEJB.find(ResolucionesModificativas.class, vwContratoModificatoria.getIdResolucionModif()).getDetalleModificativaList()) {
                    for (DetalleModificativa detalle : resolucionAdjudicativaEJB.findDetalleModificativa(vwContratoModificatoria.getIdResolucionModif())) {
                        if (detalle.getEstadoEliminacion() == 0) {
                            DetalleModificativa detalleModificativa = new DetalleModificativa();
                            detalleModificativa.setIdProducto(detalle.getIdProducto());
                            detalleModificativa.setIdNivelEducativo(detalle.getIdNivelEducativo());
                            detalleModificativa.setCantidadOld(detalle.getCantidadNew());
                            detalleModificativa.setNoItem(detalle.getNoItem());
                            detalleModificativa.setPrecioUnitarioOld(detalle.getPrecioUnitarioNew());
                            detalleModificativa.setConsolidadoEspTec(detalle.getConsolidadoEspTec());

                            PreciosRefRubroEmp precio = proveedorEJB.getPrecioRef(contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdEmpresa(), detalleModificativa.getIdNivelEducativo(), detalleModificativa.getIdProducto(),
                                    detalleProceso.getIdRubroAdq().getIdRubroInteres(), detalleProceso.getIdProcesoAdq().getIdAnho().getIdAnho());

                            detalleModificativa.setPrecioUnitarioNew(precio.getPrecioReferencia());
                            detalleModificativa.setFechaInsercion(new Date());
                            detalleModificativa.setEstadoEliminacion(detalle.getEstadoEliminacion());
                            detalleModificativa.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                            detalleModificativa.setIdResolucionModif(resolucionesModificativas);

                            lstDetalleModificativas.add(detalleModificativa);
                        }
                    }
                } else {//modificación de un contrato
                    for (DetalleOfertas detalle : resolucionAdjudicativaEJB.findDetalleOfertas(contratoOriginal.getIdResolucionAdj().getIdParticipante())) {
                        if (!detalle.getEliminar()) {
                            DetalleModificativa detalleModificativa = new DetalleModificativa();
                            detalleModificativa.setIdProducto(detalle.getIdProducto().getIdProducto());
                            detalleModificativa.setIdNivelEducativo(detalle.getIdNivelEducativo().getIdNivelEducativo());
                            detalleModificativa.setCantidadOld(detalle.getCantidad().intValue());
                            detalleModificativa.setNoItem(detalle.getNoItem());
                            detalleModificativa.setPrecioUnitarioOld(detalle.getPrecioUnitario());
                            detalleModificativa.setConsolidadoEspTec(detalle.getConsolidadoEspTec());

                            PreciosRefRubroEmp precio = proveedorEJB.getPrecioRef(contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdEmpresa(), detalleModificativa.getIdNivelEducativo(), detalleModificativa.getIdProducto(),
                                    detalleProceso.getIdRubroAdq().getIdRubroInteres(), detalleProceso.getIdProcesoAdq().getIdAnho().getIdAnho());

                            detalleModificativa.setPrecioUnitarioNew(precio.getPrecioReferencia());
                            detalleModificativa.setFechaInsercion(new Date());
                            detalleModificativa.setEstadoEliminacion(Short.parseShort("0"));
                            detalleModificativa.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                            detalleModificativa.setIdResolucionModif(resolucionesModificativas);

                            lstDetalleModificativas.add(detalleModificativa);
                        }
                    }
                }

                resolucionesModificativas.setDetalleModificativaList(lstDetalleModificativas);
                resolucionesModificativas.setIdContrato(contratoOriginal);

                if (vwContratoModificatoria.getIdResModifPadre() != null) {
                    resolucionesModificativas.setIdResModifPadre(utilEJB.find(ResolucionesModificativas.class, vwContratoModificatoria.getIdResolucionModif()));
                } else {
                    resolucionesModificativas.setIdResModifPadre(utilEJB.find(ResolucionesModificativas.class, vwContratoModificatoria.getIdResolucionModif()));
                }

                lstTipoModifContratos = modificativaEJB.getLstTipoModifByTipoUsuario(VarSession.getUsuarioSession().getIdTipoUsuario().getIdTipoUsuario());
                lstItem = proveedorEJB.findItemProveedor(contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdEmpresa(), getDetallePrincipal(detalleProceso));
            } else if (vwContratoModificatoria.getIdResolucionAdj() != null
                    && vwContratoModificatoria.getIdResModifPadre() != null
                    && (vwContratoModificatoria.getIdResModifPadre().longValue() == vwContratoModificatoria.getIdResolucionModif().longValue())) {
                JsfUtil.mensajeInformacion("Ha seleccionado el contrato original, y no una modificativa ya creada.");
            } else {
                vwBusquedaContrato = modificativaEJB.getContratosById(vwContratoModificatoria.getIdContrato());
                resolucionesModificativas = utilEJB.find(ResolucionesModificativas.class, vwContratoModificatoria.getIdResolucionModif());
                if (resolucionesModificativas == null) {
                    JsfUtil.mensajeInformacion("Ha seleccionado el contrato original, y no una modificativa ya creada.");
                } else {
                    lstDetalleModificativas = resolucionesModificativas.getDetalleModificativaList();
                    contratoOriginal = resolucionesModificativas.getIdContrato();
                    idRubro = contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres();
                    detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
                    lstTipoModifContratos = modificativaEJB.getLstTipoModifByTipoUsuario(VarSession.getUsuarioSession().getIdTipoUsuario().getIdTipoUsuario());
                    lstItem = proveedorEJB.findItemProveedor(contratoOriginal.getIdResolucionAdj().getIdParticipante().getIdEmpresa(), getDetallePrincipal(detalleProceso));
                    idTipoModif = resolucionesModificativas.getIdModifContrato().getIdModifContrato();
                }
            }
        }
    }

    private DetalleProcesoAdq getDetallePrincipal(DetalleProcesoAdq detallePro) {
        if (detallePro.getIdProcesoAdq().getPadreIdProcesoAdq() != null) {
            for (DetalleProcesoAdq detallePadre : detallePro.getIdProcesoAdq().getPadreIdProcesoAdq().getDetalleProcesoAdqList()) {
                if (detallePro.getIdRubroAdq().getIdRubroInteres().intValue() == detallePadre.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    return detallePadre;
                }
            }
        } else {
            return detallePro;
        }
        return null;
    }

    public void onCargarModificativa(SelectEvent event) {
        if (event.getObject() instanceof VwContratoModificatoria) {
            cargarModif((VwContratoModificatoria) event.getObject());
        }
    }

    private void cargarModif(VwContratoModificatoria vw) {
        vwContratoModificatoria = vw;
        vwBusquedaContrato = modificativaEJB.getContratosById(vwContratoModificatoria.getIdContrato());
        contratoExtinsion.setIdContrato(utilEJB.find(ContratosOrdenesCompras.class, vwContratoModificatoria.getIdContrato()));
        idRubro = contratoExtinsion.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdRubroAdq().getIdRubroInteres();
        resolucionesModificativas = utilEJB.find(ResolucionesModificativas.class, vwContratoModificatoria.getIdResolucionModif());
        if (resolucionesModificativas == null) {
            JsfUtil.mensajeInformacion("Ha seleccionado el contrato original, y no una modificativa ya creada.");
        } else {
            idTipoModif = resolucionesModificativas.getIdModifContrato().getIdModifContrato();
            lstDetalleModificativas = resolucionAdjudicativaEJB.findDetalleModificativa(resolucionesModificativas.getIdResolucionModif());
            techoCE = resolucionAdjudicativaEJB.findTechoRubroEntEdu(resolucionesModificativas.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdOferta().getCodigoEntidad().getCodigoEntidad(),
                    resolucionesModificativas.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdOferta().getIdDetProcesoAdq().getIdDetProcesoAdq());
            idEstadoReserva = resolucionesModificativas.getIdEstadoReserva();

            montoAdjudicacionActual = getMontoAdjudicadoNew().add(getMontoAdjudicadoOld().negate());
            montoSaldo = techoCE.getMontoAdjudicado().add(montoAdjudicacionActual);
        }
    }

    public void buscarSaldoPresupuestoCE() {
        buscarProceso();
        if (detalleProceso != null) {
            techoCE = resolucionAdjudicativaEJB.findTechoRubroEntEdu(codigoEntidad, detalleProceso.getIdDetProcesoAdq());
        }
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

    public String getMontoPresupuestado() {
        return JsfUtil.getFormatoNum(techoCE.getMontoPresupuestado(), false);
    }

    public String getMontoAdjudicado() {
        return JsfUtil.getFormatoNum(techoCE.getMontoAdjudicado(), false);
    }

    public BigDecimal getMontoAdjActual() {
        return montoAdjudicacionActual;
    }

    public String getMontoSaldo() {
        if (resolucionesModificativas == null || resolucionesModificativas.getIdResolucionModif() == null) {
            return "0.00";
        }
        if (techoCE != null) {
            if (resolucionesModificativas == null || resolucionesModificativas.getIdResolucionModif() == null || resolucionesModificativas.getIdEstadoReserva().compareTo(new BigDecimal("2")) == 0) {
                saldoActual = techoCE.getMontoDisponible();
            } else if (resolucionesModificativas.getIdEstadoReserva().compareTo(new BigDecimal("4")) == 0) { //reserva anulada
                saldoActual = techoCE.getMontoDisponible();
            } else {
                saldoActual = techoCE.getMontoDisponible().add(montoSaldo);
            }

            negativo = saldoActual.compareTo(BigDecimal.ZERO) == -1;
            positivo = saldoActual.compareTo(BigDecimal.ZERO) == 1;
            return JsfUtil.getFormatoNum(saldoActual, false);
        } else {
            return "0.00";
        }
    }

    public void onRowSelect(SelectEvent event) {
        vwBusquedaContrato = (VwBusquedaContratos) event.getObject();
    }

    public void cerrarFiltro() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void validarExtension() {
        String msj = "";
        if (contratoExtinsion.getIdContrato() == null) {
            msj = "Debe de selecionar un contrato válido";
        } else if (contratoExtinsion.getIdModifContrato() == null) {
            msj = "Debe de sleccionar un tipo de extinsión";
        } else if (contratoExtinsion.getJustificacionModificativa().trim().isEmpty()) {
            msj = "Debe de ingresar una observación";
        }

        if (msj.isEmpty()) {
            guardarExtinsion();
        } else {
            JsfUtil.mensajeAlerta(msj);
        }
    }

    public void validarAprobExtension() {
        String msj = "";
        if (resolucionesModificativas.getIdContrato() == null) {
            msj = "Debe de selecionar un contrato válido";
        } else if (idTipoModif == null) {
            msj = "Debe de sleccionar un tipo de extinsión";
        } else if (resolucionesModificativas.getJustificacionModificativa().trim().isEmpty()) {
            msj = "Debe de ingresar una observación";
        }

        if (msj.isEmpty()) {
            PrimeFaces.current().executeScript("PF('dlgImprimir').show()");
        } else {
            JsfUtil.mensajeAlerta(msj);
        }
    }

    public void guardarExtinsion() {
        //contratoExtinsion.setIdEstadoContrato(new EstadoContrato(2));
        //if (contratoExtinsion.getIdContratoEstado() == null) {
        contratoExtinsion.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        contratoExtinsion.setFechaInsercion(new Date());
        contratoExtinsion.setEstadoEliminacion(Short.parseShort("0"));
        contratoExtinsion.setDiasProrroga(0);
        contratoExtinsion.setValor(BigDecimal.ZERO);
        contratoExtinsion.setIdEstadoReserva(BigDecimal.ONE);

        modificativaEJB.guardarExtision(contratoExtinsion);

        contratoOriginal = contratoExtinsion.getIdContrato();
        contratoOriginal.setModificativa(Short.parseShort("0"));
        contratoOriginal.setActivo(BigInteger.ZERO);
        contratoOriginal.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        contratoOriginal.setFechaModificacion(new Date());
        resolucionAdjudicativaEJB.editContrato(contratoOriginal);

        /*HashMap<String, Object> param = resolucionAdjudicativaEJB.aplicarReservaDeFondos(contratoOriginal.getIdResolucionAdj(), new EstadoReserva(new BigDecimal(3)), "Reversión por Extinsión de Contrato", VarSession.getVariableSessionUsuario());
         param = resolucionAdjudicativaEJB.aplicarReservaDeFondos(contratoOriginal.getIdResolucionAdj(), new EstadoReserva(new BigDecimal(4)), "Anulación por Extinsión de Contrato", VarSession.getVariableSessionUsuario());*/
        JsfUtil.mensajeInsert();
        deshabilitar = true;
        //}
    }

    public void aprobarExtinsion() {
        resolucionesModificativas.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        resolucionesModificativas.setFechaModificacion(new Date());
        modificativaEJB.guardarExtision(resolucionesModificativas);

        HashMap<String, Object> param = resolucionAdjudicativaEJB.aplicarReservaDeFondos(resolucionesModificativas.getIdContrato().getIdResolucionAdj(),
                new BigDecimal(3), codigoEntidad, "Reversión por Extinsión de Contrato", VarSession.getVariableSessionUsuario());
        param = resolucionAdjudicativaEJB.aplicarReservaDeFondos(resolucionesModificativas.getIdContrato().getIdResolucionAdj(),
                new BigDecimal(4), codigoEntidad, "Anulación por Extinsión de Contrato", VarSession.getVariableSessionUsuario());

        JsfUtil.mensajeUpdate();
    }

    public void imprimirDocumentos() {
        Boolean isPersonaNatural = (resolucionesModificativas.getIdContrato().getIdResolucionAdj().getIdParticipante().getIdEmpresa().getIdPersoneria().getIdPersoneria().intValue() == 1);

        switch (idTipoModif.intValue()) {
            case 1:
            case 4:
            case 5:
            case 6:
            case 3:
                imprimirResolucionModificatoria(idTipoModif.intValue(), isPersonaNatural);
                break;
            case 2:
                //case 3:
                imprimirContratoFormatoOriginal(isPersonaNatural);
                break;
        }
    }

    private void imprimirResolucionModificatoria(int idModif, Boolean isPersonaNatural) {
        try {
            String nombreRpt = "";

            List<JasperPrint> jasperPrintList = new ArrayList();
            List<ContratoDto> lstModificacionContratos;

            switch (idModif) {
                case 1:
                case 6:
                    nombreRpt = "rptModCantSinIncrementoMonto";
                    break;
                case 4:
                    nombreRpt = "rptModProrroga";
                    break;
                case 3:
                case 5:
                    nombreRpt = "rptModIncrementoMonto";
                    break;
            }

            lstModificacionContratos = modificativaEJB.generarResolucionModificativa(resolucionesModificativas);

            lstModificacionContratos.get(0).setFechaCreacionModif(lstModificacionContratos.get(0).getFechaResolucion());

            if (nombreRpt.contains("rptModProrroga")) {
                lstModificacionContratos.get(0).setFechaInicio(sumarRestarDiasFecha(resolucionesModificativas.getIdContrato().getFechaOrdenInicio(), 60));
                lstModificacionContratos.get(0).setFechaFin(sumarRestarDiasFecha(resolucionesModificativas.getIdContrato().getFechaOrdenInicio(), 60 + resolucionesModificativas.getDiasProrroga()));
            }

            if (isPersonaNatural) {
                jasperPrintList.add(imprimirRpt(nombreRpt + "PerNat.jasper", lstModificacionContratos, "modificatoriaContrato"));
            } else {
                jasperPrintList.add(imprimirRpt(nombreRpt + "PerJur.jasper", lstModificacionContratos, "modificatoriaContrato"));
            }

            Reportes.generarReporte(jasperPrintList, "modificatoriaContrato");
        } catch (IOException | JRException ex) {
            Logger.getLogger(ModificatoriaController.class.getName()).log(Level.WARNING, "Error en la impresion de los documentos de modificativa {0}", resolucionesModificativas);
        }
    }

    public Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0

        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

    }

    private void imprimirContratoFormatoOriginal(Boolean isPersonaNatural) {
        try {
            List<RptDocumentos> lstRptDocumentos;
            List<Integer> lstRpt = new ArrayList();
            lstRpt.add(7);

            lstRptDocumentos = resolucionAdjudicativaEJB.getDocumentosAImprimir(detalleProceso.getIdDetProcesoAdq(), lstRpt);

            List<JasperPrint> jasperPrintList
                    = ((ContratosOrdenesComprasController) FacesContext.getCurrentInstance().getApplication().getELResolver().
                            getValue(FacesContext.getCurrentInstance().getELContext(), null, "contratosOrdenesComprasController")).
                            imprimirDesdeModificativa(lstRptDocumentos, isPersonaNatural, resolucionesModificativas.getIdContrato(), codigoEntidad);

            Reportes.generarReporte(jasperPrintList, "documentos_" + codigoEntidad);
        } catch (IOException | JRException ex) {
            Logger.getLogger(ModificatoriaController.class.getName()).log(Level.WARNING, "Error en la impresion de los documentos del contrato original {0}", resolucionesModificativas.getIdContrato());
        }
    }

    public JasperPrint imprimirRpt(String nombreRpt, List<?> lst, String nombrePdf) {
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lst);

            HashMap param = new HashMap();
            param.put("SUBREPORT_DIR", JsfUtil.getPathReportes().concat(Reportes.PATH_REPORTES) + File.separator);
            param.put("ubicacionImagenes", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
            param.put("pJustificacion", resolucionesModificativas.getJustificacionModificativa());
            param.put("pPlazoEntrega", resolucionesModificativas.getIdContrato().getPlazoPrevistoEntrega().intValue());
            if (nombreRpt.contains("rptModProrroga")) {
                param.put("JUSTIFICACION", resolucionesModificativas.getJustificacionModificativa());
                param.put("DIAS_PRORROGA", resolucionesModificativas.getDiasProrroga().toString());
            }

            return JasperFillManager.
                    fillReport(Reportes.getPathReporte("sv/gob/mined/apps/reportes/contratos/modificativas" + File.separator + nombreRpt), param, ds);
        } catch (JRException ex) {
            Logger.getLogger(ModificatoriaController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(int tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public List<VwDepartamentoModificativas> getLstCeDetModificativas() {
        return lstCeDetModificativas;
    }

    public List<VwDetalleModificativas> getLstProDetModificativas() {
        return lstProDetModificativas;
    }

    public void buscarDetalleModificativas() {
        if (tipoConsulta == 1) {
            lstCeDetModificativas = modificativaEJB.lstDetalleModificativasOrderDepa(detalleProceso.getIdDetProcesoAdq());
        } else {
            lstProDetModificativas = modificativaEJB.lstDetalleModificativasOrderProveedor(detalleProceso.getIdDetProcesoAdq());
        }
    }

    public void buscarTerminacionesContratos() {
        lstProDetModificativas = modificativaEJB.lstTerminacionContratos(detalleProceso.getIdDetProcesoAdq());
    }
}
