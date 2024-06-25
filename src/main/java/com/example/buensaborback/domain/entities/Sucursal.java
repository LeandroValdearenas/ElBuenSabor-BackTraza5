package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Builder
@Audited
public class Sucursal extends Base {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imagen_id")
    @NotAudited
    protected ImagenSucursal imagen;
    private String nombre;
    private Boolean casaMatriz;
    private Boolean eliminado = false;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilio;
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    @ToString.Exclude
    @JsonIgnoreProperties("sucursales")
    private Empresa empresa;
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "sucursal_empleados")
    private Set<Empleado> empleados = new HashSet<>();

    @ManyToMany(mappedBy = "sucursales", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @JsonBackReference(value = "sucursal_categorias")
    private Set<Categoria> categorias = new HashSet<>();

    @ManyToMany(mappedBy = "sucursales")
    @ToString.Exclude
    @JsonBackReference(value = "sucursal_unidadmedida")
    @Builder.Default
    private Set<UnidadMedida> unidadmedidas = new HashSet<>();

    @ManyToMany(mappedBy = "sucursales")
    @ToString.Exclude
    @JsonBackReference(value = "sucursal_articulos_manufacturados")
    @Builder.Default
    private Set<ArticuloManufacturado> articulosManufacturados = new HashSet<>();

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "sucursal_stocks_insumos")
    private Set<StockInsumo> stocksInsumos = new HashSet<>();

    @ManyToMany(mappedBy = "sucursales")
    @ToString.Exclude
    @JsonBackReference(value = "sucursal_promociones")
    @Builder.Default
    private Set<Promocion> promociones = new HashSet<>();

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "sucursal_pedidos")
    private Set<Pedido> pedidos = new HashSet<>();

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<HorarioSucursal> horarios = new HashSet<>();
}
