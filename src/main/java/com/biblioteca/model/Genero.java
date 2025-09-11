// src/main/java/com/biblioteca/model/Genero.java
package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "genero")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idgenero")
    private Integer idGenero;

    @NotBlank(message = "A descrição do gênero é obrigatória.")
    @Size(max = 50, message = "A descrição deve ter no máximo 50 caracteres.")
    @Column(name = "descricaogenero", nullable = false, length = 50) // <-- nome correto no banco
    private String descricao;

    @Column(name = "ficcao", nullable = false)
    private boolean ficcao;

    public Genero() { }

    public Genero(Integer idGenero, String descricao, boolean ficcao) {
        this.idGenero = idGenero;
        this.descricao = descricao;
        this.ficcao = ficcao;
    }

    public Integer getIdGenero() { return idGenero; }
    public void setIdGenero(Integer idGenero) { this.idGenero = idGenero; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isFiccao() { return ficcao; }
    public void setFiccao(boolean ficcao) { this.ficcao = ficcao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genero)) return false;
        Genero genero = (Genero) o;
        return Objects.equals(idGenero, genero.idGenero);
    }

    @Override
    public int hashCode() { return Objects.hash(idGenero); }

    @Override
    public String toString() {
        return "Genero{" +
                "idGenero=" + idGenero +
                ", descricao='" + descricao + '\'' +
                ", ficcao=" + ficcao +
                '}';
    }
}
