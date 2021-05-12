/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.pagoprov.modulo;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.AnhoProcesoEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.Anho;
import sv.gob.mined.paquescolar.model.Empresa;

/**
 *
 * @author MISanchez
 */
@Named
@SessionScoped
public class ProveedorMB implements Serializable {

    private BigDecimal idAnho = BigDecimal.ZERO;
    private Anho anho = new Anho();
    private Empresa empresa = new Empresa();

    @Inject
    private UtilEJB utilEJB;
    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private AnhoProcesoEJB anhoProcesoEJB;

    @PostConstruct
    public void ini() {
        if (VarSession.isVariableSession("idEmpresa")) {
            //empresa = proveedorEJB.findEmpresaByPk((BigDecimal) VarSession.getVariableSession("idEmpresa"));
            anho = anhoProcesoEJB.getLstAnhos().get(0);
            idAnho = anho.getIdAnho();
        }
    }

    public Empresa getEmpresa() {
        if (VarSession.isVariableSession("idEmpresa")) {
            empresa = proveedorEJB.findEmpresaByPk((BigDecimal) VarSession.getVariableSession("idEmpresa"));
        }
        return empresa;
    }

    public BigDecimal getIdAnho() {
        return idAnho;
    }

    public void setIdAnho(BigDecimal idAnho) {
        this.idAnho = idAnho;
    }

    public Anho getAnho() {
        return anho;
    }

    public void actualizarVista() throws IOException {
        anho = utilEJB.find(Anho.class, idAnho);

        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();

        FacesContext.getCurrentInstance().getExternalContext().redirect(url.split("/")[(url.split("/").length - 1)]);
    }

}
