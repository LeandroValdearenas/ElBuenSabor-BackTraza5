package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@SuperBuilder
@Audited
public class ArticuloInsumo extends Articulo {

    private Double precioCompra;
    private Boolean esParaElaborar;

    @OneToMany(mappedBy = "articuloInsumo", fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "insumo_manufacturadodetalles")
    private Set<ArticuloManufacturadoDetalle> articuloManufacturadoDetalles = new HashSet<>();


    @OneToMany(mappedBy = "articuloInsumo", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StockInsumo> stocksInsumo = new HashSet<>();
}
