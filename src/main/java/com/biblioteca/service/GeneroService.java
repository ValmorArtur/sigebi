// src/main/java/com/biblioteca/service/GeneroService.java
package com.biblioteca.service;

import com.biblioteca.model.Genero;
import com.biblioteca.repository.GeneroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeneroService {

    @Autowired
    private GeneroRepository repo;

    /**
     * Novo padrão: usado pelo controller que trabalha com Pageable direto.
     */
    public Page<Genero> listarPaginado(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return repo.findAll(pageable);
        }
        return repo.findByDescricaoContainingIgnoreCase(filtro, pageable);
    }

    /**
     * Compatibilidade: mantém a assinatura antiga e delega para o novo.
     */
    public Page<Genero> listar(String filtro, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("descricao").ascending());
        return listarPaginado(filtro, p);
    }

    /**
     * Novo nome usado no controller atualizado (retorna null se não encontrar).
     */
    public Genero buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Assinatura antiga (mantida). Lança NoSuchElementException se não encontrar.
     */
    public Genero buscar(Integer id) {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    public Genero salvar(Genero g) {
        return repo.save(g);
    }

    @Transactional
    public void excluir(Integer id) {
        repo.deleteById(id);
    }

    public List<Genero> listarTodos() {
        return repo.findAll();
    }

}
