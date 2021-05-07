/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.app.web.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import sv.gob.mined.paquescolar.model.pojos.Bean;

/**
 *
 * @author oamartinez
 * Modificación: 11octubre2018
 * Comentario: Ordenamiento de código y estandarización de generación de archivo a generar
 */
public class Bean2Excel {

    private HSSFWorkbook workbook;
    private HSSFFont boldFont;
    private HSSFDataFormat format;
    private String rubro = "";
    private String nombreCentroEducativo = "";
    private String codigoCentroEducativo = "";
    private String fuenteFinanciamiento = "";
    private String fechaElaboracion = "";
    private String digitador = "";
    private List listado;
    private String[] proveedoresAMostrar;

    public Bean2Excel() {
        workbook = new HSSFWorkbook();
        boldFont = workbook.createFont();
        boldFont.setBold(true);
        format = workbook.createDataFormat();
    }

    public Bean2Excel(List listadoProv, String rubroCE, String nombreCE, String codigoCE, String fuenteFinCE, String fechaElabCE, String digitadorCE) {
        workbook = new HSSFWorkbook();
        boldFont = workbook.createFont();
        boldFont.setBold(true);
        format = workbook.createDataFormat();

        rubro = rubroCE.toUpperCase();
        nombreCentroEducativo = nombreCE.toUpperCase();
        codigoCentroEducativo = codigoCE.toUpperCase();
        fuenteFinanciamiento = fuenteFinCE.toUpperCase();
        fechaElaboracion = fechaElabCE.toUpperCase();
        digitador = digitadorCE.toUpperCase();
        listado = listadoProv;
    }

    public void createFile(String codigoEntidad, String nombreDirector, String nombreEncargadoCompra) {
        try {
            this.addSheetAnalisisEconomico(listado, nombreDirector, nombreEncargadoCompra);
            this.addSheetAnalisisTecnico(nombreDirector, nombreEncargadoCompra);
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            workbook.write(outByteStream);

            UtilFile.downloadFileBytes(outByteStream.toByteArray(), "analisis_" + codigoEntidad, UtilFile.CONTENIDO_XLS, UtilFile.EXTENSION_XLS);
        } catch (IOException e) {
            Logger.getLogger(Bean2Excel.class.getName()).log(Level.INFO, null, "Error creación de analisis tecnico y economico");
        }
    }

