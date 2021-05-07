/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

/**
 *
 * @author oamartinez
 */
public class ReportBean {

    private Integer pivote;
    private Integer item;
    private String descripcionItem;
    private Integer cantidadRequerida;
    private String nombreProveedorP1;
    private Integer cantidadOfertadaP1;
    private Double precioUnitarioP1;
    private Integer cantidadAdjudicadaP1;
    private String nombreProveedorP2;
    private Integer cantidadOfertadaP2;
    private Double precioUnitarioP2;
    private Integer cantidadAdjudicadaP2;
    private String nombreProveedorP3;
    private Integer cantidadOfertadaP3;
    private Double precioUnitarioP3;
    private Integer cantidadAdjudicadaP3;

    public ReportBean() {
        pivote = null;
        item = null;
        descripcionItem = "";
        nombreProveedorP1 = "";
        cantidadOfertadaP1 = null;
        precioUnitarioP1 = null;
        cantidadAdjudicadaP1 = null;
        nombreProveedorP2 = "";
        cantidadOfertadaP2 = null;
        precioUnitarioP2 = null;
        cantidadAdjudicadaP2 = null;
        nombreProveedorP3 = "";
        cantidadOfertadaP3 = null;
        precioUnitarioP3 = null;
        cantidadAdjudicadaP3 = null;
    }

    public Integer getPivote() {
        return pivote;
    }

    public void setPivote(Integer pivote) {
        this.pivote = pivote;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public Integer getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(Integer cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }

    public String getNombreProveedorP1() {
        return nombreProveedorP1;
    }

    public void setNombreProveedorP1(String nombreProveedorP1) {
        this.nombreProveedorP1 = nombreProveedorP1;
    }

    public Integer getCantidadOfertadaP1() {
        return cantidadOfertadaP1;
    }

    public void setCantidadOfertadaP1(Integer cantidadOfertadaP1) {
        this.cantidadOfertadaP1 = cantidadOfertadaP1;
    }

    public Double getPrecioUnitarioP1() {
        return precioUnitarioP1;
    }

    public void setPrecioUnitarioP1(Double precioUnitarioP1) {
        this.precioUnitarioP1 = precioUnitarioP1;
    }

    public Integer getCantidadAdjudicadaP1() {
        return cantidadAdjudicadaP1;
    }

    public void setCantidadAdjudicadaP1(Integer cantidadAdjudicadaP1) {
        this.cantidadAdjudicadaP1 = cantidadAdjudicadaP1;
    }

    public String getNombreProveedorP2() {
        return nombreProveedorP2;
    }

    public void setNombreProveedorP2(String nombreProveedorP2) {
        this.nombreProveedorP2 = nombreProveedorP2;
    }

    public Integer getCantidadOfertadaP2() {
        return cantidadOfertadaP2;
    }

    public void setCantidadOfertadaP2(Integer cantidadOfertadaP2) {
        this.cantidadOfertadaP2 = cantidadOfertadaP2;
    }

    public Double getPrecioUnitarioP2() {
        return precioUnitarioP2;
    }

    public void setPrecioUnitarioP2(Double precioUnitarioP2) {
        this.precioUnitarioP2 = precioUnitarioP2;
    }

    public Integer getCantidadAdjudicadaP2() {
        return cantidadAdjudicadaP2;
    }

    public void setCantidadAdjudicadaP2(Integer cantidadAdjudicadaP2) {
        this.cantidadAdjudicadaP2 = cantidadAdjudicadaP2;
    }

    public String getNombreProveedorP3() {
        return nombreProveedorP3;
    }

    public void setNombreProveedorP3(String nombreProveedorP3) {
        this.nombreProveedorP3 = nombreProveedorP3;
    }

    public Integer getCantidadOfertadaP3() {
        return cantidadOfertadaP3;
    }

    public void setCantidadOfertadaP3(Integer cantidadOfertadaP3) {
        this.cantidadOfertadaP3 = cantidadOfertadaP3;
    }

    public Double getPrecioUnitarioP3() {
        return precioUnitarioP3;
    }

    public void setPrecioUnitarioP3(Double precioUnitarioP3) {
        this.precioUnitarioP3 = precioUnitarioP3;
    }

    public Integer getCantidadAdjudicadaP3() {
        return cantidadAdjudicadaP3;
    }

    public void setCantidadAdjudicadaP3(Integer cantidadAdjudicadaP3) {
        this.cantidadAdjudicadaP3 = cantidadAdjudicadaP3;
    }
}
