package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idautor")
    private Integer idAutor;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nomeautor", length = 100)
    private String nomeAutor;

    @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres.")
    @Column(name = "descricaoautor", length = 200)
    private String descricaoAutor;

    public Integer getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getDescricaoAutor() {
        return descricaoAutor;
    }

    public void setDescricaoAutor(String descricaoAutor) {
        this.descricaoAutor = descricaoAutor;
    }

    // equals/hashCode por ID para funcionar bem com Set<> e th:selected
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Autor))
            return false;
        Autor a = (Autor) o;
        return Objects.equals(idAutor, a.idAutor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAutor);
    }
}
