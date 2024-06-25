package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.ArticuloManufacturado;
import com.example.buensaborback.service.ArticuloManufacturadoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/manufacturados")
public class ArticuloManufacturadoController extends BaseControllerImpl<ArticuloManufacturado, ArticuloManufacturadoServiceImpl> {

    @Autowired
    protected ArticuloManufacturadoServiceImpl service;

    protected ArticuloManufacturadoController(ArticuloManufacturadoServiceImpl service) {
        super(service);
    }

    @GetMapping("buscar")
    public ResponseEntity<?> buscarXSucursalYDenominacionYEliminado(@RequestParam(required = false) Long sucursalId, @RequestParam(required = false) String busqueda, @RequestParam(required = false) boolean soloEliminados) {
        try {
            List<ArticuloManufacturado> articulosManufacturados;

            articulosManufacturados = service.buscarXSucursalYDenominacionYEliminado(sucursalId, busqueda, soloEliminados);

            // Se calcula el costo y el stock en base a la sucursal
            for (ArticuloManufacturado articulo : articulosManufacturados) {
                articulo.setPrecioCosto(articulo.precioCostoCalculado());
                if (sucursalId != null) {
                    articulo.setStockActual(articulo.stockCalculado(sucursalId));
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(articulosManufacturados);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @Override
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody ArticuloManufacturado manufacturado) {
        try {
            manufacturado.getImagenes().forEach(imagen -> imagen.setArticulo(manufacturado));
            manufacturado.getArticuloManufacturadoDetalles().forEach(detalle -> detalle.setArticuloManufacturado(manufacturado));
            return ResponseEntity.status(HttpStatus.OK).body(service.save(manufacturado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloManufacturado manufacturado) {
        try {
            manufacturado.getImagenes().forEach(imagen -> imagen.setArticulo(manufacturado));
            manufacturado.getArticuloManufacturadoDetalles().forEach(detalle -> detalle.setArticuloManufacturado(manufacturado));

            ArticuloManufacturado searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }

            return ResponseEntity.status(HttpStatus.OK).body(service.update(manufacturado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}

