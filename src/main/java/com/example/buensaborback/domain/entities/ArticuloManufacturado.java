package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@SuperBuilder
@Audited
public class ArticuloManufacturado extends Articulo {

    private String resumen;
    private String descripcion;
    private Integer tiempoEstimadoMinutos;
    @Column(columnDefinition = "TEXT")
    private String preparacion;
    @Transient
    private Integer stockActual;
    @Transient
    private Double precioCosto;

    @OneToMany(mappedBy = "articuloManufacturado", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private Set<ArticuloManufacturadoDetalle> articuloManufacturadoDetalles = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @JoinTable(name = "sucursal_articulo_manufacturado",
            joinColumns = @JoinColumn(name = "articulo_manufacturado_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id"))
    @Builder.Default
    @JsonIgnoreProperties(value = {"nombre", "domicilio"}, allowSetters = true)
    private Set<Sucursal> sucursales = new HashSet<>();

    public static double redondear(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Integer stockCalculado(Long idSucursal) {

        // Se calculan los stocks disponibles de cada ingrediente por la cantidad que se vaya a usar de cada uno
        List<Integer> stocksPorCantidad = articuloManufacturadoDetalles.stream().map(detalle -> {
            Optional<StockInsumo> stockFiltrado = (detalle.getArticuloInsumo()
                    .getStocksInsumo().stream()
                    .filter(stock -> Objects.equals(stock.getSucursal().getId(), idSucursal))
            ).findAny();

            return stockFiltrado.map(stockInsumo -> (int) (stockInsumo.getStockActual() / detalle.getCantidad())).orElse(0);
        }).toList();

        // Se busca el mínimo stock disponible
        Integer stockMinimo = stocksPorCantidad.stream().reduce(Integer.MAX_VALUE, (a, b) -> a < b ? a : b);

        // Si no hay stock, se lanza una excepción
        if (stockMinimo < 0) {
            return 0;
        } else {
            return stockMinimo;
        }
    }

    public Double precioCostoCalculado() {
        // Se calcula el costo de cada ingrediente por la cantidad que se vaya a usar de cada uno
        List<Double> costos = articuloManufacturadoDetalles.stream().map(
                detalle -> (detalle.getArticuloInsumo().getPrecioCompra() * detalle.getCantidad())
        ).toList();

        // Se suman los costos
        Double costo = redondear(costos.stream().reduce(0d, Double::sum));
        return costo;
    }
}
