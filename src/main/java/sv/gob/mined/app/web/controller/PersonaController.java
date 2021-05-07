/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.PersonaEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.Departamento;
import sv.gob.mined.paquescolar.model.Genero;
import sv.gob.mined.paquescolar.model.Persona;
import sv.gob.mined.paquescolar.model.TipoUsuario;
import sv.gob.mined.paquescolar.model.Usuario;

/**
 *
 * @author misanchez
 */
@ManagedBean
@ViewScoped
public class PersonaController implements Serializable {

    private Departamento departamento = new Departamento();
    private Persona current = new Persona();
    private Persona edicion = new Persona();
    private Usuario usuarioObj = new Usuario();
    private TipoUsuario idTipoUsuario = new TipoUsuario();
    private String nit;
    private String usuario;
    private String password;
    private String password2;
    private String usuario1;
    private String clave1;
    private String clave2;
    private Boolean showCambiarClave = false;
    private Boolean cambiarClave = false;
    private Boolean errorPassword = false;
    private Boolean disableClave = true;
    private Boolean deshabilitado = true;
    private Boolean deshabilitadoModificar = true;
    private Boolean periodoDeAcceso = false;
    private Boolean dialogFiltroPersona;
    private Boolean usuarioActivo = false;
    private Date fechaVencimientoClave = new Date();
    private Date fechaInicioLogin;
    private Date fechaFinLogin;
    private BigDecimal idGenero = BigDecimal.ZERO;
    @EJB
    private PersonaEJB personaEJB;
    @EJB
    private UtilEJB utilEJB;

    /**
     * Creates a new instance of PersonaController
     */
    public PersonaController() {
        edicion.setPrimerNombre("");
        edicion.setSegundoNombre("");
        edicion.setPrimerApellido("");
        edicion.setSegundoApellido("");
        edicion.setAcasada("");
    }

    public BigDecimal getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(BigDecimal idGenero) {
        this.idGenero = idGenero;
    }

    public void onItemSelect(SelectEvent event) {

    }

    public List<Persona> completeUser(String query) {
        List<Persona> lstUsers = new ArrayList();
        if (query.trim().length() > 3) {
            lstUsers = personaEJB.buscarUsuarioByUsuarioOrNombre(query);
        }
        return lstUsers;
    }

    public Usuario getUsuarioObj() {
        return usuarioObj;
    }

    public void setUsuarioObj(Usuario usuarioObj) {
        this.usuarioObj = usuarioObj;
    }

    public String prepareCreate() {
        edicion = new Persona();
        idTipoUsuario = new TipoUsuario();
        deshabilitado = false;
        disableClave = false;
        showCambiarClave = false;
        dialogFiltroPersona = false;
        errorPassword = false;
        deshabilitadoModificar = true;
        VarSession.setVariableSessionED("1");
        usuario1 = "";

        usuarioActivo = false;
        periodoDeAcceso = false;
        fechaInicioLogin = null;
        fechaFinLogin = null;
        return null;
    }

    public String prepareEdit() {
        VarSession.setVariableSessionED("2");
        nit = "";
        edicion = new Persona();
        errorPassword = false;
        dialogFiltroPersona = true;
        deshabilitadoModificar = false;
        idTipoUsuario = new TipoUsuario();
        deshabilitado = true;
        usuario1 = "";
        return null;
    }

