package com.biblioteca.service;

import com.biblioteca.model.Editora;
import com.biblioteca.repository.EditoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

@Service
public class EditoraService {

    @Autowired
    private EditoraRepository repo;

    public Page<Editora> listar(String filtro, int page, int size){
        Pageable p = PageRequest.of(page, size, Sort.by("nomeEditora").ascending());

        if (filtro == null || filtro.isBlank()) {
            return repo.findAll(p);
        }

        String f = filtro.trim();
        Boolean filtroBool = parseAtivo(f); // "sim/ativo/true/1" e "não/inativo/false/0"
        return repo.searchAll(f, filtroBool, p);
    }

    public Editora buscar(Integer id){
        return repo.findById(id).orElseThrow();
    }

    public Editora salvar(Editora e){
        // --- Normalização de SITE centralizada ---
        String site = e.getSiteEditora();
        if (site != null) {
            site = site.trim();
            if (site.isEmpty()) {
                // string vazia vira NULL -> passa na validação
                e.setSiteEditora(null);
            } else if (!site.matches("(?i)^https?://.*$")) {
                // prefixa https:// quando faltar protocolo
                e.setSiteEditora("https://" + site);
            } else {
                e.setSiteEditora(site);
            }
        }
        return repo.save(e);
    }

    public void excluir(Integer id){
        repo.deleteById(id);
    }

    // suporte a busca por "Sim/Não", "Ativo/Inativo", "true/false", "1/0"
    private Boolean parseAtivo(String s){
        String t = s.toLowerCase();
        if (t.equals("sim") || t.equals("ativo") || t.equals("true") || t.equals("1")) return Boolean.TRUE;
        if (t.equals("nao") || t.equals("não") || t.equals("inativo") || t.equals("false") || t.equals("0")) return Boolean.FALSE;
        return null;
    }
    
    public List<Editora> listarTodos() {
        return repo.findAll();
    }
}
