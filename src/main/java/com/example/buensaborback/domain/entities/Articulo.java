package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArticuloInsumo.class, name = "insumo"),
        @JsonSubTypes.Type(value = ArticuloManufacturado.class, name = "manufacturado"),
        @JsonSubTypes.Type(value = Promocion.class, name = "promocion")
})
public abstract class Articulo extends Base {

    protected String denominacion;
    protected Double precioVenta;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @NotAudited
    @Builder.Default
    protected Set<ImagenArticulo> imagenes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "UnidadMedida_ID")
    @JsonIgnoreProperties(value = {"sucursales"}, allowSetters = true)
    protected UnidadMedida unidadMedida;

    @ManyToOne
    @JoinColumn(name = "Categoria_ID")
    @JsonIgnoreProperties(value = {"subCategorias", "categoriaPadre", "sucursales", "articulos"}, allowSetters = true)
    protected Categoria categoria;

//    @OneToMany(mappedBy = "articulo")
//    @ToString.Exclude
//    @Builder.Default
//    @JsonBackReference(value = "articulo_promociondetalles")
//    protected Set<PromocionDetalle> promocionDetalles = new HashSet<>();

    @OneToMany(mappedBy = "articulo")
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "articulo_detallepedidos")
    protected Set<DetallePedido> detallePedidos = new HashSet<>();

}
