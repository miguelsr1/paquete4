/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import sv.gob.mined.paquescolar.model.pojos.credito.ResumenCreditosDto;
import sv.gob.mined.paquescolar.model.pojos.VwRptProveedoresContratadosDto;
import sv.gob.mined.paquescolar.model.pojos.pagoprove.DatosProveDto;
import sv.gob.mined.paquescolar.model.pojos.credito.DatosProveedoresFinanDto;

/**
 *
 * @author misanchez
 */
public class RptExcel {

    private static HSSFWorkbook wb1;
    private static DataFormat FORMATO_DATA;

    public static void generarRptGenerico(List<? extends Object> lst, String nombreExcel, int rowInicial) {
        HSSFCellStyle style;
        int row = rowInicial;
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/" + nombreExcel + ".xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            style = wb1.createCellStyle();
            style.setWrapText(true);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            HSSFSheet s1 = wb1.getSheetAt(0);   //sheet by index

            Field[] attributosClase = lst.get(0).getClass().getDeclaredFields();

            for (Object item : lst) {
                int col = 0;
                for (Field attibuto : attributosClase) {
                    if (!attibuto.getName().equals("idRow") && attibuto.getModifiers() == 2) {
                        switch (attibuto.getType().getName()) {
                            case "java.lang.String":
                                escribirTexto(callMethodReflection("get".concat(StringUtils.capitalize(attibuto.getName())), item), row, col, style, s1);
                                break;
                            case "java.math.BigDecimal":
                                Object value = callMethodReflection("get".concat(StringUtils.capitalize(attibuto.getName())), item);
                                if (value != null) {
                                    escribirNumero(value.toString(), row, col, style, true, s1);
                                }
                                break;
                            case "java.math.Integer":
                                break;
                        }
                        col++;
                    }
                }
                row++;
            }

            generarArchivo(wb1, nombreExcel);
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static <T extends Object> T callMethodReflection(String nombreMetodo, Object item) {
        Object valorRetorno;

        try {
            Method method = item.getClass().getMethod(nombreMetodo);
            valorRetorno = method.invoke(item);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, "ERROR: En ejecución de metodo " + nombreMetodo, ex);
            throw new RuntimeException(ex);
        }

        return (T) valorRetorno;
    }

