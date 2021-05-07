/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.controller.contratacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import sv.gob.mined.app.web.controller.ParametrosMB;
import sv.gob.mined.app.web.util.JsfUtil;
import sv.gob.mined.app.web.util.RecuperarProcesoUtil;
import sv.gob.mined.paquescolar.ejb.EntidadEducativaEJB;
import sv.gob.mined.paquescolar.ejb.ProveedorEJB;
import sv.gob.mined.paquescolar.ejb.ServiciosJsonEJB;
import sv.gob.mined.paquescolar.ejb.UtilEJB;
import sv.gob.mined.paquescolar.model.DetalleProcesoAdq;
import sv.gob.mined.paquescolar.model.pojos.contratacion.AvanceFeriaDto;
import sv.gob.mined.paquescolar.model.pojos.GraficoTipoEmpresaDTO;
import sv.gob.mined.paquescolar.model.pojos.contratacion.SaldoProveedorDto;

/**
 *
 * @author misanchez
 */
@Named
@ViewScoped
public class ConsultasContratacionMB extends RecuperarProcesoUtil implements Serializable {

    @Inject
    private ServiciosJsonEJB serviciosJsonEJB;
    @Inject
    private ProveedorEJB proveedorEJB;
    @Inject
    private EntidadEducativaEJB entidadEducativaEJB;
    @Inject
    private UtilEJB utilEJB;

    private String codigoDepartamento;
    private String porcentajeAvance;
    private String totalAsistido;

    private int uniformes = 0;
    private int utiles = 0;
    private int zapatos = 0;

    private float totalIngresados = 0;
    private float totalProcesadosUni = 0;
    private float totalProcesadosUti = 0;
    private float totalProcesadosZap = 0;
    private float totalPendientes = 0;

    private boolean mostrarGrafico = false;
    private boolean mostrarTabla = false;

    private BigDecimal idRubro = new BigDecimal(0);
    private BigDecimal parametroMayor = new BigDecimal(0);

    private DetalleProcesoAdq detalleProceso = new DetalleProcesoAdq();

    private List<AvanceFeriaDto> listaAvance = new ArrayList();
    private List<GraficoTipoEmpresaDTO> listaCapacidad = new ArrayList();
    private List<SaldoProveedorDto> lstSaldos = new ArrayList();

    private PieChartModel pieModelUni;
    private PieChartModel pieModelUti;
    private PieChartModel pieModelZap;
    private BarChartModel barModelUni;
    private BarChartModel barModelUti;
    private BarChartModel barModelZap;

    /**
     * Creates a new instance of ConsultasContratacionMB
     */
    public ConsultasContratacionMB() {
    }

