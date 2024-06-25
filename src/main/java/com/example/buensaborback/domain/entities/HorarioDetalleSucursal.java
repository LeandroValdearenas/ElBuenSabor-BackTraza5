package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@SuperBuilder
@Audited
public class HorarioDetalleSucursal extends Base {

    @ManyToOne
    @JoinColumn(name = "horario_id")
    @ToString.Exclude
    @JsonBackReference(value = "horario_detalle")
    private HorarioSucursal horario;

    private LocalTime horaInicio;

    private LocalTime horaFin;

}