    private void addSheetAnalisisEconomico(List<?> listado, String nombreDirector, String nombreEncargadoCompra) {
        ReportColumn[] columns = new ReportColumn[]{
            new ReportColumn("numItem", "ITEM", FormatType.TEXT),
            new ReportColumn("item", "DESCRIPCION DEL ITEM", FormatType.TEXT),
            new ReportColumn("cantidadRequerida", "CANTIDAD REQUERIDA", FormatType.INTEGER),
            new ReportColumn("listadoProveedores", "NOMBRES DE LAS PERSONAS PROVEEDORAS", FormatType.OBJECT)
        };

        ReportColumn[] columns1 = new ReportColumn[]{
            new ReportColumn("nombreProveedor", "Nombre Proveedor", FormatType.TEXT),
            new ReportColumn("listadoDatos", "", FormatType.OBJECT)
        };

        ReportColumn[] columns2 = new ReportColumn[]{
            new ReportColumn("cantidadOfertada", "Cantidad Ofertada", FormatType.TEXT),
            new ReportColumn("precioUnitario", "Precio Unitario", FormatType.TEXT),
            new ReportColumn("cantidadAdjudicada", "Cantidad Adjudicada", FormatType.TEXT)
        };

        HSSFSheet sheet = workbook.createSheet("económico");
        int numCols = columns.length;
        int numCols1 = columns1.length;
        int numCols2 = columns2.length;
        int currentRow = 0;
        HSSFRow row;

        LinkedHashMap<String, Integer> mapaItems = (LinkedHashMap) listado.get(0);
        LinkedHashMap<String, Integer> mapaRazonSocial = (LinkedHashMap) listado.get(1);
        Bean[][] data = (Bean[][]) listado.get(2);
        LinkedHashMap<String, String> mapaItemsIndex = (LinkedHashMap) listado.get(3);
        LinkedHashMap<String, Integer> mapaCantidadItems = (LinkedHashMap) listado.get(4);

        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 8);
        hSSFFont.setBold(true);

        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.LETTER_PAPERSIZE);
        sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
        sheet.setMargin(HSSFSheet.TopMargin, 0.75);
        sheet.setMargin(HSSFSheet.LeftMargin, 0.75);
        sheet.setMargin(HSSFSheet.RightMargin, 0.75);

        HSSFCellStyle myBoldStyle = workbook.createCellStyle();
        myBoldStyle.setFont(this.boldFont);

        HSSFCellStyle myNoParticipateStyle = workbook.createCellStyle(); 
        myNoParticipateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myNoParticipateStyle.setBorderBottom(BorderStyle.THIN);
        myNoParticipateStyle.setBorderTop(BorderStyle.THIN);
        myNoParticipateStyle.setBorderRight(BorderStyle.THIN);
        myNoParticipateStyle.setBorderLeft(BorderStyle.THIN);
        myNoParticipateStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        myNoParticipateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        HSSFCellStyle myNormalStyle = workbook.createCellStyle();
        myNormalStyle.setFont(hSSFFont);
        myNormalStyle.setWrapText(true);
        myNormalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myNormalStyle.setBorderBottom(BorderStyle.THIN);
        myNormalStyle.setBorderTop(BorderStyle.THIN);
        myNormalStyle.setBorderRight(BorderStyle.THIN);
        myNormalStyle.setBorderLeft(BorderStyle.THIN);

        HSSFCellStyle myParticularStyle = workbook.createCellStyle();
        myParticularStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myParticularStyle.setBorderBottom(BorderStyle.NONE);
        myParticularStyle.setBorderTop(BorderStyle.NONE);
        myParticularStyle.setBorderRight(BorderStyle.NONE);
        myParticularStyle.setBorderLeft(BorderStyle.NONE);

        HSSFCellStyle myStyle = workbook.createCellStyle();
        myStyle.setRotation(new Short("90"));
        myStyle.setFont(this.boldFont);
        myStyle.setBorderBottom(BorderStyle.THIN);
        myStyle.setBorderTop(BorderStyle.THIN);
        myStyle.setBorderRight(BorderStyle.THIN);
        myStyle.setBorderLeft(BorderStyle.THIN);

        HSSFCellStyle myDataStyle = workbook.createCellStyle();
        myDataStyle.setBorderBottom(BorderStyle.THIN);
        myDataStyle.setBorderTop(BorderStyle.THIN);
        myDataStyle.setBorderRight(BorderStyle.THIN);
        myDataStyle.setBorderLeft(BorderStyle.THIN);

        proveedoresAMostrar = new String[mapaRazonSocial.size()];
        String[] itemsAMostrar = new String[mapaItems.size()];

        mapaRazonSocial.keySet().toArray(proveedoresAMostrar);
        mapaItems.keySet().toArray(itemsAMostrar);

        currentRow++;
        currentRow++;
        currentRow++;
        // Create the report header at row 0
        row = sheet.createRow(currentRow);
        // Loop over all the column beans and populate the report headers
        for (int i = 0; i < numCols; i++) {
            // Get the header text from the bean and write it to the cell
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(myNormalStyle);
            cell.setCellValue(columns[i].getHeader());
            cell.setCellType(CellType.STRING);
        }
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 3, 2 + (proveedoresAMostrar.length * numCols2)));

        currentRow++; // increment the spreadsheet row before we step into the data
        // Create the report header at row 0
        row = sheet.createRow(currentRow);
        // Loop over all the column beans and populate the report headers
        for (int l = 0; l < proveedoresAMostrar.length; l++) {
            for (int m = 0; m < numCols1; m++) {
                // Get the header text from the bean and write it to the cell
                HSSFCell cell = row.createCell(m + 3 + (l * 3));
                cell.setCellStyle(myNormalStyle);
                cell.setCellValue(proveedoresAMostrar[l]);
                cell.setCellType(CellType.STRING);
            }
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 3 + (l * 3), 5 + (l * 3)));
        }

        currentRow++; // increment the spreadsheet row before we step into the data
        // Create the report header at row 0
        row = sheet.createRow(currentRow);
        row.setHeight(new Short("2100"));
        // Loop over all the column beans and populate the report headers           
        for (int l = 0; l < proveedoresAMostrar.length; l++) {
            for (int m = 0; m < numCols2; m++) {
                // Get the header text from the bean and write it to the cell
                HSSFCell cell = row.createCell(m + 3 + (l * 3));
                cell.setCellStyle(myStyle);
                cell.setCellValue(columns2[m].getHeader());
                cell.setCellType(CellType.STRING);
            }
        }

        currentRow++; // increment the spreadsheet row before we step into the data
        for (String itemsAMostrar1 : itemsAMostrar) {
            row = sheet.createRow(currentRow);
            HSSFCell cell = row.createCell(0);
            cell.setCellStyle(myDataStyle);
            cell.setCellValue(mapaItemsIndex.get(itemsAMostrar1));
            cell = row.createCell(1);
            cell.setCellStyle(myDataStyle);
            cell.setCellValue(itemsAMostrar1);
            cell = row.createCell(2);
            cell.setCellStyle(myDataStyle);
            cell.setCellValue(mapaCantidadItems.get(itemsAMostrar1));
            currentRow++;
        }

        for (int j = 0; j < itemsAMostrar.length; j++) {
            HSSFCell cell;
            row = sheet.getRow(j + 6);
            for (int k = 0; k < proveedoresAMostrar.length; k++) {
                Bean bean = (Bean) data[j][k];
                if (bean != null) {
                    cell = row.createCell((k * 3) + 3);
                    cell.setCellStyle(myDataStyle);
                    cell.setCellValue(bean.getCantidadOfertada());
                    cell = row.createCell((k * 3) + 4);
                    cell.setCellStyle(myDataStyle);
                    cell.setCellValue(bean.getPrecioUnitario());
                    cell = row.createCell((k * 3) + 5);
                    cell.setCellStyle(myDataStyle);
                    cell.setCellValue(bean.getCantidadAdjudicada());
                } else {
                    cell = row.createCell((k * 3) + 3);
                    cell.setCellStyle(myNoParticipateStyle);
                    cell.setCellValue("");
                    cell = row.createCell((k * 3) + 4);
                    cell.setCellStyle(myNoParticipateStyle);
                    cell.setCellValue("");
                    cell = row.createCell((k * 3) + 5);
                    cell.setCellStyle(myNoParticipateStyle);
                    cell.setCellValue("");
                }
            }
        }

        sheet.getRow(4).setHeight(new Short("1000"));
        row = sheet.getRow(5);
        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
            sheet.autoSizeColumn(colNum);
        }

        currentRow++;
        currentRow++;
        row = sheet.createRow(currentRow);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("F._____________________________________");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(nombreDirector);
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Representante Legal (Presidente del Organismo de Administración Escolar(a)");
        
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("F._____________________________________");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(nombreEncargadoCompra);
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Encargado de Compra");

        row = sheet.getRow(3);
        cell = row.getCell(0);
        cell.setCellValue("");
        cell.setCellStyle(myParticularStyle);
        cell = row.getCell(1);
        cell.setCellValue("");
        cell.setCellStyle(myParticularStyle);
        cell = row.getCell(2);
        cell.setCellValue("");
        cell.setCellStyle(myParticularStyle);

        row = sheet.getRow(5);
        cell = row.createCell(0);
        cell.setCellStyle(myNormalStyle);
        cell.setCellValue("ITEM");
        sheet.setColumnWidth(cell.getColumnIndex(), 1500);
        cell = row.createCell(1);
        cell.setCellStyle(myNormalStyle);
        cell.setCellValue("DESCRIPCIÓN DEL ITEM");
        cell = row.createCell(2);
        cell.setCellStyle(myNormalStyle);
        cell.setCellValue("CANTIDAD REQUERIDA");
        sheet.setColumnWidth(cell.getColumnIndex(), 3000);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2 + (proveedoresAMostrar.length * numCols2)));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2 + (proveedoresAMostrar.length * numCols2)));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 2 + (proveedoresAMostrar.length * numCols2)));

        row = sheet.createRow(0);
        HSSFCell cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldStyle);
        cell1.setCellValue("ANALISIS ECONOMICO RUBRO " + rubro);
        currentRow++;
        row = sheet.createRow(1);
        cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldStyle);
        cell1.setCellValue("NOMBRE DEL CENTRO EDUCATIVO : " + nombreCentroEducativo + " CODIGO : " + codigoCentroEducativo);
        currentRow++;
        row = sheet.createRow(2);
        cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldStyle);
        cell1.setCellValue("FECHA DE ELABORACIÓN : " + fechaElaboracion);
        currentRow++;

        String leyenda = HSSFHeader.font("Arial", "Bold") + HSSFHeader.fontSize((short) 8) + "ANALISIS TÉCNICO RUBRO : " + rubro + ", CENTRO EDUCATIVO : " + nombreCentroEducativo + " CODIGO : " + codigoCentroEducativo;
        sheet.getHeader().setCenter(leyenda);
        sheet.getFooter().setLeft(digitador);
        sheet.getFooter().setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());

        sheet.setAutobreaks(false);
        sheet.setRowBreak(sheet.getLastRowNum());
        sheet.setColumnBreak(20);
    }

    private void addSheetAnalisisTecnico(String nombreDirector, String nombreEncargadoCompra) {
        HSSFSheet sheet = workbook.createSheet("técnico");
        int currentRow = 0;
        HSSFRow row;

        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 8);
        hSSFFont.setBold(true);

        HSSFCellStyle myBoldTitleStyle = workbook.createCellStyle();
        myBoldTitleStyle.setFont(this.boldFont);

        HSSFCellStyle myBoldStyle = workbook.createCellStyle();
        myBoldStyle.setFont(this.boldFont);
        myBoldStyle.setWrapText(true);
        myBoldStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myBoldStyle.setVerticalAlignment(VerticalAlignment.TOP);

        HSSFCellStyle myNormalStyle = workbook.createCellStyle();
        myNormalStyle.setFont(hSSFFont);
        myNormalStyle.setWrapText(true);
        myNormalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myNormalStyle.setVerticalAlignment(VerticalAlignment.TOP);
        myNormalStyle.setBorderBottom(BorderStyle.THIN);
        myNormalStyle.setBorderTop(BorderStyle.THIN);
        myNormalStyle.setBorderRight(BorderStyle.THIN);
        myNormalStyle.setBorderLeft(BorderStyle.THIN);

        HSSFCellStyle myParticularStyle = workbook.createCellStyle();
        myParticularStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        myParticularStyle.setBorderBottom(BorderStyle.NONE);
        myParticularStyle.setBorderTop(BorderStyle.NONE);
        myParticularStyle.setBorderRight(BorderStyle.NONE);
        myParticularStyle.setBorderLeft(BorderStyle.NONE);

        HSSFCellStyle myStyle = workbook.createCellStyle();
        myStyle.setRotation(new Short("90"));
        myStyle.setFont(this.boldFont);
        myStyle.setBorderBottom(BorderStyle.THIN);
        myStyle.setBorderTop(BorderStyle.THIN);
        myStyle.setBorderRight(BorderStyle.THIN);
        myStyle.setBorderLeft(BorderStyle.THIN);

        HSSFCellStyle myDataStyle = workbook.createCellStyle();
        myDataStyle.setBorderBottom(BorderStyle.THIN);
        myDataStyle.setBorderTop(BorderStyle.THIN);
        myDataStyle.setBorderRight(BorderStyle.THIN);
        myDataStyle.setBorderLeft(BorderStyle.THIN);

        Map<String, String> proveedores = new LinkedHashMap<>();
        for (String provee : proveedoresAMostrar) {
            if (!proveedores.containsKey(provee)) {
                proveedores.put(provee, provee);
            }
        }
        proveedoresAMostrar = proveedores.keySet().toArray(new String[0]);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.LETTER_PAPERSIZE);
        sheet.setMargin(HSSFSheet.BottomMargin, 0.75);
        sheet.setMargin(HSSFSheet.TopMargin, 0.75);
        sheet.setMargin(HSSFSheet.LeftMargin, 0.75);
        sheet.setMargin(HSSFSheet.RightMargin, 0.75);

        row = sheet.createRow(0);
        HSSFCell cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldTitleStyle);
        cell1.setCellValue(
                "MODELO DE FORMATO PARA ANALISIS TÉCNICO RUBRO " + rubro);
        currentRow++;
        row = sheet.createRow(1);
        cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldTitleStyle);
        cell1.setCellValue("NOMBRE DEL CENTRO EDUCATIVO : "
                + nombreCentroEducativo + " CODIGO : " + codigoCentroEducativo);
        currentRow++;
        row = sheet.createRow(2);
        cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldTitleStyle);
        cell1.setCellValue("FECHA DE ELABORACIÓN : " + fechaElaboracion);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1 + (proveedoresAMostrar.length * 2)));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1 + (proveedoresAMostrar.length * 2)));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1 + (proveedoresAMostrar.length * 2)));

        currentRow++;
        //Fila para el titulo de los nombres de proveedores
        row = sheet.createRow(currentRow);
        cell1 = row.createCell(2);
        cell1.setCellStyle(myNormalStyle);
        cell1.setCellValue("NOMBRES DE LOS PROVEEDORES");
        sheet.addMergedRegion(new CellRangeAddress(currentRow,
                currentRow, 2, 1 + (proveedoresAMostrar.length * 2)));

        currentRow++;
        //Fila para ingresar en la hoja el nombre de los proveedores
        row = sheet.createRow(currentRow);
        int cellIndex = 2;
        for (String prov : proveedoresAMostrar) {
            cell1 = row.createCell(cellIndex);
            cell1.setCellStyle(myNormalStyle);
            cell1.setCellValue(prov);
            sheet.addMergedRegion(new CellRangeAddress(currentRow,
                    currentRow, cellIndex, cellIndex + 1));
            cellIndex += 2;
        }
        currentRow++;
        //Fila para ingresar leyendas de cumple y no cumple
        row = sheet.createRow(currentRow);

        cell1 = row.createCell(0);
        cell1.setCellStyle(myBoldStyle);
        cell1.setCellValue("DESCRIPCIÓN DEL RUBRO");
        cell1 = row.createCell(1);
        cell1.setCellStyle(myBoldStyle);
        cell1.setCellValue("MEDICIÓN");

        int cellIndexLabel = 2;
        for (String prov : proveedoresAMostrar) {
            cell1 = row.createCell(cellIndexLabel);
            cell1.setCellStyle(myNormalStyle);
            cell1.setCellValue("Cumple");

            cell1 = row.createCell(cellIndexLabel + 1);
            cell1.setCellStyle(myNormalStyle);
            cell1.setCellValue("No Cumple");
            cellIndexLabel += 2;
        }
        currentRow++;
        //Fila para ingreso de usuario
        row = sheet.createRow(currentRow);
        cell1 = row.createCell(0);
        cell1.setCellStyle(myNormalStyle);
        cell1.setCellValue(rubro);
        cell1 = row.createCell(1);
        cell1.setCellStyle(myNormalStyle);
        cell1.setCellValue("PRESENTACIÓN DEL DOCUMENTO DE LA DECLARACION JURADA GLOBAL DE CUMPLIMIENTO DEL TÉRMINO DE REFERENCIA PLAZO Y LUGAR DE ENTREGA.");

        for (int i = 2; i < (proveedoresAMostrar.length * 2) + 2; i++) {
            if (i % 2 == 0) {
            } else {
                cell1.setCellValue("X");
            }
            cell1 = row.createCell(i);
            cell1.setCellStyle(myNormalStyle);
        }

        currentRow++;
        currentRow++;
        currentRow++;
        row = sheet.createRow(currentRow);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("F._____________________________________");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(nombreDirector);
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Representante Legal (Presidente del Organismo de Administración Escolar (a)");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("F._____________________________________");
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue(nombreEncargadoCompra);
        currentRow++;
        row = sheet.createRow(currentRow);
        cell = row.createCell(0);
        cell.setCellValue("Encargado de Compra");

        String leyenda = HSSFHeader.font("Arial", "Bold")
                + HSSFHeader.fontSize((short) 8) + "ANALISIS TÉCNICO RUBRO : " + rubro
                + ", CENTRO EDUCATIVO : " + nombreCentroEducativo + " CODIGO : "
                + codigoCentroEducativo;
        sheet.getHeader().setCenter(leyenda);
        sheet.getFooter().setLeft(digitador);
        sheet.getFooter().setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());

        sheet.getRow(4).setHeight(new Short("1000"));
        sheet.getRow(5).setHeight(new Short("500"));
        sheet.getRow(6).setHeight(new Short("2000"));
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        row = sheet.getRow(5);
        for (int colNum = 2; colNum < row.getLastCellNum();
                colNum++) {
            sheet.setColumnWidth(colNum, 2500);
        }

        sheet.setAutobreaks(false);
        sheet.setRowBreak(sheet.getLastRowNum());
        sheet.setColumnBreak(7);
    }

    public enum FormatType {
        TEXT, INTEGER, FLOAT, DATE, MONEY, PERCENTAGE, OBJECT, DOUBLE
    }
}
