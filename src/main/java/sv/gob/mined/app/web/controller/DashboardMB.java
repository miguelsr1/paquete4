/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.app.web.util.VarSession;
import sv.gob.mined.paquescolar.ejb.ServiciosJsonEJB;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.Usuario;
import sv.gob.mined.paquescolar.model.pojos.contratacion.AvanceContratosDto;
import sv.gob.mined.paquescolar.model.pojos.dashboard.TotalContratadoDto;
import sv.gob.mined.paquescolar.model.pojos.dashboard.TotalResumenDto;
import sv.gob.mined.paquescolar.model.pojos.dashboard.TotalTipoEmpDto;

/**
 *
 * @author misanchez
 */
@ManagedBean
@ViewScoped
public class DashboardMB extends RecuperarProcesoUtil implements Serializable {

    private Integer idDetProcesoAdq;
    private int divisor = 1;
    private Boolean disabledDep = true;
    private String codigoDepartamento = "00";
    private BigDecimal rubro = BigDecimal.ZERO;
    private BigDecimal montoTotalRubro = BigDecimal.ZERO;
    private BigDecimal cantidadTotalRubro = BigDecimal.ZERO;
    private BigDecimal montoTotalEmp = BigDecimal.ZERO;
    private BigDecimal cantidadTotalEmp = BigDecimal.ZERO;
    private BigDecimal montoTotalGenero = BigDecimal.ZERO;
    private BigDecimal cantidadTotalGenero = BigDecimal.ZERO;

    private TotalContratadoDto departamentoContratado = new TotalContratadoDto();
    private TotalTipoEmpDto tipoEmpresa = new TotalTipoEmpDto();
    private List<TotalContratadoDto> lstTotalContratado = new ArrayList<>();
    private List<TotalTipoEmpDto> lstTotTipoEmp = new ArrayList<>();
    private List<TotalResumenDto> lstTotaGeneroEmp = new ArrayList<>();

    private List<AvanceContratosDto> lstContratosCe = new ArrayList<>();
    private List<AvanceContratosDto> lstContratosProv = new ArrayList<>();

    private BarChartModel barModel;

    @EJB
    public ServiciosJsonEJB serviciosJsonEJB;

    public DashboardMB() {
    }

    @PostConstruct
    public void ini() {
        Usuario usuario = VarSession.getUsuarioSession();
        disabledDep = !usuario.getCodigoDepartamento().equals("00");
        updateDatos();
    }

    public int getDivisor() {
        return divisor;
    }

    public TotalContratadoDto getDepartamentoContratado() {
        return departamentoContratado;
    }

    public void setDepartamentoContratado(TotalContratadoDto departamentoContratado) {
        this.departamentoContratado = departamentoContratado;
    }