    @PostConstruct
    public void ini() {
        idRubro = ((ParametrosMB) FacesContext.getCurrentInstance().getApplication().getELResolver().
                getValue(FacesContext.getCurrentInstance().getELContext(), null, "parametrosMB")).getRubro();
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);//anhoProcesoEJB.getDetProcesoAdq(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public List<SaldoProveedorDto> getLstSaldos() {
        return lstSaldos;
    }

    public void setLstSaldos(List<SaldoProveedorDto> lstSaldos) {
        this.lstSaldos = lstSaldos;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public BigDecimal getIdRubro() {
        return idRubro;
    }

    public void setIdRubro(BigDecimal idRubro) {
        this.idRubro = idRubro;
    }

    public String getDepa() {
        return codigoDepartamento;
    }

    public void setDepa(String departamento) {
        if (departamento != null) {
            this.codigoDepartamento = departamento;
        }
    }

    public boolean getMostrarGrafico() {
        return mostrarGrafico;
    }

    public void setMostrarGrafico(boolean mostrarGrafico) {
        this.mostrarGrafico = mostrarGrafico;
    }

    public boolean isMostrarTabla() {
        return mostrarTabla;
    }

    public void setMostrarTabla(boolean mostrarTabla) {
        this.mostrarTabla = mostrarTabla;
    }

    public float getTotalIngresados() {
        return totalIngresados;
    }

    public void setTotalIngresados(float totalIngresados) {
        this.totalIngresados = totalIngresados;
    }

    public float getTotalProcesadosUni() {
        return totalProcesadosUni;
    }

    public float getTotalProcesadosUti() {
        return totalProcesadosUti;
    }

    public float getTotalProcesadosZap() {
        return totalProcesadosZap;
    }

    public String getTotalAsistido() {
        return totalAsistido;
    }

    public void setTotalAsistido(String totalAsistido) {
        this.totalAsistido = totalAsistido;
    }

    public List<GraficoTipoEmpresaDTO> getListaCapacidad() {
        return listaCapacidad;
    }

    public void setListaCapacidad(List<GraficoTipoEmpresaDTO> listaCapacidad) {
        this.listaCapacidad = listaCapacidad;
    }

    public BigDecimal getParametroMayor() {
        return parametroMayor;
    }

    public void setParametroMayor(BigDecimal parametroMayor) {
        this.parametroMayor = parametroMayor;
    }

    // <!editor-fold>
    public void buscarAvance() {
        this.porcentajeAvance = "";
        if (codigoDepartamento != null) {
            mostrarGrafico = true;
            for (DetalleProcesoAdq det : getRecuperarProceso().getProcesoAdquisicion().getDetalleProcesoAdqList()) {
                switch (det.getIdRubroAdq().getIdRubroInteres().intValue()) {
                    case 1:
                    case 4:
                        uniformes = det.getIdDetProcesoAdq();
                        break;
                    case 2:
                        utiles = det.getIdDetProcesoAdq();
                        break;
                    case 3:
                        zapatos = det.getIdDetProcesoAdq();
                        break;
                }
            }

            List<Object> lista = proveedorEJB.findAvanceDocumentosProcesados(codigoDepartamento, uniformes, utiles, zapatos);
            listaAvance = new ArrayList(0);
            totalIngresados = 0;
            totalProcesadosUni = 0;
            totalProcesadosUti = 0;
            totalProcesadosZap = 0;
            if (!lista.isEmpty()) {
                for (Object object : lista) {
                    AvanceFeriaDto result = new AvanceFeriaDto();
                    Object[] datos = (Object[]) object;
                    result.setCodigoMunicipio(datos[0].toString());
                    result.setNombreMun(datos[1].toString());
                    result.setAsistenciaCE(Integer.parseInt(datos[2].toString()));
                    result.setProcesadoUniformes(Integer.parseInt(datos[3].toString()));
                    result.setProcesadoUtiles(Integer.parseInt(datos[4].toString()));
                    result.setProcesadoZapatos(Integer.parseInt(datos[5].toString()));
                    totalIngresados = totalIngresados + result.getAsistenciaCE();
                    totalProcesadosUni = totalProcesadosUni + result.getProcesadoUniformes();
                    totalProcesadosUti = totalProcesadosUti + result.getProcesadoUtiles();
                    totalProcesadosZap = totalProcesadosZap + result.getProcesadoZapatos();
                    totalAsistido = JsfUtil.getFormatoNum(totalIngresados, true);
                    listaAvance.add(result);
                }
                mostrarTabla = true;
            } else {
                JsfUtil.mensajeInformacion("No se encontraron registros.");
            }
        }
    }

    public PieChartModel getPieModelUni() {
        float a = totalIngresados;
        float b = totalProcesadosUni;

        pieModelUni = new PieChartModel();
        pieModelUni.setExtender("skinPie");
        pieModelUni.set("Asistentes " + (int) totalIngresados, ((a - b) * 100) / a);
        pieModelUni.set("Procesados " + (int) totalProcesadosUni, 100 - ((a - b) * 100) / a);
        pieModelUni.setLegendPosition("e");
        pieModelUni.setFill(false);
        pieModelUni.setShowDataLabels(true);
        pieModelUni.setDiameter(150);

        Float valor = 100 - ((a - b) * 100) / a;
        porcentajeAvance = "Documentos Procesados: " + (int) (valor + 0.5F) + "%";
        totalIngresados = (int) (totalIngresados);
        totalProcesadosUni = (int) (totalProcesadosUni);

        return pieModelUni;
    }

    public PieChartModel getPieModelUti() {
        float a = totalIngresados;
        float c = totalProcesadosUti;

        pieModelUti = new PieChartModel();
        pieModelUti.setExtender("skinPie");

        pieModelUti.set("Asistentes " + (int) totalIngresados, ((a - c) * 100) / a);
        pieModelUti.set("Procesados " + (int) totalProcesadosUti, 100 - ((a - c) * 100) / a);
        pieModelUti.setLegendPosition("e");
        pieModelUti.setFill(false);
        pieModelUti.setShowDataLabels(true);
        pieModelUti.setDiameter(150);
        totalProcesadosUti = (int) (totalProcesadosUti);

        return pieModelUti;
    }

    public PieChartModel getPieModelZap() {
        float a = totalIngresados;
        float d = totalProcesadosZap;

        pieModelZap = new PieChartModel();
        pieModelZap.setExtender("skinPie");

        pieModelZap.set("Asistentes " + (int) totalIngresados, ((a - d) * 100) / a);
        pieModelZap.set("Procesados " + (int) totalProcesadosZap, 100 - ((a - d) * 100) / a);
        pieModelZap.setLegendPosition("e");
        pieModelZap.setFill(false);
        pieModelZap.setShowDataLabels(true);
        pieModelZap.setDiameter(150);

        totalProcesadosZap = (int) (totalProcesadosZap);

        return pieModelZap;
    }

    public BarChartModel getBarModelUni() {
        barModelUni = new BarChartModel();
        barModelUni.setExtender("skinBar");
        if (codigoDepartamento != null) {
            obtenerDatos(utilEJB.find(DetalleProcesoAdq.class, uniformes));

            for (GraficoTipoEmpresaDTO graficoTipoEmpresaDTO : listaCapacidad) {
                ChartSeries tipoEmp = new ChartSeries();
                tipoEmp.setLabel(graficoTipoEmpresaDTO.getNombretipoEmpresa());
                tipoEmp.set("", graficoTipoEmpresaDTO.getTotal());
                barModelUni.addSeries(tipoEmp);
            }
        }

        barModelUni.setTitle("");
        barModelUni.setLegendPosition("ne");

        Axis xAxis = barModelUni.getAxis(AxisType.X);
        xAxis.setLabel("Tipo Empresa");

        Axis yAxis = barModelUni.getAxis(AxisType.Y);
        yAxis.setLabel("Miles $");
        yAxis.setTickFormat("%.0f");
        yAxis.setMin(0);

        return barModelUni;
    }

    public BarChartModel getBarModelUti() {
        barModelUti = new BarChartModel();
        barModelUti.setExtender("skinBar");
        if (codigoDepartamento != null) {
            obtenerDatos(utilEJB.find(DetalleProcesoAdq.class, utiles));

            for (GraficoTipoEmpresaDTO graficoTipoEmpresaDTO : listaCapacidad) {
                ChartSeries tipoEmp = new ChartSeries();
                tipoEmp.setLabel(graficoTipoEmpresaDTO.getNombretipoEmpresa());
                tipoEmp.set("", graficoTipoEmpresaDTO.getTotal());
                barModelUti.addSeries(tipoEmp);
            }
        }

        barModelUti.setTitle("");
        barModelUti.setLegendPosition("ne");

        Axis xAxis = barModelUti.getAxis(AxisType.X);
        xAxis.setLabel("Tipo Empresa");

        Axis yAxis = barModelUti.getAxis(AxisType.Y);
        yAxis.setLabel("Miles $");
        yAxis.setTickFormat("%.0f");
        yAxis.setMin(0);

        return barModelUti;
    }

    public BarChartModel getBarModelZap() {
        barModelZap = new BarChartModel();
        barModelZap.setExtender("skinBar");
        if (codigoDepartamento != null) {
            obtenerDatos(utilEJB.find(DetalleProcesoAdq.class, zapatos));

            for (GraficoTipoEmpresaDTO graficoTipoEmpresaDTO : listaCapacidad) {
                ChartSeries tipoEmp = new ChartSeries();
                tipoEmp.setLabel(graficoTipoEmpresaDTO.getNombretipoEmpresa());
                tipoEmp.set("", graficoTipoEmpresaDTO.getTotal().intValue());
                barModelZap.addSeries(tipoEmp);
            }
        }

        barModelZap.setTitle("");
        barModelZap.setLegendPosition("ne");

        Axis xAxis = barModelZap.getAxis(AxisType.X);
        xAxis.setLabel("Tipo Empresa");

        Axis yAxis = barModelZap.getAxis(AxisType.Y);
        yAxis.setLabel("Miles $");
        yAxis.setTickFormat("%.0f");

        yAxis.setMin(0);

        return barModelZap;
    }

    public void obtenerDatos(DetalleProcesoAdq detalleProceso) {
        List<Object> lista = proveedorEJB.findAvanceContratacionByDepartamento(detalleProceso, codigoDepartamento);
        listaCapacidad = new ArrayList(0);
        BigDecimal total1 = BigDecimal.ZERO;
        parametroMayor = BigDecimal.ZERO;
        if (!lista.isEmpty()) {
            for (Object object : lista) {
                GraficoTipoEmpresaDTO result = new GraficoTipoEmpresaDTO();
                Object[] datos = (Object[]) object;
                result.setNombretipoEmpresa(datos[0].toString());
                result.setTotal(new BigDecimal(datos[1].toString()));
                if (result.getTotal().compareTo(total1) == 1) {
                    total1 = result.getTotal();
                }
                result.setTotalFormatedo(JsfUtil.getFormatoNum(result.getTotal(), false));
                result.setTotaltipoEmp(new BigDecimal(datos[2].toString()));
                listaCapacidad.add(result);
            }
            parametroMayor = total1;
        } else {
            mostrarGrafico = false;
            JsfUtil.mensajeInformacion("No se encontraron registros ");
        }
    }

    public void consultaSaldo() {
        detalleProceso = JsfUtil.findDetalle(getRecuperarProceso().getProcesoAdquisicion(), idRubro);//anhoProcesoEJB.getDetProcesoAdq(getRecuperarProceso().getProcesoAdquisicion(), idRubro);
        lstSaldos = serviciosJsonEJB.getLstSaldoProveedoresByDepAndCodDepa(detalleProceso, codigoDepartamento);
    }

    public String nombreDepartamento(String codigoDepartamento) {
        return JsfUtil.getNombreDepartamentoByCodigo(codigoDepartamento);
    }
}
