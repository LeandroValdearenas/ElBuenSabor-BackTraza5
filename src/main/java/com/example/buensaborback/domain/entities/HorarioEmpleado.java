package com.example.buensaborback.domain.entities;

import com.example.buensaborback.domain.enums.Dia;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class HorarioEmpleado extends Base {

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    @ToString.Exclude
    @JsonIgnoreProperties({"rol", "domicilio", "nombre", "apellido", "telefono", "email", "fechaNacimiento", "usuario", "imagen", "sucursal", "horarios"})
    private Empleado empleado;

    private Dia diaSemana;

    @OneToMany(mappedBy = "horario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference(value = "horario_detalle")
    private Set<HorarioDetalleEmpleado> horarioDetalles = new HashSet<>();

}
