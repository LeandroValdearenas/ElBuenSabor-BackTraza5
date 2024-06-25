package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.enums.Estado;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface PedidoRepository extends BaseRepository<Pedido, Long> {
    @Query(value = "select p from Pedido p where p.sucursal.id = :idSucursal AND concat(LOWER(p.cliente.nombre), ' ', LOWER(p.cliente.apellido)) LIKE %:busqueda% AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta ORDER BY p.fechaPedido desc,p.id desc")
    List<Pedido> buscarXNombreIdFecha(Long idSucursal, String busqueda, Date desde, Date hasta);

    @Query(value = "select p from Pedido p where p.sucursal.id = :idSucursal AND p.estado = :estado AND concat(LOWER(p.cliente.nombre), ' ', LOWER(p.cliente.apellido)) LIKE %:busqueda% AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta ORDER BY p.fechaPedido desc,p.id desc")
    List<Pedido> buscarXNombreIdFechaEstado(Long idSucursal, String busqueda, Date desde, Date hasta, Estado estado);

    @Query(value = "select p from Pedido p where p.cliente.id = :id AND p.sucursal.id = :idSucursal AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta ORDER BY p.fechaPedido desc,p.id desc ")
    List<Pedido> buscarXClienteYSucursal(Long id, Long idSucursal, Date desde, Date hasta);

    // RANKING PRODUCTOS
    @Query(value = "select a.denominacion as producto, COUNT(p) as ventas from Articulo a" +
            " left join DetallePedido d ON d.articulo.id = a.id left join Pedido p ON p.id = d.pedido.id" +
            " where (:idCategoria = 0 OR a.categoria.id = :idCategoria) AND p.sucursal.id = :idSucursal" +
            " AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta GROUP BY a.id ORDER BY ventas DESC limit 10")
    List<Object[]> rankingProductos(Long idCategoria, Long idSucursal, Date desde, Date hasta);

    // RANKING CLIENTES ORDENADO POR PEDIDO
    @Query(value = "select CONCAT(c.nombre, ' ', c.apellido) as nombreApellido, c.id as id, COUNT(p) as pedidos, SUM(p.total) as total" +
            " from Cliente c left join Pedido p ON c.id = p.cliente.id where p.sucursal.id = :idSucursal" +
            " AND c.eliminado = false AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta" +
            " GROUP BY c.id ORDER BY pedidos DESC, c.nombre, c.apellido limit :limite")
    List<Object[]> rankingClientesOrdenadoPedidos(Long limite, Long idSucursal, Date desde, Date hasta);

    // RANKING CLIENTES ORDENADO POR MONTO
    @Query(value = "select CONCAT(c.nombre, ' ', c.apellido) as nombreApellido, c.id as id, COUNT(p) as pedidos, SUM(p.total) as total" +
            " from Cliente c left join Pedido p ON c.id = p.cliente.id where p.sucursal.id = :idSucursal" +
            " AND c.eliminado = false AND p.fechaPedido >= :desde AND p.fechaPedido <= :hasta" +
            " GROUP BY c.id ORDER BY total DESC, c.nombre, c.apellido limit :limite")
    List<Object[]> rankingClientesOrdenadoMonto(Long limite, Long idSucursal, Date desde, Date hasta);

    // MOVIMIENTOS ESTADÍSTICOS
    @Query(value = "SELECT CONCAT(YEAR(p.fechaPedido), '-', MONTH(p.fechaPedido)) as mes, " +
            "SUM(p.total) as ingresos, " +
            "SUM(p.totalCosto) as costos, " +
            "SUM(p.total - p.totalCosto) as ganancias " +
            "FROM Pedido p " +
            "WHERE p.sucursal.id = :idSucursal " +
            "AND p.fechaPedido >= :desde " +
            "AND p.fechaPedido <= :hasta " +
            "GROUP BY mes " +
            "ORDER BY mes")
    List<Object[]> movimientosMonetariosMensual(Long idSucursal, Date desde, Date hasta);

    // MOVIMIENTOS ESTADÍSTICOS
    @Query(value = "SELECT p.fechaPedido as dia, " +
            "SUM(p.total) as ingresos, " +
            "SUM(p.totalCosto) as costos, " +
            "SUM(p.total - p.totalCosto) as ganancias " +
            "FROM Pedido p " +
            "WHERE p.sucursal.id = :idSucursal " +
            "AND p.fechaPedido >= :desde " +
            "AND p.fechaPedido <= :hasta " +
            "GROUP BY dia " +
            "ORDER BY dia")
    List<Object[]> movimientosMonetariosDiario(Long idSucursal, Date desde, Date hasta);
}
