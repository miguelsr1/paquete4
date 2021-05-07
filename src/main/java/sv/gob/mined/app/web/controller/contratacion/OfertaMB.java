/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.contratacion;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.Bean2Excel;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.DatosGeograficosEJB;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.OfertaBienesServiciosEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.OfertaBienesServicios;
import sv.gob.mined.paquescolar.model.Participantes;
import sv.gob.mined.paquescolar.model.ResolucionesAdjudicativas;
import sv.gob.mined.paquescolar.model.pojos.VwRptCertificacionPresupuestaria;
import sv.gob.mined.paquescolar.model.pojos.contratacion.PrecioReferenciaEmpresaDto;
import sv.gob.mined.paquescolar.model.pojos.contratacion.ProveedorDisponibleDto;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;
import sv.gob.mined.paquescolar.util.Constantes;

/**
 *
 * @author misanchez
 */
@Named
@javax.faces.view.ViewScoped
public class OfertaMB extends RecuperarProcesoUtil implements Serializable {

    private int rowEdit = 0;
    private String estiloSeleccionado = "-";
    private String municipioCe;
    private String codigoEntidad;
    private String valorDeBusqueda;
    private String tipoDocumentoImp = "";
    private String nombreEmp = "";
    private String nombreEmpOtros = "";
    private Boolean showProductos = false;
    private Boolean deshabilitar = true;
    private Boolean modifDesac = false;
    private Boolean abrirDialogCe = false;
    private Boolean pageResolucion = false;
    private BigDecimal rubro = BigDecimal.ZERO;
    private BigDecimal cantidadAlumnos = BigDecimal.ZERO;

    private Empresa empresaSeleccionada;
    private Participantes participanteSeleccionado;
    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();
    private OfertaBienesServicios current = new OfertaBienesServicios();
    private VwCatalogoEntidadEducativa entidadEducativa = new VwCatalogoEntidadEducativa();
    private SelectItem[] lstEstilos = new SelectItem[0];
    private HashMap<String, String> mapItems;

    private ProveedorDisponibleDto proveedorSeleccionado = new ProveedorDisponibleDto();

    private List<String> images = new ArrayList();
    private List<ProveedorDisponibleDto> lstCapaEmpresas = new ArrayList();
    private List<ProveedorDisponibleDto> lstCapaEmpresasOtros = new ArrayList();
    private List<ProveedorDisponibleDto> lstEmpresas = new ArrayList();
    private List<ProveedorDisponibleDto> lstEmpresasOtros = new ArrayList();
    private List<PrecioReferenciaEmpresaDto> lstPrecios = new ArrayList();

    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private OfertaBienesServiciosEJB ofertaBienesServiciosEJB;
    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private DatosGeograficosEJB datosGeograficosEJB;
    @Inject
    private ReportesEJB reportesEJB;

    /**
     * Creates a new instance of OfertaMB
     */
    public OfertaMB() {
    }

