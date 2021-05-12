/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov.modulo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.DatosGeograficosEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.Anho;
import sv.gob.mined.paquescolar.model.Canton;
import sv.gob.mined.paquescolar.model.CapaDistribucionAcre;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.Departamento;
import sv.gob.mined.paquescolar.model.DetRubroMuestraInteres;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.Municipio;
import sv.gob.mined.paquescolar.model.ProcesoAdquisicion;

/**
 *
 * @author MISanchez
 */
@Named
@ViewScoped
public class DatosGeneralesMB implements Serializable {

    private Boolean rubroUniforme = false;
    private Boolean datosVerificados = false;
    private Boolean personaNatural = false;
    private Boolean mismaDireccion = false;

    private Boolean inscritoIva = false;
    private Boolean deseaInscribirseIva = false;

    private String tapEmpresa;
    private String tapPersona;
    private String codigoDepartamentoCalificado;
    private String codigoDepartamento = "";
    private String codigoDepartamentoLocal = "";

    private BigDecimal idMunicipio = BigDecimal.ZERO;
    private BigDecimal idMunicipioLocal = BigDecimal.ZERO;

    private String idCanton;
    private String idCantonLocal;

    private Empresa empresa = new Empresa();
    private CapaInstPorRubro capacidadInst = new CapaInstPorRubro();
    private CapaDistribucionAcre departamentoCalif = new CapaDistribucionAcre();

    private ProveedorMB proveedorMB;

    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private UtilEJB utilEJB;
    @Inject
    private DatosGeograficosEJB datosGeograficosEJB;

