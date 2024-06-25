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
public class UnidadMedida extends Base {

    private String denominacion;

    @OneToMany(mappedBy = "unidadMedida")
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "unidadmedida_articulos")
    private Set<Articulo> articulos = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @JoinTable(name = "sucursal_unidadmedida",
            joinColumns = @JoinColumn(name = "unidadmedida_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id"))
    @Builder.Default
    @JsonIgnoreProperties(value = {"nombre", "domicilio",}, allowSetters = true)
    private Set<Sucursal> sucursales = new HashSet<>();
}
