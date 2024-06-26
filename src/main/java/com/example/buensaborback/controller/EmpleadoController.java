package com.example.buensaborback.controller;

import com.example.buensaborback.domain.entities.Empleado;
import com.example.buensaborback.domain.enums.Rol;
import com.example.buensaborback.service.Auth0Service;
import com.example.buensaborback.service.EmpleadoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "api/empleados")
public class EmpleadoController extends BaseControllerImpl<Empleado, EmpleadoServiceImpl> {

    @Autowired
    private Auth0Service auth0Service;

    @Autowired
    private EmpleadoServiceImpl service;

    protected EmpleadoController(EmpleadoServiceImpl service) {
        super(service);
    }

    @PreAuthorize("hasAnyAuthority('administrador', 'superadmin')")
    @GetMapping("buscar")
    public ResponseEntity<?> buscarXSucursalYNombre(@RequestParam(required = false) Long sucursalId, @RequestParam(required = false) String busqueda) {
        try {
            List<Empleado> empleados;
            if (sucursalId == null) sucursalId = 0L;
            if (busqueda == null) busqueda = "";
            empleados = service.buscarXSucursalYNombre(sucursalId, busqueda.toLowerCase());
            return ResponseEntity.status(HttpStatus.OK).body(empleados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @GetMapping("auth0id/{idAuth0}")
    public ResponseEntity<?> buscarXUsuarioAuth0(@PathVariable String idAuth0) {
        try {
            Empleado empleado = service.buscarXUsuarioAuth0(idAuth0);
            if (empleado.getSucursal() == null && empleado.getRol() != Rol.Superadmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.status(HttpStatus.OK).body(empleado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Su cuenta ha sido desactivada\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Empleado empleado) {
        try {
            String password = "DefaultPassword123!";

            // Se crea el usuario en Auth0
            String idAuth0 = (String) auth0Service.createAuth0User(empleado.getEmail(), empleado.getNombre() + " " + empleado.getApellido(), password, empleado.getRol()).get("user_id");
            empleado.getUsuario().setAuth0Id(idAuth0);

            // Se guardan los horarios
            empleado.getHorarios().forEach(horario -> horario.setEmpleado(empleado));
            return ResponseEntity.status(HttpStatus.OK).body(service.save(empleado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Empleado empleado) {
        try {
            Empleado searchedEntity = service.findById(id);
            if (searchedEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. No se encontro la entidad\"}");
            }

            // Actualizar id de auth0 de empleado con el de la base
            empleado.getUsuario().setAuth0Id(searchedEntity.getUsuario().getAuth0Id());

            if (empleado.getRol() != searchedEntity.getRol())
                auth0Service.assignRoles(empleado.getUsuario().getAuth0Id(), empleado.getRol());

            empleado.getHorarios().forEach(horario -> horario.setEmpleado(empleado));
            return ResponseEntity.status(HttpStatus.OK).body(service.update(empleado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente luego\"}");
        }
    }
}