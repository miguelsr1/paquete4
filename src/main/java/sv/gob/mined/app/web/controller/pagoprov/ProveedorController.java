/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.RptExcel;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.CreditosEJB;
import sv.gob.mined.paquescolar.ejb.DatosGeograficosEJB;
import sv.gob.mined.paquescolar.ejb.PreciosReferenciaEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.CapaDistribucionAcre;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.CatalogoProducto;
import sv.gob.mined.paquescolar.model.ContratosOrdenesCompras;
import sv.gob.mined.paquescolar.model.Departamento;
import sv.gob.mined.paquescolar.model.DetRubroMuestraInteres;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.DisMunicipioInteres;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.EntidadFinanciera;
import sv.gob.mined.paquescolar.model.Municipio;
import sv.gob.mined.paquescolar.model.NivelEducativo;
import sv.gob.mined.paquescolar.model.PreciosRefRubro;
import sv.gob.mined.paquescolar.model.PreciosRefRubroEmp;
import sv.gob.mined.paquescolar.model.ProcesoAdquisicion;
import sv.gob.mined.paquescolar.model.RubrosAmostrarInteres;
import sv.gob.mined.paquescolar.model.pojos.proveedor.DetalleAdjudicacionEmpDto;
import sv.gob.mined.paquescolar.model.pojos.proveedor.MunicipioDto;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class ProveedorController extends RecuperarProcesoUtil implements Serializable {

    private int idRow;

    private Boolean resetUsuario = false;
    private Boolean resetAceptacion = false;
    private Boolean deshabiliar = true;
    private Boolean showFoto = true;
    private Boolean showCapacidadAdjudicada = false;
    private Boolean showUpdateEmpresa = false;
    private String nit;

    private String numeroNit;
    private String rowEdit;
    private String fotoProveedor;
    private String estiloZapato;
    private String tapEmpresa;
    private String tapPersona;
    private String codigoDepartamentoCalificado;
    private String fileName = "fotoProveedores/profile.png";
    private String codEntFinanciera;
    private String url;
    private String msjError = "";
    private String codigoDepartamento = "";
    private String codigoDepartamentoLocal = "";
    private BigDecimal idMunicipioLocal = BigDecimal.ZERO;

    private BigDecimal idAnho = BigDecimal.ZERO;
    private BigDecimal rubro = BigDecimal.ZERO;
    private BigDecimal idMunicipio = BigDecimal.ZERO;
    private BigDecimal totalItems = BigDecimal.ZERO;
    private BigDecimal totalMonto = BigDecimal.ZERO;
    private File carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/");

    private Municipio municipio = new Municipio();
    private DetalleProcesoAdq detalleProcesoAdq = new DetalleProcesoAdq();
    private RubrosAmostrarInteres rubrosAmostrarInteres = new RubrosAmostrarInteres();
    private Empresa empresa = new Empresa();
    private CapaDistribucionAcre departamentoCalif = new CapaDistribucionAcre();
    private CapaInstPorRubro capacidadInst = new CapaInstPorRubro();
    private PreciosRefRubroEmp precioRef = new PreciosRefRubroEmp();
    private PreciosRefRubro preMaxRefPar = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefCi = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefCii = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefCiii = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefCiiiMf = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefBac = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefBacMf = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG1 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG2 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG3 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG4 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG5 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG6 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG7 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG8 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefG9 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefB1 = new PreciosRefRubro();
    private PreciosRefRubro preMaxRefB2 = new PreciosRefRubro();

    private List<String> images = new ArrayList();

    private List<MunicipioDto> lstMunSource = new ArrayList();
    private List<MunicipioDto> lstMunTarget = new ArrayList();
    private List<CatalogoProducto> lstItem = new ArrayList();
    private List<PreciosRefRubroEmp> lstPreciosReferencia = new ArrayList();
    private List<DetalleAdjudicacionEmpDto> lstResumenAdj = new ArrayList();
    private List<DetalleAdjudicacionEmpDto> lstDetalleAdj = new ArrayList();
    private DualListModel<MunicipioDto> lstMunicipiosInteres = new DualListModel();
    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private DatosGeograficosEJB datosGeograficosEJB;
    @Inject
    private UtilEJB utilEJB;
    @Inject
    private ReportesEJB reportesEJB;
    @Inject
    private CreditosEJB creditosEJB;
    @Inject
    private PreciosReferenciaEJB preciosReferenciaEJB;

    /**
     * Creates a new instance of ProveedorController
     */
    public ProveedorController() {
    }

    @PostConstruct
    public void ini() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        url = req.getRequestURL().toString();

        if (VarSession.isVariableSession("idEmpresa")) {
            empresa = proveedorEJB.findEmpresaByPk((BigDecimal) VarSession.getVariableSession("idEmpresa"));
            showUpdateEmpresa = ((Integer) VarSession.getVariableSession("idTipoUsuario") == 1);
            cargarDetalleCalificacion();
            if (url.contains("DatosGenerales")) {
                idMunicipio = empresa.getIdPersona().getIdMunicipio().getIdMunicipio();
                codigoDepartamento = empresa.getIdPersona().getIdMunicipio().getCodigoDepartamento().getCodigoDepartamento();
            } else if (url.contains("MunicipiosInteres")) {
                cargarMunInteres();
            } else if (url.contains("PreciosReferencia") && getRecuperarProceso().getProcesoAdquisicion() != null && getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq() != null) {
                cargarPrecioRef();
            } else if (url.contains("FotografiaMuestras") && getRecuperarProceso().getProcesoAdquisicion() != null && getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq() != null) {
            }
        }

        if (VarSession.getVariableSessionUsuario().equals("MSANCHEZ")) {
            showCapacidadAdjudicada = true;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public Boolean getResetUsuario() {
        return resetUsuario;
    }

    public void setResetUsuario(Boolean resetUsuario) {
        this.resetUsuario = resetUsuario;
    }

    public Boolean getResetAceptacion() {
        return resetAceptacion;
    }

    public void setResetAceptacion(Boolean resetAceptacion) {
        this.resetAceptacion = resetAceptacion;
    }

    public String getCodigoDepartamentoLocal() {
        return codigoDepartamentoLocal;
    }

    public void setCodigoDepartamentoLocal(String codigoDepartamentoLocal) {
        this.codigoDepartamentoLocal = codigoDepartamentoLocal;
    }

    public BigDecimal getIdMunicipioLocal() {
        return idMunicipioLocal;
    }

    public void setIdMunicipioLocal(BigDecimal idMunicipioLocal) {
        this.idMunicipioLocal = idMunicipioLocal;
    }

    public BigDecimal getRubro() {
        return rubro;
    }

    public void setRubro(BigDecimal rubro) {
        this.rubro = rubro;
    }

    public List<DetalleAdjudicacionEmpDto> getLstDetalleAdj() {
        return lstDetalleAdj;
    }

    public CapaDistribucionAcre getDepartamentoCalif() {
        return departamentoCalif;
    }

    public void setDepartamentoCalif(CapaDistribucionAcre departamentoCalif) {
        this.departamentoCalif = departamentoCalif;
    }

    public String getCodigoDepartamentoCalificado() {
        return codigoDepartamentoCalificado;
    }

    public void setCodigoDepartamentoCalificado(String codigoDepartamentoCalificado) {
        this.codigoDepartamentoCalificado = codigoDepartamentoCalificado;
    }

    public String getCodEntFinanciera() {
        return codEntFinanciera;
    }

    public void setCodEntFinanciera(String entidadFinanciera) {
        this.codEntFinanciera = entidadFinanciera;
    }

    public List<EntidadFinanciera> getLstEntidades() {
        return creditosEJB.findEntidadFinancieraEntities((short) 1);
    }

    public String getEstiloZapato() {
        return estiloZapato;
    }

    public void setEstiloZapato(String estiloZapato) {
        this.estiloZapato = estiloZapato;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Boolean getShowCapacidadAdjudicada() {
        return showCapacidadAdjudicada;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public void setShowCapacidadAdjudicada(Boolean showCapacidadAdjudicada) {
        this.showCapacidadAdjudicada = showCapacidadAdjudicada;
    }

    public String getFotoProveedor() {
        return fotoProveedor;
    }

    public void setFotoProveedor(String fotoProveedor) {
        this.fotoProveedor = fotoProveedor;
    }

    public Boolean getShowFoto() {
        return showFoto;
    }

    public void setShowFoto(Boolean showFoto) {
        this.showFoto = showFoto;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public BigDecimal getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(BigDecimal idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Boolean getDeshabiliar() {
        return deshabiliar;
    }

    public void setDeshabiliar(Boolean deshabiliar) {
        this.deshabiliar = deshabiliar;
    }

    public CapaInstPorRubro getCapacidadInst() {
        if (capacidadInst == null) {
            capacidadInst = new CapaInstPorRubro();
        }
        return capacidadInst;
    }

    public void setCapacidadInst(CapaInstPorRubro capacidadInst) {
        this.capacidadInst = capacidadInst;
    }

    public List<Departamento> getLstDepartamentos() {
        return datosGeograficosEJB.getLstDepartamentos();
    }

    public DualListModel<MunicipioDto> getLstMunicipiosInteres() {
        return lstMunicipiosInteres;
    }

    public void setLstMunicipiosInteres(DualListModel<MunicipioDto> lstMunicipiosInteres) {
        if (!(lstMunicipiosInteres.getSource().isEmpty() && lstMunicipiosInteres.getTarget().isEmpty())) {
            this.lstMunicipiosInteres = lstMunicipiosInteres;
        }
    }

    public DetalleProcesoAdq getDetalleProcesoAdq() {
        return detalleProcesoAdq;
    }

    public void setDetalleProcesoAdq(DetalleProcesoAdq detalleProcesoAdq) {
        this.detalleProcesoAdq = detalleProcesoAdq;
    }

    public RubrosAmostrarInteres getRubrosAmostrarInteres() {
        return rubrosAmostrarInteres;
    }

    public void setRubrosAmostrarInteres(RubrosAmostrarInteres rubrosAmostrarInteres) {
        this.rubrosAmostrarInteres = rubrosAmostrarInteres;
    }

    public List<PreciosRefRubroEmp> getLstPreciosReferencia() {
        return lstPreciosReferencia;
    }

    public void setLstPreciosReferencia(List<PreciosRefRubroEmp> lstPreciosReferencia) {
        this.lstPreciosReferencia = lstPreciosReferencia;
    }

    public PreciosRefRubroEmp getPrecioRef() {
        return precioRef;
    }

    public void setPrecioRef(PreciosRefRubroEmp preciosRef) {
        this.precioRef = preciosRef;
    }

    public int getIdRow() {
        return idRow;
    }

    public void setIdRow(int idRow) {
        this.idRow = idRow;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }

    public List<CatalogoProducto> getLstItem() {
        return lstItem;
    }

    public void setLstItem(List<CatalogoProducto> lstItem) {
        this.lstItem = lstItem;
    }

    public List<DetalleAdjudicacionEmpDto> getLstResumenAdj() {
        return lstResumenAdj;
    }

    public String getTotalItems() {
        return JsfUtil.getFormatoNum(totalItems, true);
    }

    public String getTotalMonto() {
        return JsfUtil.getFormatoNum(totalMonto, false);
    }

    public List<Municipio> getLstMunicipios() {
        return datosGeograficosEJB.getLstMunicipiosByDepartamento(codigoDepartamento);
    }

    public List<Municipio> getLstMunicipiosLocal() {
        return datosGeograficosEJB.getLstMunicipiosByDepartamento(codigoDepartamentoLocal);
    }

    public Boolean getShowUpdateEmpresa() {
        return showUpdateEmpresa;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String getTapEmpresa() {
        if (empresa == null) {
            tapEmpresa = "";
        } else if (empresa.getIdPersoneria().getIdPersoneria() == null) {
            tapEmpresa = "";
        } else if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 2) {
            tapEmpresa = "Empresa";
        } else {
            tapEmpresa = "";
        }
        return tapEmpresa;
    }

    public String getTapPersona() {
        if (empresa == null) {
            tapPersona = "";
        } else if (empresa.getIdPersoneria().getIdPersoneria() == null) {
            tapPersona = "";
        } else if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 2) {
            tapPersona = "Representante Legal";
        } else {
            tapPersona = "Proveedor";
        }
        return tapPersona;
    }

    public String getNumeroNit() {
        return numeroNit;
    }

    public void setNumeroNit(String numeroNit) {
        this.numeroNit = numeroNit;
    }
    // </editor-fold>  

    public void cargarFotografias() {
        carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + empresa.getNumeroNit() + File.separator);
        if (!carpetaNfs.exists()) {
            System.out.println("Se creo carpeta: " + empresa.getNumeroNit() + " - " + carpetaNfs.mkdir());
        }
        carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + empresa.getNumeroNit() + File.separator + estiloZapato + File.separator);
        if (!carpetaNfs.exists()) {
            System.out.println("Se creo carpeta: " + empresa.getNumeroNit() + File.separator + estiloZapato + File.separator + " - " + carpetaNfs.mkdir());
        }

        carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + empresa.getNumeroNit() + File.separator + estiloZapato + File.separator);
        images = new ArrayList();

        if (carpetaNfs.list() != null) {
            for (String string : carpetaNfs.list()) {
                images.add("Fotos_Zapatos/" + empresa.getNumeroNit() + "/" + estiloZapato + "/" + string);
            }
        }
    }

    public void filtroProveedores() {
        empresa = new Empresa();
        capacidadInst = new CapaInstPorRubro();
        lstPreciosReferencia.clear();
        lstMunSource.clear();
        lstMunTarget.clear();
        fileName = "fotoProveedores/profile.png";
        showFoto = true;
        deshabiliar = true;
        images.clear();
        Map<String, Object> options = new HashMap();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("contentHeight", 500);
        options.put("contentWidth", 850);

        PrimeFaces.current().dialog().openDynamic("/app/comunes/dialogos/proveedor/filtroProveedor", options, null);
    }

    public void dlgFotografia() {
        if (empresa == null || empresa.getIdEmpresa() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un empresa");
        } else {
            deshabiliar = true;
            Map<String, Object> options = new HashMap();
            options.put("modal", true);
            options.put("draggable", false);
            options.put("resizable", false);
            options.put("contentHeight", 400);
            options.put("contentWidth", 554);
            VarSession.setVariableSession("nitEmpresa", empresa.getNumeroNit());
            PrimeFaces.current().dialog().openDynamic("/app/comunes/filtroFotoProveedor", options, null);
        }
    }

    public void dlgFotografiaZapato() {
        if (empresa == null || empresa.getIdEmpresa() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un empresa");
        } else {
            deshabiliar = true;
            Map<String, Object> options = new HashMap();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("position", "center center");
            options.put("resizable", false);
            options.put("contentHeight", 600);
            options.put("contentWidth", 660);
            VarSession.setVariableSession("nitEmpresa", empresa.getNumeroNit());
            VarSession.setVariableSession("carpetaFoto", carpetaNfs.getAbsolutePath());
            PrimeFaces.current().dialog().openDynamic("/app/control/oferentes/fotografia", options, null);
        }
    }

    private void cargarDetalleCalificacion() {
        ProcesoAdquisicion proceso = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getProceso();
        idAnho = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getAnho().getIdAnho();
        if (proceso == null || proceso.getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe seleccionar un proceso de contratación");
        } else {
            if (proceso.getPadreIdProcesoAdq() != null) {
                proceso = proceso.getPadreIdProcesoAdq();
            }
            DetRubroMuestraInteres detRubro = proveedorEJB.findDetRubroByAnhoAndRubro(idAnho, empresa.getIdEmpresa());
            capacidadInst = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), idAnho, empresa, CapaInstPorRubro.class);
            if (capacidadInst == null) {
                JsfUtil.mensajeAlerta("No se han cargado los datos de este proveedor para el proceso de contratación del año " + proceso.getIdAnho().getAnho());
            } else {
                detalleProcesoAdq = JsfUtil.findDetalleByRubroAndAnho(proceso,
                        capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres(),
                        capacidadInst.getIdMuestraInteres().getIdAnho().getIdAnho());
                departamentoCalif = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), idAnho, empresa, CapaDistribucionAcre.class);
                /**
                 * Fecha: 30/08/2018 Comentario: Adición de validación de
                 * departamento calificado para proveedor
                 */

                if (departamentoCalif == null || departamentoCalif.getCodigoDepartamento() == null) {
                    JsfUtil.mensajeAlerta("Este proveedor no posee departamento de calificación " + proceso.getIdAnho().getAnho());
                } else {
                    codigoDepartamentoCalificado = departamentoCalif.getCodigoDepartamento().getCodigoDepartamento();

                    idMunicipioLocal = empresa.getIdMunicipio().getIdMunicipio();
                    codigoDepartamentoLocal = empresa.getIdMunicipio().getCodigoDepartamento().getCodigoDepartamento();

                    deshabiliar = false;
                    if (empresa.getIdPersona().getUrlImagen() == null) {
                        fileName = "fotoProveedores/profile.png";
                    } else {
                        fileName = "fotoProveedores/" + empresa.getIdPersona().getUrlImagen();
                    }
                }
            }
        }
    }

    public void empresaSeleccionada(SelectEvent event) {
        if (event.getObject() != null) {
            if (event.getObject() instanceof Empresa) {
                empresa = (Empresa) event.getObject();

                if (url.contains("DatosGenerales")) {
                    idMunicipio = empresa.getIdPersona().getIdMunicipio().getIdMunicipio();
                    codigoDepartamento = empresa.getIdPersona().getIdMunicipio().getCodigoDepartamento().getCodigoDepartamento();
                }

                if (empresa.getIdMunicipio() == null) {
                    empresa.setIdMunicipio(utilEJB.find(Municipio.class,
                            BigDecimal.ONE));

                    if (empresa.getIdPersona().getIdMunicipio() == null) {
                        empresa.getIdPersona().setIdMunicipio(utilEJB.find(Municipio.class,
                                BigDecimal.ONE));
                    }
                }
                VarSession.setVariableSession("idEmpresa", empresa.getIdEmpresa());
                cargarDetalleCalificacion();
                showUpdateEmpresa = ((Integer) VarSession.getVariableSession("idTipoUsuario") == 1);
            }
        } else {
            deshabiliar = false;
            JsfUtil.mensajeAlerta("Debe de seleccionar una empresa");
        }
    }

    public void empSelecMuniInteres(SelectEvent event) {
        if (event.getObject() != null) {
            if (event.getObject() instanceof Empresa) {
                empresa = (Empresa) event.getObject();
                VarSession.setVariableSession("idEmpresa", empresa.getIdEmpresa());
                cargarMunInteres();
            } else {
                Logger.getLogger(ProveedorController.class
                        .getName()).log(Level.INFO, "No se pudo convertir el objeto a la clase Empresa{0}", event.getObject());
            }
        } else {
            deshabiliar = false;
            JsfUtil.mensajeAlerta("Debe de seleccionar una empresa");
        }
    }

    private void cargarMunInteres() {
        cargarDetalleCalificacion();
        if (capacidadInst != null && capacidadInst.getIdCapInstRubro() != null) {
            if (departamentoCalif != null && departamentoCalif.getCodigoDepartamento() != null) {
                lstMunSource = datosGeograficosEJB.getLstMunicipiosDisponiblesDeInteres(departamentoCalif.getIdCapaDistribucion(), departamentoCalif.getCodigoDepartamento().getCodigoDepartamento());
                lstMunTarget = datosGeograficosEJB.getLstMunicipiosDeInteres(departamentoCalif.getIdCapaDistribucion());
                lstMunicipiosInteres = new DualListModel(lstMunSource, lstMunTarget);
            }
        }
    }

    public void empSelecPrecioRef(SelectEvent event) {
        if (event.getObject() != null) {
            if (event.getObject() instanceof Empresa) {
                empresa = (Empresa) event.getObject();
                fileName = "fotoProveedores/" + empresa.getIdPersona().getFoto();
                VarSession.setVariableSession("idEmpresa", empresa.getIdEmpresa());
                cargarDetalleCalificacion();
                if (capacidadInst != null && capacidadInst.getIdCapInstRubro() != null) {
                    cargarPrecioRef();
                }
            } else {
                Logger.getLogger(ProveedorController.class
                        .getName()).log(Level.WARNING, "No se pudo convertir el objeto a la clase Empresa{0}", event.getObject());
            }
        }
        estiloZapato = "0";
    }

    private void cargarPrecioRef() {
        if (capacidadInst != null && capacidadInst.getIdCapInstRubro() != null) {
            rubrosAmostrarInteres = capacidadInst.getIdMuestraInteres().getIdRubroInteres();
            lstItem = proveedorEJB.findItemProveedor(empresa, detalleProcesoAdq);
            lstPreciosReferencia = proveedorEJB.findPreciosRefRubroEmpRubro(getEmpresa(),
                    getDetalleProcesoAdq().getIdRubroAdq().getIdRubroInteres(),
                    getDetalleProcesoAdq().getIdProcesoAdq().getIdAnho().getIdAnho());
            switch (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue()) {
                case 9://año 2021
                    if (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue() != 1) { //utiles y zapatos
                        preMaxRefPar = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(22), detalleProcesoAdq);
                    }
                    preMaxRefCi = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(3), detalleProcesoAdq);
                    preMaxRefCii = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(4), detalleProcesoAdq);
                    preMaxRefCiii = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(5), detalleProcesoAdq);
                    preMaxRefBac = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(6), detalleProcesoAdq);

                    if (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue() == 2) { //utiles
                        preMaxRefCiiiMf = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(23), detalleProcesoAdq);
                        preMaxRefBacMf = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(24), detalleProcesoAdq);
                    }
                    break;
                default:
                    preMaxRefPar = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(BigDecimal.ONE, detalleProcesoAdq);
                    preMaxRefCi = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(3), detalleProcesoAdq);
                    preMaxRefCii = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(4), detalleProcesoAdq);
                    preMaxRefCiii = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(5), detalleProcesoAdq);
                    preMaxRefBac = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(6), detalleProcesoAdq);
                    if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() > 6
                            && detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue() == 2) {
                        //procesos de contratación mayores a 2018 y rubro de utiles
                        preMaxRefG1 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(10), detalleProcesoAdq);
                        preMaxRefG2 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(11), detalleProcesoAdq);
                        preMaxRefG3 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(12), detalleProcesoAdq);
                        preMaxRefG4 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(13), detalleProcesoAdq);
                        preMaxRefG5 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(14), detalleProcesoAdq);
                        preMaxRefG6 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(15), detalleProcesoAdq);
                        preMaxRefG7 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(7), detalleProcesoAdq);
                        preMaxRefG8 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(8), detalleProcesoAdq);
                        preMaxRefG9 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(9), detalleProcesoAdq);
                        preMaxRefB1 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(16), detalleProcesoAdq);
                        preMaxRefB2 = preciosReferenciaEJB.findPreciosRefRubroByNivelEduAndRubro(new BigDecimal(17), detalleProcesoAdq);
                    }
                    break;
            }
        }
        PrimeFaces.current().ajax().update("frmPrincipal");
    }

    public void guardarDatosGenerales() {
        if (showUpdateEmpresa) {
            if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) {
                empresa.setRazonSocial(empresa.getIdPersona().getNombreCompletoProveedor());
                empresa.setIdMunicipio(utilEJB.find(Municipio.class, idMunicipioLocal));
                empresa.setDireccionCompleta(empresa.getIdPersona().getDomicilio());
                empresa.setTelefonos(empresa.getIdPersona().getNumeroTelefono());
                empresa.setNumeroCelular(empresa.getIdPersona().getNumeroCelular());
                empresa.setNumeroNit(empresa.getIdPersona().getNumeroNit());
            }
            if (empresa.getIdPersona().getIdMunicipio().getIdMunicipio().intValue() != idMunicipio.intValue()) {
                empresa.getIdPersona().setIdMunicipio(new Municipio(idMunicipio));
            }

            proveedorEJB.guardar(empresa);

        }

        departamentoCalif.setCodigoDepartamento(utilEJB.find(Departamento.class,
                codigoDepartamentoCalificado));

        if (proveedorEJB.guardarCapaInst(departamentoCalif, capacidadInst)) {
            JsfUtil.mensajeUpdate();
        }
    }

    public void guardarMunicipioInteres() {
        List<DisMunicipioInteres> lstMunicipioIntereses = proveedorEJB.findMunicipiosInteres(departamentoCalif);

        lstMunicipioIntereses.forEach((disMunicipioInteres) -> {
            disMunicipioInteres.setEstadoEliminacion(BigInteger.ONE);
        });

        if (!(lstMunicipiosInteres.getSource().isEmpty() && lstMunicipiosInteres.getTarget().isEmpty())) {
            for (MunicipioDto mun : getLstMunicipiosInteres().getTarget()) {
                Boolean existe = false;
                for (DisMunicipioInteres disMunicipioInteres : lstMunicipioIntereses) {
                    if (disMunicipioInteres.getIdMunicipio().getIdMunicipio().compareTo(mun.getIdMunicipio()) == 0) {
                        disMunicipioInteres.setEstadoEliminacion(BigInteger.ZERO);
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    DisMunicipioInteres disMunicipio = new DisMunicipioInteres();
                    disMunicipio.setEstadoEliminacion(BigInteger.ZERO);
                    disMunicipio.setFechaInsercion(new Date());
                    disMunicipio.setIdCapaDistribucion(departamentoCalif);
                    disMunicipio.setIdMunicipio(new Municipio(mun.getIdMunicipio()));
                    disMunicipio.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                    lstMunicipioIntereses.add(disMunicipio);
                }
            }

            Boolean val = false;
            for (DisMunicipioInteres disMunicipioInteres : lstMunicipioIntereses) {
                val = proveedorEJB.guardar(disMunicipioInteres);
                if (val == false) {
                    break;
                }
            }

            if (val) {
                JsfUtil.mensajeUpdate();
            } else {
                JsfUtil.mensajeError("Ha ocurrido un error en el registro de los datos.<br/>Reportar por favor al adminsitrador del sistema");
            }
        }
    }

    public void guardarPreciosRef() {
        if (detalleProcesoAdq.getIdDetProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de contratación");
        } else {
            String msj;
            Boolean preciosValidos = true;
            for (PreciosRefRubroEmp precio : lstPreciosReferencia) {
                if (precio.getNoItem() != null && !precio.getNoItem().isEmpty() && precio.getIdNivelEducativo() != null && precio.getIdProducto() != null && precio.getPrecioReferencia() != null && precio.getPrecioReferencia().compareTo(BigDecimal.ZERO) == 1) {
                } else {
                    preciosValidos = false;
                    break;
                }
            }

            if (preciosValidos) {
                lstPreciosReferencia.forEach((precio) -> {
                    proveedorEJB.guardar(precio);
                });
                lstPreciosReferencia = proveedorEJB.findPreciosRefRubroEmpRubro(getEmpresa(),
                        getDetalleProcesoAdq().getIdRubroAdq().getIdRubroInteres(),
                        getDetalleProcesoAdq().getIdProcesoAdq().getIdAnho().getIdAnho());

                msj = "Actualización exitosa";

                if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() > 8) { // anho mayor de 2020
                    //validación de ingreso de todos los item para el rubro de uniforme
                    if (detalleProcesoAdq.getIdRubroAdq().getIdRubroUniforme().intValue() == 1) {
                        if (lstPreciosReferencia.size() < 12) {
                            msj = "Se han guardado los precios, pero es necesario que registre todos los item disponibles.";
                        }
                    }
                }

                JsfUtil.mensajeInformacion(msj);

            } else {
                JsfUtil.mensajeInformacion("Los precios de referencia no han sido guardados debido a que existen datos incompletos o erroneos.");
            }
        }
    }

    public void onCellEdit(CellEditEvent event) {
        msjError = "";
        //DataTable tbl = (DataTable) event.getSource();
        FacesContext context = FacesContext.getCurrentInstance();
        precioRef = context.getApplication().evaluateExpressionGet(context, "#{precio}", PreciosRefRubroEmp.class);
        boolean valido = true;
        if (!valido) {
            precioRef.setIdProducto(null);
            precioRef.setIdNivelEducativo(null);
        } else {
            this.rowEdit = String.valueOf(event.getRowIndex());
            if (event.getColumn().getColumnKey().contains("item")) {
                String numItem = event.getNewValue().toString();
                editarNumeroDeItem(event.getRowIndex(), numItem);
            } else if (event.getColumn().getColumnKey().contains("precio")) {
                agregarPrecio();
            }
        }
    }

    public void updateFilaDetalle() {
        if (rowEdit != null) {
            PrimeFaces.current().ajax().update("tblDetallePrecio:" + rowEdit + ":descripcionItem");
            PrimeFaces.current().ajax().update("tblDetallePrecio:" + rowEdit + ":nivelEducativo");
            PrimeFaces.current().ajax().update("tblDetallePrecio:" + rowEdit + ":precio");
            PrimeFaces.current().ajax().update("tblDetallePrecio:" + rowEdit + ":precio2");
        }
        if (!msjError.isEmpty()) {
            JsfUtil.mensajeAlerta(msjError);
        }
    }

    public void agregarPrecio() {
        if (precioRef != null) {
            BigDecimal preRef = BigDecimal.ZERO;

            if (precioRef.getIdNivelEducativo() != null) {
                switch (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    case 1://UNIFORMES
                    case 4:
                    case 5:
                        preRef = getPrecioRefUniforme();
                        break;
                    case 2:
                        preRef = getPrecioRefUtiles();
                        break;
                    case 3:
                        if (precioRef.getIdNivelEducativo().getIdNivelEducativo().compareTo(new BigDecimal("6")) == 0) {
                            preRef = new BigDecimal("16.00");
                        } else {
                            preRef = new BigDecimal("14.60");
                        }
                        break;
                }
            }

            if (precioRef.getPrecioReferencia() != null && precioRef.getPrecioReferencia().compareTo(preRef) == 1) {
                precioRef.setPrecioReferencia(BigDecimal.ZERO);
                switch (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    case 1:
                    case 5:
                        msjError = "Precio Máximo de Referencia para: <br/>"
                                + "1)<strong> Parvularia</strong>: - Blusa, Falda y Camisa: $ 4.25 y Pantalon Corto $ 4.00<br />"
                                + "2)<strong> Básica y Bachillerato</strong>: - Blusa, Falda y Camisa: $ 4.50 y Pantalon Corto y Pantalon: $ 6.00<br/>";
                        break;
                    case 2:
                        if (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue() == 9) {
                            switch (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                                case 4:
                                case 5:
                                    msjError = "Precio Máximo de Referencia para: <br/>"
                                            + "1)<strong> Parvularia</strong>: $ " + preMaxRefPar.getPrecioMaxMas() + "<br/>"
                                            + "2)<strong> Primer Ciclo</strong>: $ " + preMaxRefCi.getPrecioMaxMas() + "<br/>"
                                            + "3)<strong> Segundo Ciclo</strong>: $ " + preMaxRefCii.getPrecioMaxMas() + "<br/>"
                                            + "4)<strong> Tercer Ciclo</strong>: $ " + preMaxRefCiii.getPrecioMaxMas() + "<br/>"
                                            + "5)<strong> Bachillerato: $ " + preMaxRefBac.getPrecioMaxMas() + "</strong>";
                                    break;
                                case 2:
                                    msjError = "Precio Máximo de Referencia para: <br/>"
                                            + "1)<strong> Inicial y Parvularia</strong>: $ " + preMaxRefPar.getPrecioMaxMas() + "<br/>"
                                            + "2)<strong> Primer Ciclo</strong>: $ " + preMaxRefCi.getPrecioMaxMas() + "<br/>"
                                            + "3)<strong> Segundo Ciclo</strong>: $ " + preMaxRefCii.getPrecioMaxMas() + "<br/>"
                                            + "4)<strong> Tercer Ciclo</strong>: $ " + preMaxRefCiii.getPrecioMaxMas() + "<br/>"
                                            + "4)<strong> Tercer Ciclo - Mod.Flexible</strong>: $ " + preMaxRefCiiiMf.getPrecioMaxMas() + "<br/>"
                                            + "5)<strong> Bachillerato: </strong>$ " + preMaxRefBac.getPrecioMaxMas() + "<br/>"
                                            + "5)<strong> Bachillerato - Mod.Flexible: </strong>$ " + preMaxRefBacMf.getPrecioMaxMas();
                                    break;
                                case 3:
                                    msjError = "Precio Máximo de Referencia para: <br/>"
                                            + "1)<strong> Inicial y Parvularia</strong>: $ " + preMaxRefPar.getPrecioMaxMas() + "<br/>"
                                            + "2)<strong> Primer Ciclo</strong>: $ " + preMaxRefCi.getPrecioMaxMas() + "<br/>"
                                            + "3)<strong> Segundo Ciclo</strong>: $ " + preMaxRefCii.getPrecioMaxMas() + "<br/>"
                                            + "4)<strong> Tercer Ciclo</strong>: $ " + preMaxRefCiii.getPrecioMaxMas() + "<br/>"
                                            + "5)<strong> Bachillerato: </strong>$ " + preMaxRefBac.getPrecioMaxMas();
                                    break;
                            }
                        } else {
                            msjError = "Precio Máximo de Referencia para: <br/>"
                                    + "1)<strong> Parvularia</strong>: $ " + preMaxRefPar.getPrecioMaxMas() + "<br/>"
                                    + "2)<strong> Primer Ciclo</strong>: $ " + preMaxRefCi.getPrecioMaxMas() + "<br/>"
                                    + "3)<strong> Segundo Ciclo</strong>: $ " + preMaxRefCii.getPrecioMaxMas() + "<br/>"
                                    + "4)<strong> Tercer Ciclo</strong>: $ " + preMaxRefCiii.getPrecioMaxMas() + "<br/>"
                                    + "5)<strong> Bachillerato: $ " + preMaxRefBac.getPrecioMaxMas() + "</strong>";
                        }
                        break;
                    case 3:
                        msjError = "Precio Máximo de Referencia para Zapatos escolares de:<br/> "
                                + "<strong>Parvularia y Básica</strong>: $ 14.60 <br/>"
                                + "<strong>Bachillerato</strong>: $16.00";
                        break;
                }
            }
        }
    }

    private BigDecimal getPrecioRefUniforme() {
        BigDecimal preRef = BigDecimal.ZERO;

        switch (precioRef.getIdProducto().getIdProducto().intValue()) {
            case 29:
            case 30:
            case 44:
                switch (precioRef.getIdNivelEducativo().getIdNivelEducativo().intValue()) {
                    case 1:
                        preRef = new BigDecimal("4.25");
                        break;
                    case 2:
                    case 6:
                        preRef = new BigDecimal("4.50");
                        break;
                }
                break;
            case 31:
                switch (precioRef.getIdNivelEducativo().getIdNivelEducativo().intValue()) {
                    case 1:
                        preRef = new BigDecimal("4.00");
                        break;
                }
                break;
            case 34:
                switch (precioRef.getIdNivelEducativo().getIdNivelEducativo().intValue()) {
                    case 1:
                        preRef = new BigDecimal("6.00");
                        break;
                    case 2:
                    case 6:
                        preRef = new BigDecimal("6.00");
                        break;
                }
                break;
        }

        return preRef;
    }

    private BigDecimal getPrecioRefUtiles() {
        BigDecimal preRef = BigDecimal.ZERO;

        switch (precioRef.getIdNivelEducativo().getIdNivelEducativo().intValue()) {
            case 1: //parvularia
                preRef = preMaxRefPar.getPrecioMaxMas();
                break;
            case 22: //incial y parvularia
                preRef = preMaxRefPar.getPrecioMaxMas();
                break;
            case 3: //ciclo I
                preRef = preMaxRefCi.getPrecioMaxMas();
                break;
            case 4: //ciclo II
                preRef = preMaxRefCii.getPrecioMaxMas();
                break;
            case 5://ciclo III
                preRef = preMaxRefCiii.getPrecioMaxMas();
                break;
            case 23://modalidad flexible y ciclo III
                preRef = preMaxRefCiii.getPrecioMaxMas();
                break;
            case 6: //Bachillerato
                preRef = preMaxRefBac.getPrecioMaxMas();
                break;
            case 24: //modalidad flexible y Bachillerato
                preRef = preMaxRefBac.getPrecioMaxMas();
                break;
        }

        return preRef;
    }

    private void editarNumeroDeItem(int rowEdit, String numItem) {
        Boolean itemRepetido = false;
        BigDecimal precioLibro = BigDecimal.ZERO;
        for (int i = 0; i < lstPreciosReferencia.size(); i++) {
            if (i != rowEdit) {
                if (lstPreciosReferencia.get(i).getNoItem() != null
                        && lstPreciosReferencia.get(i).getNoItem().equals(numItem)) {
                    itemRepetido = true;
                    break;
                }
            }
        }

        if (itemRepetido) {
            precioRef.setNoItem("");
            msjError = "Este Item ya fue ingresado.";
        } else {
            CatalogoProducto item = null;
            NivelEducativo nivel = null;
            if (numItem != null && !numItem.isEmpty()) {
                switch (detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    case 1: //UNIFORMES
                    case 4:
                        switch (Integer.parseInt(numItem)) {
                            case 0:
                                break;
                            case 1:
                            case 6:
                            case 10:
                                item = proveedorEJB.findProducto("30");
                                switch (Integer.parseInt(numItem)) {
                                    case 1:
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 6:
                                        nivel = proveedorEJB.findNivelEducativo("2");
                                        break;
                                    default:
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                }
                                break;
                            case 2:
                            case 7:
                            case 11:
                                item = proveedorEJB.findProducto("44");
                                switch (Integer.parseInt(numItem)) {
                                    case 2:
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 7:
                                        nivel = proveedorEJB.findNivelEducativo("2");
                                        break;
                                    default:
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                }
                                break;
                            case 3:
                            case 8:
                            case 12:
                                item = proveedorEJB.findProducto("29");
                                switch (Integer.parseInt(numItem)) {
                                    case 3:
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 8:
                                        nivel = proveedorEJB.findNivelEducativo("2");
                                        break;
                                    default:
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                }
                                break;
                            case 4:
                                item = proveedorEJB.findProducto("31");
                                nivel = proveedorEJB.findNivelEducativo("1");
                                break;
                            case 5:
                            case 9:
                            case 13:
                                item = proveedorEJB.findProducto("34");
                                switch (Integer.parseInt(numItem)) {
                                    case 5:
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 9:
                                        nivel = proveedorEJB.findNivelEducativo("2");
                                        break;
                                    default:
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                }
                                break;
                            default:
                                item = null;
                                nivel = null;
                                msjError = "El item ingresado no es válido.";
                                break;
                        }
                        break;
                    case 2: //UTILES
                        switch (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue()) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                //procesos antes de la contratacion de 2019
                                switch (Integer.parseInt(numItem)) {
                                    case 1:
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 2:
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case 3:
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case 4:
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case 5:
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    default:
                                        item = null;
                                        nivel = null;
                                        msjError = "El item ingresado no es válido.";
                                        break;
                                }
                                break;
                            case 7:
                            case 8:
                                //procesos mayor o igual a la contratacion de 2019
                                switch (numItem) {
                                    case "1":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case "2":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case "3":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case "4":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case "5":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    case "2.1": //grado 1
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("10");
                                        precioLibro = new BigDecimal("4.10");
                                        break;
                                    case "2.2": //grado 2
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("11");
                                        precioLibro = new BigDecimal("4.10");
                                        break;
                                    case "2.3": //grado 3
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("12");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "3.1": //grado 4
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("13");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "3.2": //grado 5
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("14");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "3.3": //grado 6
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("15");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "4.1": //grado 7
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("7");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "4.2": //grado 8
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("8");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "4.3": //grado 9
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("9");
                                        precioLibro = new BigDecimal("3.62");
                                        break;
                                    case "5.1": //1er año bachillerato
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("16");
                                        precioLibro = new BigDecimal("2.05");
                                        break;
                                    case "5.2": //2do año bachillerato
                                        item = proveedorEJB.findProducto("1");
                                        nivel = proveedorEJB.findNivelEducativo("17");
                                        precioLibro = new BigDecimal("2.05");
                                        break;
                                    default:
                                        item = null;
                                        nivel = null;
                                        msjError = "El item ingresado no es válido.";
                                        break;
                                }
                                break;
                            case 9:
                                //procesos mayor o igual a la contratacion de 2021
                                switch (numItem) {
                                    case "1":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("22");
                                        break;
                                    case "2":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case "3":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case "4":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case "4.4":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("23");
                                        break;
                                    case "5":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    case "5.1":
                                        item = proveedorEJB.findProducto("54");
                                        nivel = proveedorEJB.findNivelEducativo("24");
                                        break;

                                    default:
                                        item = null;
                                        nivel = null;
                                        msjError = "El item ingresado no es válido.";
                                        break;
                                }
                                break;
                        }
                        break;
                    case 3: //ZAPATOS
                        switch (detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho().intValue()) {
                            case 9: //año 2021
                                switch (Integer.parseInt(numItem)) {
                                    case 1:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("22");
                                        break;
                                    case 2:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("22");
                                        break;
                                    case 3:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case 4:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case 5:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case 6:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case 7:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case 8:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case 9:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    case 10:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    default:
                                        item = null;
                                        nivel = null;
                                        msjError = "El item ingresado no es válido.";
                                        break;
                                }
                                break;
                            default:
                                switch (Integer.parseInt(numItem)) {
                                    case 1:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 2:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("1");
                                        break;
                                    case 3:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case 4:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("3");
                                        break;
                                    case 5:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case 6:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("4");
                                        break;
                                    case 7:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case 8:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("5");
                                        break;
                                    case 9:
                                        item = proveedorEJB.findProducto("21");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    case 10:
                                        item = proveedorEJB.findProducto("43");
                                        nivel = proveedorEJB.findNivelEducativo("6");
                                        break;
                                    default:
                                        item = null;
                                        nivel = null;
                                        msjError = "El item ingresado no es válido.";
                                        break;
                                }
                                break;
                        }
                        break;
                }

                if (item == null && nivel == null) {
                } else if (isProductoIsValid(item.getIdProducto())) {
                    precioRef.setIdProducto(item);
                    precioRef.setIdNivelEducativo(nivel);
                    if (precioLibro.intValue() > 0) {
                        precioRef.setPrecioReferencia(precioLibro);
                    }
                } else {
                    precioRef.setNoItem("");
                    msjError = "El proveedore NO ESTA CALIFICADO para ofertar este ITEM";
                }

            }
        }
    }

    private boolean isProductoIsValid(BigDecimal idProducto) {
        if (lstItem.stream().anyMatch(producto -> (producto.getIdProducto().intValue() == idProducto.intValue()))) {
            return true;
        }
        JsfUtil.mensajeError("El proveedore NO ESTA CALIFICADO para ofertar este ITEM");
        return false;
    }

    public void eliminarDetalle() {
        if (precioRef != null) {
            if (precioRef.getEstadoEliminacion().compareTo(BigInteger.ZERO) == 0) {
                if (precioRef.getIdPrecioRefEmp() != null) {
                    precioRef.setEstadoEliminacion(BigInteger.ONE);
                } else {
                    lstPreciosReferencia.remove(idRow);
                }
            } else {
                precioRef.setEstadoEliminacion(BigInteger.ZERO);
            }

            precioRef = null;
        } else {
            JsfUtil.mensajeAlerta("Debe seleccionar un precio para poder eliminarlo.");
        }
        idRow = -1;
    }

    public void agregarNewPrecio() {
        if (detalleProcesoAdq.getIdDetProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de contratación");
        } else {
            if (detalleProcesoAdq.getIdDetProcesoAdq() != null) {
                PreciosRefRubroEmp current = new PreciosRefRubroEmp();
                current.setEstadoEliminacion(BigInteger.ZERO);
                current.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                current.setFechaInsercion(new Date());
                current.setIdEmpresa(empresa);
                current.setIdMuestraInteres(capacidadInst.getIdMuestraInteres());
                lstPreciosReferencia.add(current);
            } else {
                JsfUtil.mensajeInformacion("Debe de seleccionar un proceso de contratación.");
            }
        }
    }

    public void updateFrm(SelectEvent event) {
        fotoProveedor = (String) event.getObject();
        if (!fotoProveedor.contains("/")) {
            showFoto = false;
        } else {
            empresa.getIdPersona().setUrlImagen(fotoProveedor);
        }
    }

    public void updateFrmFotoZapato(SelectEvent event) {
        cargarFotografias();
    }

    public void impOfertaGlobal() {
        try {
            List<JasperPrint> jasperPrintList = Reportes.getReporteOfertaDeProveedor(capacidadInst, empresa, detalleProcesoAdq,
                    reportesEJB.getLstOfertaGlobal(empresa.getNumeroNit(), detalleProcesoAdq.getIdRubroAdq().getIdRubroInteres(), detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getIdAnho()),
                    reportesEJB.getDeclaracionJurada(empresa, detalleProcesoAdq, VarSession.getNombreMunicipioSession()));
            if (!jasperPrintList.isEmpty()) {
                Reportes.generarReporte(jasperPrintList, "oferta_global_" + getEmpresa().getNumeroNit());
            }
        } catch (JRException | IOException ex) {
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarAdjudicacionesProveedor(SelectEvent event) {
        if (event.getObject() != null) {
            if (event.getObject() instanceof Empresa) {
                empresa = (Empresa) event.getObject();
                cargarDetalleCalificacion();
                /**
                 * Fecha: 05/09/2018 Comentario: Validación para la capacidad
                 * instalada del proveedor seleccionado
                 */
                if (capacidadInst != null && capacidadInst.getIdCapInstRubro() != null) {
                    detalleProcesoAdq = JsfUtil.findDetalleByRubroAndAnho(getRecuperarProceso().getProcesoAdquisicion(),
                            capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres(),
                            capacidadInst.getIdMuestraInteres().getIdAnho().getIdAnho());

                    lstResumenAdj = proveedorEJB.resumenAdjProveedor(empresa.getNumeroNit(), detalleProcesoAdq.getIdDetProcesoAdq());
                    if (lstResumenAdj.isEmpty()) {
                        JsfUtil.mensajeInformacion("No se encontrarón adjudicaciones para este proveedor.");
                    } else {
                        totalItems = BigDecimal.ZERO;
                        totalMonto = BigDecimal.ZERO;
                        lstResumenAdj.forEach(resumen -> {
                            totalItems = totalItems.add(resumen.getCantidad());
                            totalMonto = totalMonto.add(resumen.getMonto());
                        });
                        lstDetalleAdj = proveedorEJB.detalleAdjProveedor(empresa.getNumeroNit(), detalleProcesoAdq.getIdDetProcesoAdq());
                    }
                }
            }
        } else {
            deshabiliar = false;
            JsfUtil.mensajeAlerta("Debe de seleccionar una empresa");

        }
    }

    /*@FacesConverter(forClass = MunicipioDto.class, value = "muniConverter")
    public static class MunicipioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProveedorController controller = (ProveedorController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "proveedorController");
            MunicipioDto mun = new MunicipioDto();
            try {
                BeanUtils.copyProperties(mun, controller.utilEJB.find(Municipio.class, getKey(value)));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
            }

            return mun;
        }

        java.math.BigDecimal getKey(String value) {
            java.math.BigDecimal key;
            key = new java.math.BigDecimal(value);
            return key;
        }

        String getStringKey(java.math.BigDecimal value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MunicipioDto) {
                MunicipioDto o = (MunicipioDto) object;
                return getStringKey(o.getIdMunicipio());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Municipio.class.getName());
            }
        }
    }*/

    public void resumenAdjudicacionesXls(Object document) {
        int[] numEnt = {0, 4};
        int[] numDec = {5};
        RptExcel.generarRptExcelGenerico((HSSFWorkbook) document, numEnt, numDec);
    }

    public String getNombreRubroProveedor() {
        if (capacidadInst != null && capacidadInst.getIdMuestraInteres() != null) {
            return JsfUtil.getNombreRubroById(capacidadInst.getIdMuestraInteres().getIdRubroInteres().getIdRubroInteres());
        } else {
            return "";
        }
    }

    public void calcularNoItems() {
        if (getRecuperarProceso().getProcesoAdquisicion() != null) {
            proveedorEJB.calcularNoItems(rubro, getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getIdAnho());
        }
    }

    public void calcularNoItemByNit() {
        if (getRecuperarProceso().getProcesoAdquisicion() != null) {
            proveedorEJB.calcularNoItems(rubro, getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getIdAnho(), numeroNit, null);
            proveedorEJB.calcularPreRefByNit(rubro, getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getIdAnho(), numeroNit);
        }
    }

    public void calcularPreciosEmp() {
        if (getRecuperarProceso().getProcesoAdquisicion() != null) {
            proveedorEJB.calcularPreRef(rubro, getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getIdAnho());
        }
    }

    public void imprimirDeclaraciones() {
        try {
            String nombreRpt;
            JasperPrint rptTemp;
            List<JasperPrint> lstRptAImprimir = new ArrayList();
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

            for (ContratosOrdenesCompras contratosOrdenesCompras : proveedorEJB.getLstContratosByNitAndAnho(empresa.getNumeroNit(), getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho())) {
                HashMap param = new HashMap();
                param.put("idContrato", contratosOrdenesCompras.getIdContrato());
                param.put("ubicacionImagenes", ctx.getRealPath(Reportes.PATH_IMAGENES) + File.separator);
                param.put("pAnho", detalleProcesoAdq.getIdProcesoAdq().getIdAnho().getAnho());

                nombreRpt = "sv/gob/mined/apps/reportes/declaracion/rptDeclaracionAdjudicatorio".concat(contratosOrdenesCompras.getIdResolucionAdj().getIdParticipante().getIdEmpresa().getIdPersoneria().getIdPersoneria().intValue() == 1 ? "PerNat" : "PerJur").concat(param.get("pAnho").toString());
                rptTemp = reportesEJB.getRpt(param, Reportes.getPathReporte(nombreRpt + ".jasper"));
                lstRptAImprimir.add(rptTemp);
            }

            Reportes.generarReporte(lstRptAImprimir, "documentos_prov_" + empresa.getNumeroNit());
        } catch (IOException | JRException ex) {
            Logger.getLogger(ProveedorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetDatosProveedor() {
        Boolean continuar = false;
        if (numeroNit == null || numeroNit.isEmpty()) {
            JsfUtil.mensajeAlerta("Por favor digite un NIT");
        } else {
            Empresa emp = proveedorEJB.findEmpresaByNit(numeroNit, false);
            if (emp == null) {
                JsfUtil.mensajeAlerta("No existe ningún Proveedor o Representante Legal con el NIT ingresado");
            } else {
                continuar = true;
            }
        }

        if (resetAceptacion && continuar) {
            if (rubro != null) {
                proveedorEJB.resetAceptacion(numeroNit, JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro).getIdDetProcesoAdq());
            } else {
                JsfUtil.mensajeAlerta("Debe de seleccionar un Rubro de Adquisición");
            }
        }

        if (resetUsuario && continuar) {
            proveedorEJB.resetActivacion(numeroNit);
        }
    }

    public void generarCodigoSeguridad() {
        //proveedorEJB.generarCodigoSeguridad(JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro).getIdDetProcesoAdq());
    }
}
