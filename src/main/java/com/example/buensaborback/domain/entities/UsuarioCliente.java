package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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
public class UsuarioCliente extends Base {

    private String auth0Id;
    private String username;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonBackReference(value = "cliente_usuario")
    private Cliente cliente;
}