    @PostConstruct
    public void init() {
        rubro = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getRubro();

        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        if (params.containsKey("txtCodigoEntidad")) {
            current = ofertaBienesServiciosEJB.getOfertaByProcesoCodigoEntidad(params.get("txtCodigoEntidad"), detalleProceso);

            codigoEntidad = params.get("txtCodigoEntidad");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public ProveedorDisponibleDto getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(ProveedorDisponibleDto proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    public BigDecimal getCantidadAlumnos() {
        return cantidadAlumnos;
    }

    public String getNombreEmpOtros() {
        return nombreEmpOtros;
    }

    public void setNombreEmpOtros(String nombreEmpOtros) {
        this.nombreEmpOtros = nombreEmpOtros;
    }

    public String getNombreEmp() {
        return nombreEmp;
    }

    public void setNombreEmp(String nombreEmp) {
        this.nombreEmp = nombreEmp;
    }

    public Boolean getPageResolucion() {
        return pageResolucion;
    }

    public void setPageResolucion(Boolean pageResolucion) {
        this.pageResolucion = pageResolucion;
    }

    public BigDecimal getRubro() {
        return rubro;
    }

    public void setRubro(BigDecimal rubro) {
        if (rubro != null) {
            VarSession.crearCookie("rubro", rubro.toString());
            ParametrosMB controller = (ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                    getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB");
            controller.setRubro(rubro);
            //controller.findDetalleProcesoAdq();

            this.rubro = rubro;
        }
    }

    public Boolean getAbrirDialogCe() {
        return abrirDialogCe;
    }

    public Participantes getParticipanteSeleccionado() {
        return participanteSeleccionado;
    }

    public void setParticipanteSeleccionado(Participantes participanteSeleccionado) {
        this.participanteSeleccionado = participanteSeleccionado;
    }

    public Boolean getDeshabilitar() {
        return deshabilitar;
    }

    public void setDeshabilitar(Boolean deshabilitar) {
        this.deshabilitar = deshabilitar;
    }

    public Boolean getModifDesac() {
        return modifDesac;
    }

    public void setModifDesac(Boolean modifDesac) {
        this.modifDesac = modifDesac;
    }

    public void setSelected(OfertaBienesServicios oferta) {
        if (oferta == null) {
            this.current = oferta;
        }
    }

    public OfertaBienesServicios getSelected() {
        if (current == null) {
            current = new OfertaBienesServicios();
        }
        return current;
    }

    public DetalleProcesoAdq getDetalleProceso() {
        return detalleProceso;
    }

    public void setDetalleProceso(DetalleProcesoAdq detalleProceso) {
        this.detalleProceso = detalleProceso;
    }

    public Empresa getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
        this.empresaSeleccionada = empresaSeleccionada;
    }

    public String getMunicipioCe() {
        return municipioCe;
    }

    public void setMunicipioCe(String municipioCe) {
        this.municipioCe = municipioCe;
    }

    public Boolean getShowProductos() {
        return showProductos;
    }

    public void setShowProductos(Boolean showProductos) {
        this.showProductos = showProductos;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

//    public CapaInstPorRubro getCapaInstSeleccionada() {
//        return capaInstSeleccionada;
//    }
//
//    public void setCapaInstSeleccionada(CapaInstPorRubro capaInstSeleccionada) {
//        if (capaInstSeleccionada != null) {
//            this.capaInstSeleccionada = capaInstSeleccionada;
//        }
//    }
    public List<ProveedorDisponibleDto> getLstCapaEmpresas() {
        return lstCapaEmpresas;
    }

    public void setLstCapaEmpresas(List<ProveedorDisponibleDto> lstCapaEmpresas) {
        this.lstCapaEmpresas = lstCapaEmpresas;
    }

    public List<ProveedorDisponibleDto> getLstCapaEmpresasOtros() {
        return lstCapaEmpresasOtros;
    }

    public void setLstCapaEmpresasOtros(List<ProveedorDisponibleDto> lstCapaEmpresasOtros) {
        this.lstCapaEmpresasOtros = lstCapaEmpresasOtros;
    }

    public List<PrecioReferenciaEmpresaDto> getLstPrecios() {
        return lstPrecios;
    }

    public void setLstPrecios(List<PrecioReferenciaEmpresaDto> lstPrecios) {
        this.lstPrecios = lstPrecios;
    }

    public SelectItem[] getLstEstilos() {
        return lstEstilos;
    }

    public String getEstiloSeleccionado() {
        return estiloSeleccionado;
    }

    public void setEstiloSeleccionado(String estiloSeleccionado) {
        this.estiloSeleccionado = estiloSeleccionado;
    }

    public boolean getEENuevo() {
        return VarSession.getVariableSessionED() == 1;
    }

    public boolean getEEModificar() {
        return VarSession.getVariableSessionED() == 2;
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

    public int getRowEdit() {
        return rowEdit;
    }

    public void setRowEdit(int rowEdit) {
        this.rowEdit = rowEdit;
    }

    public String getTipoDocumentoImp() {
        return tipoDocumentoImp;
    }

    public void setTipoDocumentoImp(String tipoDocumentoImp) {
        this.tipoDocumentoImp = tipoDocumentoImp;
    }

    // </editor-fold>
    public void mostrarFiltroCE() {
        if (detalleProceso == null || detalleProceso.getIdDetProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de contratación");
        } else {
            Map<String, Object> opt = new HashMap();
            opt.put("modal", true);
            opt.put("draggable", true);
            opt.put("resizable", false);
            opt.put("contentHeight", 380);
            opt.put("contentWidth", 615);
            PrimeFaces.current().dialog().openDynamic("/app/comunes/filtroCentroEscolar.mined", opt, null);
        }
    }

    public void cargarFotosPorEstilo() {
        if (!estiloSeleccionado.equals("-")) {
            File carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + empresaSeleccionada.getNumeroNit() + "/" + estiloSeleccionado + "/");
            images = new ArrayList();

            if (carpetaNfs.list() != null) {
                for (String string : carpetaNfs.list()) {
                    images.add("Fotos_Zapatos/" + empresaSeleccionada.getNumeroNit() + "/" + estiloSeleccionado + "/" + string);
                }
            }
        }
    }

    public String getFotoPrincipal(String nit) {
        String imagen = "Fotos_Zapatos/sin_foto.png";
        File carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + nit + "/");
        if (carpetaNfs.list() != null) {
            for (String nombreCarpeta : carpetaNfs.list()) {
                File carpetaImagenes = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + nit + "/" + nombreCarpeta + "/");
                if (carpetaImagenes.list() != null) {
                    for (String imagenes : carpetaImagenes.list()) {
                        return "Fotos_Zapatos/" + nit + "/" + nombreCarpeta + "/" + imagenes;
                    }
                }
            }
        }

        return imagen;
    }

    public void limpiarFiltros() {
        if (current != null) {
            current.getCodigoEntidad().setCodigoEntidad("");
        }
        lstCapaEmpresas = new ArrayList();
        lstCapaEmpresasOtros = new ArrayList();
        limpiarCampos();
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
    }

    public void onSelect(BigDecimal idEmpresa) {
        try {
            if (idEmpresa == null) {
                JsfUtil.mensajeAlerta("Debe de seleccionar un proveedor");
            } else {
                empresaSeleccionada = proveedorEJB.findEmpresaByPk(idEmpresa);
                lstPrecios = proveedorEJB.getLstPreciosByIdEmpresaAndIdProcesoAdq(empresaSeleccionada.getIdEmpresa(), detalleProceso.getIdProcesoAdq().getIdAnho().getIdAnho(), mapItems.get("idNivelesCe"));
                if (lstPrecios.isEmpty()) {
                    JsfUtil.mensajeAlerta("Este proveedor no posee precios de referencia. No se puede ingresar a la oferta.");
                } else {
                    if (VarSession.getDepartamentoUsuarioSession() != null) {
                        if (proveedorEJB.isDepaCalificado(empresaSeleccionada, entidadEducativa.getCodigoDepartamento().getCodigoDepartamento(), detalleProceso)) {
                            if (!findParticipanteEnOferta(empresaSeleccionada)) {
                                Participantes participante = new Participantes();

                                participante.setEstadoEliminacion(BigInteger.ZERO);
                                participante.setFechaInsercion(new Date());
                                participante.setIdEmpresa(empresaSeleccionada);
                                participante.setIdOferta(current);
                                participante.setModificativa(BigInteger.ZERO);
                                participante.setUsuarioInsercion(VarSession.getVariableSession("Usuario").toString());

                                participante.setPorcentajeCapacidad(new BigDecimal(proveedorSeleccionado.getPorcentajeCapacidad() + proveedorSeleccionado.getPorcentajeCapacidadItem()));
                                participante.setPorcentajeGeo(new BigDecimal(proveedorSeleccionado.getPorcentajeGeo()));
                                participante.setPorcentajePrecio(new BigDecimal(proveedorSeleccionado.getPorcentajePrecio()));

                                current.getParticipantesList().add(participante);

                                JsfUtil.mensajeInformacion("Se agrego el proveedor seleccionado");
                            }
                        } else {
                            JsfUtil.mensajeAlerta("Este proveedor no esta calificado para este departamento.");
                        }
                    } else {
                        JsfUtil.mensajeAlerta("Debe de seleccionar un departamento y municipio.");
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(OfertaMB.class.getName()).log(Level.INFO, null, "Error OfertaBienesServiciosController.onSelect()");
            Logger.getLogger(OfertaMB.class.getName()).log(Level.INFO, null, "Codigo Entidad " + codigoEntidad);
            Logger.getLogger(OfertaMB.class.getName()).log(Level.INFO, null, "id Empresa " + idEmpresa);
            Logger.getLogger(OfertaMB.class.getName()).log(Level.INFO, null, "Error: " + e.getMessage());
        }
    }

    private boolean findParticipanteEnOferta(Empresa empresa) {
        if (current.getParticipantesList() == null) {
            current.setParticipantesList(new ArrayList(0));
        }

        for (Participantes par : current.getParticipantesList()) {
            if (par.getIdEmpresa().getNumeroNit().equals(empresa.getNumeroNit()) && par.getEstadoEliminacion().compareTo(BigInteger.ZERO) == 0) {
                JsfUtil.mensajeError("El proveedor seleccionado ya existe en la oferta actual.");
                return true;
            }
        }
        return false;
    }

    public void prepareCreate() {
        current = new OfertaBienesServicios();
        deshabilitar = false;
        VarSession.setVariableSessionED("1");
        limpiarCampos();
    }

    public void limpiarCampos() {
        current = new OfertaBienesServicios();
        codigoEntidad = "";
        entidadEducativa = new VwCatalogoEntidadEducativa();
    }

    public void create() {
        HashMap<String, Object> parametros = ofertaBienesServiciosEJB.create(current, VarSession.getVariableSessionUsuario());

        if ((Boolean) parametros.get(Constantes.ERROR)) {
            JsfUtil.mensajeError(parametros.get(Constantes.MSJ_ERROR).toString());
        } else if ((Boolean) parametros.get(Constantes.WARNING)) {
            JsfUtil.mensajeError(parametros.get(Constantes.MSJ_WARNING).toString());
        } else {
            JsfUtil.mensajeInsert();
            VarSession.setVariableSessionED("2");
        }
    }

    public void guardar() {
        if (current == null || current.getCodigoEntidad() == null || current.getCodigoEntidad().getCodigoEntidad().isEmpty()) {
            JsfUtil.mensajeAlerta("Debe de agregar el centro escolar y el detalle de proveedores.");
        } else if (current.getFechaApertura() == null) {
            JsfUtil.mensajeAlerta("Debe de agregar la fecha de registro de la oferta.");
        } else if (current.getParticipantesList() == null) {
            JsfUtil.mensajeAlerta("Debe de agregar el detalle de provedores");
        } else if (current != null && current.getIdDetProcesoAdq() != null) {
            if (current.getIdOferta() != null) {
                update();
            } else {
                create();
                VarSession.setVariableSessionED("2");
            }
        }
    }

    public void prepareEdit() {
        VarSession.setVariableSessionED("2");
        entidadEducativa = null;
        deshabilitar = false;
        limpiarCampos();
    }

    public void update() {
        try {
            current = ofertaBienesServiciosEJB.edit(current);
            JsfUtil.mensajeUpdate();
        } catch (Exception e) {
            JsfUtil.mensajeError("Error en la actulización de la oferta.");
        }
    }

    public void imprimir() throws Exception {
        if (tipoDocumentoImp.equals("1")) {
            imprimirAnalisisEconomico();
        } else if (tipoDocumentoImp.equals("2")) {
            imprimirCertificacion();
        }
    }

    public void imprimirAnalisisEconomico() throws Exception {
        if (getSelected().getIdOferta() == null) {
            JsfUtil.mensajeAlerta("Primero debe de guardar la oferta!!!");
        } else {
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
            List lst = ofertaBienesServiciosEJB.getDatosRptAnalisisEconomico(getSelected().getCodigoEntidad().getCodigoEntidad(), getSelected().getIdDetProcesoAdq());
            Bean2Excel oReport = new Bean2Excel(lst, detalleProceso.getIdRubroAdq().getDescripcionRubro(), entidadEducativa.getNombre(), entidadEducativa.getCodigoEntidad(), "", sd.format(getSelected().getFechaApertura()), getSelected().getUsuarioInsercion());
            oReport.createFile(getSelected().getCodigoEntidad().getCodigoEntidad(), 
                    entidadEducativaEJB.getPresidenteOrganismoEscolar(codigoEntidad).getNombreMiembro(), 
                    entidadEducativaEJB.getMiembro(codigoEntidad, "ENCARGADO_COMPRA").getNombreMiembro());
        }
    }

    public void imprimirCertificacion() {
        List<VwRptCertificacionPresupuestaria> lst = new ArrayList();
        HashMap param = new HashMap();

        if (detalleProceso != null) {
            VwRptCertificacionPresupuestaria vw = entidadEducativaEJB.getCertificacion(codigoEntidad, getRecuperarProceso().getProcesoAdquisicion(), (getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq() >= 12));

            if (vw != null) {
                vw.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                lst.add(vw);
                String nombreRpt = "";

                switch (detalleProceso.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    case 1:
                        nombreRpt = "rptCertUni.jasper";
                        break;
                    case 2:
                        nombreRpt = "rptCertUti.jasper";
                        break;
                    case 3:
                        nombreRpt = "rptCertZap.jasper";
                        break;
                }
                Reportes.generarRptsContractuales(lst, param, codigoEntidad, detalleProceso.getIdProcesoAdq().getDescripcionProcesoAdq().contains("SOBREDEMANDA"), null, nombreRpt);
            } else {
                JsfUtil.mensajeAlerta("Error obteniendo las estadisticas de censo rápido para el centro escolar: " + codigoEntidad);
            }
        }
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
                detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
                abrirDialogCe = false;
                entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);
                if (entidadEducativa == null) {
                    JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
                } else {
                    cantidadAlumnos = entidadEducativaEJB.getCantidadTotalByCodEntAndIdProcesoAdq(codigoEntidad, getRecuperarProceso().getProcesoAdquisicion().getIdProcesoAdq());

                    if (cantidadAlumnos == null || cantidadAlumnos.intValue() == 0) {
                        JsfUtil.mensajeAlerta("Es necesario registrar las estadisticas para este centro educativo");
                    } else {
                        getSelected().setIdDetProcesoAdq(detalleProceso);
                        getSelected().setCodigoEntidad(entidadEducativa);

                        if (VarSession.getDepartamentoUsuarioSession() != null) {
                            String dep = getRecuperarProceso().getDepartamento();
                            if (entidadEducativa.getCodigoDepartamento().getCodigoDepartamento().equals(dep) || (Integer) VarSession.getVariableSession("idTipoUsuario") == 1) {
                                if (VarSession.getVariableSessionED() == 1) {
                                    if (ofertaBienesServiciosEJB.isOfertaRubro(codigoEntidad, detalleProceso)) {
                                        JsfUtil.mensajeError("Ya existe un proceso de contratación para este centro escolar.");
                                    } else {
                                        cargaItemsPorCe();
                                    }
                                } else if (VarSession.getVariableSessionED() == 2) {
                                    current = ofertaBienesServiciosEJB.getOfertaByProcesoCodigoEntidad(codigoEntidad, detalleProceso);

                                    if (current == null) {
                                        JsfUtil.mensajeError("No existe un proceso de contratación para este centro escolar.");
                                    } else {
                                        cargaItemsPorCe();
                                    }
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
            }
        } else {
            if (current != null && current.getParticipantesList() != null) {
                current.getParticipantesList().clear();
            }
            entidadEducativa = null;
        }
    }

    private void cargaItemsPorCe() {
        deshabilitar = !current.getIdDetProcesoAdq().getHabilitarRegistro();
        abrirDialogCe = true;
        if (!current.getIdDetProcesoAdq().getHabilitarRegistro()) {
            JsfUtil.mensajeInformacion("El registro de contratos ha sido deshabilitado por el Administrador.");
        } else {
            mapItems = entidadEducativaEJB.getNoItemsByCodigoEntidadAndIdProcesoAdq(codigoEntidad, detalleProceso,
                    detalleProceso.getIdRubroAdq().getIdRubroUniforme().intValue() == 1);
        }
    }

    private void ejecutarCalculo(String cod) {
        int ed = VarSession.getVariableSessionED();
        ResolucionesAdjudicativasController controller = (ResolucionesAdjudicativasController) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "resolucionesAdjudicativasController");
        controller.setCodigoEntidad(cod);
        controller.buscarSaldoPresupuestoCE(detalleProceso.getIdDetProcesoAdq());
        VarSession.setVariableSessionED(String.valueOf(ed));
    }

    public void abrirDialogProveedor() {
        if (current != null && current.getCodigoEntidad() != null && current.getCodigoEntidad().getCodigoEntidad().length() == 5) {
            if (current.getIdDetProcesoAdq() == null) {
                current.setIdDetProcesoAdq(detalleProceso);
            }
            consultarEmpresa();
            PrimeFaces.current().executeScript("PF('dlgProveedor').show();");
        } else {
            JsfUtil.mensajeAlerta("Debe de ingresar un código de infraestructura válido");
        }
    }

    public void consultarEmpresa() {
        BigInteger cantidad;
        String idMunicipios;
        municipioCe = datosGeograficosEJB.findNombreMunicipioCe(codigoEntidad);

        if (detalleProceso.getIdRubroAdq().getIdRubroUniforme().intValue() == 1) {
            cantidad = cantidadAlumnos.multiply(new BigDecimal(2)).toBigInteger();
        } else {
            cantidad = cantidadAlumnos.toBigInteger();
        }

        idMunicipios = JsfUtil.findIdMunicipios(entidadEducativa.getIdMunicipio()).getIdMunicipios();

        //calcular el menor precio de ambos listados
        lstEmpresas = proveedorEJB.getLstCapaEmpPorNitOrRazonSocialAndRubroAndMunicipioCe(current.getIdDetProcesoAdq(),
                codigoEntidad, entidadEducativa.getCodigoDepartamento().getCodigoDepartamento(), entidadEducativa.getCodigoMunicipio(), entidadEducativa.getCodigoCanton(),
                entidadEducativa.getIdMunicipio().intValue(), idMunicipios, true, cantidad, mapItems);
        lstEmpresasOtros = proveedorEJB.getLstCapaEmpPorNitOrRazonSocialAndRubroAndMunicipioCe(current.getIdDetProcesoAdq(),
                codigoEntidad, entidadEducativa.getCodigoDepartamento().getCodigoDepartamento(), entidadEducativa.getCodigoMunicipio(), entidadEducativa.getCodigoCanton(),
                entidadEducativa.getIdMunicipio().intValue(), idMunicipios, false, cantidad, mapItems);

        lstCapaEmpresas.clear();
        lstCapaEmpresas.addAll(lstEmpresas);
        lstCapaEmpresasOtros.clear();
        lstCapaEmpresasOtros.addAll(lstEmpresasOtros);

        if (lstEmpresas.isEmpty()) {
            JsfUtil.mensajeInformacion("No se encontró ninguna coincidencia con el valor: <strong>" + valorDeBusqueda + "</strong>");
        }
    }

    public void eliminarDetalle() {
        if (participanteSeleccionado != null) {
            if (participanteSeleccionado.getEstadoEliminacion().compareTo(BigInteger.ZERO) == 0) {
                if (participanteSeleccionado.getIdParticipante() != null) {
                    participanteSeleccionado.setEstadoEliminacion(BigInteger.ONE);
                } else {
                    getSelected().getParticipantesList().remove(rowEdit);
                }
            } else {
                participanteSeleccionado.setEstadoEliminacion(BigInteger.ZERO);
            }

            participanteSeleccionado = null;
        } else {
            JsfUtil.mensajeAlerta("Debe seleccionar un participante para poder eliminarlo.");
        }
    }

    public String editarOfertaParticipante() {
        String r = "";
        boolean modificaciones = false;

        for (Participantes participante : current.getParticipantesList()) {
            if (participante.getIdParticipante() == null) {
                modificaciones = true;
                break;
            }
        }

        if (modificaciones == false) {
            switch (VarSession.getVariableSessionED()) {
                case 2:
                    r = "reg02DetalleOferta";
                    break;
            }
        } else {
            JsfUtil.mensajeAlerta("Primero debe de guardar los cambios realizados.");
        }

        return r;
    }

    public void deleteParticipante() {
        if (participanteSeleccionado.getIdParticipante() == null) {
            current.getParticipantesList().remove(participanteSeleccionado);
        } else {
            ResolucionesAdjudicativas res = ofertaBienesServiciosEJB.findResolucionesAdjudicativas(participanteSeleccionado);
            if (res != null) {
                //El proveedor seleccionado tiene un reserva de fondos
                switch (res.getIdEstadoReserva().getIdEstadoReserva().intValue()) {
                    case 1:
                    case 3:
                        res.setEstadoEliminacion(BigInteger.ONE);
                        try {
                            ofertaBienesServiciosEJB.editResolucion(res, VarSession.getVariableSessionUsuario());
                            participanteSeleccionado.setEstadoEliminacion(BigInteger.ONE);
                            participanteSeleccionado.setFechaEliminacion(new Date());
                            participanteSeleccionado.setUsuarioModificacion(VarSession.getVariableSessionUsuario());

                        } catch (Exception ex) {
                            JsfUtil.mensajeError("Ocurrio un error en la operación.\n" + ex.getMessage());
                        }
                        break;
                    case 2:
                    case 4:
                    case 5:
                        JsfUtil.mensajeAlerta("No se puede eliminar este participante, "
                                + "porque se encuentra en una reserva de fondos con estado: " + res.getIdEstadoReserva().getDescripcionReserva());
                        break;
                }
            } else {
                //El proveedor seleccionado NO tiene reserva de fondos
                try {
                    participanteSeleccionado.setEstadoEliminacion(BigInteger.ONE);
                    participanteSeleccionado.setFechaEliminacion(new Date());
                    participanteSeleccionado.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
                    ofertaBienesServiciosEJB.edit(current);
                    buscarEntidadEducativa();
                } catch (Exception ex) {
                    JsfUtil.mensajeError("Ocurrio un error en la eliminación del participante.");
                }
            }
        }
    }

//    public void onRowSelect(SelectEvent event) {
//        capaInstSeleccionada = (CapaInstPorRubro) event.getObject();
//    }
    /**
     * Este metodo se ejecuta en el filtro de centros escolares en la pagina
     * regReservaFondos.xhtml
     */
    public void buscarEntidadEducativaRes() {
        if (codigoEntidad.length() == 5) {
            /**
             * Fecha: 30/08/2018 Comentario: Validación de seleccion del año y
             * proceso de adquisición
             */
            if (getRecuperarProceso().getProcesoAdquisicion() == null) {
                JsfUtil.mensajeAlerta("Debe de seleccionar un año y proceso de contratación.");
            } else {
                detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
                abrirDialogCe = false;

                current = ofertaBienesServiciosEJB.getOfertaByProcesoCodigoEntidad(codigoEntidad, detalleProceso);

                if (current == null) {
                    entidadEducativa = entidadEducativaEJB.getEntidadEducativa(codigoEntidad);
                    if (entidadEducativa == null) {
                        JsfUtil.mensajeAlerta("No se ha encontrado el centro escolar con código: " + codigoEntidad);
                    } else {
                        JsfUtil.mensajeError("No existe un proceso de contratación para este centro escolar.");
                    }
                } else {
                    entidadEducativa = current.getCodigoEntidad();
                    ejecutarCalculo(codigoEntidad);
                    cargarOferta();
                }
            }
        } else {
            if (current != null && current.getParticipantesList() != null) {
                current.getParticipantesList().clear();
            }
            entidadEducativa = null;
        }
    }

    private void cargarOferta() {
        if (VarSession.getDepartamentoUsuarioSession() != null) {
            String dep = getRecuperarProceso().getDepartamento();
            if (entidadEducativa.getCodigoDepartamento().getCodigoDepartamento().equals(dep)
                    || (Integer) VarSession.getVariableSession("idTipoUsuario") == 1) {
                if (VarSession.getVariableSessionED() == 1) {
                    if (current != null) {
                        JsfUtil.mensajeError("Ya existe un proceso de contratación para este centro escolar.");
                    }
                } else if (VarSession.getVariableSessionED() == 2) {
                    if (current == null) {
                        JsfUtil.mensajeError("No existe un proceso de contratación para este centro escolar.");
                    }
                }
            } else {
                JsfUtil.mensajeAlerta("El codigo del centro escolar no pertenece al departamento " + JsfUtil.getNombreDepartamentoByCodigo(dep) + "<br/>"
                        + "Departamento del CE: " + entidadEducativa.getCodigoEntidad() + " es " + entidadEducativa.getCodigoDepartamento().getNombreDepartamento());
            }
            abrirDialogCe = true;
        } else {
            JsfUtil.mensajeAlerta("Debe de seleccionar un departamento y municipio.");
        }
    }

    public void cargarDetalleProveedor(BigDecimal idEmpresa) {
        //capaInstSeleccionada = capa;
        empresaSeleccionada = proveedorEJB.findEmpresaByPk(idEmpresa);
        File carpetaNfs = new File("/imagenes/PaqueteEscolar/Fotos_Zapatos/" + empresaSeleccionada.getNumeroNit() + "/");
        lstPrecios = proveedorEJB.getLstPreciosByIdEmpresaAndIdProcesoAdq(empresaSeleccionada.getIdEmpresa(), detalleProceso.getIdProcesoAdq().getIdAnho().getIdAnho(), mapItems.get("idNivelesCe"));

        if (carpetaNfs.list() != null) {
            lstEstilos = new SelectItem[carpetaNfs.list().length + 1];
            int i = 0;
            lstEstilos[i] = new SelectItem("-", "Seleccione");
            i++;
            for (String string : carpetaNfs.list()) {
                lstEstilos[i] = new SelectItem(string, string);
                i++;
            }
        }
    }

    public void filterProveedores() {
        lstCapaEmpresas = JsfUtil.getListFilterByStream(lstEmpresas, nombreEmp);
    }

    public void filterProveedoresOtros() {
        lstCapaEmpresasOtros = JsfUtil.getListFilterByStream(lstEmpresasOtros, nombreEmpOtros);
    }

    public void filterReiniciarProveedores() {
        lstCapaEmpresas.clear();
        lstCapaEmpresas.addAll(lstEmpresas);
        lstCapaEmpresasOtros.clear();
        lstCapaEmpresasOtros.addAll(lstEmpresasOtros);
    }

    public void imprimirDetalleAContratar() {
        String nombreRubroTemp = (detalleProceso.getIdRubroAdq().getIdRubroUniforme().intValue() == 1 ? "Uniforme" : (detalleProceso.getIdRubroAdq().getIdRubroInteres().intValue() == 2 ? "Utiles" : "Zapatos"));
        HashMap param = new HashMap();
        param.put("pRubro", detalleProceso.getIdRubroAdq().getDescripcionRubro());
        param.put("pProceso", detalleProceso.getIdProcesoAdq().getIdProcesoAdq());
        param.put("pAnho", detalleProceso.getIdProcesoAdq().getIdAnho().getAnho());
        param.put("pCodigoEntidad", codigoEntidad);
        param.put("pHoraYFecha", entidadEducativa.getCodigoDepartamento().getNombreDepartamento() + ", " + JsfUtil.getFechaString(new Date()));

        Reportes.generarRptSQLConnection(reportesEJB, param, "sv/gob/mined/apps/reportes/contratos/", "rptDetalleDeBienes" + nombreRubroTemp, "rptDetalleDeBienes" + nombreRubroTemp + "_");
    }

    public BigDecimal porcentajeCapacidad(BigDecimal capacidadDisponible) {
        Boolean esUniforme;
        BigDecimal porcentaje;
        switch (rubro.toBigInteger().intValue()) {
            case 4:
            case 5:
                esUniforme = true;
                porcentaje = new BigDecimal(0.125);
                break;
            default:
                esUniforme = false;
                porcentaje = new BigDecimal(0.175);
                break;
        }
        if (capacidadDisponible.compareTo(cantidadAlumnos.multiply(esUniforme ? new BigDecimal(2) : BigDecimal.ONE)) < 0) {
            return (capacidadDisponible.multiply(new BigDecimal(100))).divide(cantidadAlumnos.multiply(esUniforme ? new BigDecimal(2) : BigDecimal.ONE), 2).multiply(porcentaje);
        } else {
            return new BigDecimal(100);
        }
    }
}
