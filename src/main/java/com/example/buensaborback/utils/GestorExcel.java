package com.example.buensaborback.utils;

import com.example.buensaborback.domain.entities.DetallePedido;
import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.entities.Promocion;
import com.example.buensaborback.domain.enums.FormaPago;
import com.example.buensaborback.domain.enums.TipoEnvio;
import com.example.buensaborback.service.PedidoServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.List;

public class GestorExcel {

    private final PedidoServiceImpl service;

    public GestorExcel(PedidoServiceImpl service) {
        this.service = service;
    }

    // EXCEL PEDIDOS CLIENTE
    public SXSSFWorkbook imprimirExcelPedidos(Long id, Long idSucursal, String desde, String hasta) throws Exception {
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Pedidos");

        // Definir anchos de columna
        hoja.setColumnWidth(0, 4000);  // Ancho de columna para "Fecha Pedido"
        hoja.setColumnWidth(1, 7000);  // Ancho de columna para "Cliente"
        hoja.setColumnWidth(2, 10000); // Ancho de columna para "E-mail del cliente"
        hoja.setColumnWidth(3, 7000);  // Ancho de columna para "Teléfono del cliente"
        hoja.setColumnWidth(4, 15000); // Ancho de columna para "Dirección de emisión del pedido"
        hoja.setColumnWidth(5, 4000);  // Ancho de columna para "Estado del pedido"
        hoja.setColumnWidth(6, 4000);  // Ancho de columna para "Tipo de envío"
        hoja.setColumnWidth(7, 4000);  // Ancho de columna para "Forma de pago"
        hoja.setColumnWidth(8, 7000);  // Ancho de columna para "Artículo"
        hoja.setColumnWidth(9, 4000);  // Ancho de columna para "Cantidad"
        hoja.setColumnWidth(10, 4000); // Ancho de columna para "Precio Unitario"
        hoja.setColumnWidth(11, 4000); // Ancho de columna para "Subtotal Artículo"
        hoja.setColumnWidth(12, 4000); // Ancho de columna para "Subtotal Pedido"
        hoja.setColumnWidth(13, 4000); // Ancho de columna para "Descuentos"
        hoja.setColumnWidth(14, 4000); // Ancho de columna para "Total Pedido"

        XSSFFont fontHeader = (XSSFFont) libro.createFont();
        fontHeader.setBold(true);
        XSSFCellStyle styleHeader = (XSSFCellStyle) libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle styleHeaderCentrado = (XSSFCellStyle) libro.createCellStyle();
        styleHeaderCentrado.cloneStyleFrom(styleHeader);
        styleHeaderCentrado.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle stylePedido = (XSSFCellStyle) libro.createCellStyle();
        stylePedido.setBorderBottom(BorderStyle.THIN);
        stylePedido.setBorderTop(BorderStyle.THIN);
        stylePedido.setBorderRight(BorderStyle.THIN);
        stylePedido.setBorderLeft(BorderStyle.THIN);
        stylePedido.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle styleContent = (XSSFCellStyle) libro.createCellStyle();
        styleContent.cloneStyleFrom(stylePedido);
        styleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        DataFormat dataFormat = libro.createDataFormat();

        XSSFCellStyle styleNumberPedido = (XSSFCellStyle) libro.createCellStyle();
        styleNumberPedido.cloneStyleFrom(stylePedido);
        styleNumberPedido.setDataFormat(dataFormat.getFormat("#,##0.00"));

        XSSFCellStyle styleNumber = (XSSFCellStyle) libro.createCellStyle();
        styleNumber.cloneStyleFrom(styleNumberPedido);
        styleNumber.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        int nroColumna = 0;
        SXSSFRow row = hoja.createRow(0);
        SXSSFCell cell = row.createCell(nroColumna);
        cell.setCellValue("Desde: " + desde);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellValue("Hasta: " + hasta);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);

        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 7, 14));

        nroColumna = 0;
        row = hoja.createRow(1);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Fecha Pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("E-mail del cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Teléfono del cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Domicilio de envío");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Estado del pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Tipo de envío");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Forma de pago");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Artículo");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Cantidad");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Precio Unitario");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Subtotal Artículo");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Subtotal Pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Descuentos");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("Total Pedido");
        cell.setCellStyle(styleHeader);

        int nroFila = 2;
        List<Pedido> pedidos = service.buscarXClienteYSucursal(id, idSucursal, desde, hasta);
        if (pedidos != null) {
            for (Pedido pedido : pedidos) {
                boolean firstDetail = true;
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    nroColumna = 0;
                    row = hoja.createRow(nroFila++);
                    if (firstDetail) {
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFechaPedido().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getEmail());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getTelefono());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getTipoEnvio() == TipoEnvio.TakeAway
                                ? "N/A"
                                : (pedido.getDomicilio().getCalle() + " " + pedido.getDomicilio().getNumero() + ", " + pedido.getDomicilio().getLocalidad().getNombre()));
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getEstado().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getTipoEnvio().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFormaPago().toString());
                        cell.setCellStyle(stylePedido);
                    } else {
                        nroColumna += 8;
                    }
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getArticulo().getDenominacion());
                    cell.setCellStyle(firstDetail ? stylePedido : styleContent);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getCantidad());
                    cell.setCellStyle(firstDetail ? stylePedido : styleContent);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getArticulo() instanceof Promocion ? ((Promocion) detalle.getArticulo()).getPrecioPromocional() : detalle.getArticulo().getPrecioVenta());
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getSubTotal());
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    if (firstDetail) {
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFormaPago() == FormaPago.Efectivo ? pedido.getTotal() / 0.9 : pedido.getTotal());
                        cell.setCellStyle(styleNumberPedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFormaPago() == FormaPago.Efectivo ? pedido.getTotal() / 9 : 0d);
                        cell.setCellStyle(styleNumberPedido);
                        cell = row.createCell(nroColumna);
                        cell.setCellValue(pedido.getTotal());
                        cell.setCellStyle(styleNumberPedido);
                        firstDetail = false;
                    }
                }
            }
        }
        return libro;
    }

    // EXCEL PEDIDOS
    public SXSSFWorkbook imprimirExcelPedidos(Long idSucursal, String estado, String busqueda, String desde, String hasta) throws Exception {
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Pedidos");

        // Definir anchos de columna
        hoja.setColumnWidth(0, 4000);  // Ancho de columna para "Fecha Pedido"
        hoja.setColumnWidth(1, 7000);  // Ancho de columna para "Cliente"
        hoja.setColumnWidth(2, 10000); // Ancho de columna para "E-mail del cliente"
        hoja.setColumnWidth(3, 7000);  // Ancho de columna para "Teléfono del cliente"
        hoja.setColumnWidth(4, 15000); // Ancho de columna para "Dirección de emisión del pedido"
        hoja.setColumnWidth(5, 4000);  // Ancho de columna para "Estado del pedido"
        hoja.setColumnWidth(6, 4000);  // Ancho de columna para "Tipo de envío"
        hoja.setColumnWidth(7, 4000);  // Ancho de columna para "Forma de pago"
        hoja.setColumnWidth(8, 7000);  // Ancho de columna para "Artículo"
        hoja.setColumnWidth(9, 4000);  // Ancho de columna para "Cantidad"
        hoja.setColumnWidth(10, 4000); // Ancho de columna para "Precio Unitario"
        hoja.setColumnWidth(11, 4000); // Ancho de columna para "Subtotal Artículo"
        hoja.setColumnWidth(12, 4000); // Ancho de columna para "Subtotal Pedido"
        hoja.setColumnWidth(13, 4000); // Ancho de columna para "Descuentos"
        hoja.setColumnWidth(14, 4000); // Ancho de columna para "Total Pedido"

        XSSFFont fontHeader = (XSSFFont) libro.createFont();
        fontHeader.setBold(true);
        XSSFCellStyle styleHeader = (XSSFCellStyle) libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle styleHeaderCentrado = (XSSFCellStyle) libro.createCellStyle();
        styleHeaderCentrado.cloneStyleFrom(styleHeader);
        styleHeaderCentrado.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle stylePedido = (XSSFCellStyle) libro.createCellStyle();
        stylePedido.setBorderBottom(BorderStyle.THIN);
        stylePedido.setBorderTop(BorderStyle.THIN);
        stylePedido.setBorderRight(BorderStyle.THIN);
        stylePedido.setBorderLeft(BorderStyle.THIN);
        stylePedido.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle styleContent = (XSSFCellStyle) libro.createCellStyle();
        styleContent.cloneStyleFrom(stylePedido);
        styleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        DataFormat dataFormat = libro.createDataFormat();

        XSSFCellStyle styleNumberPedido = (XSSFCellStyle) libro.createCellStyle();
        styleNumberPedido.cloneStyleFrom(stylePedido);
        styleNumberPedido.setDataFormat(dataFormat.getFormat("#,##0.00"));

        XSSFCellStyle styleNumber = (XSSFCellStyle) libro.createCellStyle();
        styleNumber.cloneStyleFrom(styleNumberPedido);
        styleNumber.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        List<Pedido> pedidos = service.buscarXNombreIdFecha(idSucursal, busqueda, desde, hasta, estado);

        if (estado == null)
            estado = "Todos";
        if (busqueda == null)
            busqueda = "N/A";
        if (desde == null)
            desde = "N/A";
        if (hasta == null)
            hasta = "N/A";

        int nroColumna = 0;
        SXSSFRow row = hoja.createRow(0);
        SXSSFCell cell = row.createCell(nroColumna);
        cell.setCellValue("Desde: " + desde);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellValue("Hasta: " + hasta);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellValue("Estado: " + estado);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellValue("Busqueda: " + busqueda);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(++nroColumna);
        cell.setCellStyle(styleHeaderCentrado);

        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 3, 6));
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 7, 10));
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 11, 14));

        nroColumna = 0;
        row = hoja.createRow(1);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Fecha Pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("E-mail del cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Teléfono del cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Dirección de emisión del pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Estado del pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Tipo de envío");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Forma de pago");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Artículo");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Cantidad");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Precio Unitario");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Subtotal Artículo");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Subtotal Pedido");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Descuentos");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("Total Pedido");
        cell.setCellStyle(styleHeader);

        int nroFila = 2;
        if (pedidos != null) {
            for (Pedido pedido : pedidos) {
                boolean firstDetail = true;
                Double subtotal = 0d;
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    nroColumna = 0;
                    row = hoja.createRow(nroFila++);
                    if (firstDetail) {
                        subtotal = 0d;
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFechaPedido().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getEmail());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getCliente().getTelefono());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getDomicilio().getCalle() + " " + pedido.getDomicilio().getNumero() + ", " + pedido.getDomicilio().getLocalidad().getNombre());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getEstado().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getTipoEnvio().toString());
                        cell.setCellStyle(stylePedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFormaPago().toString());
                        cell.setCellStyle(stylePedido);
                    } else {
                        nroColumna += 8;
                    }
                    cell = row.createCell(nroColumna);
                    cell.setCellValue(detalle.getArticulo().getDenominacion());
                    cell.setCellStyle(firstDetail ? stylePedido : styleContent);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getCantidad());
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getArticulo().getPrecioVenta());
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getArticulo().getPrecioVenta());
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    cell = row.createCell(nroColumna++);
                    cell.setCellValue(detalle.getSubTotal());
                    subtotal += detalle.getSubTotal();
                    cell.setCellStyle(firstDetail ? styleNumberPedido : styleNumber);
                    if (firstDetail) {
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(subtotal.toString());
                        cell.setCellStyle(styleNumberPedido);
                        cell = row.createCell(nroColumna++);
                        cell.setCellValue(pedido.getFormaPago() == FormaPago.Efectivo ? Double.toString(subtotal * 0.1) : Double.toString(0d));
                        cell.setCellStyle(styleNumberPedido);
                        cell = row.createCell(nroColumna);
                        cell.setCellValue(pedido.getTotal().toString());
                        cell.setCellStyle(styleNumberPedido);
                        firstDetail = false;
                    }
                }
            }
        }
        return libro;
    }

    // RANKING PRODUCTOS
    public SXSSFWorkbook imprimirExcelRankingProductos(Long idCategoria, Long idSucursal, String desde, String hasta) throws Exception {
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Ranking productos");

        // Definir anchos de columna
        hoja.setColumnWidth(0, 10000); // Ancho de columna para "Artículo"
        hoja.setColumnWidth(1, 7000);  // Ancho de columna para "Ventas"

        XSSFFont fontHeader = (XSSFFont) libro.createFont();
        fontHeader.setBold(true);
        XSSFCellStyle styleHeader = (XSSFCellStyle) libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle styleHeaderCentrado = (XSSFCellStyle) libro.createCellStyle();
        styleHeaderCentrado.cloneStyleFrom(styleHeader);
        styleHeaderCentrado.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle stylePedido = (XSSFCellStyle) libro.createCellStyle();
        stylePedido.setBorderBottom(BorderStyle.THIN);
        stylePedido.setBorderTop(BorderStyle.THIN);
        stylePedido.setBorderRight(BorderStyle.THIN);
        stylePedido.setBorderLeft(BorderStyle.THIN);
        stylePedido.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle styleContent = (XSSFCellStyle) libro.createCellStyle();
        styleContent.cloneStyleFrom(stylePedido);
        styleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        DataFormat dataFormat = libro.createDataFormat();

        XSSFCellStyle styleNumberPedido = (XSSFCellStyle) libro.createCellStyle();
        styleNumberPedido.cloneStyleFrom(stylePedido);
        styleNumberPedido.setDataFormat(dataFormat.getFormat("#,##0.00"));

        XSSFCellStyle styleNumber = (XSSFCellStyle) libro.createCellStyle();
        styleNumber.cloneStyleFrom(styleNumberPedido);
        styleNumber.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        int nroColumna = 0;
        SXSSFRow row = hoja.createRow(0);
        SXSSFCell cell = row.createCell(nroColumna);

        cell.setCellValue("Ranking Producto");
        cell.setCellStyle(styleHeaderCentrado);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        nroColumna = 0;
        row = hoja.createRow(1);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Desde: " + desde);
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("Hasta: " + hasta);
        cell.setCellStyle(styleHeader);

        nroColumna = 0;
        row = hoja.createRow(2);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Artículo");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("Ventas");
        cell.setCellStyle(styleHeader);

        int nroFila = 3;
        List<Object[]> rankingProductos = service.rankingProductos(idCategoria, idSucursal, desde, hasta);
        if (rankingProductos != null) {
            for (Object[] ranking : rankingProductos) {
                nroColumna = 0;
                row = hoja.createRow(nroFila++);
                cell = row.createCell(nroColumna++);
                cell.setCellValue(ranking[0].toString());
                cell.setCellStyle(stylePedido);
                cell = row.createCell(nroColumna++);
                cell.setCellValue(ranking[1].toString());
                cell.setCellStyle(styleNumberPedido);
            }
        }
        return libro;
    }

    public SXSSFWorkbook imprimirExcelRankingClientes(Long limite, Long idSucursal, String desde, String hasta, boolean ordenarPorPedidos) throws Exception {
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Ranking clientes");

        hoja.setColumnWidth(0, 10000); // Ancho de columna para "Cliente"
        hoja.setColumnWidth(1, 5000);  // Ancho de columna para "Pedidos"
        hoja.setColumnWidth(2, 7000);  // Ancho de columna para "Monto total"

        XSSFFont fontHeader = (XSSFFont) libro.createFont();
        fontHeader.setBold(true);
        XSSFCellStyle styleHeader = (XSSFCellStyle) libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle styleHeaderCentrado = (XSSFCellStyle) libro.createCellStyle();
        styleHeaderCentrado.cloneStyleFrom(styleHeader);
        styleHeaderCentrado.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle stylePedido = (XSSFCellStyle) libro.createCellStyle();
        stylePedido.setBorderBottom(BorderStyle.THIN);
        stylePedido.setBorderTop(BorderStyle.THIN);
        stylePedido.setBorderRight(BorderStyle.THIN);
        stylePedido.setBorderLeft(BorderStyle.THIN);
        stylePedido.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle styleContent = (XSSFCellStyle) libro.createCellStyle();
        styleContent.cloneStyleFrom(stylePedido);
        styleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        DataFormat dataFormat = libro.createDataFormat();

        XSSFCellStyle styleNumberPedido = (XSSFCellStyle) libro.createCellStyle();
        styleNumberPedido.cloneStyleFrom(stylePedido);
        styleNumberPedido.setDataFormat(dataFormat.getFormat("#,##0.00"));

        XSSFCellStyle styleNumber = (XSSFCellStyle) libro.createCellStyle();
        styleNumber.cloneStyleFrom(styleNumberPedido);
        styleNumber.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        int nroColumna = 0;
        SXSSFRow row = hoja.createRow(0);
        SXSSFCell cell = row.createCell(nroColumna);

        cell.setCellValue("Ranking Clientes");
        cell.setCellStyle(styleHeaderCentrado);

        nroColumna = 0;
        row = hoja.createRow(1);
        cell = row.createCell(nroColumna++);
        cell.setCellValue(ordenarPorPedidos ? "Ordenado por Pedidos" : "Ordenado por monto total");
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        nroColumna = 0;
        row = hoja.createRow(2);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Desde: " + desde);
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Hasta: " + hasta);
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("TOP " + limite);
        cell.setCellStyle(styleHeader);

        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));

        nroColumna = 0;
        row = hoja.createRow(3);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Cliente");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Pedidos");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna);
        cell.setCellValue("Monto total");
        cell.setCellStyle(styleHeader);

        int nroFila = 4;
        List<Object[]> rankingClientes = service.rankingClientes(limite, idSucursal, desde, hasta, ordenarPorPedidos);
        if (rankingClientes != null) {
            for (Object[] ranking : rankingClientes) {
                nroColumna = 0;
                row = hoja.createRow(nroFila++);
                cell = row.createCell(nroColumna++);
                cell.setCellValue(ranking[0].toString());
                cell.setCellStyle(stylePedido);
                cell = row.createCell(nroColumna++);
                cell.setCellValue(ranking[2].toString());
                cell.setCellStyle(styleNumberPedido);
                cell = row.createCell(nroColumna);
                cell.setCellValue("$" + ranking[3].toString());
                cell.setCellStyle(styleNumberPedido);
            }
        }
        return libro;
    }

    public SXSSFWorkbook imprimirExcelMovimientosMonetarios(Long idSucursal, String desde, String hasta, boolean esDiario) throws Exception {
        SXSSFWorkbook libro = new SXSSFWorkbook(50);
        SXSSFSheet hoja = libro.createSheet("Movimientos monetarios");

        // Definir anchos de columna
        hoja.setColumnWidth(0, 5000); // Ancho de columna para "Fecha" o "Mes"
        hoja.setColumnWidth(1, 7000); // Ancho de columna para "Ingresos"
        hoja.setColumnWidth(2, 7000); // Ancho de columna para "Costos"
        hoja.setColumnWidth(3, 7000); // Ancho de columna para "Ganancias"

        XSSFFont fontHeader = (XSSFFont) libro.createFont();
        fontHeader.setBold(true);
        XSSFCellStyle styleHeader = (XSSFCellStyle) libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle styleHeaderCentrado = (XSSFCellStyle) libro.createCellStyle();
        styleHeaderCentrado.cloneStyleFrom(styleHeader);
        styleHeaderCentrado.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle stylePedido = (XSSFCellStyle) libro.createCellStyle();
        stylePedido.setBorderBottom(BorderStyle.THIN);
        stylePedido.setBorderTop(BorderStyle.THIN);
        stylePedido.setBorderRight(BorderStyle.THIN);
        stylePedido.setBorderLeft(BorderStyle.THIN);
        stylePedido.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        stylePedido.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle styleContent = (XSSFCellStyle) libro.createCellStyle();
        styleContent.cloneStyleFrom(stylePedido);
        styleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        DataFormat dataFormat = libro.createDataFormat();

        XSSFCellStyle styleNumberPedido = (XSSFCellStyle) libro.createCellStyle();
        styleNumberPedido.cloneStyleFrom(stylePedido);
        styleNumberPedido.setDataFormat(dataFormat.getFormat("#,##0.00"));

        XSSFCellStyle styleNumber = (XSSFCellStyle) libro.createCellStyle();
        styleNumber.cloneStyleFrom(styleNumberPedido);
        styleNumber.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        int nroColumna = 0;
        SXSSFRow row = hoja.createRow(0);
        SXSSFCell cell = row.createCell(nroColumna++);
        cell.setCellValue("Movimientos Monetarios");
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        nroColumna = 0;
        row = hoja.createRow(1);
        cell = row.createCell(nroColumna++);
        cell.setCellValue(esDiario ? "Agrupado por dia" : "Agrupado por mes");
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna);
        cell.setCellStyle(styleHeaderCentrado);
        nroColumna = 0;
        row = hoja.createRow(2);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Desde: " + desde);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Hasta: " + hasta);
        cell.setCellStyle(styleHeaderCentrado);
        cell = row.createCell(nroColumna);
        cell.setCellStyle(styleHeaderCentrado);

        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 3)); // Une celdas de la fila 0, columnas 0 a 3
        hoja.addMergedRegion(new CellRangeAddress(1, 1, 0, 3)); // Une celdas de la fila 0, columnas 0 a 3
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 0, 1)); // Une celdas de la fila 1, columnas 0 a 1
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 2, 3)); // Une celdas de la fila 1, columnas 2 a 3

        nroColumna = 0;
        row = hoja.createRow(3);
        cell = row.createCell(nroColumna++);
        cell.setCellValue(esDiario ? "Fecha" : "Mes");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Ingresos");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Costos");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);
        cell.setCellValue("Ganancias");
        cell.setCellStyle(styleHeader);
        cell = row.createCell(nroColumna++);

        int nroFila = 4;
        List<Object[]> movimientosMonetarios = service.movimientosMonetarios(idSucursal, desde, hasta, esDiario);
        if (movimientosMonetarios != null) {
            for (Object[] ranking : movimientosMonetarios) {
                nroColumna = 0;
                row = hoja.createRow(nroFila++);
                cell = row.createCell(nroColumna++);
                cell.setCellValue(ranking[0].toString());
                cell.setCellStyle(stylePedido);
                cell = row.createCell(nroColumna++);
                cell.setCellValue("$" + ranking[1].toString());
                cell.setCellStyle(styleNumberPedido);
                cell = row.createCell(nroColumna++);
                cell.setCellValue("$" + ranking[2].toString());
                cell.setCellStyle(styleNumberPedido);
                cell = row.createCell(nroColumna);
                cell.setCellValue("$" + ranking[3].toString());
                cell.setCellStyle(styleNumberPedido);
            }
        }
        return libro;
    }
}