    public TotalTipoEmpDto getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TotalTipoEmpDto tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public void updateRubro() {
        rubro = BigDecimal.ZERO;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public List<AvanceContratosDto> getLstContratosCe() {
        return lstContratosCe;
    }

    public List<AvanceContratosDto> getLstContratosProv() {
        return lstContratosProv;
    }

    public void updateDatos() {
        divisor = 1;
        departamentoContratado = new TotalContratadoDto();
        tipoEmpresa = new TotalTipoEmpDto();
        codigoDepartamento = "00";
        DetalleProcesoAdq detProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
        if (detProceso != null) {
            divisor = (detProceso.getIdRubroAdq().getIdRubroInteres().intValue() == 1) ? 4 : 2;

            idDetProcesoAdq = detProceso.getIdDetProcesoAdq();
            lstTotalContratado = serviciosJsonEJB.getLstTotalContratado(idDetProcesoAdq, codigoDepartamento);
            updateListados();
        }
    }

    public void updateDepaListados() {
        tipoEmpresa = null;
        codigoDepartamento = departamentoContratado.getCodDepCe();
        updateListados();
    }

    public void updateGeneroListados() {
        lstTotaGeneroEmp = serviciosJsonEJB.getLstTotalGeneroContratado(idDetProcesoAdq, codigoDepartamento, tipoEmpresa.getIdTipoEmp());
    }

    public void updateListados() {
        lstTotTipoEmp = serviciosJsonEJB.getLstTotalTipoEmpContratado(idDetProcesoAdq, codigoDepartamento);
        lstTotaGeneroEmp = serviciosJsonEJB.getLstTotalGeneroContratado(idDetProcesoAdq, codigoDepartamento, null);

        totalesPorRubro();

        barModel = new BarChartModel();
        barModel.setExtender("skinBar");
        ChartSeries microEmp = new ChartSeries("Micro");
        ChartSeries pequeEmp = new ChartSeries("Peq");
        ChartSeries mediaEmp = new ChartSeries("Med");
        ChartSeries cuentaEmp = new ChartSeries("Cuenta");

        for (TotalTipoEmpDto totalTipoEmpDto : lstTotTipoEmp) {
            switch (totalTipoEmpDto.getIdTipoEmp().intValue()) {
                case 1:
                    microEmp.set(getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho(), totalTipoEmpDto.getMonto());
                    barModel.addSeries(microEmp);
                    break;
                case 2:
                    pequeEmp.set(getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho(), totalTipoEmpDto.getMonto());
                    barModel.addSeries(pequeEmp);
                    break;
                case 3:
                    mediaEmp.set(getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho(), totalTipoEmpDto.getMonto());
                    barModel.addSeries(mediaEmp);
                    break;
                case 9:
                    cuentaEmp.set(getRecuperarProceso().getProcesoAdquisicion().getIdAnho().getAnho(), totalTipoEmpDto.getMonto());
                    barModel.addSeries(cuentaEmp);
                    break;
            }
        }

        barModel.setTitle("Tipo de Empresa");
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Tipo Empresa");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("$");
        yAxis.setTickFormat("%'d");
        yAxis.setTickAngle(-30);
        yAxis.setMin(0);
    }

    public BigDecimal getRubro() {
        return rubro;
    }

    public void setRubro(BigDecimal rubro) {
        this.rubro = rubro;
    }

    public Boolean getDisabledDep() {
        return disabledDep;
    }

    public List<TotalContratadoDto> getLstTotalContratado() {
        return lstTotalContratado;
    }

    public List<TotalTipoEmpDto> getLstTotTipoEmp() {
        return lstTotTipoEmp;
    }

    public List<TotalResumenDto> getLstTotaGeneroEmp() {
        return lstTotaGeneroEmp;
    }

    public void totalesPorRubro() {
        montoTotalRubro = BigDecimal.ZERO;
        cantidadTotalRubro = BigDecimal.ZERO;
        montoTotalEmp = BigDecimal.ZERO;
        cantidadTotalEmp = BigDecimal.ZERO;

        for (TotalContratadoDto totalContratadoDto : lstTotalContratado) {
            montoTotalRubro = montoTotalRubro.add(totalContratadoDto.getMonto());
            cantidadTotalRubro = cantidadTotalRubro.add(totalContratadoDto.getCantidad());
        }

        for (TotalTipoEmpDto tipoEmpDto : lstTotTipoEmp) {
            montoTotalEmp = montoTotalEmp.add(tipoEmpDto.getMonto());
            cantidadTotalEmp = cantidadTotalEmp.add(tipoEmpDto.getCantidadEmp());
        }
    }

    public BigDecimal getMontoTotalRubro() {
        return montoTotalRubro;
    }

    public BigDecimal getCantidadTotalRubro() {
        return cantidadTotalRubro;
    }

    public BigDecimal getMontoTotalEmp() {
        return montoTotalEmp;
    }

    public BigDecimal getCantidadTotalEmp() {
        return cantidadTotalEmp;
    }

    public BigDecimal getMontoTotalGenero() {
        return montoTotalGenero;
    }

    public BigDecimal getCantidadTotalGenero() {
        return cantidadTotalGenero;
    }

    public void calculateTotal(Object valueOfThisSorting) {
        montoTotalGenero = BigDecimal.ZERO;
        cantidadTotalGenero = BigDecimal.ZERO;
        for (TotalResumenDto ent : lstTotaGeneroEmp) { // this is the list used in the value attribute of datatable
            if (ent.getIdPersoneria().equals(valueOfThisSorting)) {
                montoTotalGenero = montoTotalGenero.add(ent.getMonto());
                cantidadTotalGenero = cantidadTotalGenero.add(ent.getCantidadEmp());
            }
        }
    }
    
    public void generarDatosAvance(){
        DetalleProcesoAdq detProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), rubro);
        lstContratosCe = serviciosJsonEJB.getLstAvanceContratosDtoByidDetalleProcesoAdq(detProceso.getIdDetProcesoAdq());
        lstContratosProv = serviciosJsonEJB.getLstAvanceContratosProveDtoByidDetalleProcesoAdq(detProceso.getIdDetProcesoAdq());
    }
}
