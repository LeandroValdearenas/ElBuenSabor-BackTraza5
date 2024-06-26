package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Empleado;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;


public interface EmpleadoRepository extends BaseRepository<Empleado, Long> {
    @Query(value = "select e from Empleado e where e.sucursal is null and e.rol != 0 and concat(LOWER(e.nombre), ' ', LOWER(e.apellido)) LIKE %:busqueda% order by e.nombre, e.apellido")
    List<Empleado> buscarXNombreEliminados(String busqueda);

    @Query(value = "select e from Empleado e where e.sucursal.id = :sucursalId and concat(LOWER(e.nombre), ' ', LOWER(e.apellido)) LIKE %:busqueda% order by e.nombre, e.apellido")
    List<Empleado> buscarXSucursalYNombre(Long sucursalId, String busqueda);

    @Query(nativeQuery = true, value =
            "select COUNT(e.id) " +
                    "from Empleado e " +
                    "LEFT JOIN Horario_Empleado h on h.empleado_id = e.id " +
                    "LEFT JOIN Horario_Detalle_Empleado hd on hd.horario_id = h.id " +
                    "where e.sucursal_id = :sucursalId " +
                    "and e.rol = 3 " +
                    "and h.dia_semana = :dia " +
                    "and :hora >= hd.hora_inicio " +
                    "and (hd.hora_fin = '00:00:00' OR :hora <= hd.hora_fin)"
    )
    int buscarEmpleadosEnProduccion(Long sucursalId, int dia, LocalTime hora);

    @Query(value = "select e from Empleado e where e.usuario.auth0Id = :idAuth0 order by e.id limit 1")
    Empleado buscarXUsuarioAuth0(String idAuth0);
}
