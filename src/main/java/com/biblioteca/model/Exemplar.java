package com.biblioteca.model;

import com.biblioteca.model.enums.SituacaoExemplar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "exemplar")
public class Exemplar {

    @Id
    @Column(name = "idtombo", length = 20, nullable = false)
    private String idTombo;

    @NotNull(message = "O acervo é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idacervo", nullable = false)
    private Acervo acervo;

    @NotNull(message = "A situação é obrigatória.")
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoExemplar situacao = SituacaoExemplar.DISPONIVEL;

    @Size(max = 255)
    @Column(name = "observacao", length = 255)
    private String observacao;

    //marcar updatable=false no criado_em para o JPA não mexer após persist
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
        if (this.situacao == null) {
            this.situacao = SituacaoExemplar.DISPONIVEL;
        }
        // >>> NADA aqui para idTombo: ele é gerado no Service antes do save()
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }


    // getters e setters
    public String getIdTombo() {
        return idTombo;
    }

    public void setIdTombo(String idTombo) {
        this.idTombo = idTombo;
    }

    public Acervo getAcervo() {
        return acervo;
    }

    public void setAcervo(Acervo acervo) {
        this.acervo = acervo;
    }

    public SituacaoExemplar getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoExemplar situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exemplar)) return false;
        Exemplar that = (Exemplar) o;
        return Objects.equals(idTombo, that.idTombo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTombo);
    }
}
