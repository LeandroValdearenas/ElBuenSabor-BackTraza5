package com.example.buensaborback.service;

import com.example.buensaborback.domain.entities.ArticuloManufacturado;
import com.example.buensaborback.repositories.ArticuloManufacturadoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ArticuloManufacturadoServiceImpl extends BaseServiceImpl<ArticuloManufacturado, Long> implements ArticuloManufacturadoService {

    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;

    public ArticuloManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository) {
        super(articuloManufacturadoRepository);
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
    }

    @Transactional
    public List<ArticuloManufacturado> buscarXSucursalYDenominacionYEliminado(Long idSucursal, String busqueda, boolean soloEliminados) throws Exception {
        try {
            if (idSucursal == null) {
                throw new RuntimeException("El id de la sucursal es obligatorio");
            }

            if (busqueda == null) {
                busqueda = "";
            }

            if (soloEliminados) {
                return articuloManufacturadoRepository.buscarXSucursalYDenominacionEliminados(idSucursal, busqueda.toLowerCase());
            } else {
                return articuloManufacturadoRepository.buscarXSucursalYDenominacion(idSucursal, busqueda.toLowerCase());
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
