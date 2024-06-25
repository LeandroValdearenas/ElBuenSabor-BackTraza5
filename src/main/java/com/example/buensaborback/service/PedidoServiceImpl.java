package com.example.buensaborback.service;

import com.example.buensaborback.domain.entities.Pedido;
import com.example.buensaborback.domain.enums.Estado;
import com.example.buensaborback.repositories.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;


@Service
public class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        super(pedidoRepository);
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public List<Pedido> buscarXNombreIdFecha(Long idSucursal, String busqueda, String desde, String hasta, String estado) throws Exception {
        try {
            if (busqueda == null)
                busqueda = "";
            if (desde == null)
                desde = "2000-01-01";
            if (hasta == null)
                hasta = "2999-01-01";

            if (estado == null)
                return pedidoRepository.buscarXNombreIdFecha(idSucursal, busqueda.toLowerCase(), Date.valueOf(desde), Date.valueOf(hasta));
            else
                return pedidoRepository.buscarXNombreIdFechaEstado(idSucursal, busqueda.toLowerCase(), Date.valueOf(desde), Date.valueOf(hasta), Estado.valueOf(estado));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Pedido> buscarXClienteYSucursal(Long id, Long idSucursal, String desde, String hasta) throws Exception {
        try {
            if (desde == null)
                desde = "2000-01-01";
            if (hasta == null)
                hasta = "2999-01-01";
            return pedidoRepository.buscarXClienteYSucursal(id, idSucursal, Date.valueOf(desde), Date.valueOf(hasta));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Object[]> rankingProductos(Long idCategoria, Long idSucursal, String desde, String hasta) throws Exception {
        try {
            if (idCategoria == null)
                idCategoria = 0L;
            if (desde == null)
                desde = "2000-01-01";
            if (hasta == null)
                hasta = "2999-01-01";
            return pedidoRepository.rankingProductos(idCategoria, idSucursal, Date.valueOf(desde), Date.valueOf(hasta));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Object[]> rankingClientes(Long limite, Long idSucursal, String desde, String hasta, boolean ordenarPorPedidos) throws Exception {
        try {
            if (limite == null)
                limite = 5L;
            if (desde == null)
                desde = "2000-01-01";
            if (hasta == null)
                hasta = "2999-01-01";
            if (ordenarPorPedidos) {
                return pedidoRepository.rankingClientesOrdenadoPedidos(limite, idSucursal, Date.valueOf(desde), Date.valueOf(hasta));
            } else {
                return pedidoRepository.rankingClientesOrdenadoMonto(limite, idSucursal, Date.valueOf(desde), Date.valueOf(hasta));
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Object[]> movimientosMonetarios(Long idSucursal, String desde, String hasta, boolean esDiario) throws Exception {
        try {
            if (desde == null)
                desde = "2000-01-01";
            if (hasta == null)
                hasta = "2999-01-01";
            if (esDiario)
                return pedidoRepository.movimientosMonetariosDiario(idSucursal, Date.valueOf(desde), Date.valueOf(hasta));
            else
                return pedidoRepository.movimientosMonetariosMensual(idSucursal, Date.valueOf(desde), Date.valueOf(hasta));

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
