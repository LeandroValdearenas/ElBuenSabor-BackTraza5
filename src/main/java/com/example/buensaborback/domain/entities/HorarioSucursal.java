package com.example.buensaborback.domain.entities;

import com.example.buensaborback.domain.enums.Dia;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@SuperBuilder
@Audited
public class HorarioSucursal extends Base {

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    @ToString.Exclude
    @JsonBackReference
    private Sucursal sucursal;

    private Dia diaSemana;

    @OneToMany(mappedBy = "horario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference(value = "horario_detalle")
    private Set<HorarioDetalleSucursal> horarioDetalles = new HashSet<>();

}