    public static void generarRptProveedoresHacienda(List<VwRptProveedoresContratadosDto> lst) {
        HSSFCellStyle style;
        int row = 1;
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/proveedoresHacienda.xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            style = wb1.createCellStyle();
            style.setWrapText(true);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            HSSFSheet s1 = wb1.getSheetAt(0);   //sheet by index

            for (VwRptProveedoresContratadosDto dato : lst) {
                escribirTexto(dato.getRubro(), row, 0, style, s1);
                escribirTexto(dato.getNombreDepartamentoEmp(), row, 1, style, s1);
                escribirTexto(dato.getNumeroNit(), row, 2, style, s1);
                escribirTexto(dato.getNombreDepartamentoCe(), row, 3, style, s1);
                escribirNumero(dato.getCantidadContrato().toString(), row, 4, style, true, s1);
                escribirNumero(dato.getMontoContrato().toString(), row, 5, style, false, s1);
                row++;
            }

            generarArchivo(wb1, "proveedoresHacienda");
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generarRptRentaAnual(List<DatosProveDto> lstDatos, String anho) {
        HSSFCellStyle style;
        int row = 1;
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/rptRentaAnual.xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            style = wb1.createCellStyle();

            HSSFSheet s1 = wb1.getSheetAt(0);   //sheet by index

            for (DatosProveDto dato : lstDatos) {
                escribirTexto(JsfUtil.formatearNumero(40, dato.getRazonSocial().replace("ñ", "Ñ"), false), row, 0, style, s1);
                escribirTexto(JsfUtil.formatearNumero(14, dato.getNumeroNit().replace("-", ""), false), row, 1, style, s1);
                escribirTexto("11", row, 2, style, s1);
                escribirNumero(dato.getMontoRetencion().toString(), row, 3, style, false, s1);
                escribirNumero(dato.getMontoRenta().toString(), row, 4, style, false, s1);
                escribirTexto(anho, row, 5, style, s1);
                row++;
            }

            generarArchivo(wb1, "rptRentaAnual");
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void escribirTexto(String text, Integer row, Integer col, CellStyle style, HSSFSheet hoja) {
        HSSFRichTextString texto = new HSSFRichTextString(text);
        if (hoja.getRow(row) == null) {
            Row row1 = hoja.createRow(row);
            Cell c = row1.createCell(col);
            c.setCellValue(texto);
            c.setCellStyle(style);
        } else {
            Row row1 = hoja.getRow(row);
            Cell c = row1.createCell(col);
            c.setCellValue(texto);
            c.setCellStyle(style);
        }
    }

    private static void escribirFecha(Date fecha, Integer row, Integer col, CellStyle style, HSSFSheet hoja) {
        HSSFRow row1 = hoja.getRow(row);
        HSSFCell cell = row1.getCell(col);
        if (cell == null) {
            cell = row1.createCell(col);
        }
        if (fecha != null) {
            cell.setCellValue(JsfUtil.FORMAT_DATE.format(fecha));
        }
        cell.setCellStyle(style);
    }

    private static void escribirNumero(String text, Integer row, Integer col, CellStyle style, Boolean entero, HSSFSheet hoja) {
        text = text.replace(",", "");
        HSSFRow hrow = hoja.getRow(row);
        HSSFCell cell = hrow.getCell(col);
        if (cell == null) {
            hrow.createCell(col);
            cell = hrow.getCell(col);
        }
        style.setDataFormat(entero ? FORMATO_DATA.getFormat("#,##0") : FORMATO_DATA.getFormat("#,##0.00"));
        cell.setCellStyle(style);
        if (text != null && !text.isEmpty()) {
            cell.setCellValue(entero ? Integer.parseInt(text) : Double.parseDouble(text));
        }
    }

    private static void generarArchivo(Workbook wb, String nombreFile) {
        try (ByteArrayOutputStream outByteStream = new ByteArrayOutputStream()) {
            wb.write(outByteStream);
            byte[] outArray = outByteStream.toByteArray();
            UtilFile.downloadFileBytes(outArray, nombreFile, UtilFile.CONTENIDO_XLS, UtilFile.EXTENSION_XLS);
        } catch (IOException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generarRptRentaMensual(List<DatosProveDto> lst) {
        HSSFCellStyle style;
        int row = 2;
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/rentaMensual.xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            style = wb1.createCellStyle();
            style.setWrapText(true);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            HSSFSheet s1 = wb1.getSheetAt(0);   //sheet by index

            for (DatosProveDto dato : lst) {
                escribirTexto(dato.getRazonSocial(), row, 0, style, s1);
                escribirTexto(dato.getNumeroNit(), row, 1, style, s1);
                escribirTexto(dato.getNumCheque(), row, 2, style, s1);
                escribirFecha(dato.getFechaCheque(), row, 3, style, s1);
                escribirTexto(dato.getDocPago(), row, 4, style, s1);
                escribirNumero(dato.getMontoActual().toString(), row, 5, style, false, s1);
                escribirNumero(dato.getMontoRetencion().toString(), row, 6, style, false, s1);
                escribirNumero(dato.getMontoRenta().toString(), row, 7, style, false, s1);
                row++;
            }
            generarArchivo(wb1, "Paquete-RentaMensual");
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generarRptResumenGeneralCreditos(List<ResumenCreditosDto> listaResumenGen, String anho) {
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/rptCreditoResumenGeneral.xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            HSSFSheet hoja = wb1.getSheetAt(0);

            HSSFFont font = (HSSFFont) wb1.createFont();
            font.setBold(true);
            font.setFontName(HSSFFont.FONT_ARIAL);

            HSSFCellStyle style = (HSSFCellStyle) wb1.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFont(font);

            HSSFRow fila = hoja.createRow(0);
            Cell celda0 = fila.createCell(0);
            celda0.setCellStyle(style);
            celda0.setCellValue("MINISTERIO DE EDUCACION");

            fila = hoja.createRow(1);
            Cell celda1 = fila.createCell(0);
            celda1.setCellStyle(style);
            celda1.setCellValue("CONSOLIDADO DE CREDITOS A PROVEEDORES DE PAQUETE ESCOLAR " + anho + "  AL _____");

            hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

            fila = hoja.createRow(2);
            Cell celdaEn0 = fila.createCell(0);
            celdaEn0.setCellStyle(style);
            celdaEn0.setCellValue("BANCO O FINANCIERA");

            Cell celdaEn1 = fila.createCell(1);
            celdaEn1.setCellStyle(style);
            celdaEn1.setCellValue("No.créditos *");

            Cell celdaEn2 = fila.createCell(2);
            celdaEn2.setCellStyle(style);
            celdaEn2.setCellValue("Monto otorgado UTILES");

            Cell celdaEn3 = fila.createCell(3);
            celdaEn3.setCellStyle(style);
            celdaEn3.setCellValue("Monto otorgado ZAPATOS");

            Cell celdaEn4 = fila.createCell(4);
            celdaEn4.setCellStyle(style);
            celdaEn4.setCellValue("Monto otorgado UNIFORMES");

            hoja.autoSizeColumn(0);
            hoja.autoSizeColumn(1);
            hoja.autoSizeColumn(2);
            hoja.autoSizeColumn(3);
            hoja.autoSizeColumn(4);

            int x = 3;

            for (ResumenCreditosDto object : listaResumenGen) {
                escribirTexto(object.getNombreEntFinan(), x, 0, style, hoja);
                escribirNumero(object.getCantidadCreditos().toString(), x, 1, style, true, hoja);
                escribirNumero(object.getMontoCreditoUtiles().toString(), x, 2, style, false, hoja);
                escribirNumero(object.getMontoCreditoZapatos().toString(), x, 3, style, false, hoja);
                escribirNumero(object.getMontoCreditoUniforme().toString(), x, 4, style, false, hoja);
                x++;
            }

            fila = hoja.createRow(x);

            Cell celda2 = fila.createCell(0);
            celda2.setCellStyle(style);
            celda2.setCellValue("Total");

            Cell celda3 = fila.createCell(1);
            celda3.setCellStyle(style);
            String rango = "B4:B" + x + "";
            String StrFormula = "SUM(" + rango + ")";
            celda3.setCellType(CellType.FORMULA);
            celda3.setCellFormula(StrFormula);

            Cell celda4 = fila.createCell(2);
            celda4.setCellStyle(style);
            String rango1 = "C4:C" + x + "";
            String StrFormula1 = "SUM(" + rango1 + ")";
            celda4.setCellType(CellType.FORMULA);
            celda4.setCellFormula(StrFormula1);

            Cell celda5 = fila.createCell(3);
            celda5.setCellStyle(style);
            String rango2 = "D4:D" + x + "";
            String StrFormula2 = "SUM(" + rango2 + ")";
            celda5.setCellType(CellType.FORMULA);
            celda5.setCellFormula(StrFormula2);

            Cell celda6 = fila.createCell(4);
            celda6.setCellStyle(style);
            String rango3 = "E4:E" + x + "";
            String StrFormula3 = "SUM(" + rango3 + ")";
            celda6.setCellType(CellType.FORMULA);
            celda6.setCellFormula(StrFormula3);

            fila = hoja.createRow(x + 1);
            Cell celda7 = fila.createCell(0);
            celda7.setCellStyle(style);
            celda7.setCellValue("* El dato es por número de créditos pues un proveedor puede tener varios contratos");

            String patron = "dd/MM/yyyy";
            SimpleDateFormat formato = new SimpleDateFormat(patron);
            fila = hoja.createRow(x + 2);
            Cell celda8 = fila.createCell(0);
            celda8.setCellValue(formato.format(new Date()));
            celda8.setCellStyle(style);

            generarArchivo(wb1, "Paquete" + anho + "-ResumenGeneralCreditos");
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generarRptResumenPorRubroYFinanciera(List<DatosProveedoresFinanDto> listaProvGral, String anho) {
        try (InputStream ins = Reportes.getPathReporte("sv/gob/mined/apps/reportes/excel/rptCreditoRubroEntidadFinanciera.xls")) {
            wb1 = (HSSFWorkbook) WorkbookFactory.create(ins);
            FORMATO_DATA = wb1.createDataFormat();
            HSSFSheet hoja = wb1.getSheetAt(0);

            HSSFFont font = (HSSFFont) wb1.createFont();
            font.setBold(true);
            font.setColor(HSSFColor.BLACK.index);
            font.setFontName(HSSFFont.FONT_ARIAL);

            HSSFCellStyle style = (HSSFCellStyle) wb1.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFont(font);

            HSSFCellStyle style2 = (HSSFCellStyle) wb1.createCellStyle();
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setFont(font);
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            HSSFRow fila = hoja.createRow(0);
            HSSFCell celda0 = fila.createCell(0);
            celda0.setCellStyle(style);
            celda0.setCellValue("DIRECCION GENERAL DE ADMINISTRACION");

            fila = hoja.createRow(1);
            Cell celda1 = fila.createCell(0);

            celda1.setCellStyle(style2);
            celda1.setCellValue("DETALLE DE PROVEEDORES CON CREDITOS CON INSTITUCIONES BANCARIAS");
            hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            fila = hoja.createRow(2);
            Cell celdaEn0 = fila.createCell(0);
            celdaEn0.setCellStyle(style);
            celdaEn0.setCellValue("NIT DEL PROVEEDOR");

            Cell celdaEn1 = fila.createCell(1);
            celdaEn1.setCellStyle(style);
            celdaEn1.setCellValue("NOMBRE DEL PROVEEDOR");

            Cell celdaEn2 = fila.createCell(2);
            celdaEn2.setCellStyle(style);
            celdaEn2.setCellValue("ENTIDAD FINANCIERA");

            Cell celdaEn3 = fila.createCell(3);
            celdaEn3.setCellStyle(style);
            celdaEn3.setCellValue("DEPARTAMENTO C.E");

            Cell celdaEn4 = fila.createCell(4);
            celdaEn4.setCellStyle(style);
            celdaEn4.setCellValue("CODIGO C.E");

            Cell celdaEn5 = fila.createCell(5);
            celdaEn5.setCellStyle(style);
            celdaEn5.setCellValue("NOMBRE DEL C.E");

            Cell celdaEn6 = fila.createCell(6);
            celdaEn6.setCellStyle(style);
            celdaEn6.setCellValue("MONTO DEL CREDITO");

            Cell celdaEn7 = fila.createCell(7);
            celdaEn7.setCellStyle(style);
            celdaEn7.setCellValue("ESTATUS");

            Cell celdaEn8 = fila.createCell(8);
            celdaEn8.setCellStyle(style);
            celdaEn8.setCellValue("RUBRO");

            Cell celdaEn9 = fila.createCell(9);
            celdaEn9.setCellStyle(style);
            celdaEn9.setCellValue("MONTO DEL CONTRATO");

            hoja.autoSizeColumn(0);
            hoja.autoSizeColumn(1);
            hoja.autoSizeColumn(2);
            hoja.autoSizeColumn(3);
            hoja.autoSizeColumn(4);
            hoja.autoSizeColumn(5);
            hoja.autoSizeColumn(6);
            hoja.autoSizeColumn(7);
            hoja.autoSizeColumn(8);
            hoja.autoSizeColumn(9);

            int x = 3;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            for (DatosProveedoresFinanDto object : listaProvGral) {
                escribirTexto(object.getNumeroNit(), x, 0, style, hoja);
                escribirTexto(object.getRazonSocial(), x, 1, style, hoja);
                escribirTexto(object.getNombreEntFinan(), x, 2, style, hoja);
                escribirTexto(object.getNombreDepartamento(), x, 3, style, hoja);
                escribirTexto(object.getCodigoEntidad(), x, 4, style, hoja);
                escribirTexto(object.getNombre(), x, 5, style, hoja);
                escribirNumero(object.getMontoCredito().toString(), x, 6, style, false, hoja);
                escribirTexto(object.getDescripCredAct(), x, 7, style, hoja);
                escribirTexto(object.getDescripcionRubro(), x, 8, style, hoja);
                escribirNumero(object.getMontoContrato().toString(), x, 9, style, false, hoja);
                x++;
            }

            fila = hoja.createRow(x);

            Cell celda2 = fila.createCell(8);
            celda2.setCellStyle(style);
            celda2.setCellValue("Total");

            Cell celda5 = fila.createCell(9);
            celda5.setCellStyle(style);
            String rango2 = "J4:J" + x + "";
            String StrFormula2 = "SUM(" + rango2 + ")";
            celda5.setCellType(CellType.FORMULA);
            celda5.setCellFormula(StrFormula2);

            generarArchivo(wb1, "Paquete" + anho + "-ResumenPorRubroYFinanciera");
        } catch (IOException | InvalidFormatException ex) {
            Logger.getLogger(RptExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generarRptExcelGenerico(HSSFWorkbook wb, int colsNumEnteros[], int colsNumDecimal[]) {
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFCellStyle styleNumber = wb.createCellStyle();
        styleNumber.setAlignment(HorizontalAlignment.RIGHT);
        FORMATO_DATA = wb.createDataFormat();

        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            HSSFRow row = sheet.getRow(j);
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                String valor = row.getCell(i).getRichStringCellValue().getString();
                for (Integer colsNumEntero : colsNumEnteros) {
                    if (i == colsNumEntero) {
                        escribirNumero(valor, j, i, styleNumber, true, sheet);
                    }
                }
                for (Integer colsNumEntero : colsNumDecimal) {
                    if (i == colsNumEntero) {
                        escribirNumero(valor, j, i, styleNumber, false, sheet);
                    }
                }
            }
        }

        sheet.autoSizeColumn(1);
    }
}