    public void guardarUsuario() {
        edicion.setUsuario(usuario1);
        edicion.setIdGenero(utilEJB.find(Genero.class, idGenero));

        switch (VarSession.getVariableSessionED()) {
            case 1:
                if (clave1.isEmpty()) {
                    JsfUtil.mensajeAlerta("Debe de asignar una clave de acceso al usuario.");
                } else {
                    edicion.setClaveAcceso(personaEJB.encriptarclave(clave1));
                    List<Persona> lista = personaEJB.buscarNitPersona(edicion.getNumeroNit());
                    if (lista.isEmpty()) {
                        List<Persona> lista2 = personaEJB.buscarUsuario(usuario1);

                        if (lista2.isEmpty()) {
                            edicion.setUsuarioCreacion(VarSession.getVariableSessionUsuario());
                            edicion.setFechaInsercion(new Date());
                            edicion.setEstadoEliminacion(BigInteger.ZERO);

                            personaEJB.create(edicion);

                            usuarioObj = new Usuario();
                            usuarioObj.setIdPersona(edicion);
                            usuarioObj.setUsuarioInsercion(VarSession.getVariableSession("Usuario").toString());
                            usuarioObj.setFechaInsercion(new Date());
                            usuarioObj.setEstadoEliminacion(BigInteger.ZERO);
                            usuarioObj.setIdTipoUsuario(idTipoUsuario);
                            usuarioObj.setCodigoDepartamento(departamento.getCodigoDepartamento());
                            usuarioObj.setFechaVencimientoClave(fechaVencimientoClave);

                            usuarioObj.setActivo(usuarioActivo ? (short) 1 : 0);

                            if (usuarioActivo) {
                                usuarioObj.setRangoFechaLogin(periodoDeAcceso ? (short) 1 : (short) 0);
                                if (!periodoDeAcceso) {
                                    fechaInicioLogin = null;
                                    fechaFinLogin = null;
                                }
                                usuarioObj.setFechaInicioLogin(fechaInicioLogin);
                                usuarioObj.setFechaFinLogin(fechaFinLogin);
                            }

                            personaEJB.setOpcionesUsuario(usuarioObj, edicion);

                            JsfUtil.mensajeInsert();
                        } else {
                            JsfUtil.mensajeError("Ya existe el nombre de usuario " + usuario1 + ".Asigne otro nombre de usuario a esta persona.");
                        }
                    } else {
                        JsfUtil.mensajeError("Ya existe usuario con mismo numero NIT");
                    }
                    break;
                }
            case 2:
                if (cambiarClave) {
                    if (clave1.isEmpty()) {
                        JsfUtil.mensajeAlerta("Debe de asignar una clave de acceso al usuario.");
                    } else {
                        edicion.setClaveAcceso(personaEJB.encriptarclave(clave1));
                        modificarUsuario();
                    }
                } else {
                    modificarUsuario();
                }
                break;
        }
    }

    private void modificarUsuario() {
        if (periodoDeAcceso) {
            usuarioObj.setRangoFechaLogin((short) 1);
            usuarioObj.setFechaInicioLogin(fechaInicioLogin);
            usuarioObj.setFechaFinLogin(fechaFinLogin);
        } else {
            usuarioObj.setRangoFechaLogin((short) 0);
            usuarioObj.setFechaInicioLogin(null);
            usuarioObj.setFechaFinLogin(null);
        }

        edicion.setUsuarioModificacion(VarSession.getVariableSession("Usuario").toString());
        edicion.setFechaModificacion(new Date());

        personaEJB.edit(edicion);

        usuarioObj.setActivo(usuarioActivo ? (short) 1 : 0);
        usuarioObj.setFechaModificacion(new Date());
        usuarioObj.setUsuarioModificacion(VarSession.getVariableSession("Usuario").toString());
        usuarioObj.setIdTipoUsuario(idTipoUsuario);
        usuarioObj.setCodigoDepartamento(departamento.getCodigoDepartamento());

        personaEJB.setOpcionesUsuario(usuarioObj, edicion);

        JsfUtil.mensajeUpdate();
        prepareCreate();
    }

    public Persona getEdicion() {
        return edicion;
    }

    public void setEdicion(Persona persona) {
        if (persona != null) {
            edicion = persona;
        }
    }

    public Persona getSelected() {
        if (current == null) {
            current = new Persona();
        }
        return current;
    }

