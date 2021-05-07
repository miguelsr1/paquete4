/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.contratacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.Reportes;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.EMailEJB;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.PreciosReferenciaEJB;
import sv.gob.mined.paquescolar.ejb.ReportesEJB;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.EstadisticaCenso;
import sv.gob.mined.paquescolar.model.NivelEducativo;
import sv.gob.mined.paquescolar.model.OrganizacionEducativa;
import sv.gob.mined.paquescolar.model.PreciosRefRubro;
import sv.gob.mined.paquescolar.model.ProcesoAdquisicion;
import sv.gob.mined.paquescolar.model.TechoRubroEntEdu;
import sv.gob.mined.paquescolar.model.pojos.VwRptCertificacionPresupuestaria;
import sv.gob.mined.paquescolar.model.view.VwCatalogoEntidadEducativa;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class EstadisticasCensoController implements Serializable {

    private String nombreEncargadoCompras;
    private String codigoEntidad;
    private String nombre;
    private String nombreTesorero;
    private String nombreConsejal;
    private String telefono1;
    private String telefono2;
    private String numeroDui;

    private Boolean deshabilitado = true;
    private Boolean isProcesoAdq = true;
    private Boolean mostrarInicial = false;
    private Boolean mostrarModFlex = false;
    private Boolean uniformes = true;
    private Boolean utiles = true;
    private Boolean zapatos = true;
    private Boolean declaracion = true;
    private Boolean editDirector = false;
    private BigInteger totalAlumnosMas = BigInteger.ZERO;
    private BigInteger totalAlumnosFem = BigInteger.ZERO;
    private BigInteger totalMatricula = BigInteger.ZERO;
    private BigDecimal nivelParUni = BigDecimal.ZERO;
    private BigDecimal nivelCiclo1Uni = BigDecimal.ZERO;
    private BigDecimal nivelCiclo2Uni = BigDecimal.ZERO;
    private BigDecimal nivelCiclo3Uni = BigDecimal.ZERO;
    private BigDecimal nivelMediaUni = BigDecimal.ZERO;
    private BigDecimal nivelParUti = BigDecimal.ZERO;
    private BigDecimal nivelCiclo1Uti = BigDecimal.ZERO;
    private BigDecimal nivelCiclo2Uti = BigDecimal.ZERO;
    private BigDecimal nivelCiclo3Uti = BigDecimal.ZERO;
    private BigDecimal nivelMediaUti = BigDecimal.ZERO;
    private BigDecimal nivelParZap = BigDecimal.ZERO;
    private BigDecimal nivelCiclo1Zap = BigDecimal.ZERO;
    private BigDecimal nivelCiclo2Zap = BigDecimal.ZERO;
    private BigDecimal nivelCiclo3Zap = BigDecimal.ZERO;
    private BigDecimal nivelMediaZap = BigDecimal.ZERO;
    private BigDecimal preUniformes = BigDecimal.ZERO;
    private BigDecimal preUtiles = BigDecimal.ZERO;
    private BigDecimal preZapatos = BigDecimal.ZERO;
    private VwCatalogoEntidadEducativa entidadEducativa;
    private ProcesoAdquisicion procesoAdquisicion = new ProcesoAdquisicion();
    private OrganizacionEducativa organizacionEducativa;
    private OrganizacionEducativa organizacionEducativaEncargadoCompra;
    private OrganizacionEducativa orgTesorero;
    private OrganizacionEducativa orgConsejal;
    private DetalleProcesoAdq detProAdqUni;
    private DetalleProcesoAdq detProAdqUni2;
    private DetalleProcesoAdq detProAdqUti;
    private DetalleProcesoAdq detProAdqZap;
    //private DetalleProcesoAdq detProAdqMascarilla;
    private EstadisticaCenso estaditicaPar = new EstadisticaCenso();
    private EstadisticaCenso estaditicaIniPar = new EstadisticaCenso();
    private EstadisticaCenso estaditicaCiclo1 = new EstadisticaCenso();
    private EstadisticaCenso estaditicaCiclo2 = new EstadisticaCenso();
    private EstadisticaCenso estaditicaCiclo3 = new EstadisticaCenso();
    private EstadisticaCenso estaditicaCiclo3MF = new EstadisticaCenso();
    private EstadisticaCenso estInicial1grado = new EstadisticaCenso();
    private EstadisticaCenso estInicial2grado = new EstadisticaCenso();
    private EstadisticaCenso est1grado = new EstadisticaCenso();
    private EstadisticaCenso est2grado = new EstadisticaCenso();
    private EstadisticaCenso est3grado = new EstadisticaCenso();
    private EstadisticaCenso est4grado = new EstadisticaCenso();
    private EstadisticaCenso est5grado = new EstadisticaCenso();
    private EstadisticaCenso est6grado = new EstadisticaCenso();
    private EstadisticaCenso est7grado = new EstadisticaCenso();
    private EstadisticaCenso est8grado = new EstadisticaCenso();
    private EstadisticaCenso est9grado = new EstadisticaCenso();
    private EstadisticaCenso est1media = new EstadisticaCenso();
    private EstadisticaCenso est2media = new EstadisticaCenso();
    private EstadisticaCenso est3media = new EstadisticaCenso();
    private EstadisticaCenso estaditicaBac = new EstadisticaCenso();
    private EstadisticaCenso estaditicaBacMF = new EstadisticaCenso();
    private PreciosRefRubro preParUni = new PreciosRefRubro();
    private PreciosRefRubro preCicloIUni = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIUni = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIIUni = new PreciosRefRubro();
    private PreciosRefRubro preBacUni = new PreciosRefRubro();

    private PreciosRefRubro preParUti = new PreciosRefRubro();
    private PreciosRefRubro preCicloIUti = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIUti = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIIUti = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIIMFUti = new PreciosRefRubro();
    private PreciosRefRubro preBacUti = new PreciosRefRubro();
    private PreciosRefRubro preBacMFUti = new PreciosRefRubro();

    private PreciosRefRubro preParZap = new PreciosRefRubro();
    private PreciosRefRubro preCicloIZap = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIZap = new PreciosRefRubro();
    private PreciosRefRubro preCicloIIIZap = new PreciosRefRubro();
    private PreciosRefRubro preBacZap = new PreciosRefRubro();

    private PreciosRefRubro preGrado1Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado2Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado3Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado4Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado5Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado6Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado7Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado8Uti = new PreciosRefRubro();
    private PreciosRefRubro preGrado9Uti = new PreciosRefRubro();
    private PreciosRefRubro preBachi1Uti = new PreciosRefRubro();
    private PreciosRefRubro preBachi2Uti = new PreciosRefRubro();

    private TechoRubroEntEdu techoUni = new TechoRubroEntEdu();
    private TechoRubroEntEdu techoUni2 = new TechoRubroEntEdu();
    private TechoRubroEntEdu techoUti = new TechoRubroEntEdu();
    private TechoRubroEntEdu techoZap = new TechoRubroEntEdu();

    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private PreciosReferenciaEJB preciosReferenciaEJB;
    @Inject
    private ReportesEJB reportesEJB;
    @Inject
    private EMailEJB eMailEJB;

    private static final ResourceBundle UTIL_CORREO = ResourceBundle.getBundle("Bundle");

    /**
     * Creates a new instance of EstadisticaCensoController
     */
    public EstadisticasCensoController() {
    }

    @PostConstruct
    public void init() {
        VarSession.setVariableSessionED("0");
        prepareEdit();
        procesoAdquisicion = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getProceso();
        if (procesoAdquisicion == null || procesoAdquisicion.getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de adquisición.");
        } else {
            mostrarInicial = (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8);
            mostrarModFlex = (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8);
        }
    }

    public String getNombreTesorero() {
        return nombreTesorero;
    }

    public void setNombreTesorero(String nombreTesorero) {
        this.nombreTesorero = nombreTesorero;
    }

    public String getNombreConsejal() {
        return nombreConsejal;
    }

    public void setNombreConsejal(String nombreConsejal) {
        this.nombreConsejal = nombreConsejal;
    }

    public String getNombreEncargadoCompras() {
        return nombreEncargadoCompras;
    }

    public void setNombreEncargadoCompras(String nombreEncargadoCompras) {
        this.nombreEncargadoCompras = nombreEncargadoCompras;
    }

    public EstadisticaCenso getEstaditicaIniPar() {
        return estaditicaIniPar;
    }

    public void setEstaditicaIniPar(EstadisticaCenso estaditicaIniPar) {
        this.estaditicaIniPar = estaditicaIniPar;
    }

    public EstadisticaCenso getEstaditicaCiclo3MF() {
        return estaditicaCiclo3MF;
    }

    public void setEstaditicaCiclo3MF(EstadisticaCenso estaditicaCiclo3MF) {
        this.estaditicaCiclo3MF = estaditicaCiclo3MF;
    }

    public EstadisticaCenso getEstInicial1grado() {
        return estInicial1grado;
    }

    public void setEstInicial1grado(EstadisticaCenso estInicial1grado) {
        this.estInicial1grado = estInicial1grado;
    }

    public EstadisticaCenso getEstInicial2grado() {
        return estInicial2grado;
    }

    public void setEstInicial2grado(EstadisticaCenso estInicial2grado) {
        this.estInicial2grado = estInicial2grado;
    }

    public EstadisticaCenso getEstaditicaBacMF() {
        return estaditicaBacMF;
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public void setEstaditicaBacMF(EstadisticaCenso estaditicaBacMF) {
        this.estaditicaBacMF = estaditicaBacMF;
    }

    public PreciosRefRubro getPreCicloIIIMFUti() {
        return preCicloIIIMFUti;
    }

    public void setPreCicloIIIMFUti(PreciosRefRubro preCicloIIIMFUti) {
        this.preCicloIIIMFUti = preCicloIIIMFUti;
    }

    public PreciosRefRubro getPreBacMFUti() {
        return preBacMFUti;
    }

    public void setPreBacMFUti(PreciosRefRubro preBacMFUti) {
        this.preBacMFUti = preBacMFUti;
    }

    public Boolean getMostrarInicial() {
        return mostrarInicial;
    }

    public Boolean getMostrarModFlex() {
        return mostrarModFlex;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getNumeroDui() {
        return numeroDui;
    }

    public void setNumeroDui(String numeroDui) {
        this.numeroDui = numeroDui;
    }

    public Boolean getEditDirector() {
        return editDirector;
    }

    public void setEditDirector(Boolean editDirector) {
        this.editDirector = editDirector;
    }

    public Boolean getDeclaracion() {
        return declaracion;
    }

    public void setDeclaracion(Boolean declaracion) {
        this.declaracion = declaracion;
    }

    public Boolean getUniformes() {
        return uniformes;
    }

    public void setUniformes(Boolean uniformes) {
        this.uniformes = uniformes;
    }

    public Boolean getUtiles() {
        return utiles;
    }

    public void setUtiles(Boolean utiles) {
        this.utiles = utiles;
    }

    public Boolean getZapatos() {
        return zapatos;
    }

    public void setZapatos(Boolean zapatos) {
        this.zapatos = zapatos;
    }

    public Boolean getIsProcesoAdq() {
        return isProcesoAdq;
    }

    public void setIsProcesoAdq(Boolean isProcesoAdq) {
        this.isProcesoAdq = isProcesoAdq;
    }

    public void habilitarFrm() {
        isProcesoAdq = false;
    }

    public ProcesoAdquisicion getProcesoAdquisicion() {
        return procesoAdquisicion;
    }

    public void setProcesoAdquisicion(ProcesoAdquisicion procesoAdquisicion) {
        this.procesoAdquisicion = procesoAdquisicion;
    }

    public Boolean getDeshabilitado() {
        return deshabilitado;
    }

    public void setDeshabilitado(Boolean deshabilitado) {
        this.deshabilitado = deshabilitado;
    }

    public BigDecimal getNivelParUni() {
        return nivelParUni;
    }

    public void setNivelParUni(BigDecimal nivelParUni) {
        this.nivelParUni = nivelParUni;
    }

    public BigDecimal getNivelCiclo1Uni() {
        return nivelCiclo1Uni;
    }

    public void setNivelCiclo1Uni(BigDecimal nivelCiclo1Uni) {
        this.nivelCiclo1Uni = nivelCiclo1Uni;
    }

    public BigDecimal getNivelCiclo2Uni() {
        return nivelCiclo2Uni;
    }

    public void setNivelCiclo2Uni(BigDecimal nivelCiclo2Uni) {
        this.nivelCiclo2Uni = nivelCiclo2Uni;
    }

    public BigDecimal getNivelCiclo3Uni() {
        return nivelCiclo3Uni;
    }

    public void setNivelCiclo3Uni(BigDecimal nivelCiclo3Uni) {
        this.nivelCiclo3Uni = nivelCiclo3Uni;
    }

    public BigDecimal getNivelMediaUni() {
        return nivelMediaUni;
    }

    public void setNivelMediaUni(BigDecimal nivelMediaUni) {
        this.nivelMediaUni = nivelMediaUni;
    }

    public BigDecimal getNivelParUti() {
        return nivelParUti;
    }

    public void setNivelParUti(BigDecimal nivelParUti) {
        this.nivelParUti = nivelParUti;
    }

    public BigDecimal getNivelCiclo1Uti() {
        return nivelCiclo1Uti;
    }

    public void setNivelCiclo1Uti(BigDecimal nivelCiclo1Uti) {
        this.nivelCiclo1Uti = nivelCiclo1Uti;
    }

    public BigDecimal getNivelCiclo2Uti() {
        return nivelCiclo2Uti;
    }

    public void setNivelCiclo2Uti(BigDecimal nivelCiclo2Uti) {
        this.nivelCiclo2Uti = nivelCiclo2Uti;
    }

    public BigDecimal getNivelCiclo3Uti() {
        return nivelCiclo3Uti;
    }

    public void setNivelCiclo3Uti(BigDecimal nivelCiclo3Uti) {
        this.nivelCiclo3Uti = nivelCiclo3Uti;
    }

    public BigDecimal getNivelMediaUti() {
        return nivelMediaUti;
    }

    public void setNivelMediaUti(BigDecimal nivelMediaUti) {
        this.nivelMediaUti = nivelMediaUti;
    }

    public BigDecimal getNivelParZap() {
        return nivelParZap;
    }

    public void setNivelParZap(BigDecimal nivelParZap) {
        this.nivelParZap = nivelParZap;
    }

    public BigDecimal getNivelCiclo1Zap() {
        return nivelCiclo1Zap;
    }

    public void setNivelCiclo1Zap(BigDecimal nivelCiclo1Zap) {
        this.nivelCiclo1Zap = nivelCiclo1Zap;
    }

    public BigDecimal getNivelCiclo2Zap() {
        return nivelCiclo2Zap;
    }

    public void setNivelCiclo2Zap(BigDecimal nivelCiclo2Zap) {
        this.nivelCiclo2Zap = nivelCiclo2Zap;
    }

    public BigDecimal getNivelCiclo3Zap() {
        return nivelCiclo3Zap;
    }

    public void setNivelCiclo3Zap(BigDecimal nivelCiclo3Zap) {
        this.nivelCiclo3Zap = nivelCiclo3Zap;
    }

    public BigDecimal getNivelMediaZap() {
        return nivelMediaZap;
    }

    public void setNivelMediaZap(BigDecimal nivelMediaZap) {
        this.nivelMediaZap = nivelMediaZap;
    }

    public BigInteger getTotalAlumnosMas() {
        return totalAlumnosMas;
    }

    public void setTotalAlumnosMas(BigInteger totalAlumnosMas) {
        this.totalAlumnosMas = totalAlumnosMas;
    }

    public BigInteger getTotalAlumnosFem() {
        return totalAlumnosFem;
    }

    public void setTotalAlumnosFem(BigInteger totalAlumnosFem) {
        this.totalAlumnosFem = totalAlumnosFem;
    }

    public BigInteger getTotalMatricula() {
        return totalMatricula;
    }

    public void setTotalMatricula(BigInteger totalMatricula) {
        this.totalMatricula = totalMatricula;
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

    public OrganizacionEducativa getOrganizacionEducativa() {
        return organizacionEducativa;
    }

    public void setOrganizacionEducativa(OrganizacionEducativa organizacionEducativa) {
        this.organizacionEducativa = organizacionEducativa;
    }

    public EstadisticaCenso getEstaditicaPar() {
        return estaditicaPar;
    }

    public void setEstaditicaPar(EstadisticaCenso estaditicaPar) {
        this.estaditicaPar = estaditicaPar;
    }

    public EstadisticaCenso getEstaditicaCiclo1() {
        return estaditicaCiclo1;
    }

    public void setEstaditicaCiclo1(EstadisticaCenso estaditicaCiclo1) {
        this.estaditicaCiclo1 = estaditicaCiclo1;
    }

    public EstadisticaCenso getEstaditicaCiclo2() {
        return estaditicaCiclo2;
    }

    public void setEstaditicaCiclo2(EstadisticaCenso estaditicaCiclo2) {
        this.estaditicaCiclo2 = estaditicaCiclo2;
    }

    public EstadisticaCenso getEstaditicaCiclo3() {
        return estaditicaCiclo3;
    }

    public void setEstaditicaCiclo3(EstadisticaCenso estaditicaCiclo3) {
        this.estaditicaCiclo3 = estaditicaCiclo3;
    }

    public EstadisticaCenso getEstaditicaBac() {
        return estaditicaBac;
    }

    public void setEstaditicaBac(EstadisticaCenso estaditicaBac) {
        this.estaditicaBac = estaditicaBac;
    }

    public PreciosRefRubro getPreParUni() {
        return preParUni;
    }

    public void setPreParUni(PreciosRefRubro preParUni) {
        this.preParUni = preParUni;
    }

    public PreciosRefRubro getPreCicloIUni() {
        return preCicloIUni;
    }

    public void setPreCicloIUni(PreciosRefRubro preCicloIUni) {
        this.preCicloIUni = preCicloIUni;
    }

    public PreciosRefRubro getPreCicloIIUni() {
        return preCicloIIUni;
    }

    public void setPreCicloIIUni(PreciosRefRubro preCicloIIUni) {
        this.preCicloIIUni = preCicloIIUni;
    }

    public PreciosRefRubro getPreCicloIIIUni() {
        return preCicloIIIUni;
    }

    public void setPreCicloIIIUni(PreciosRefRubro preCicloIIIUni) {
        this.preCicloIIIUni = preCicloIIIUni;
    }

    public PreciosRefRubro getPreBacUni() {
        return preBacUni;
    }

    public void setPreBacUni(PreciosRefRubro preBacUni) {
        this.preBacUni = preBacUni;
    }

    public PreciosRefRubro getPreParUti() {
        return preParUti;
    }

    public void setPreParUti(PreciosRefRubro preParUti) {
        this.preParUti = preParUti;
    }

    public PreciosRefRubro getPreCicloIUti() {
        return preCicloIUti;
    }

    public void setPreCicloIUti(PreciosRefRubro preCicloIUti) {
        this.preCicloIUti = preCicloIUti;
    }

    public PreciosRefRubro getPreCicloIIUti() {
        return preCicloIIUti;
    }

    public void setPreCicloIIUti(PreciosRefRubro preCicloIIUti) {
        this.preCicloIIUti = preCicloIIUti;
    }

    public PreciosRefRubro getPreCicloIIIUti() {
        return preCicloIIIUti;
    }

    public void setPreCicloIIIUti(PreciosRefRubro preCicloIIIUti) {
        this.preCicloIIIUti = preCicloIIIUti;
    }

    public PreciosRefRubro getPreBacUti() {
        return preBacUti;
    }

    public void setPreBacUti(PreciosRefRubro preBacUti) {
        this.preBacUti = preBacUti;
    }

    public PreciosRefRubro getPreParZap() {
        return preParZap;
    }

    public void setPreParZap(PreciosRefRubro preParZap) {
        this.preParZap = preParZap;
    }

    public PreciosRefRubro getPreCicloIZap() {
        return preCicloIZap;
    }

    public void setPreCicloIZap(PreciosRefRubro preCicloIZap) {
        this.preCicloIZap = preCicloIZap;
    }

    public PreciosRefRubro getPreCicloIIZap() {
        return preCicloIIZap;
    }

    public void setPreCicloIIZap(PreciosRefRubro preCicloIIZap) {
        this.preCicloIIZap = preCicloIIZap;
    }

    public PreciosRefRubro getPreCicloIIIZap() {
        return preCicloIIIZap;
    }

    public void setPreCicloIIIZap(PreciosRefRubro preCicloIIIZap) {
        this.preCicloIIIZap = preCicloIIIZap;
    }

    public PreciosRefRubro getPreBacZap() {
        return preBacZap;
    }

    public void setPreBacZap(PreciosRefRubro preBacZap) {
        this.preBacZap = preBacZap;
    }

    public EstadisticaCenso getEst7grado() {
        return est7grado;
    }

    public void setEst7grado(EstadisticaCenso est7grado) {
        this.est7grado = est7grado;
    }

    public EstadisticaCenso getEst8grado() {
        return est8grado;
    }

    public void setEst8grado(EstadisticaCenso est8grado) {
        this.est8grado = est8grado;
    }

    public EstadisticaCenso getEst9grado() {
        return est9grado;
    }

    public void setEst9grado(EstadisticaCenso est9grado) {
        this.est9grado = est9grado;
    }

    public EstadisticaCenso getEst1grado() {
        return est1grado;
    }

    public void setEst1grado(EstadisticaCenso est1grado) {
        this.est1grado = est1grado;
    }

    public EstadisticaCenso getEst2grado() {
        return est2grado;
    }

    public void setEst2grado(EstadisticaCenso est2grado) {
        this.est2grado = est2grado;
    }

    public EstadisticaCenso getEst3grado() {
        return est3grado;
    }

    public void setEst3grado(EstadisticaCenso est3grado) {
        this.est3grado = est3grado;
    }

    public EstadisticaCenso getEst4grado() {
        return est4grado;
    }

    public void setEst4grado(EstadisticaCenso est4grado) {
        this.est4grado = est4grado;
    }

    public EstadisticaCenso getEst5grado() {
        return est5grado;
    }

    public void setEst5grado(EstadisticaCenso est5grado) {
        this.est5grado = est5grado;
    }

    public EstadisticaCenso getEst6grado() {
        return est6grado;
    }

    public void setEst6grado(EstadisticaCenso est6grado) {
        this.est6grado = est6grado;
    }

    public EstadisticaCenso getEst1media() {
        return est1media;
    }

    public void setEst1media(EstadisticaCenso est1media) {
        this.est1media = est1media;
    }

    public EstadisticaCenso getEst2media() {
        return est2media;
    }

    public void setEst2media(EstadisticaCenso est2media) {
        this.est2media = est2media;
    }

    public EstadisticaCenso getEst3media() {
        return est3media;
    }

    public void setEst3media(EstadisticaCenso est3media) {
        this.est3media = est3media;
    }

    public PreciosRefRubro getPreGrado1Uti() {
        return preGrado1Uti;
    }

    public PreciosRefRubro getPreGrado2Uti() {
        return preGrado2Uti;
    }

    public PreciosRefRubro getPreGrado3Uti() {
        return preGrado3Uti;
    }

    public PreciosRefRubro getPreGrado4Uti() {
        return preGrado4Uti;
    }

    public PreciosRefRubro getPreGrado5Uti() {
        return preGrado5Uti;
    }

    public PreciosRefRubro getPreGrado6Uti() {
        return preGrado6Uti;
    }

    public PreciosRefRubro getPreGrado7Uti() {
        return preGrado7Uti;
    }

    public PreciosRefRubro getPreGrado8Uti() {
        return preGrado8Uti;
    }

    public PreciosRefRubro getPreGrado9Uti() {
        return preGrado9Uti;
    }

    public PreciosRefRubro getPreBachi1Uti() {
        return preBachi1Uti;
    }

    public PreciosRefRubro getPreBachi2Uti() {
        return preBachi2Uti;
    }

    // </editor-fold>
    public boolean getEENuevo() {
        return VarSession.getVariableSessionED() == 1;
    }

    public boolean getEEModificar() {
        return VarSession.getVariableSessionED() == 2;
    }

    public void prepareCreate() {
        VarSession.setVariableSessionED("1");
        deshabilitado = false;
    }

    public void prepareEdit() {
        VarSession.setVariableSessionED("0");
        deshabilitado = false;
        codigoEntidad = "";
        entidadEducativa = new VwCatalogoEntidadEducativa();
        organizacionEducativa = new OrganizacionEducativa();
        estaditicaPar = new EstadisticaCenso();
        estaditicaCiclo1 = new EstadisticaCenso();
        estaditicaCiclo2 = new EstadisticaCenso();
        estaditicaCiclo3 = new EstadisticaCenso();
        estaditicaBac = new EstadisticaCenso();
    }

    public void buscarEntidadEducativa() {
        if (codigoEntidad != null && codigoEntidad.length() == 5) {

            if (procesoAdquisicion != null) {
                organizacionEducativa = entidadEducativaEJB.getPresidenteOrganismoEscolar(codigoEntidad);
                organizacionEducativaEncargadoCompra = entidadEducativaEJB.getMiembro(codigoEntidad, "ENCARGADO_COMPRA");
                orgTesorero = entidadEducativaEJB.getMiembro(codigoEntidad, "TESORERO");
                orgConsejal = entidadEducativaEJB.getMiembro(codigoEntidad, "CONSEJAL");

                if (organizacionEducativa.getIdOrganizacionEducativa() == null) {
                    organizacionEducativa.setCargo("Presidente Propietario, Director");
                    organizacionEducativa.setCodigoEntidad(codigoEntidad);
                    organizacionEducativa.setEstadoEliminacion(BigInteger.ZERO);
                    organizacionEducativa.setFechaInsercion(new Date());
                    organizacionEducativa.setFirmaContrato(BigInteger.ONE);
                    organizacionEducativa.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                } else {
                    nombre = organizacionEducativa.getNombreMiembro();
                    telefono1 = organizacionEducativa.getTelDirector();
                    telefono2 = organizacionEducativa.getTelDirector2();
                    numeroDui = organizacionEducativa.getNumeroDui();
                    nombreTesorero = orgTesorero.getNombreMiembro();
                    nombreConsejal = orgConsejal.getNombreMiembro();
                }

                if (organizacionEducativaEncargadoCompra.getIdOrganizacionEducativa() == null) {
                    organizacionEducativaEncargadoCompra.setCargo("ENCARGADO_COMPRA");
                    organizacionEducativaEncargadoCompra.setCodigoEntidad(codigoEntidad);
                    organizacionEducativaEncargadoCompra.setEstadoEliminacion(BigInteger.ZERO);
                    organizacionEducativaEncargadoCompra.setFechaInsercion(new Date());
                    organizacionEducativaEncargadoCompra.setFirmaContrato(BigInteger.ZERO);
                    organizacionEducativaEncargadoCompra.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                } else {
                    nombreEncargadoCompras = organizacionEducativaEncargadoCompra.getNombreMiembro();
                }

                isProcesoAdq = false;
                entidadEducativa = entidadEducativaEJB.getEntidadEducativaEstadistica(codigoEntidad);

                if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() < 6) {//menor a 2018
                    detProAdqUni = JsfUtil.findDetalle(procesoAdquisicion, BigDecimal.ONE);
                } else {
                    detProAdqUni = JsfUtil.findDetalle(procesoAdquisicion, new BigDecimal(4));
                    detProAdqUni2 = JsfUtil.findDetalle(procesoAdquisicion, new BigDecimal(5));
                }

                detProAdqUti = JsfUtil.findDetalle(procesoAdquisicion, new BigDecimal(2));
                detProAdqZap = JsfUtil.findDetalle(procesoAdquisicion, new BigDecimal(3));

                recuperarEstadisticas();
                recuperarPreciosMaxRef();
                recuperarTechos();
            } else {
                JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de adquisición.");
            }

            PrimeFaces.current().executeScript("actualizarDatos();");
        } else {
            isProcesoAdq = true;
        }
    }

    private void recuperarTechos() {
        if (detProAdqUni == null) {
            JsfUtil.mensajeError("Error en el proceso de adquisicion.");
        } else {
            List<TechoRubroEntEdu> lstTechos = entidadEducativaEJB.getLstTechosByProceso(procesoAdquisicion.getIdProcesoAdq(), codigoEntidad);

            techoUni = getTecho(lstTechos, detProAdqUni);
            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 5) {
                for (DetalleProcesoAdq detProcesoAdq : procesoAdquisicion.getDetalleProcesoAdqList()) {
                    switch (detProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                        case 5:
                            techoUni2 = getTecho(lstTechos, detProAdqUni2);
                            break;
                    }
                }

            }
            techoUti = getTecho(lstTechos, detProAdqUti);
            techoZap = getTecho(lstTechos, detProAdqZap);
            //techoMascarilla = getTecho(lstTechos, detProAdqMascarilla);
        }
    }

    private void recuperarEstadisticas() {
        List<EstadisticaCenso> lstEstadistica = entidadEducativaEJB.getLstEstadisticaByCodEntAndProceso(codigoEntidad, procesoAdquisicion.getIdProcesoAdq());

        if (lstEstadistica.isEmpty()) {
            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() == 9) {
                estInicial1grado = crearEstadistica(new BigDecimal(25));
                estInicial2grado = crearEstadistica(new BigDecimal(26));
                estaditicaIniPar = crearEstadistica(new BigDecimal(22));
                estaditicaCiclo3MF = crearEstadistica(new BigDecimal(23));
                estaditicaBacMF = crearEstadistica(new BigDecimal(24));
            }
            estaditicaPar = crearEstadistica(BigDecimal.ONE);
            estaditicaCiclo1 = crearEstadistica(new BigDecimal(3));
            estaditicaCiclo2 = crearEstadistica(new BigDecimal(4));
            estaditicaCiclo3 = crearEstadistica(new BigDecimal(5));
            estaditicaBac = crearEstadistica(new BigDecimal(6));

            est7grado = crearEstadistica(new BigDecimal(7));
            est8grado = crearEstadistica(new BigDecimal(8));
            est9grado = crearEstadistica(new BigDecimal(9));
            est1grado = crearEstadistica(new BigDecimal(10));
            est2grado = crearEstadistica(new BigDecimal(11));
            est3grado = crearEstadistica(new BigDecimal(12));
            est4grado = crearEstadistica(new BigDecimal(13));
            est5grado = crearEstadistica(new BigDecimal(14));
            est6grado = crearEstadistica(new BigDecimal(15));
            est1media = crearEstadistica(new BigDecimal(16));
            est2media = crearEstadistica(new BigDecimal(17));
            est3media = crearEstadistica(new BigDecimal(18));
        } else {
            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() == 9) {
                estInicial1grado = getEstadisticaCenso(lstEstadistica, 25);
                estInicial2grado = getEstadisticaCenso(lstEstadistica, 26);
                estaditicaIniPar = getEstadisticaCenso(lstEstadistica, 22);
                estaditicaCiclo3MF = getEstadisticaCenso(lstEstadistica, 23);
                estaditicaBacMF = getEstadisticaCenso(lstEstadistica, 24);
            }
            estaditicaPar = getEstadisticaCenso(lstEstadistica, 1);
            estaditicaCiclo1 = getEstadisticaCenso(lstEstadistica, 3);
            estaditicaCiclo2 = getEstadisticaCenso(lstEstadistica, 4);
            estaditicaCiclo3 = getEstadisticaCenso(lstEstadistica, 5);
            estaditicaBac = getEstadisticaCenso(lstEstadistica, 6);
            est7grado = getEstadisticaCenso(lstEstadistica, 7);
            est8grado = getEstadisticaCenso(lstEstadistica, 8);
            est9grado = getEstadisticaCenso(lstEstadistica, 9);
            est1grado = getEstadisticaCenso(lstEstadistica, 10);
            est2grado = getEstadisticaCenso(lstEstadistica, 11);
            est3grado = getEstadisticaCenso(lstEstadistica, 12);
            est4grado = getEstadisticaCenso(lstEstadistica, 13);
            est5grado = getEstadisticaCenso(lstEstadistica, 14);
            est6grado = getEstadisticaCenso(lstEstadistica, 15);
            est1media = getEstadisticaCenso(lstEstadistica, 16);
            est2media = getEstadisticaCenso(lstEstadistica, 17);
            est3media = getEstadisticaCenso(lstEstadistica, 18);
        }
    }

    private void recuperarPreciosMaxRef() {
        if (detProAdqUni == null) {
            JsfUtil.mensajeError("Error en el proceso de adquisicion.");
        } else {
            List<PreciosRefRubro> lstPrecios = preciosReferenciaEJB.getLstPreciosRefRubroByRubro(detProAdqUni);

            if (lstPrecios.isEmpty()) {
                JsfUtil.mensajeError("Se deben de registrar los precios máximos de referencia.");
            } else {
                preParUni = getPrecioMax(lstPrecios, 1);
                preCicloIUni = getPrecioMax(lstPrecios, 3);
                preCicloIIUni = getPrecioMax(lstPrecios, 4);
                preCicloIIIUni = getPrecioMax(lstPrecios, 5);
                preBacUni = getPrecioMax(lstPrecios, 6);
            }

            lstPrecios = preciosReferenciaEJB.getLstPreciosRefRubroByRubro(detProAdqUti);

            if (lstPrecios.isEmpty()) {
                JsfUtil.mensajeError("Se deben de registrar los precios máximos de referencia.");
            } else {
                preParUti = getPrecioMax(lstPrecios, ((procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) ? 22 : 1));
                preCicloIUti = getPrecioMax(lstPrecios, 3);
                preCicloIIUti = getPrecioMax(lstPrecios, 4);
                preCicloIIIUti = getPrecioMax(lstPrecios, 5);
                if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
                    preCicloIIIMFUti = getPrecioMax(lstPrecios, 23);
                    preBacMFUti = getPrecioMax(lstPrecios, 24);
                }
                preBacUti = getPrecioMax(lstPrecios, 6);
            }

            lstPrecios = preciosReferenciaEJB.getLstPreciosRefRubroByRubro(detProAdqZap);
            if (lstPrecios.isEmpty()) {
                JsfUtil.mensajeError("Se deben de registrar los precios máximos de referencia.");
            } else {
                preParZap = getPrecioMax(lstPrecios, ((procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) ? 22 : 1));
                preCicloIZap = getPrecioMax(lstPrecios, 3);
                preCicloIIZap = getPrecioMax(lstPrecios, 4);
                preCicloIIIZap = getPrecioMax(lstPrecios, 5);
                preBacZap = getPrecioMax(lstPrecios, 6);
            }

            /*if (detProAdqMascarilla != null) {
                lstPrecios = preciosReferenciaEJB.getLstPreciosRefRubroByRubro(detProAdqMascarilla);

                if (lstPrecios.isEmpty()) {
                    JsfUtil.mensajeError("Se deben de registrar los precios máximos de referencia.");
                } else {
                    if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() == 9) {
                        preParMascarilla = getPrecioMax(lstPrecios, 22);
                    } else {
                        preParMascarilla = getPrecioMax(lstPrecios, 1);
                    }

                    preCicloIMascarilla = getPrecioMax(lstPrecios, 3);
                    preCicloIIMascarilla = getPrecioMax(lstPrecios, 4);
                    preCicloIIIMascarilla = getPrecioMax(lstPrecios, 5);
                    preBacMascarilla = getPrecioMax(lstPrecios, 6);
                }
            }*/
        }
    }

    private EstadisticaCenso crearEstadistica(BigDecimal idNivel) {
        EstadisticaCenso est = new EstadisticaCenso();
        est.setCodigoEntidad(codigoEntidad);
        est.setEstadoEliminacion((short) 0);
        est.setFechaInsercion(new Date());
        est.setFemenimo(BigInteger.ZERO);
        est.setIdNivelEducativo(new NivelEducativo(idNivel));
        est.setIdProcesoAdq(procesoAdquisicion);
        est.setMasculino(BigInteger.ZERO);
        est.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        return est;
    }

    private EstadisticaCenso getEstadisticaCenso(List<EstadisticaCenso> lstEstadistica, int idNivel) {
        EstadisticaCenso estC;
        try {
            estC = lstEstadistica.stream()
                    .filter(est -> est.getIdNivelEducativo().getIdNivelEducativo().intValue() == idNivel).findAny().get();
        } catch (NoSuchElementException e) {
            estC = null;
        }
        if (estC == null) {
            estC = crearEstadistica(new BigDecimal(idNivel));
        }
        return estC;
    }

    private TechoRubroEntEdu getTecho(List<TechoRubroEntEdu> lstTechos, DetalleProcesoAdq detProcesoAdq) {
        TechoRubroEntEdu techo = null;

        if (detProcesoAdq != null) {
            Optional<TechoRubroEntEdu> techoOptional = lstTechos.stream()
                    .filter(te -> te.getIdDetProcesoAdq().getIdDetProcesoAdq().compareTo(detProcesoAdq.getIdDetProcesoAdq()) == 0).findAny();
            if (techoOptional.isPresent()) {
                techo = techoOptional.get();
            }
        }

        if (techo == null) {
            techo = new TechoRubroEntEdu();

            techo.setCodigoEntidad(codigoEntidad);
            techo.setEstadoEliminacion(BigInteger.ZERO);
            techo.setFechaInsercion(new Date());
            techo.setIdDetProcesoAdq(detProcesoAdq);
            techo.setMontoAdjudicado(BigDecimal.ZERO);
            techo.setMontoDisponible(BigDecimal.ZERO);
            techo.setMontoPresupuestado(BigDecimal.ZERO);
            techo.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
        }
        return techo;
    }

    private PreciosRefRubro getPrecioMax(List<PreciosRefRubro> lstPrecios, int idDet) {
        Optional<PreciosRefRubro> precioOptional = lstPrecios.stream()
                .filter(p -> p.getIdNivelEducativo().getIdNivelEducativo().intValue() == idDet).findAny();
        if (precioOptional.isPresent()) {
            return precioOptional.get();
        } else {
            return null;
        }
    }

    public void updateFilaTotal() {
        PrimeFaces.current().ajax().update("lblUni6");
        PrimeFaces.current().ajax().update("lblUti6");
        PrimeFaces.current().ajax().update("lblZap6");
    }

    public void guardar() {
        Boolean error;
        Boolean isSegundoUniforme = false;

        if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
            estaditicaIniPar.setFemenimo(estInicial1grado.getFemenimo().add(estInicial2grado.getFemenimo()).add(estaditicaPar.getFemenimo()));
            estaditicaIniPar.setMasculino(estInicial1grado.getMasculino().add(estInicial2grado.getMasculino()).add(estaditicaPar.getMasculino()));
        }

        estaditicaCiclo1.setFemenimo(est1grado.getFemenimo().add(est2grado.getFemenimo()).add(est3grado.getFemenimo()));
        estaditicaCiclo1.setMasculino(est1grado.getMasculino().add(est2grado.getMasculino()).add(est3grado.getMasculino()));

        estaditicaCiclo2.setFemenimo(est4grado.getFemenimo().add(est5grado.getFemenimo()).add(est6grado.getFemenimo()));
        estaditicaCiclo2.setMasculino(est4grado.getMasculino().add(est5grado.getMasculino()).add(est6grado.getMasculino()));

        estaditicaCiclo3.setFemenimo(est7grado.getFemenimo().add(est8grado.getFemenimo()).add(est9grado.getFemenimo()));
        estaditicaCiclo3.setMasculino(est7grado.getMasculino().add(est8grado.getMasculino()).add(est9grado.getMasculino()));

        estaditicaBac.setFemenimo(est1media.getFemenimo().add(est2media.getFemenimo()).add(est3media.getFemenimo()));
        estaditicaBac.setMasculino(est1media.getMasculino().add(est2media.getMasculino()).add(est3media.getMasculino()));

        if (procesoAdquisicion == null || procesoAdquisicion.getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe de seleccionar un proceso de adquisición.");
            return;
        }

        if (entidadEducativa == null || entidadEducativa.getCodigoEntidad() == null || entidadEducativa.getCodigoEntidad().isEmpty()) {
            JsfUtil.mensajeAlerta("Debe de ingresar un código válido de un Centro Educativo");
        } else {
            String msjError = "Hubo error en los niveles: ";
            String msjInfo = "Se guardaron los niveles:<br/>";

            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
                if (entidadEducativaEJB.guardarEstadistica(estInicial1grado, VarSession.getVariableSessionUsuario())) {
                    msjError += "Inicial 1, ";
                } else {
                    msjInfo += "Inicial 1, ";
                }

                if (entidadEducativaEJB.guardarEstadistica(estInicial2grado, VarSession.getVariableSessionUsuario())) {
                    msjError += "Inicial 2, ";
                } else {
                    msjInfo += "Inicial 2, ";
                }
            }

            if (entidadEducativaEJB.guardarEstadistica(estaditicaPar, VarSession.getVariableSessionUsuario())) {
                msjError += "Parvularia, ";
            } else {
                msjInfo += "Parvularia<br/>";
            }
            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
                if (entidadEducativaEJB.guardarEstadistica(estaditicaIniPar, VarSession.getVariableSessionUsuario())) {
                    msjError += "Edu. Inicial y Parvularia, ";
                } else {
                    msjInfo += "<b>Edu. Inicial y Parvularia</b><br/>";
                }
            }

            if (entidadEducativaEJB.guardarEstadistica(est1grado, VarSession.getVariableSessionUsuario())) {
                msjError += "1er Grado, ";
            } else {
                msjInfo += "1er Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est2grado, VarSession.getVariableSessionUsuario())) {
                msjError += "2do Grado, ";
            } else {
                msjInfo += "2do Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est3grado, VarSession.getVariableSessionUsuario())) {
                msjError += "3er Grado, ";
            } else {
                msjInfo += "3er Grado<br/>";
            }
            if (entidadEducativaEJB.guardarEstadistica(estaditicaCiclo1, VarSession.getVariableSessionUsuario())) {
                msjError += "1er Ciclo, ";
            } else {
                msjInfo += "<b>1er Ciclo</b><br/>";
            }
            if (entidadEducativaEJB.guardarEstadistica(est4grado, VarSession.getVariableSessionUsuario())) {
                msjError += "4to Grado, ";
            } else {
                msjInfo += "4to Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est5grado, VarSession.getVariableSessionUsuario())) {
                msjError += "5to Grado, ";
            } else {
                msjInfo += "5to Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est6grado, VarSession.getVariableSessionUsuario())) {
                msjError += "6to Grado, ";
            } else {
                msjInfo += "6to Grado<br/>";
            }
            if (entidadEducativaEJB.guardarEstadistica(estaditicaCiclo2, VarSession.getVariableSessionUsuario())) {
                msjError += "2do Ciclo, ";
            } else {
                msjInfo += "<b>2do Ciclo</b><br/>";
            }

            if (entidadEducativaEJB.guardarEstadistica(est7grado, VarSession.getVariableSessionUsuario())) {
                msjError += "7mo Grado, ";
            } else {
                msjInfo += "7mo Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est8grado, VarSession.getVariableSessionUsuario())) {
                msjError += "8vo Grado, ";
            } else {
                msjInfo += "8vo Grado, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est9grado, VarSession.getVariableSessionUsuario())) {
                msjError += "9no Grado, ";
            } else {
                msjInfo += "9no Grado<br/>";
            }
            if (entidadEducativaEJB.guardarEstadistica(estaditicaCiclo3, VarSession.getVariableSessionUsuario())) {
                msjError += "3er Ciclo, ";
            } else {
                msjInfo += "<b>3er Ciclo</b><br/>";
            }
            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
                if (entidadEducativaEJB.guardarEstadistica(estaditicaCiclo3MF, VarSession.getVariableSessionUsuario())) {
                    msjError += "3er Ciclo - Mod. Flexible, ";
                } else {
                    msjInfo += "<b>3er Ciclo - Mod. Flexible</b><br/>";
                }
            }

            if (entidadEducativaEJB.guardarEstadistica(est1media, VarSession.getVariableSessionUsuario())) {
                msjError += "1er año de Bachillerato,";
            } else {
                msjInfo += "1er año de Bachillerato, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est2media, VarSession.getVariableSessionUsuario())) {
                msjError += "2do año de Bachillerato";
            } else {
                msjInfo += "2do año de Bachillerato, ";
            }
            if (entidadEducativaEJB.guardarEstadistica(est3media, VarSession.getVariableSessionUsuario())) {
                msjError += "3er año de Bachillerato";
            } else {
                msjInfo += "3er año de Bachillerato<br/>";
            }

            if (entidadEducativaEJB.guardarEstadistica(estaditicaBac, VarSession.getVariableSessionUsuario())) {
                msjError += "Bachillerato,";
            } else {
                msjInfo += "<b>Bachillerato</b><br/>";
            }

            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8) {
                if (entidadEducativaEJB.guardarEstadistica(estaditicaBacMF, VarSession.getVariableSessionUsuario())) {
                    msjError += "Media - Mod. Flexible, ";
                } else {
                    msjInfo += "<b>Media - Mod. Flexible</b>";
                }
            }

            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() < 6) {//menor a 2018
                techoUni.setMontoPresupuestado(calcularPresupuesto(1));
            } else {
                for (DetalleProcesoAdq detProcesoAdq : procesoAdquisicion.getDetalleProcesoAdqList()) {
                    switch (detProcesoAdq.getIdRubroAdq().getIdRubroInteres().intValue()) {
                        case 4:
                            techoUni.setMontoPresupuestado(calcularPresupuesto(4));
                            break;
                        case 5:
                            isSegundoUniforme = true;
                            techoUni2.setMontoPresupuestado(calcularPresupuesto(5));
                            if (techoUni2.getMontoAdjudicado().compareTo(BigDecimal.ZERO) == 0) {
                                techoUni2.setMontoDisponible(techoUni2.getMontoPresupuestado());
                            } else {
                                techoUni2.setMontoDisponible(techoUni2.getMontoPresupuestado().add(techoUni2.getMontoAdjudicado().negate()));
                            }
                            break;
                    }
                }
            }

            if (techoUni != null) {
                if (techoUni.getMontoAdjudicado().compareTo(BigDecimal.ZERO) == 0) {
                    techoUni.setMontoDisponible(techoUni.getMontoPresupuestado());
                } else {
                    techoUni.setMontoDisponible(techoUni.getMontoPresupuestado().add(techoUni.getMontoAdjudicado().negate()));
                }
            }
            if (techoUti != null) {
                techoUti.setMontoPresupuestado(calcularPresupuesto(2));
                if (techoUti.getMontoAdjudicado().compareTo(BigDecimal.ZERO) == 0) {
                    techoUti.setMontoDisponible(techoUti.getMontoPresupuestado());
                } else {
                    techoUti.setMontoDisponible(techoUti.getMontoPresupuestado().add(techoUti.getMontoAdjudicado().negate()));
                }
            }
            if (techoZap != null) {
                techoZap.setMontoPresupuestado(calcularPresupuesto(3));
                if (techoZap.getMontoAdjudicado().compareTo(BigDecimal.ZERO) == 0) {
                    techoZap.setMontoDisponible(techoZap.getMontoPresupuestado());
                } else {
                    techoZap.setMontoDisponible(techoZap.getMontoPresupuestado().add(techoZap.getMontoAdjudicado().negate()));
                }
            }

            if (!msjInfo.replace("Se guardaron los niveles: ", "").isEmpty()) {
                JsfUtil.mensajeInformacion(msjInfo);
            }

            if (!msjError.replace("Hubo error en los niveles: ", "").isEmpty()) {
                JsfUtil.mensajeError(msjError);
            }

            if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() < 6) {
                error = entidadEducativaEJB.guardarPresupuesto(VarSession.getVariableSessionUsuario(), techoUni, techoUti, techoZap);
            } else if (procesoAdquisicion.getDescripcionProcesoAdq().contains("SOBREDEMANDA")) {
                if (isSegundoUniforme) {
                    error = entidadEducativaEJB.guardarPresupuesto(VarSession.getVariableSessionUsuario(), techoUni, techoUni2, techoUti, techoZap);
                } else {
                    error = entidadEducativaEJB.guardarPresupuesto(VarSession.getVariableSessionUsuario(), techoUni, techoUti, techoZap);
                }
            } else {
                error = entidadEducativaEJB.guardarPresupuesto(VarSession.getVariableSessionUsuario(), techoUni, techoUni2, techoUti, techoZap);
            }

            if (error) {
                JsfUtil.mensajeError("No se ha podido crear el presupuesto del C.E.");
            }

            if (organizacionEducativa.getIdOrganizacionEducativa() == null) {
                entidadEducativaEJB.create(organizacionEducativa);
            } else {
                if (editDirector) {
                    organizacionEducativa.setNombreMiembro(nombre);
                    organizacionEducativa.setTelDirector(telefono1);
                    organizacionEducativa.setTelDirector2(telefono2);
                    organizacionEducativa.setNumeroDui(numeroDui);

                    entidadEducativaEJB.edit(organizacionEducativa);
                    entidadEducativaEJB.updateNombreDirectorContratoYPago(codigoEntidad, procesoAdquisicion.getIdAnho().getIdAnho().intValue(), nombre);
                }
            }

            organizacionEducativaEncargadoCompra.setNombreMiembro(nombreEncargadoCompras);
            initOrg(orgTesorero, "TESORERO", nombreTesorero);
            initOrg(orgConsejal, "CONSEJAL", nombreConsejal);

            entidadEducativaEJB.guardar(organizacionEducativaEncargadoCompra);
            entidadEducativaEJB.guardar(orgTesorero);
            entidadEducativaEJB.guardar(orgConsejal);
        }
    }

    private void initOrg(OrganizacionEducativa org, String cargo, String nombre) {
        org.setNombreMiembro(nombre);
        org.setCodigoEntidad(codigoEntidad);
        if (org.getIdOrganizacionEducativa() == null) {
            org.setFechaInsercion(new Date());
            org.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
            org.setCargo(cargo);
            org.setFirmaContrato(BigInteger.ZERO);
            org.setEstadoEliminacion(BigInteger.ZERO);
        } else {
            org.setFechaModificacion(new Date());
            org.setUsuarioModificacion(VarSession.getVariableSessionUsuario());
        }
    }

    public BigDecimal getPreUniformes() {
        return preUniformes;
    }

    public void setPreUniformes(BigDecimal preUniformes) {
        this.preUniformes = preUniformes;
    }

    public BigDecimal getPreUtiles() {
        return preUtiles;
    }

    public void setPreUtiles(BigDecimal preUtiles) {
        this.preUtiles = preUtiles;
    }

    public BigDecimal getPreZapatos() {
        return preZapatos;
    }

    public void setPreZapatos(BigDecimal preZapatos) {
        this.preZapatos = preZapatos;
    }

    private BigDecimal calcularPresupuesto(int idRubro) {
        BigDecimal num = BigDecimal.ONE;
        BigDecimal presupuesto = BigDecimal.ZERO;
        PreciosRefRubro preParTemp, preCiclo1Temp, preCiclo2Temp, preCiclo3Temp, preBacTemp;

        switch (idRubro) {
            case 1:
                preParTemp = preParUni;
                preCiclo1Temp = preCicloIUni;
                preCiclo2Temp = preCicloIIUni;
                preCiclo3Temp = preCicloIIIUni;
                preBacTemp = preBacUni;
                num = new BigDecimal(2);
                break;
            case 2:
                preParTemp = preParUti;
                preCiclo1Temp = preCicloIUti;
                preCiclo2Temp = preCicloIIUti;
                preCiclo3Temp = preCicloIIIUti;
                preBacTemp = preBacUti;
                break;
            case 3:
                preParTemp = preParZap;
                preCiclo1Temp = preCicloIZap;
                preCiclo2Temp = preCicloIIZap;
                preCiclo3Temp = preCicloIIIZap;
                preBacTemp = preBacZap;
                break;
            default://para rubro 4 y 5, 1er y 2do uniforme respectivamente
                preParTemp = preParUni;
                preCiclo1Temp = preCicloIUni;
                preCiclo2Temp = preCicloIIUni;
                preCiclo3Temp = preCicloIIIUni;
                preBacTemp = preBacUni;
                num = BigDecimal.ONE;
                break;
        }

        presupuesto = presupuesto.add(preParTemp.getPrecioMaxMas().multiply(new BigDecimal(estaditicaPar.getMasculino())).multiply(num));
        presupuesto = presupuesto.add(preParTemp.getPrecioMaxFem().multiply(new BigDecimal(estaditicaPar.getFemenimo())).multiply(num));

        presupuesto = presupuesto.add(preCiclo1Temp.getPrecioMaxMas().multiply(new BigDecimal(estaditicaCiclo1.getMasculino())).multiply(num));
        presupuesto = presupuesto.add(preCiclo1Temp.getPrecioMaxFem().multiply(new BigDecimal(estaditicaCiclo1.getFemenimo())).multiply(num));

        presupuesto = presupuesto.add(preCiclo2Temp.getPrecioMaxMas().multiply(new BigDecimal(estaditicaCiclo2.getMasculino())).multiply(num));
        presupuesto = presupuesto.add(preCiclo2Temp.getPrecioMaxFem().multiply(new BigDecimal(estaditicaCiclo2.getFemenimo())).multiply(num));

        presupuesto = presupuesto.add(preCiclo3Temp.getPrecioMaxMas().multiply(new BigDecimal(estaditicaCiclo3.getMasculino())).multiply(num));
        presupuesto = presupuesto.add(preCiclo3Temp.getPrecioMaxFem().multiply(new BigDecimal(estaditicaCiclo3.getFemenimo())).multiply(num));

        presupuesto = presupuesto.add(preBacTemp.getPrecioMaxFem().multiply(new BigDecimal(estaditicaBac.getFemenimo())).multiply(num));
        presupuesto = presupuesto.add(preBacTemp.getPrecioMaxMas().multiply(new BigDecimal(estaditicaBac.getMasculino())).multiply(num));

        //Presupuesto de libros
        if (idRubro == 2 && procesoAdquisicion.getIdAnho().getIdAnho().intValue() >= 6) { //mayor o igual de anho 2018

            switch (procesoAdquisicion.getIdAnho().getIdAnho().intValue()) {
                case 6:
                case 7:
                    //este calculo es comun para años 2018 y 2019
                    presupuesto = presupuesto.add(new BigDecimal(est7grado.getMasculino().add(est7grado.getFemenimo())).multiply(preGrado7Uti.getPrecioMaxMas()));
                    presupuesto = presupuesto.add(new BigDecimal(est8grado.getMasculino().add(est8grado.getFemenimo())).multiply(preGrado8Uti.getPrecioMaxMas()));
                    presupuesto = presupuesto.add(new BigDecimal(est9grado.getMasculino().add(est9grado.getFemenimo())).multiply(preGrado9Uti.getPrecioMaxMas()));

                    if (procesoAdquisicion.getIdAnho().getIdAnho().intValue() == 7) {//2019 libros desde 1 grado a 2 año de bachillerato
                        presupuesto = presupuesto.add(new BigDecimal(est1grado.getMasculino().add(est1grado.getFemenimo())).multiply(preGrado1Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est2grado.getMasculino().add(est2grado.getFemenimo())).multiply(preGrado2Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est3grado.getMasculino().add(est3grado.getFemenimo())).multiply(preGrado3Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est4grado.getMasculino().add(est4grado.getFemenimo())).multiply(preGrado4Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est5grado.getMasculino().add(est5grado.getFemenimo())).multiply(preGrado5Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est6grado.getMasculino().add(est6grado.getFemenimo())).multiply(preGrado6Uti.getPrecioMaxMas()));

                        presupuesto = presupuesto.add(new BigDecimal(est1media.getMasculino().add(est1media.getFemenimo())).multiply(preBachi1Uti.getPrecioMaxMas()));
                        presupuesto = presupuesto.add(new BigDecimal(est2media.getMasculino().add(est2media.getFemenimo())).multiply(preBachi2Uti.getPrecioMaxMas()));
                    }
                    break;
                case 9: //incluye inicial 1, 2 y modalidades flexibles en los niveles de 3er ciclo y media
                    presupuesto = presupuesto.add(new BigDecimal(estInicial1grado.getMasculino().add(estInicial1grado.getFemenimo())).multiply(preParUti.getPrecioMaxMas()));
                    presupuesto = presupuesto.add(new BigDecimal(estInicial2grado.getMasculino().add(estInicial2grado.getFemenimo())).multiply(preParUti.getPrecioMaxMas()));
                    presupuesto = presupuesto.add(new BigDecimal(estaditicaCiclo3MF.getMasculino().add(estaditicaCiclo3MF.getFemenimo())).multiply(preCicloIIIMFUti.getPrecioMaxMas()));
                    presupuesto = presupuesto.add(new BigDecimal(estaditicaBacMF.getMasculino().add(estaditicaBacMF.getFemenimo())).multiply(preBacMFUti.getPrecioMaxMas()));
                    break;
            }
        } else if (idRubro == 3 && procesoAdquisicion.getIdAnho().getIdAnho().intValue() >= 9) { //mayor o igual de anho 2018
            presupuesto = presupuesto.add(new BigDecimal(estInicial1grado.getMasculino().add(estInicial1grado.getFemenimo())).multiply(preParZap.getPrecioMaxMas()));
            presupuesto = presupuesto.add(new BigDecimal(estInicial2grado.getMasculino().add(estInicial2grado.getFemenimo())).multiply(preParZap.getPrecioMaxMas()));
        }

        return presupuesto;
    }

    public void imprimir() {
        try {
            if (entidadEducativa == null || entidadEducativa.getCodigoEntidad() == null) {
                JsfUtil.mensajeAlerta("Debe de ingresar un código de centro escolar válido!");
            } else {
                List<VwRptCertificacionPresupuestaria> lst = new ArrayList();
                HashMap param = new HashMap();
                String reportes = "";

                VwRptCertificacionPresupuestaria vw = entidadEducativaEJB.getCertificacion(codigoEntidad, procesoAdquisicion, (detProAdqUti.getIdProcesoAdq().getIdProcesoAdq() >= 12));
                vw.setUsuarioInsercion(VarSession.getVariableSessionUsuario());
                lst.add(vw);

                if (uniformes) {
                    reportes = "rptCertUni.jasper";
                }
                if (utiles) {
                    if (detProAdqUti.getIdDetProcesoAdq() >= 32) {
                        reportes += (reportes.isEmpty() ? "" : ",") + "rptCertUti" + procesoAdquisicion.getIdAnho().getAnho() + ".jasper";
                    } else {
                        reportes += (reportes.isEmpty() ? "" : ",") + "rptCertUti2017.jasper";
                    }
                }
                if (zapatos) {
                    reportes += (reportes.isEmpty() ? "" : ",") + "rptCertZap.jasper";
                }
                if (declaracion && procesoAdquisicion.getIdAnho().getIdAnho().intValue() >= 7) {
                    param.put("descripcionProcesoAdq", detProAdqUni.getIdProcesoAdq().getDescripcionProcesoAdq());
                    param.put("pAnho", detProAdqUni.getIdProcesoAdq().getIdAnho().getAnho());
                    reportes += (reportes.isEmpty() ? "" : ",") + "rptDeclaracionJuradaMatricula" + procesoAdquisicion.getIdAnho().getAnho() + ".jasper";
                }
                param.put("codigoEntidad", entidadEducativa.getCodigoEntidad());
                param.put("pIdProceso", procesoAdquisicion.getIdProcesoAdq());
                param.put("pInicial", (procesoAdquisicion.getIdAnho().getIdAnho().intValue() > 8));
                param.put("pUsuarioInsercion", VarSession.getVariableSessionUsuario());
                Reportes.generarRptsContractuales(lst, param, codigoEntidad, procesoAdquisicion.getDescripcionProcesoAdq().contains("SOBREDEMANDA"), reportesEJB, procesoAdquisicion.getIdAnho().getAnho(), reportes.split(","));
            }
        } catch (Exception e) {
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "=============================================================");
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "Error en la impresion de reporte de la certificación presupuestaria");
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "C\u00f3digo Entidad: {0}", codigoEntidad);
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "Rubros uniformes: {0} - \u00fatiles: {1} - zapatos: {2}", new Object[]{uniformes, utiles, zapatos});
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "Error: {0}", e.getMessage());
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.WARNING, "=====================================================");
        }
    }

    public void updateCantidadesCiclo3() {
        getEstaditicaCiclo3().setFemenimo(getEst7grado().getFemenimo().add(getEst8grado().getFemenimo()).add(getEst9grado().getFemenimo()));
        getEstaditicaCiclo3().setMasculino(getEst7grado().getMasculino().add(getEst8grado().getMasculino()).add(getEst9grado().getMasculino()));
    }

    public void editNombre() {
        if (editDirector) {

        } else {
            nombre = organizacionEducativa.getNombreMiembro();
            telefono1 = organizacionEducativa.getTelDirector();
            telefono2 = organizacionEducativa.getTelDirector2();
            numeroDui = organizacionEducativa.getNumeroDui();
        }
    }

    public void enviarCorreos() {
        String titulo = "Validacion de matricula y resguardo de bienes";
        String mensaje = UTIL_CORREO.getString("contratacion.estadistica.correo.cuerpo");

        List<OrganizacionEducativa> lst = entidadEducativaEJB.lstCorreosDirectores();
        HashMap<String, String> archivos = new HashMap<>();
        archivos.put("PDF", "DeclaraciónJuradaDirectores.pdf");
        archivos.put("XLXS", "FormatoValidación.xlsx");

        List<String> to = new ArrayList();
        List<String> cc = new ArrayList();

        to.add("miguel.sanchez@mined.gob.sv");

        lst.forEach((org) -> {
            to.add(org.getCorreoElectronico());
        });

        to.add("rafael.arias@mined.gob.sv");

        to.forEach((string) -> {
            List<String> lista = new ArrayList();
            lista.add(string);

            eMailEJB.enviarMail("rafael.arias@mined.gob.sv", titulo, mensaje, lista, cc, new ArrayList(), archivos);
            Logger.getLogger(EstadisticasCensoController.class.getName()).log(Level.INFO, "Correo enviado a: {0}", string);
        });
    }

}
