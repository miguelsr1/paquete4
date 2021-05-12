/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(".letras").keypress(function (key) {
    window.console.log(key.charCode)
    if ((key.charCode < 97 || key.charCode > 122)//letras mayusculas
            && (key.charCode < 65 || key.charCode > 90) //letras minusculas
            && (key.charCode != 45) //retroceso
            && (key.charCode != 241) //ñ
            && (key.charCode != 209) //Ñ
            && (key.charCode != 32) //espacio
            && (key.charCode != 225) //á
            && (key.charCode != 233) //é
            && (key.charCode != 237) //í
            && (key.charCode != 243) //ó
            && (key.charCode != 250) //ú
            && (key.charCode != 193) //Á
            && (key.charCode != 201) //É
            && (key.charCode != 205) //Í
            && (key.charCode != 211) //Ó
            && (key.charCode != 218) //Ú

            )
        return false;
});

function soloNumeros(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[0-9]|\./;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function submitform() {
    console.log('prueba');
    location.reload();
}

function onClick(nombreBoton) {
    $('#' + nombreBoton)[0].click()
    //return true;
}

function disableEnter() {
    return event.keyCode != 13;
}

function disableEnterKey(e) {
    var key;
    if (window.event) {
        key = window.event.keyCode;
    } else {
        key = e.which;
    }
    if (key == 13) {
        return false;
    } else {
        return true;
    }
}

function cUpper(cObj) {
    cObj.value = cObj.value.toUpperCase();
}


// FNC for detecting for click outside of any elements ============== 
$.fn.clickOff = function (callback, selfDestroy) {
    var clicked = false;
    var parent = this;
    var destroy = selfDestroy || true;

    parent.click(function () {
        clicked = true;
    });

    $(document).click(function (event) {
        if (!clicked) {
            callback(parent, event);
        }
        if (destroy) {
        }
        ;
        clicked = false;
    });
};

function queryEntidad(value) {
    if (value.length == 5) {
        findEntidad();
    }
}

function queryEmpresa(value) {
    if (value.length == 5) {
        findEmpresa();
    }else if(value.length == 0){
        findEmpresaReiniciar();
    }
}

function queryEmpresaOtros(value) {
    if (value.length == 5) {
        findEmpresaOtros();
    }else if(value.length == 0){
        findEmpresaReiniciar();
    }
}

function calcular(fem, mas, lbl) {
    var fem = $("#" + fem).val();
    var mas = $("#" + mas).val();
    $("#" + lbl).text(Number(fem) + Number(mas));
}
function calcularCiclo3(fem, mas, lbl, gra7, gra8, gra9, lblCiclo3) {
    var fem = $("#" + fem).val();
    var mas = $("#" + mas).val();
    var grado7 = $("#" + gra7).text();
    var grado8 = $("#" + gra8).text();
    var grado9 = $("#" + gra9).text();
    $("#" + lbl).text(Number(fem) + Number(mas));
    $("#" + lblCiclo3).text(Number(grado7) + Number(grado8) + Number(grado9));
}
$(document).ready(function () {
    $(window).scroll(function () {
        var scroll = $(window).scrollTop();
        if (scroll > 1) {
            $("#menuBar").css("-webkit-box-shadow", "0px 2px 4px 0px rgba(0,0,0,0.75)");
        } else {
            $("#menuBar").css("-webkit-box-shadow", "0px 0px 0px 0px rgba(0,0,0,0.75)");
        }
    })
})
