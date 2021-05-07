/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.LoginEJB;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class ActualizarClaveView implements Serializable {

    private String usuario;
    private String clave;

    @EJB
    private LoginEJB loginEJB;

    @PostConstruct
    public void init() {
        if (VarSession.isVariableSession("cambioClave")) {
            VarSession.removeVariableSession("cambioClave");
            usuario = VarSession.getVariableSessionUsuario();
        }
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String guardarClaveAcceso() {
        loginEJB.actualizarClaveAcceso(usuario, clave);
        return "/app/inicial?faces-redirect=true";
    }
}
