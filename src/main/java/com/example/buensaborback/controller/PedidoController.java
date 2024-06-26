package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.domain.enums.Estado;
import com.example.buensaborback.domain.enums.FormaPago;
import com.example.buensaborback.domain.enums.TipoEnvio;
import com.example.buensaborback.service.EmpleadoServiceImpl;
import com.example.buensaborback.service.PedidoServiceImpl;
import com.example.buensaborback.service.StockInsumoServiceImpl;
import com.example.buensaborback.utils.GestorExcel;
import com.example.buensaborback.utils.GestorPdf;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/pedidos")
public class PedidoController extends BaseControllerImpl<Pedido, PedidoServiceImpl> {

    @Autowired
    private PedidoServiceImpl service;
    @Autowired
    private EmpleadoServiceImpl empleadoService;
    @Autowired
    private StockInsumoServiceImpl stockInsumoService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public PedidoController(PedidoServiceImpl service) {
        super(service);
    }

    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin', 'cajero', 'cocinero', 'delivery')")
    @MessageMapping("/pedidos")
    @SendTo("/topic/pedidos")
    @GetMapping("buscar/{idSucursal}")
    public ResponseEntity<?> buscarXNombreId(@PathVariable Long idSucursal, @RequestParam(required = false) String estado, @RequestParam(required = false) String busqueda, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        try {
            List<Pedido> pedidos;
            pedidos = service.buscarXNombreIdFecha(idSucursal, busqueda, desde, hasta, estado);
            return ResponseEntity.status(HttpStatus.OK).body(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PreAuthorize("hasAuthority('cliente')")
    @GetMapping("cliente/{id}")
    public ResponseEntity<?> buscarXClienteYSucursal(@PathVariable Long id, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        try {
            List<Pedido> pedido = service.buscarXClienteYSucursal(id, idSucursal, desde, hasta);
            return ResponseEntity.status(HttpStatus.OK).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    // ~ ESTADÍSTICAS ~
    // RANKING DE PRODUCTOS
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("rankingproductos")
    public ResponseEntity<?> rankingProductos(@RequestParam(required = false) Long idCategoria, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        try {
            List<List<Object>> data = new ArrayList<>();
            List<Object[]> rs;
            try {
                rs = service.rankingProductos(idCategoria, idSucursal, desde, hasta);
                rs.forEach(dato -> data.add(Arrays.asList(dato)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    // EXCEL
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("descargarexcelrankingproductos")
    public ResponseEntity<?> descargarExcelRankingProductos(@RequestParam(required = false) Long idCategoria, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        GestorExcel gestorExcel = new GestorExcel(service);
        try {
            SXSSFWorkbook libroExcel = gestorExcel.imprimirExcelRankingProductos(idCategoria, idSucursal, desde, hasta);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            libroExcel.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "ranking-productos-" + desde + "-" + hasta + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            libroExcel.close();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // RANKING DE CLIENTES
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("rankingclientes")
    public ResponseEntity<?> rankingClientes(@RequestParam(required = false) Long limite, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta, @RequestParam(required = false) boolean ordenarPorPedidos) {
        try {
            List<List<Object>> data = new ArrayList<>();
            List<Object[]> rs;
            try {
                rs = service.rankingClientes(limite, idSucursal, desde, hasta, ordenarPorPedidos);
                rs.forEach(dato -> data.add(Arrays.asList(dato)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    // EXCEL
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("descargarexcelrankingclientes")
    public ResponseEntity<?> descargarExcelRankingClientes(@RequestParam(required = false) Long limite, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta, @RequestParam(required = false) boolean ordenarPorPedidos) {
        GestorExcel gestorExcel = new GestorExcel(service);
        try {
            SXSSFWorkbook libroExcel = gestorExcel.imprimirExcelRankingClientes(limite, idSucursal, desde, hasta, ordenarPorPedidos);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            libroExcel.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "ranking-clientes-" + desde + "-" + hasta + "-top" + limite + "-" + (ordenarPorPedidos ? "mas_pedidos" : "mayor_total") + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            libroExcel.close();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // MOVIMIENTOS MONETARIOS
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("movimientosmonetarios")
    public ResponseEntity<?> movimientosMonetarios(@RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta, @RequestParam(required = false) boolean esDiario) {
        try {
            List<List<Object>> data = new ArrayList<>();
            List<Object[]> rs;
            try {
                rs = service.movimientosMonetarios(idSucursal, desde, hasta, esDiario);
                rs.forEach(dato -> data.add(Arrays.asList(dato)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    // EXCEL
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("descargarexcelmovimientosmonetarios")
    public ResponseEntity<?> descargarExcelMovimientosMonetarios(@RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta, @RequestParam(required = false) boolean esDiario) {
        GestorExcel gestorExcel = new GestorExcel(service);
        try {
            SXSSFWorkbook libroExcel = gestorExcel.imprimirExcelMovimientosMonetarios(idSucursal, desde, hasta, esDiario);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            libroExcel.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "movimientos-monetarios-" + desde + "-" + hasta + "-agrupamiento_" + (esDiario ? "diario" : "mensual") + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            libroExcel.close();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // EXCEL PEDIDOS CLIENTE
    @PreAuthorize("hasAuthority('cliente')")
    @GetMapping("descargarexcelpedidoscliente/{id}")
    public ResponseEntity<?> descargarExcelPedidosCliente(@PathVariable Long id, @RequestParam(required = false) Long idSucursal, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        GestorExcel gestorExcel = new GestorExcel(service);
        try {
            SXSSFWorkbook libroExcel = gestorExcel.imprimirExcelPedidos(id, idSucursal, desde, hasta);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            libroExcel.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "pedidos-cliente" + id + "-" + desde + "-" + hasta + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            libroExcel.close();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // EXCEL PEDIDOS FACTURACIÓN
    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin', 'cajero')")
    @GetMapping("descargarexcelpedidos/{idSucursal}")
    public ResponseEntity<?> descargarExcelPedidos(@PathVariable Long idSucursal, @RequestParam(required = false) String estado, @RequestParam(required = false) String busqueda, @RequestParam(required = false) String desde, @RequestParam(required = false) String hasta) {
        GestorExcel gestorExcel = new GestorExcel(service);
        try {
            SXSSFWorkbook libroExcel = gestorExcel.imprimirExcelPedidos(idSucursal, estado, busqueda, desde, hasta);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            libroExcel.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "pedidos.xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            libroExcel.close();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PDF FACTURA
    @GetMapping("facturar/{id}")
    public ResponseEntity<?> descargarPdfInstrumento(@PathVariable String id) {
        GestorPdf documentoManager = new GestorPdf(service, javaMailSender);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            documentoManager.imprimirPdfFactura(Long.parseLong(id), outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.setContentDispositionFormData("attachment", "factura-nro-" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PDF FACTURA ENVIAR MAIL
    @GetMapping("facturarYEnviar/{id}")
    public ResponseEntity<?> facturarYEnviar(@PathVariable String id) {
        GestorPdf documentoManager = new GestorPdf(service, javaMailSender);
        try {
            documentoManager.generarYEnviarPdfFactura(Long.parseLong(id));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<?> save(Pedido pedido) {
        List<Pedido> pedidos;
        try {
            pedidos = service.buscarXNombreIdFecha(pedido.getSucursal().getId(), null, null, null, String.valueOf(Estado.APROBADO));

            pedido.getDetallePedidos().forEach(detallePedido -> {
                // Asignar el pedido
                detallePedido.setPedido(pedido);

                //Calcular subtotales
                detallePedido.setSubTotal(detallePedido.getCantidad() * (detallePedido.getArticulo() instanceof Promocion ? ((Promocion) detallePedido.getArticulo()).getPrecioPromocional() : detallePedido.getArticulo().getPrecioVenta()));

                // Descontar el stock
                if (detallePedido.getArticulo() instanceof ArticuloInsumo) {

                    descontarStockInsumo((ArticuloInsumo) detallePedido.getArticulo(), pedido.getSucursal().getId(), detallePedido.getCantidad());

                } else if (detallePedido.getArticulo() instanceof ArticuloManufacturado articuloManufacturado) {

                    articuloManufacturado.getArticuloManufacturadoDetalles().forEach(detalle -> {
                        descontarStockInsumo(detalle.getArticuloInsumo(), pedido.getSucursal().getId(), detalle.getCantidad() * detallePedido.getCantidad());
                    });

                } else if (detallePedido.getArticulo() instanceof Promocion promocion) {

                    promocion.getPromocionDetalles().forEach(detalle -> {
                        if (detalle.getArticulo() instanceof ArticuloInsumo) {
                            descontarStockInsumo((ArticuloInsumo) detalle.getArticulo(), pedido.getSucursal().getId(), detalle.getCantidad() * detallePedido.getCantidad());
                        } else if (detalle.getArticulo() instanceof ArticuloManufacturado articuloManufacturado) {
                            articuloManufacturado.getArticuloManufacturadoDetalles().forEach(detalleManufacturado -> {
                                descontarStockInsumo(detalleManufacturado.getArticuloInsumo(), pedido.getSucursal().getId(), detalleManufacturado.getCantidad() * detalle.getCantidad() * detallePedido.getCantidad());
                            });
                        }
                    });
                }
            });

            // Calcular el total
            pedido.setTotal(pedido.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getSubTotal()).sum() * (pedido.getFormaPago() == FormaPago.Efectivo ? 0.9 : 1));

            // Calcular el total de costo
            pedido.setTotalCosto(pedido.getDetallePedidos().stream().mapToDouble(detalle -> detalle.getArticulo() instanceof ArticuloInsumo
                    ? ((ArticuloInsumo) detalle.getArticulo()).getPrecioCompra() * detalle.getCantidad()
                    : detalle.getArticulo() instanceof Promocion
                    ? ((Promocion) detalle.getArticulo())
                    .getPromocionDetalles().stream().mapToDouble(d ->
                            (d.getArticulo() instanceof ArticuloInsumo
                                    ? ((ArticuloInsumo) d.getArticulo()).getPrecioCompra()
                                    : ((ArticuloManufacturado) d.getArticulo()).getPrecioCosto())
                                    * d.getCantidad() * detalle.getCantidad()).sum()
                    : ((ArticuloManufacturado) detalle.getArticulo()).getPrecioCosto() * detalle.getCantidad()
            ).sum());

            // Asignarle hora actual
            pedido.setFechaPedido(new Date());

            // Calcular empleados en producción en este momento
            int empleadosEnProduccion = empleadoService.buscarEmpleadosEnProduccion(pedido.getSucursal().getId(),
                    pedido.getFechaPedido());

            // Si no hay empleados en producción, asignar como valor por defecto 1
            if (empleadosEnProduccion == 0)
                empleadosEnProduccion = 1;

            // Calcular el tiempo estimado
            pedido.setHoraEstimadaFinalizacion(
                    LocalTime.ofSecondOfDay(
                            (
                                    LocalTime.now().toSecondOfDay()

                                            // Se le suma el tiempo estimado de cada articulo manufacturado del pedido
                                            + pedido.getDetallePedidos().stream().mapToLong(detalle -> detalle.getArticulo() instanceof ArticuloManufacturado
                                                    ? (long) ((ArticuloManufacturado) detalle.getArticulo()).getTiempoEstimadoMinutos() * detalle.getCantidad() * 60
                                                    : detalle.getArticulo() instanceof Promocion
                                                    ? ((Promocion) detalle.getArticulo()).getPromocionDetalles().stream().mapToLong(promocionDetalle ->
                                                    promocionDetalle.getArticulo() instanceof ArticuloManufacturado
                                                            ? (long) ((ArticuloManufacturado) promocionDetalle.getArticulo()).getTiempoEstimadoMinutos() * promocionDetalle.getCantidad() * 60 * detalle.getCantidad()
                                                            : 0
                                            ).sum()
                                                    : 0
                                    ).sum()

                                            // Se le suman los tiempos estimados de otros pedidos en cocina / Cantidad de empleados
                                            + (pedidos.stream().mapToLong(p -> p.getHoraEstimadaFinalizacion().toSecondOfDay()
                                            - LocalTime.now().toSecondOfDay()).sum() / empleadosEnProduccion)

                                            // Si es delivery, se le suman 10 minutos
                                            + (pedido.getTipoEnvio() == TipoEnvio.Delivery ? 600 : 0)

                                    // Segundos en un día. si se supera, el pedido pasa al día siguiente
                            ) % 86399
                    )
            );

            // Guardar el pedido
            ResponseEntity<?> response = super.save(pedido);

            // Guardar en tema "pedidos" de la sucursal actual (para websocket)
            messagingTemplate.convertAndSend("/topic/pedidos/" + pedido.getSucursal().getId(), pedido);

            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }
            pedido.getDetallePedidos().forEach(detallePedido -> {
                detallePedido.setPedido(pedido);
            });
            try {
                messagingTemplate.convertAndSend("/topic/pedidos/" + pedido.getSucursal().getId(), pedido);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.OK).body(service.update(pedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    // Descontar stock de insumos
    private void descontarStockInsumo(ArticuloInsumo articuloInsumo, Long sucursalId, double cantidad) {
        StockInsumo stockInsumo = null;
        try {
            stockInsumo = stockInsumoService.findByArticuloInsumoAndSucursal(articuloInsumo.getId(), sucursalId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (stockInsumo != null) {
            stockInsumo.setStockActual(stockInsumo.getStockActual() - (int) cantidad);
        }
    }
}

