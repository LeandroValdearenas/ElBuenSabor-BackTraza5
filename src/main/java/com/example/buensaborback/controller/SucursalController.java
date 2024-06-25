package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.Sucursal;
import com.example.buensaborback.service.SucursalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/sucursales")
public class SucursalController extends BaseControllerImpl<Sucursal, SucursalServiceImpl> {

    @Autowired
    private SucursalServiceImpl service;

    public SucursalController(SucursalServiceImpl service) {
        super(service);
    }

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Sucursal entity) {
        try {
            entity.getHorarios().forEach(horario -> horario.setSucursal(entity));
            return ResponseEntity.status(HttpStatus.OK).body(service.save(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PutMapping("eliminado/{id}")
    public ResponseEntity<?> eliminadoLogico(@PathVariable Long id) {
        try {
            Sucursal searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }
            searchedEntity.setEliminado(!searchedEntity.getEliminado());

            return ResponseEntity.status(HttpStatus.OK).body(service.update(searchedEntity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Sucursal entity) {
        try {
            Sucursal searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }
            entity.getHorarios().forEach(horario -> horario.setSucursal(entity));
            return ResponseEntity.status(HttpStatus.OK).body(service.update(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}

