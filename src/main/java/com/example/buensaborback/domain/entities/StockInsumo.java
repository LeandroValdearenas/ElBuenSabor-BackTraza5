package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.envers.Audited;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Audited
public class StockInsumo extends Base {

    private Integer stockActual;
    private Integer stockMaximo;
    private Integer stockMinimo;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "articulo_insumo_id")
    @JsonBackReference(value = "stock_insumo_articulo_insumo")
    private ArticuloInsumo articuloInsumo;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "sucursal_id")
    @JsonIgnoreProperties(value = {"domicilio", "promociones", "horarios", "casaMatriz", "empresa", "imagen"}, allowSetters = true)
    private Sucursal sucursal;
}
