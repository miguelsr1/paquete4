/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;

/**
 *
 * @author misanchez
 */
public class RecuperarProcesoUtil {

    @Inject
    private RecuperarProceso recuperarProceso;

    public RecuperarProceso getRecuperarProceso() {
        return recuperarProceso;
    }

    public void setRecuperarProceso(RecuperarProceso recuperarProceso) {
        this.recuperarProceso = recuperarProceso;
    }
}
