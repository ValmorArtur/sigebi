// src/main/java/com/biblioteca/model/Categoria.java
package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria")
    private Integer idCategoria;

    @NotBlank(message = "A descrição da categoria é obrigatória.")
    @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres.")
    @Column(name = "descricao", nullable = false, length = 50)
    private String descricao;

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