    @PostConstruct
    public void ini() {
        if (VarSession.isVariableSession("idEmpresa")) {
            proveedorMB = ((ProveedorMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                    getValue(FacesContext.getCurrentInstance().getELContext(), null, "proveedorMB"));

            empresa = proveedorMB.getEmpresa();

            cargarDetalleCalificacion();
        }
    }

    public Boolean getRubroUniforme() {
        return rubroUniforme;
    }

    public void setRubroUniforme(Boolean rubroUniforme) {
        this.rubroUniforme = rubroUniforme;
    }

    public String getIdCanton() {
        return idCanton;
    }

    public void setIdCanton(String idCanton) {
        this.idCanton = idCanton;
    }

    public String getIdCantonLocal() {
        return idCantonLocal;
    }

    public void setIdCantonLocal(String idCantonLocal) {
        this.idCantonLocal = idCantonLocal;
    }

    public Boolean getMismaDireccion() {
        return mismaDireccion;
    }

    public void setMismaDireccion(Boolean mismaDireccion) {
        this.mismaDireccion = mismaDireccion;
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

    public Boolean getPersonaNatural() {
        return personaNatural;
    }

    public Boolean getDatosVerificados() {
        return datosVerificados;
    }

    private void cargarDetalleCalificacion() {
        Anho anho = proveedorMB.getAnho();
        ProcesoAdquisicion proceso = anho.getProcesoAdquisicionList().get(0);

        if (proceso == null || proceso.getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe seleccionar un proceso de contratación");
        } else {
            if (proceso.getPadreIdProcesoAdq() != null) {
                proceso = proceso.getPadreIdProcesoAdq();
            }
            DetRubroMuestraInteres detRubro = proveedorEJB.findDetRubroByAnhoAndRubro(anho.getIdAnho(), empresa.getIdEmpresa());
            capacidadInst = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), anho.getIdAnho(), empresa, CapaInstPorRubro.class);
            if (capacidadInst == null) {
                JsfUtil.mensajeAlerta("No se han cargado los datos de este proveedor para el proceso de contratación del año " + proceso.getIdAnho().getAnho());
            } else {
                if (capacidadInst.getIdMuestraInteres().getDatosVerificados() != null
                        && capacidadInst.getIdMuestraInteres().getDatosVerificados() == 1) {
                    datosVerificados = true;
                }

                departamentoCalif = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), anho.getIdAnho(), empresa, CapaDistribucionAcre.class);

                if (departamentoCalif == null || departamentoCalif.getCodigoDepartamento() == null) {
                    JsfUtil.mensajeAlerta("Este proveedor no posee departamento de calificación " + proceso.getIdAnho().getAnho());
                } else {
                    codigoDepartamentoCalificado = departamentoCalif.getCodigoDepartamento().getCodigoDepartamento();
                    personaNatural = (empresa.getIdPersoneria().getIdPersoneria().intValue() == 1);

                    if (personaNatural) {
                        mismaDireccion = (empresa.getIdMunicipio().getIdMunicipio().intValue() == empresa.getIdPersona().getIdMunicipio().getIdMunicipio().intValue()
                                && empresa.getDireccionCompleta().equals(empresa.getIdPersona().getDomicilio()));
                    }
                    idMunicipio = empresa.getIdPersona().getIdMunicipio().getIdMunicipio();
                    codigoDepartamento = empresa.getIdPersona().getIdMunicipio().getCodigoDepartamento().getCodigoDepartamento();

                    idMunicipioLocal = empresa.getIdMunicipio().getIdMunicipio();
                    codigoDepartamentoLocal = empresa.getIdMunicipio().getCodigoDepartamento().getCodigoDepartamento();

                    rubroUniforme = (departamentoCalif.getIdMuestraInteres().getIdRubroInteres().getIdRubroUniforme().intValue() == 1);

                    if (rubroUniforme) {
                        inscritoIva = (empresa.getEsContribuyente() == 1);
                        deseaInscribirseIva = (empresa.getDeseaInscribirse() == 1);
                        idCanton = empresa.getIdPersona().getCodigoCanton();
                        idCantonLocal = empresa.getCodigoCanton();
                    }
                }
            }
        }
    }

    public Boolean getInscritoIva() {
        return inscritoIva;
    }

    public void setInscritoIva(Boolean inscritoIva) {
        this.inscritoIva = inscritoIva;
    }

    public Boolean getDeseaInscribirseIva() {
        return deseaInscribirseIva;
    }

    public void setDeseaInscribirseIva(Boolean deseaInscribirseIva) {
        this.deseaInscribirseIva = deseaInscribirseIva;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

    public String getCodigoDepartamentoCalificado() {
        return codigoDepartamentoCalificado;
    }

    public void setCodigoDepartamentoCalificado(String codigoDepartamentoCalificado) {
        this.codigoDepartamentoCalificado = codigoDepartamentoCalificado;
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

    public void guardarDatosGenerales() {
        if (empresa.getIdPersoneria().getIdPersoneria().intValue() == 1) {
            empresa.setRazonSocial(empresa.getIdPersona().getNombreCompletoProveedor());
            empresa.setTelefonos(empresa.getIdPersona().getNumeroTelefono());
            empresa.setNumeroCelular(empresa.getIdPersona().getNumeroCelular());
            empresa.setNumeroNit(empresa.getIdPersona().getNumeroNit());

            if (mismaDireccion) {
                idMunicipioLocal = idMunicipio;
                idCantonLocal = idCanton;
                empresa.setDireccionCompleta(empresa.getIdPersona().getDomicilio());

                if (rubroUniforme) {
                    empresa.setCodigoCanton(idCanton);
                }
            }
        }

        empresa.getIdPersona().setIdMunicipio(new Municipio(idMunicipio));
        empresa.getIdPersona().setCodigoCanton(idCanton);
        empresa.setIdMunicipio(new Municipio(idMunicipioLocal));
        empresa.setCodigoCanton(idCantonLocal);

        empresa.setFechaModificacion(new Date());
        empresa.setUsuarioModificacion(VarSession.getVariableSessionUsuario());

        if (rubroUniforme) {
            empresa.setEsContribuyente(inscritoIva ? (short) 1 : 0);
            empresa.setDeseaInscribirse(deseaInscribirseIva ? (short) 1 : 0);
        }

        proveedorEJB.guardar(empresa);

        departamentoCalif.setCodigoDepartamento(utilEJB.find(Departamento.class,
                codigoDepartamentoCalificado));

        if (proveedorEJB.guardarCapaInst(departamentoCalif, capacidadInst)) {
            JsfUtil.mensajeUpdate();
        }
    }

    public List<Municipio> getLstMunicipios() {
        return datosGeograficosEJB.getLstMunicipiosByDepartamento(codigoDepartamento);
    }

    public List<Municipio> getLstMunicipiosLocal() {
        return datosGeograficosEJB.getLstMunicipiosByDepartamento(codigoDepartamentoLocal);
    }

    public List<Canton> getLstCantonLocal() {
        return datosGeograficosEJB.getLstCantonByMunicipio(idMunicipioLocal);
    }

    public List<Canton> getLstCanton() {
        return datosGeograficosEJB.getLstCantonByMunicipio(idMunicipio);
    }
}
