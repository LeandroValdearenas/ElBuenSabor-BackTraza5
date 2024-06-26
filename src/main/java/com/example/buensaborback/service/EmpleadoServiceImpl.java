package com.example.buensaborback.service;

import com.example.buensaborback.domain.entities.Empleado;
import com.example.buensaborback.repositories.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Service
public class EmpleadoServiceImpl extends BaseServiceImpl<Empleado, Long> implements EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        super(empleadoRepository);
    }

    @Transactional
    public List<Empleado> buscarXSucursalYNombre(Long sucursalId, String busqueda) throws Exception {
        try {
            if (sucursalId == 0L) {
                return empleadoRepository.buscarXNombreEliminados(busqueda);
            } else {
                return empleadoRepository.buscarXSucursalYNombre(sucursalId, busqueda);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Empleado buscarXUsuarioAuth0(String busqueda) throws Exception {
        try {
            return empleadoRepository.buscarXUsuarioAuth0(busqueda);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public int buscarEmpleadosEnProduccion(Long sucursalId, Date fecha) throws Exception {
        try {
            LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Se obtiene el día de la semana
            int diaSemana = localDate.getDayOfWeek().getValue();

            // Se obtiene la hora
            LocalTime hora = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            return empleadoRepository.buscarEmpleadosEnProduccion(sucursalId, diaSemana, hora);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
