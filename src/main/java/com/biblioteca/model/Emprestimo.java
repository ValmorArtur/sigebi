package com.biblioteca.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emprestimo")
public class Emprestimo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idemprestimo")
    private Integer idEmprestimo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idtombo", referencedColumnName = "idTombo")
    private Exemplar exemplar;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario")
    private Usuario usuario;

    @Column(name = "dataemprestimo", nullable = false)
    private LocalDate dataEmprestimo = LocalDate.now();

    @Column(name = "dataprevistadevolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "datadevolucao")
    private LocalDate dataDevolucao;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private boolean reserva = false;

    // getters/setters
    // helper: status derivado
    @Transient
    public boolean isAtivo() {
        return dataDevolucao == null;
    }

    // ===== getters/setters =====
    public Integer getIdEmprestimo() { return idEmprestimo; }
    public void setIdEmprestimo(Integer idEmprestimo) { this.idEmprestimo = idEmprestimo; }

    public Exemplar getExemplar() { return exemplar; }
    public void setExemplar(Exemplar exemplar) { this.exemplar = exemplar; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) { this.dataPrevistaDevolucao = dataPrevistaDevolucao; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isReserva() { return reserva; }
    public void setReserva(boolean reserva) { this.reserva = reserva; }
}
