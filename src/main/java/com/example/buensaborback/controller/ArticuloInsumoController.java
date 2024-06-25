package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.ArticuloInsumo;
import com.example.buensaborback.service.ArticuloInsumoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/insumos")
public class ArticuloInsumoController extends BaseControllerImpl<ArticuloInsumo, ArticuloInsumoServiceImpl> {

    @Autowired
    protected ArticuloInsumoServiceImpl service;

    protected ArticuloInsumoController(ArticuloInsumoServiceImpl service) {
        super(service);
    }

    @GetMapping("buscar")
    public ResponseEntity<?> buscarXDenominacion(@RequestParam(required = false) String busqueda) {
        try {
            if (busqueda == null) {
                busqueda = "";
            }
            return ResponseEntity.status(HttpStatus.OK).body(service.buscarXDenominacion(busqueda.toLowerCase()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @Override
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody ArticuloInsumo insumo) {
        try {
            insumo.getImagenes().forEach(imagen -> imagen.setArticulo(insumo));
            insumo.getStocksInsumo().forEach(stock -> stock.setArticuloInsumo(insumo));
            return ResponseEntity.status(HttpStatus.OK).body(service.save(insumo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloInsumo insumo) {
        try {
            insumo.getImagenes().forEach(imagen -> imagen.setArticulo(insumo));
            insumo.getStocksInsumo().forEach(stock -> stock.setArticuloInsumo(insumo));
            ArticuloInsumo searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }
            return ResponseEntity.status(HttpStatus.OK).body(service.update(insumo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}

