/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.model.Empresa;
import sv.gob.mined.paquescolar.util.RC4Crypter;

/**
 *
 * @author MISanchez
 */
@ManagedBean
@ViewScoped
public class ValidarProveedorMB implements Serializable {

    private String codigo;
    private String nit;
    private String dui;
    private BigDecimal idEmpresa;
    private Boolean showPanel = false;
    private Boolean tokenValido = false;
    private String pass1;
    private String op = "";

    private static final ResourceBundle UTIL_CORREO = ResourceBundle.getBundle("Bundle");

    @EJB
    public ProveedorEJB proveedorEJB;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("op")) {
            if (params.get("op").equals("1")) {
                try {
                    op = "1";
                    nit = (new RC4Crypter()).decrypt("ha", params.get("codigo")).split(":")[0];
                    tokenValido = proveedorEJB.tokenUsuarioValido(params.get("codigo"));
                } catch (Exception e) {
                }
            }
        } else if (params.containsKey("codigo")) {
            try {
                String idEmpresaStr = (new RC4Crypter()).decrypt("ha", params.get("codigo")).split(":")[0];
                idEmpresa = new BigDecimal(idEmpresaStr);
                tokenValido = true;
            } catch (Exception e) {
                idEmpresa = null;
            }
        }
    }

    public Boolean getTokenValido() {
        return tokenValido;
    }

    public void setTokenValido(Boolean tokenValido) {
        this.tokenValido = tokenValido;
    }

    public String getPass1() {
        return pass1;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public Boolean getShowPanel() {
        return showPanel;
    }

    public void setShowPanel(Boolean showPanel) {
        this.showPanel = showPanel;
    }

    public void validarProveedor() {
        String tituloEmail = UTIL_CORREO.getString("prov.email.titulo");
        String cuerpoEmail = UTIL_CORREO.getString("prov.email.mensaje");

        int validar = proveedorEJB.validarCodigoSegEmpresa(codigo, nit, dui, tituloEmail, cuerpoEmail);
        Empresa emp;
        switch (validar) {
            case 1:
                emp = proveedorEJB.findEmpresaByNit(nit, false);
                JsfUtil.mensajeInformacion("Se ha enviado un correo a <b>" + emp.getIdPersona().getEmail() + "</b> para activar su usuario de acceso. <b>Esta es la única vez que podrá realizar este paso</b>.");
                break;
            case 2:
                JsfUtil.mensajeError("Los valores ingresados no coinciden con ningún proveedor");
                break;
            case 3:
                emp = proveedorEJB.findEmpresaByNit(nit, false);
                JsfUtil.mensajeInformacion("El correo registrado [<b>" + emp.getIdPersona().getEmail() + "</b>] no es un correo válido, por favor escribir a <a href='mailto:carlos.villegas@mined.edu.sv'>Carlos Villegas</a> para corregir su correo.");
                break;
        }

        showPanel = (validar == 1);
    }

    public String guardarPasswordProv() {
        if (op.equals("1")) {
            proveedorEJB.guardarPasswordPer(nit, pass1);
        } else {
            proveedorEJB.guardarPasswordProv(idEmpresa, pass1);
        }

        return "index.mined";
    }
}
