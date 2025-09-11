package com.biblioteca.model;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Integer idUsuario;

    @Column(name = "loginusuario", unique = true)
    private String loginUsuario;

    @Column(name = "nomeusuario")
    private String nomeUsuario;

    @Column(name = "emailusuario", unique = true)
    private String emailUsuario;

    @Column(name = "foneusuario")
    private String foneUsuario;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "senhausuario")
    private String senhaUsuario;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "administrador")
    private Boolean administrador;

    @Transient
    private String confirmaSenha;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Endereco> enderecos;

    public Usuario() {
        this.ativo = true;
        this.administrador = false;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getFoneUsuario() {
        return foneUsuario;
    }

    public void setFoneUsuario(String foneUsuario) {
        this.foneUsuario = foneUsuario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }
    
    @Transient
    public String getAtivoFormatado() {
        return Boolean.TRUE.equals(this.ativo) ? "Ativo" : "Inativo";
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Boolean getAdministrador() {
        return administrador;
    }

    @Transient
    public String getAdministradorFormatado() {
        return Boolean.TRUE.equals(this.administrador) ? "Sim" : "Não";
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public String getConfirmaSenha() {
        return confirmaSenha;
    }

    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }
}