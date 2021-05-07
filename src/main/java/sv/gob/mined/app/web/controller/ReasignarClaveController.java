/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author misanchez
 */
@ManagedBean
@ViewScoped
public class ReasignarClaveController implements Serializable{

    private String correo;

    /**
     * Creates a new instance of ReasignarClave
     */
    public ReasignarClaveController() {
    }

    public void reasignar() {
        System.out.println("reasignar");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
