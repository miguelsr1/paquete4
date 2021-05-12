/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.model.Bancos;
import sv.gob.mined.paquescolar.model.CuentaBancaria;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.view.DatosPreliminarRequerimiento;

/**
 *
 * @author misanchez
 */
@Named
@javax.faces.view.ViewScoped
public class GenerarRequerimientoMB extends RecuperarProcesoUtil implements Serializable {

    private int credito = 0;
    private int idNivelEducativo;
    private int idBanco;

    private String anho;
    private String concepto;
    private String codigoDepartamento = "";
    private String cuentaBancaria = "";

    private BigDecimal idRubro = BigDecimal.ZERO;

    private DetalleProcesoAdq detalleProcesoAdq = new DetalleProcesoAdq();
    private List<DatosPreliminarRequerimiento> lstDatos = new ArrayList();
    private List<CuentaBancaria> lstCuentaBancaria = new ArrayList();

    @Inject
    private ProveedorEJB proveedorEJB;

    @PostConstruct
    public void ini() {
        ParametrosMB controller = (ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB");
        try {
            anho = controller.getAnho().getAnho().substring(2);
        } catch (Exception e) {
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.WARNING, "ERROR CONTROLADO: No se ha asignado el año de contratación");
        }
        concepto = "DOTACION DE UNIFORMES, ZAPATOS Y PAQUETES DE UTILES ESCOLARES " + controller.getAnho();
        codigoDepartamento = getRecuperarProceso().getDepartamento();
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public int getCredito() {
        return credito;
    }

    public void setCredito(int credito) {
        this.credito = credito;
    }

    public String getAnho() {
        return anho;
    }

    public void setAnho(String anho) {
        this.anho = anho;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public int getIdNivelEducativo() {
        return idNivelEducativo;
    }

    public void setIdNivelEducativo(int idNivelEducativo) {
        this.idNivelEducativo = idNivelEducativo;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal rubro) {
        this.idRubro = rubro;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public List<DatosPreliminarRequerimiento> getLstDatos() {
        return lstDatos;
    }

    public List<CuentaBancaria> getLstCuentaBancaria() {
        return lstCuentaBancaria;
    }

    public int getNumeroModalidades() {
        return lstDatos.size();
    }

    public List<Bancos> getLstBancos() {
        return proveedorEJB.getLstBancos();
    }

    public void updateConcepto() {
        String rubroStr = "";
        if (idRubro != null) {
            rubroStr = (idRubro.intValue() == 1 ? "UNIFORMES" : (idRubro.intValue() == 2 ? "UTILES" : "ZAPATOS"));
        }
        String nivel = (idNivelEducativo == 1 ? "PAR" : (idNivelEducativo == 2 ? "BAS" : "MEDI"));
        String proceso = "";
        switch (detalleProcesoAdq.getIdDetProcesoAdq()) {
            case 28:
            case 29:
            case 30:
                proceso = "SOBREDEMANDA-";
                break;
            case 31:
                proceso = "COMPLEMENTO-";
                break;
        }

        concepto = "DOTACION DE UNIFORMES, ZAPATOS Y PAQUETES DE UTILES ESCOLARES 20" + anho
                + "(RB." + proceso + rubroStr + "-"
                + nivel + "" + (credito == 1 ? "" : "-CREDI") + ")";

        lstDatos.clear();
    }

    public void obtenerDetalleProceso() {
        detalleProcesoAdq = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
    }

    public void generarDatosPreliminares() {
        lstDatos = proveedorEJB.getLstPreRequerimiento(detalleProcesoAdq.getIdDetProcesoAdq(), codigoDepartamento, credito, idNivelEducativo);
    }

    public void generarRequerimiento() {
        provisional(idNivelEducativo, credito);
    }

    public void provisional(Integer idNivel, Integer cre) {
        credito = cre;
        updateConcepto(idNivel);
        try {
            proveedorEJB.generarRequerimiento(anho, codigoDepartamento, idBanco, cuentaBancaria, concepto,
                    concepto, "12 - 01 Dotación de Uniformes, Zapatos y Útiles Escolares",
                    VarSession.getVariableSessionUsuario(), credito == 1 ? "NO" : "SI",
                    getTotalRequerimiento(), idNivel, detalleProcesoAdq.getIdDetProcesoAdq());
        } catch (Exception ex) {
            Logger.getLogger(PagoProveedoresController.class.getName()).log(Level.WARNING,
                    "ERROR CONTROLADO: Se genero el requerimiento de fondos");
            JsfUtil.mensajeInsert();
        }
    }

    private void updateConcepto(Integer idNivel) {
        String rubroStr = "";
        if (idRubro != null) {
            rubroStr = (idRubro.intValue() == 1 ? "UNIFORMES" : (idRubro.intValue() == 2 ? "UTILES" : "ZAPATOS"));
        }
        String nivel = (idNivel == 1 ? "PAR" : (idNivel == 2 ? "BAS" : "MEDI"));
        String proceso = "";
        if (detalleProcesoAdq.getIdProcesoAdq().getDescripcionProcesoAdq().contains("SOBREDEMANDA")) {
            proceso = "SOBREDEMANDA-";
        } else if (detalleProcesoAdq.getIdProcesoAdq().getDescripcionProcesoAdq().contains("MINI")) {
            proceso = "COMPLEMENTO-";
        }

        concepto = "DOTACION DE UNIFORMES, ZAPATOS Y PAQUETES DE UTILES ESCOLARES 20" + anho
                + "(RB." + proceso + rubroStr + "-"
                + nivel + "" + (credito == 1 ? "" : "-CREDI") + ")";

        lstDatos.clear();
    }

    public int getNumeroMatricula() {
        int matricula = 0;
        for (DatosPreliminarRequerimiento lstDato : lstDatos) {
            matricula += lstDato.getCantidadTotal().intValue();
        }

        if (idRubro != null && idRubro != BigDecimal.ZERO) {
            if (idRubro.intValue() == 1) {
                return matricula / 4;
            } else {
                return matricula;
            }
        } else {
            return matricula;
        }
    }

    public BigDecimal getTotalRequerimiento() {
        BigDecimal monto = BigDecimal.ZERO;
        for (DatosPreliminarRequerimiento datosPreliminarRequerimiento : lstDatos) {
            monto = monto.add(datosPreliminarRequerimiento.getMontoTotal());
        }
        if (idRubro != null && idRubro.intValue() == 1
                && !detalleProcesoAdq.getIdProcesoAdq().getDescripcionProcesoAdq().contains("SOBRE")
                && !detalleProcesoAdq.getIdProcesoAdq().getDescripcionProcesoAdq().contains("MINI")) {
            return monto.divide(new BigDecimal(2));
        } else {
            return monto;
        }
    }

    public void updateCuentaBancaria() {
        lstCuentaBancaria = proveedorEJB.getLstCuentaByDepa(codigoDepartamento);
    }
}
