package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Cliente;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ClienteRepository extends BaseRepository<Cliente, Long> {
    @Query(value = "select c from Cliente c where c.eliminado = :mostrarEliminados and concat(LOWER(c.nombre), ' ', LOWER(c.apellido)) LIKE %:busqueda%")
    List<Cliente> buscarXNombreYEliminado(String busqueda, boolean mostrarEliminados);

    @Query(value = "select c from Cliente c where c.usuario.auth0Id = :idAuth0 order by c.id limit 1")
    Cliente buscarXUsuarioAuth0(String idAuth0);
}
