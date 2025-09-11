package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "acervo")
public class Acervo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idacervo")
    private Integer idAcervo;

    // Guarde ISBN como texto para suportar dígito 'X' no ISBN-10 (veja ALTER
    // sugerido)
    @NotBlank(message = "O ISBN é obrigatório.")
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Informe um ISBN-10 ou ISBN-13 (somente dígitos; no ISBN-10 o último pode ser X).")
    @Column(name = "isbn", unique = true, length = 13, nullable = false)
    private String isbn;

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 50)
    @Column(name = "tituloacervo", length = 50, nullable = false)
    private String tituloAcervo;

    @Size(max = 50)
    @Column(name = "subtituloacervo", length = 50)
    private String subTituloAcervo;

    @Min(1)
    @Column(name = "numedicaoacervo")
    private Integer numEdicaoAcervo;

    @Min(1000)
    @Max(2100)
    @Column(name = "anopublicacaoacervo")
    private Integer anoPublicacaoAcervo;

    @Size(max = 200)
    @Column(name = "resumoacervo", length = 200)
    private String resumoAcervo;

    @Column(name = "proibidomenor")
    private Boolean proibidoMenor = Boolean.FALSE;

    @Min(1)
    @Column(name = "qtdpaginas")
    private Integer qtdPaginas;

    @Min(1)
    @Column(name = "numvolumeacervo")
    private Integer numVolumeAcervo;

    @Size(max = 20)
    @Column(name = "corcapa", length = 20)
    private String corCapa;

    @Column(name = "ativo")
    private Boolean ativo = Boolean.TRUE;

    // ---- FKs diretas (seu SQL) ----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ideditora")
    private Editora editora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idgenero")
    private Genero genero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategoria")
    private Categoria categoria;

    // ---- Autores: many-to-many (tabela escreve) ----
    @ManyToMany
    @JoinTable(name = "escreve", // use a sua join-table real
            joinColumns = @JoinColumn(name = "idacervo"), inverseJoinColumns = @JoinColumn(name = "idautor"))
    @OrderBy("nomeAutor ASC")
    private java.util.Set<Autor> autores = new java.util.LinkedHashSet<>();


    // getters/setters
    public Integer getIdAcervo() {
        return idAcervo;
    }

    public void setIdAcervo(Integer idAcervo) {
        this.idAcervo = idAcervo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = (isbn == null ? null : isbn.trim());
    }

    public String getTituloAcervo() {
        return tituloAcervo;
    }

    public void setTituloAcervo(String tituloAcervo) {
        this.tituloAcervo = tituloAcervo;
    }

    public String getSubTituloAcervo() {
        return subTituloAcervo;
    }

    public void setSubTituloAcervo(String subTituloAcervo) {
        this.subTituloAcervo = subTituloAcervo;
    }

    public Integer getNumEdicaoAcervo() {
        return numEdicaoAcervo;
    }

    public void setNumEdicaoAcervo(Integer numEdicaoAcervo) {
        this.numEdicaoAcervo = numEdicaoAcervo;
    }

    public Integer getAnoPublicacaoAcervo() {
        return anoPublicacaoAcervo;
    }

    public void setAnoPublicacaoAcervo(Integer anoPublicacaoAcervo) {
        this.anoPublicacaoAcervo = anoPublicacaoAcervo;
    }

    public String getResumoAcervo() {
        return resumoAcervo;
    }

    public void setResumoAcervo(String resumoAcervo) {
        this.resumoAcervo = resumoAcervo;
    }

    public Boolean getProibidoMenor() {
        return proibidoMenor;
    }

    public void setProibidoMenor(Boolean proibidoMenor) {
        this.proibidoMenor = proibidoMenor;
    }

    public Integer getQtdPaginas() {
        return qtdPaginas;
    }

    public void setQtdPaginas(Integer qtdPaginas) {
        this.qtdPaginas = qtdPaginas;
    }

    public Integer getNumVolumeAcervo() {
        return numVolumeAcervo;
    }

    public void setNumVolumeAcervo(Integer numVolumeAcervo) {
        this.numVolumeAcervo = numVolumeAcervo;
    }

    public String getCorCapa() {
        return corCapa;
    }

    public void setCorCapa(String corCapa) {
        this.corCapa = corCapa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public void setAutores(java.util.Set<Autor> autores) {
        this.autores = autores;
    }
    
    public java.util.Set<Autor> getAutores() {
        return autores;
    }
}