    public TipoUsuario getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(TipoUsuario idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Boolean getDeshabilitado() {
        return deshabilitado;
    }

    public void setDeshabilitado(Boolean deshabilitado) {
        this.deshabilitado = deshabilitado;
    }

    public Boolean getDeshabilitadoModificar() {
        return deshabilitadoModificar;
    }

    public void setDeshabilitadoModificar(Boolean deshabilitadoModificar) {
        this.deshabilitadoModificar = deshabilitadoModificar;
    }

    public Boolean getDialogFiltroPersona() {
        return dialogFiltroPersona;
    }

    public void setDialogFiltroPersona(Boolean dialogFiltroPersona) {
        this.dialogFiltroPersona = dialogFiltroPersona;
    }

    public List<TipoUsuario> getLstTipoUsuario() {
        return personaEJB.getLstTipoUsuario();
    }

    public void validarPassword() {
        errorPassword = false;
        errorPassword = personaEJB.validarPassword(clave1, clave2);
    }

    public String getClave1() {
        return clave1;
    }

    public void setClave1(String clave1) {
        this.clave1 = clave1;
    }

    public String getClave2() {
        return clave2;
    }

    public void setClave2(String clave2) {
        this.clave2 = clave2;
    }

    public String getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    public Boolean getErrorPassword() {
        return errorPassword;
    }

    public void setErrorPassword(Boolean errorPassword) {
        this.errorPassword = errorPassword;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public void buscarUsuario() {
        /*
        Fecha: 05/10/2018
        Comentario: validar la existencia de un objeto de tipo Persona
         */
        if (edicion != null) {
            if (edicion.getIdPersona() != null) {
                cargarDatosPersonaEdicion();
            } else {
                edicion = personaEJB.buscarNitPersona(nit).isEmpty() ? null : personaEJB.buscarNitPersona(nit).get(0);
                if (edicion == null) {
                    JsfUtil.mensajeInformacion("No se encontro el usuario con número de nit : " + nit);
                } else {
                    cargarDatosPersonaEdicion();
                }
            }
        }
    }

    private void cargarDatosPersonaEdicion() {
        if (edicion.getUsuarioList().isEmpty()) {
            usuarioObj = new Usuario();
            deshabilitadoModificar = true;
        } else {
            usuarioObj = edicion.getUsuarioList().get(0);
            deshabilitadoModificar = false;
        }
        usuarioActivo = usuarioObj.getActivo().intValue() == 1;
        periodoDeAcceso = usuarioObj.getRangoFechaLogin().intValue() == 1;
        if (periodoDeAcceso) {
            fechaInicioLogin = usuarioObj.getFechaInicioLogin();
            fechaFinLogin = usuarioObj.getFechaFinLogin();
        } else {
            fechaInicioLogin = null;
            fechaFinLogin = null;
        }

        idGenero = edicion.getIdGenero().getIdGenero();

        usuario1 = edicion.getUsuario();
        dialogFiltroPersona = false;
        deshabilitado = false;
        showCambiarClave = true;
    }

    public List<Genero> getLstGenero() {
        return personaEJB.getLstGenero();
    }

    public Date getFechaVencimientoClave() {
        return fechaVencimientoClave;
    }

    public void setFechaVencimientoClave(Date fechaVencimientoClave) {
        this.fechaVencimientoClave = fechaVencimientoClave;
    }

    public Boolean getPeriodoDeAcceso() {
        return periodoDeAcceso;
    }

    public void setPeriodoDeAcceso(Boolean periodoDeAcceso) {
        this.periodoDeAcceso = periodoDeAcceso;
    }

    public Date getFechaInicioLogin() {
        return fechaInicioLogin;
    }

    public void setFechaInicioLogin(Date fechaInicioLogin) {
        this.fechaInicioLogin = fechaInicioLogin;
    }

    public Date getFechaFinLogin() {
        return fechaFinLogin;
    }

    public void setFechaFinLogin(Date fechaFinLogin) {
        this.fechaFinLogin = fechaFinLogin;
    }

    public Boolean getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Boolean usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }

    public Boolean getCambiarClave() {
        return cambiarClave;
    }

    public void setCambiarClave(Boolean cambiarClave) {
        this.disableClave = !cambiarClave;
        this.cambiarClave = cambiarClave;
    }

    public Boolean getShowCambiarClave() {
        return showCambiarClave;
    }

    public void setShowCambiarClave(Boolean showCambiarClave) {
        this.showCambiarClave = showCambiarClave;
    }

    public Boolean getDisableClave() {
        return disableClave;
    }

    public void setDisableClave(Boolean disableClave) {
        this.disableClave = disableClave;
    }
}
