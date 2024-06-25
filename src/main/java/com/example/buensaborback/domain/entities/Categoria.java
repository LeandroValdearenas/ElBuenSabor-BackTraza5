package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

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
public class Categoria extends Base {

    private String denominacion;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "categoria_padre_id")
    @JsonIgnoreProperties(value = {"subCategorias", "categoriaPadre"}, allowSetters = true)
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Categoria> subCategorias = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @JoinTable(name = "sucursal_categoria",
            joinColumns = @JoinColumn(name = "categoria_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id"))
    @Builder.Default
    @JsonIgnoreProperties(value = {"nombre", "domicilio", "horarios", "casaMatriz", "empresa", "imagen"}, allowSetters = true)
    private Set<Sucursal> sucursales = new HashSet<>();

    @OneToMany(mappedBy = "categoria")
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "categoria_articulos")
    private Set<Articulo> articulos = new HashSet<>();
}
