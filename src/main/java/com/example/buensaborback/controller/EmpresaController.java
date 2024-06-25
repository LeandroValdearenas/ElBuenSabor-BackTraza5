package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.Empresa;
import com.example.buensaborback.domain.entities.Sucursal;
import com.example.buensaborback.service.EmpresaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/empresas")
public class EmpresaController extends BaseControllerImpl<Empresa, EmpresaServiceImpl> {

    @Autowired
    private EmpresaServiceImpl service;

    protected EmpresaController(EmpresaServiceImpl service) {
        super(service);
    }

    @PutMapping("eliminado/{id}")
    public ResponseEntity<?> eliminadoLogico(@PathVariable Long id) {
        try {
            Empresa searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }

            for (Sucursal sucursal : searchedEntity.getSucursales()) {
                if (!sucursal.getEliminado()) {
                    throw new RuntimeException();
                }
            }
            searchedEntity.setEliminado(!searchedEntity.getEliminado());

            return ResponseEntity.status(HttpStatus.OK).body(service.update(searchedEntity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}

