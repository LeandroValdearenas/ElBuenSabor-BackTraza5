package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.ArticuloManufacturado;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticuloManufacturadoRepository extends BaseRepository<ArticuloManufacturado, Long> {
    @Query(nativeQuery = true, value =
            "select a.* from Articulo_Manufacturado a " +
                    "LEFT JOIN Sucursal_Articulo_Manufacturado sa ON a.id = sa.articulo_manufacturado_id " +
                    "WHERE sa.sucursal_id = :idSucursal " +
                    "AND LOWER(a.denominacion) LIKE %:busqueda% " +
                    "ORDER BY a.id DESC")
    List<ArticuloManufacturado> buscarXSucursalYDenominacion(Long idSucursal, String busqueda);

    @Query(nativeQuery = true, value =
            "select a.* from Articulo_Manufacturado a " +
                    "WHERE a.id NOT IN (" +
                    "  SELECT sa.articulo_manufacturado_id " +
                    "  FROM Sucursal_Articulo_Manufacturado sa " +
                    "  WHERE sa.sucursal_id = :idSucursal" +
                    ") AND LOWER(a.denominacion) LIKE %:busqueda% " +
                    "ORDER BY a.id DESC")
    List<ArticuloManufacturado> buscarXSucursalYDenominacionEliminados(Long idSucursal, String busqueda);

    @Query(value = "select a from ArticuloManufacturado a where " +
            "LOWER(a.denominacion) LIKE %:busqueda%  " +
            "ORDER BY a.id DESC")
    List<ArticuloManufacturado> buscarXDenominacion(String busqueda);
}
