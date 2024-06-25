package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Builder
public class ImagenArticulo extends Base {

    private String url;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "articulo_id")
    @JsonBackReference(value = "imagen_articulo")
    private Articulo articulo;
}
