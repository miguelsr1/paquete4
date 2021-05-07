/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import sv.gob.mined.app.web.util.UtilFile;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.AnhoProcesoEJB;
import sv.gob.mined.paquescolar.ejb.DatosGeograficosEJB;
import sv.gob.mined.paquescolar.ejb.PagoProveedoresEJB;
import sv.gob.mined.paquescolar.ejb.ResolucionAdjudicativaEJB;
import sv.gob.mined.paquescolar.model.Anho;
import sv.gob.mined.paquescolar.model.Departamento;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.EstadoReserva;
import sv.gob.mined.paquescolar.model.MunicipioAledanho;
import sv.gob.mined.paquescolar.model.TipoDocPago;

/**
 *
 * @author desarrllopc2
 */
@ManagedBean
@ApplicationScoped
public class CatalogosGeneralesController implements Serializable {

    private String version;
    private List<MunicipioAledanho> lstMunicipiosAledanho = new ArrayList();

    @EJB
    private AnhoProcesoEJB anhoProcesoEJB;
    @EJB
    private DatosGeograficosEJB datosGeograficosEJB;
    @EJB
    private ResolucionAdjudicativaEJB resolucionAdjudicativaEJB;
    @EJB
    private PagoProveedoresEJB pagoProveedoresEJB;

    public CatalogosGeneralesController() {
    }

    @PostConstruct
    public void init() {
        Manifest manifest;
        try {
            InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            manifest = new Manifest();
            manifest.read(is);

            Attributes atts = manifest.getMainAttributes();

            version = atts.getValue("Implementation-Build");
            version = version + "." + atts.getValue("build-time");
        } catch (IOException ex) {
            Logger.getLogger(CatalogosGeneralesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<MunicipioAledanho> getLstMunicipiosAledanho() {
        if (lstMunicipiosAledanho == null || lstMunicipiosAledanho.isEmpty()) {
            lstMunicipiosAledanho = datosGeograficosEJB.getLstMunicipiosAledanhos();
        }
        return lstMunicipiosAledanho;
    }

    public String getVersion() {
        return version;
    }

    public List<Anho> getLstAnho() {
        return anhoProcesoEJB.getLstAnhos();
    }
    
    public List<Anho> getLstAnhoDesde() {
        return anhoProcesoEJB.getLstAnhosDesde("7");
    }

    public List<Departamento> getLstDepartamentos() {
        return datosGeograficosEJB.getLstDepartamentos();
    }

    public List<EstadoReserva> getLstEstadoReserva() {
        return resolucionAdjudicativaEJB.findEstadoReservaEntities();
    }

    public String descripcionRubro(BigDecimal idRubro) {
        switch (idRubro.intValue()) {
            case 1:
                return "SERVICIOS DE CONFECCION DE UNIFORMES";
            case 4:
                return "SERVICIOS DE CONFECCION DE 1er UNIFORMES";
            case 5:
                return "SERVICIOS DE CONFECCION DE 2do UNIFORMES";
            case 2:
                return "SUMINISTRO DE PAQUETES DE UTILES ESCOLARES";
            case 3:
                return "PRODUCCION DE ZAPATOS";
            default:
                return "";
        }
    }

    public List<TipoDocPago> getLstTipoDocPago() {
        return pagoProveedoresEJB.findTipoDocPagoEntities();
    }

    public String getFormatoFechaReporte() {
        return UtilFile.getFechaGeneracionReporte();
    }

    public String estadoReserva(BigInteger valor) {
        if (valor == null) {
            return "";
        } else {
            switch (valor.intValue()) {
                case 1:
                    return "DIGITADA";
                case 2:
                    return "APLICADA";
                case 3:
                    return "REVERTIDA";
                case 4:
                    return "ANULADA";
                case 5:
                    return "MODIFICADA";
                default:
                    return "";
            }
        }
    }

    public Boolean isUsuarioRoot() {
        return VarSession.getVariableSessionUsuario().equals("MSANCHEZ");
    }
    public Boolean isUsuarioRafa() {
        return VarSession.getVariableSessionUsuario().equals("RAFAARIAS");
    }
}
