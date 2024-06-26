package com.example.buensaborback.utils;

import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.domain.enums.FormaPago;
import com.example.buensaborback.service.PedidoServiceImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class GestorPdf {

    private final PedidoServiceImpl service;
    private final JavaMailSender javaMailSender;

    public GestorPdf(PedidoServiceImpl service, JavaMailSender javaMailSender) {
        this.service = service;
        this.javaMailSender = javaMailSender;
    }

    public void generarYEnviarPdfFactura(Long id) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imprimirPdfFactura(id, outputStream);

        try {
            Pedido pedido = service.findById(id);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("Factura de Pedido N°" + (("0000") + pedido.getId().toString()).substring((("0000") + pedido.getId()).length() - 5));
            helper.setTo(pedido.getCliente().getEmail());
            helper.setText("Adjunto encontrarás la factura de tu pedido. Saludos.");

            // Adjuntar el PDF generado
            helper.addAttachment("factura.pdf", new ByteArrayResource(outputStream.toByteArray()));
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo electrónico con la factura adjunta");
        }
    }

    public void imprimirPdfFactura(Long id, ByteArrayOutputStream outputStream) throws Exception {
        Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Font textoBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
        Font texto = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Font precio = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);

        try {
            Pedido pedido = service.findById(id);
            Factura factura = new Factura();
            factura.setFechaFacturacion(LocalDate.now());
            factura.setPedido(pedido);
            factura.setTotalVenta(pedido.getTotal());
            factura.setFormaPago(pedido.getFormaPago());

            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            document.addTitle("Factura");
            document.addSubject(pedido.getSucursal().getEmpresa().getNombre());
            document.addKeywords("PDF");
            document.addAuthor("El Buen Sabor");
            document.addCreator("El Buen Sabor");

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // ENCABEZADO
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 4, 2});

            Image companyLogo = Image.getInstance(pedido.getSucursal().getEmpresa().getImagen().getUrl());
            companyLogo.scaleToFit(50, 50);
            PdfPCell logoCell = new PdfPCell(companyLogo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(logoCell);

            PdfPCell companyNameCell = new PdfPCell(new Paragraph(pedido.getSucursal().getEmpresa().getNombre(), titulo));
            companyNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            companyNameCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(companyNameCell);

            PdfPCell sucursalCelda = new PdfPCell(new Paragraph(pedido.getSucursal().getNombre(), titulo));
            sucursalCelda.setVerticalAlignment(Element.ALIGN_RIGHT);
            sucursalCelda.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(sucursalCelda);

            document.add(headerTable);

            LineSeparator line = new LineSeparator();
            line.setOffset(-2);
            document.add(new Chunk(line));

            // DATOS DEL CLIENTE Y SUCURSAL
            PdfPTable clientSucursalTable = new PdfPTable(2);
            clientSucursalTable.setWidthPercentage(100);
            clientSucursalTable.setSpacingBefore(10f);
            clientSucursalTable.setSpacingAfter(10f);

            // Datos del Cliente
            PdfPTable clientTable = new PdfPTable(1);
            clientTable.setWidthPercentage(100);
            clientTable.setSpacingBefore(10f);
            clientTable.setSpacingAfter(10f);

            addDescriptionCell(clientTable, "Cliente:", (pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido()), textoBold, texto);
            addDescriptionCell(clientTable, "DNI Cliente:", (pedido.getCliente().getDni()), textoBold, texto);
            addDescriptionCell(clientTable, "CUIL Cliente:", pedido.getCliente().getCuil(), textoBold, texto);
            addDescriptionCell(clientTable, "Teléfono Cliente:", pedido.getCliente().getTelefono(), textoBold, texto);
            Domicilio domicilioCliente = pedido.getDomicilio();
            String domicilioClienteStr = domicilioCliente.getCalle() + " " + domicilioCliente.getNumero() + ", " +
                    domicilioCliente.getLocalidad().getNombre() + ", " + domicilioCliente.getLocalidad().getProvincia().getNombre() + ", " + domicilioCliente.getLocalidad().getProvincia().getPais().getNombre();
            addDescriptionCell(clientTable, "Domicilio Cliente:", domicilioClienteStr, textoBold, texto);

            PdfPCell clientCell = new PdfPCell(clientTable);
            clientCell.setBorder(Rectangle.NO_BORDER);
            clientSucursalTable.addCell(clientCell);

            // Datos de la Sucursal
            PdfPTable sucursalTable = new PdfPTable(1);
            sucursalTable.setWidthPercentage(100);
            sucursalTable.setSpacingBefore(10f);
            sucursalTable.setSpacingAfter(10f);


            addDescriptionCell(sucursalTable, "Fecha Pedido:", pedido.getFechaPedido().toString(), textoBold, texto);
            addDescriptionCell(sucursalTable, "Forma de Pago:", pedido.getFormaPago().toString(), textoBold, texto);
            addDescriptionCell(sucursalTable, "Tipo de Envío:", pedido.getTipoEnvio().toString(), textoBold, texto);
            addDescriptionCell(sucursalTable, "CUIL Empresa:", pedido.getSucursal().getEmpresa().getCuil().toString(), textoBold, texto);
            addDescriptionCell(sucursalTable, "Razón Social Empresa:", pedido.getSucursal().getEmpresa().getRazonSocial(), textoBold, texto);
            Domicilio domicilioSucursal = pedido.getSucursal().getDomicilio();
            String domicilioSucursalStr = domicilioSucursal.getCalle() + " " + domicilioSucursal.getNumero() + ", " +
                    domicilioSucursal.getLocalidad().getNombre() + ", " + domicilioSucursal.getLocalidad().getProvincia().getNombre() + ", " + domicilioSucursal.getLocalidad().getProvincia().getPais().getNombre();
            addDescriptionCell(sucursalTable, "Domicilio Sucursal:", domicilioSucursalStr, textoBold, texto);


            PdfPCell sucursalCell = new PdfPCell(sucursalTable);
            sucursalCell.setBorder(Rectangle.NO_BORDER);
            clientSucursalTable.addCell(sucursalCell);

            document.add(clientSucursalTable);

            // DETALLES DEL PEDIDO
            PdfPTable orderTable = new PdfPTable(4);
            orderTable.setWidthPercentage(100);
            orderTable.setWidths(new float[]{1, 5, 2, 2});
            orderTable.setSpacingBefore(10f);
            orderTable.setSpacingAfter(10f);

            addHeaderCell(orderTable, "Cant.", textoBold, BaseColor.LIGHT_GRAY);
            addHeaderCell(orderTable, "Descripción", textoBold, BaseColor.LIGHT_GRAY);
            addHeaderCell(orderTable, "Precio unitario", textoBold, BaseColor.LIGHT_GRAY);
            addHeaderCell(orderTable, "Subtotal", textoBold, BaseColor.LIGHT_GRAY);

            Double subtotal = 0d;

            for (DetallePedido detalle : pedido.getDetallePedidos()) {
                orderTable.addCell(new PdfPCell(new Phrase(detalle.getCantidad().toString(), texto)));
                orderTable.addCell(new PdfPCell(new Phrase(detalle.getArticulo().getDenominacion(), texto)));
                orderTable.addCell(new PdfPCell(new Phrase("$" + (detalle.getArticulo() instanceof Promocion
                                ? ((Promocion) detalle.getArticulo()).getPrecioPromocional()
                                : detalle.getArticulo().getPrecioVenta()).toString(), texto)))
                        .setHorizontalAlignment(Element.ALIGN_RIGHT);
                orderTable.addCell(new PdfPCell(new Phrase("$" + detalle.getSubTotal().toString(), texto))).setHorizontalAlignment(Element.ALIGN_RIGHT);
                subtotal += detalle.getSubTotal();
            }

            document.add(orderTable);

            // TOTAL
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setSpacingBefore(10f);

            PdfPCell subtotalLabelCell = new PdfPCell(new Phrase("Subtotal:", textoBold));
            subtotalLabelCell.setBorder(Rectangle.NO_BORDER);
            subtotalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(subtotalLabelCell);

            PdfPCell subtotalValueCell = new PdfPCell(new Phrase("$" + subtotal, precio));
            subtotalValueCell.setBorder(Rectangle.NO_BORDER);
            subtotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(subtotalValueCell);

            if (pedido.getFormaPago() == FormaPago.Efectivo) {
                PdfPCell descuentosLabelCell = new PdfPCell(new Phrase("Descuentos:", textoBold));
                descuentosLabelCell.setBorder(Rectangle.NO_BORDER);
                descuentosLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(descuentosLabelCell);

                PdfPCell descuentosValueCell = new PdfPCell(new Phrase("$" + subtotal * 0.1, precio));
                descuentosValueCell.setBorder(Rectangle.NO_BORDER);
                descuentosValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(descuentosValueCell);
            }

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total:", textoBold));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase("$" + pedido.getTotal().toString(), precio));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalValueCell);

            document.add(totalTable);

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addDescriptionCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph p = new Paragraph();
        p.add(new Phrase(label + " ", labelFont));
        p.add(new Phrase(value, valueFont));
        cell.addElement(p);

        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
