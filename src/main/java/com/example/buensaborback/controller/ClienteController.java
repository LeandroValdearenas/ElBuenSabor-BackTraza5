package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.Cliente;
import com.example.buensaborback.domain.enums.Rol;
import com.example.buensaborback.service.Auth0Service;
import com.example.buensaborback.service.ClienteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/clientes")
public class ClienteController extends BaseControllerImpl<Cliente, ClienteServiceImpl> {

    @Autowired
    private Auth0Service auth0Service;

    @Autowired
    private ClienteServiceImpl service;

    protected ClienteController(ClienteServiceImpl service) {
        super(service);
    }

    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("buscar")
    public ResponseEntity<?> buscarXNombreYEliminado(@RequestParam(required = false) String busqueda, @RequestParam(required = false) boolean mostrarEliminados) {
        try {
            if (busqueda == null) busqueda = "";
            List<Cliente> clientes = service.buscarXNombreYEliminado(busqueda.toLowerCase(), mostrarEliminados);
            return ResponseEntity.status(HttpStatus.OK).body(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PreAuthorize("hasAuthority('cliente')")
    @GetMapping("auth0id/{idAuth0}")
    public ResponseEntity<?> buscarXUsuarioAuth0(@PathVariable String idAuth0) {
        try {
            Cliente cliente = service.buscarXUsuarioAuth0(idAuth0);
            if (cliente.getEliminado()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (cliente == null) {
                throw new RuntimeException();
            }
            return ResponseEntity.status(HttpStatus.OK).body(cliente);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Su cuenta ha sido desactivada\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Cuenta no válida\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @Override
    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Cliente cliente) {
        try {
            Cliente clienteExistente = service.buscarXUsuarioAuth0(cliente.getUsuario().getAuth0Id());

            if (clienteExistente == null) {
                cliente.getUsuario().setCliente(cliente);
                auth0Service.assignRoles(cliente.getUsuario().getAuth0Id(), Rol.Cliente);
                return ResponseEntity.status(HttpStatus.OK).body(service.save(cliente));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Ya existe el usuario en la BD: " + "\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor, intente luego.\"}");
        }
    }

    @PutMapping("eliminado/{id}")
    public ResponseEntity<?> eliminadoLogico(@PathVariable Long id) {
        try {
            Cliente searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }
            searchedEntity.setEliminado(!searchedEntity.getEliminado());
            return ResponseEntity.status(HttpStatus.OK).body(service.update(searchedEntity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}

