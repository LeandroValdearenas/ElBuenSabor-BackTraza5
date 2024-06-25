package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Categoria;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends BaseRepository<Categoria, Long> {
    @Query("SELECT c FROM Categoria c INNER JOIN FETCH c.sucursales sc WHERE sc.id = :id")
    List<Categoria> buscarXSucursal(@Param("id") Long id);
}
