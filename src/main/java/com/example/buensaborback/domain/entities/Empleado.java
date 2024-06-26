package com.example.buensaborback.domain.entities;

import com.example.buensaborback.domain.enums.Rol;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
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
public class Empleado extends Base {

    private Rol rol;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference(value = "empleado_pedidos")
    private Set<Pedido> pedidos = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilio;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private UsuarioEmpleado usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imagen_id")
    @NotAudited
    private ImagenEmpleado imagen;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    @JsonIgnoreProperties(value = {"categorias", "promociones"}, allowSetters = true)
    private Sucursal sucursal;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<HorarioEmpleado> horarios = new HashSet<>();

}
