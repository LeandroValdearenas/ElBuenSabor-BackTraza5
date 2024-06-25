package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.envers.Audited;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Builder
@Audited
public class DetallePedido extends Base {

    private Integer cantidad;
    private Double subTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "articulo_id")
    @JsonIgnoreProperties(value = {"imagenes", "unidadMedida", "categoria", "sucursales"}, allowSetters = true)
    private Articulo articulo;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "pedido_id")
    @JsonBackReference(value = "detallepedido_pedido")
    private Pedido pedido;
}
