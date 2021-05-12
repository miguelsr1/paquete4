/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov.modulo;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DualListModel;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.DatosGeograficosEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.model.Anho;
import sv.gob.mined.paquescolar.model.CapaDistribucionAcre;
import sv.gob.mined.paquescolar.model.CapaInstPorRubro;
import sv.gob.mined.paquescolar.model.DetRubroMuestraInteres;
import sv.gob.mined.paquescolar.model.DisMunicipioInteres;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.model.Municipio;
import sv.gob.mined.paquescolar.model.ProcesoAdquisicion;
import sv.gob.mined.paquescolar.model.pojos.proveedor.MunicipioDto;

/**
 *
 * @author MISanchez
 */
@Named
@ViewScoped
public class MunicipioInteresMB implements Serializable {

    private Boolean datosVerificados = false;
    private Empresa empresa = new Empresa();
    private CapaInstPorRubro capacidadInst = new CapaInstPorRubro();
    private CapaDistribucionAcre departamentoCalif = new CapaDistribucionAcre();

    private DualListModel<MunicipioDto> lstMunicipiosInteres = new DualListModel();
    private List<MunicipioDto> lstMunSource = new ArrayList();
    private List<MunicipioDto> lstMunTarget = new ArrayList();

    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private DatosGeograficosEJB datosGeograficosEJB;

    @PostConstruct
    public void ini() {
        if (VarSession.isVariableSession("idEmpresa")) {
            cargarDetalleCalificacion();
        }
    }

    private void cargarDetalleCalificacion() {
        ProveedorMB proveedorMB = ((ProveedorMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "proveedorMB"));
        empresa = proveedorMB.getEmpresa();
        Anho anho = proveedorMB.getAnho();
        ProcesoAdquisicion proceso = anho.getProcesoAdquisicionList().get(0);

        if (proceso == null || proceso.getIdProcesoAdq() == null) {
            JsfUtil.mensajeAlerta("Debe seleccionar un proceso de contratación");
        } else {
            DetRubroMuestraInteres detRubro = proveedorEJB.findDetRubroByAnhoAndRubro(anho.getIdAnho(), empresa.getIdEmpresa());
            capacidadInst = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), anho.getIdAnho(), empresa, CapaInstPorRubro.class);
            if (capacidadInst == null) {
                JsfUtil.mensajeAlerta("No se han cargado los datos de este proveedor para el proceso de contratación del año " + anho.getAnho());
            } else {
                if (capacidadInst.getIdMuestraInteres().getDatosVerificados() != null
                        && capacidadInst.getIdMuestraInteres().getDatosVerificados() == 1) {
                    datosVerificados = true;
                }
                departamentoCalif = proveedorEJB.findDetProveedor(detRubro.getIdRubroInteres().getIdRubroInteres(), anho.getIdAnho(), empresa, CapaDistribucionAcre.class);

                if (departamentoCalif != null && departamentoCalif.getCodigoDepartamento() != null) {
                    lstMunSource = datosGeograficosEJB.getLstMunicipiosDisponiblesDeInteres(departamentoCalif.getIdCapaDistribucion(), departamentoCalif.getCodigoDepartamento().getCodigoDepartamento());
                    lstMunTarget = datosGeograficosEJB.getLstMunicipiosDeInteres(departamentoCalif.getIdCapaDistribucion());
                    lstMunicipiosInteres = new DualListModel(lstMunSource, lstMunTarget);
                }
            }
        }
    }

    public Boolean getDatosVerificados() {
        return datosVerificados;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public CapaDistribucionAcre getDepartamentoCalif() {
        return departamentoCalif;
    }

    public void setDepartamentoCalif(CapaDistribucionAcre departamentoCalif) {
        this.departamentoCalif = departamentoCalif;
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

    public DualListModel<MunicipioDto> getLstMunicipiosInteres() {
        return lstMunicipiosInteres;
    }

    public void setLstMunicipiosInteres(DualListModel<MunicipioDto> lstMunicipiosInteres) {
        if (!(lstMunicipiosInteres.getSource().isEmpty() && lstMunicipiosInteres.getTarget().isEmpty())) {
            this.lstMunicipiosInteres = lstMunicipiosInteres;
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
}
