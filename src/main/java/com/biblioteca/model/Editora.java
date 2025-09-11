package com.biblioteca.model;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "editora")
public class Editora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ideditora")
    private Integer idEditora;

    @Column(name = "nomeeditora", nullable = false, length = 100)
    private String nomeEditora;

    @Column(name = "ruaeditora", length = 100)
    private String ruaEditora;

    @Column(name = "numeroeditora", length = 10)
    private String numeroEditora;

    @Column(name = "bairroeditora", length = 50)
    private String bairroEditora;

    @Column(name = "cidadeeditora", length = 50)
    private String cidadeEditora;

    @Column(name = "estadoeditora", length = 20)
    private String estadoEditora;

    @Column(name = "cepeditora", length = 10)
    private String cepEditora;

    @Column(name = "foneeditora", length = 15)
    private String foneEditora;

    @URL(regexp = "^(?i)https?://.+$", message = "Informe uma URL iniciando com http:// ou https://")
    @Column(name = "siteeditora", length = 100) // se preferir, aumente para 200
    private String siteEditora;

    @Column(name = "ativo")
    private Boolean ativo = Boolean.TRUE;

    // ---- Getters/Setters ----

    public Integer getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(Integer idEditora) {
        this.idEditora = idEditora;
    }

    public String getNomeEditora() {
        return nomeEditora;
    }

    public void setNomeEditora(String nomeEditora) {
        this.nomeEditora = nomeEditora;
    }

    public String getRuaEditora() {
        return ruaEditora;
    }

    public void setRuaEditora(String ruaEditora) {
        this.ruaEditora = ruaEditora;
    }

    public String getNumeroEditora() {
        return numeroEditora;
    }

    public void setNumeroEditora(String numeroEditora) {
        this.numeroEditora = numeroEditora;
    }

    public String getBairroEditora() {
        return bairroEditora;
    }

    public void setBairroEditora(String bairroEditora) {
        this.bairroEditora = bairroEditora;
    }

    public String getCidadeEditora() {
        return cidadeEditora;
    }

    public void setCidadeEditora(String cidadeEditora) {
        this.cidadeEditora = cidadeEditora;
    }

    public String getEstadoEditora() {
        return estadoEditora;
    }

    public void setEstadoEditora(String estadoEditora) {
        this.estadoEditora = estadoEditora;
    }

    public String getCepEditora() {
        return cepEditora;
    }

    public void setCepEditora(String cepEditora) {
        this.cepEditora = cepEditora;
    }

    public String getFoneEditora() {
        return foneEditora;
    }

    public void setFoneEditora(String foneEditora) {
        this.foneEditora = foneEditora;
    }

    public String getSiteEditora() {
        return siteEditora;
    }

    public void setSiteEditora(String siteEditora) {
        this.siteEditora = siteEditora;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    // ---- Auxiliar para exibir no list.html ----
    @Transient
    public String getEnderecoCompleto() {
        String num = (numeroEditora != null && !numeroEditora.isBlank()) ? ", " + numeroEditora : "";
        String bairro = (bairroEditora != null && !bairroEditora.isBlank()) ? " - " + bairroEditora : "";
        String cidade = (cidadeEditora != null && !cidadeEditora.isBlank()) ? " - " + cidadeEditora : "";
        String uf = (estadoEditora != null && !estadoEditora.isBlank()) ? "/" + estadoEditora : "";
        String cep = (cepEditora != null && !cepEditora.isBlank()) ? " (CEP " + cepEditora + ")" : "";
        String rua = (ruaEditora != null) ? ruaEditora : "";
        return (rua + num + bairro + cidade + uf + cep).trim();
    }
}
