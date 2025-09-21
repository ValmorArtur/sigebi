// src/main/java/com/biblioteca/service/GeneroService.java
package com.biblioteca.service;

import com.biblioteca.model.Genero;
import com.biblioteca.repository.AcervoRepository;
import com.biblioteca.repository.GeneroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeneroService {

    @Autowired
    private GeneroRepository repo;

    @Autowired
    private AcervoRepository acervoRepo;

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
        long qtd = acervoRepo.countByGenero_IdGenero(id);
        if (qtd > 1) {
            // Mensagem clara para o usuário (vai para o flash no controller)
            throw new IllegalStateException(
                "Não é possível excluir o Gênero: existem " + qtd + " itens do acervo relacionados a ela."
            );
        } else if (qtd == 1) {
            // Mensagem clara para o usuário (vai para o flash no controller)
            throw new IllegalStateException(
                "Não é possível excluir a Gênero: existe " + qtd + " item do acervo relacionados a ela."
            );
        }

        try {
            repo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Se alguma outra FK/constraint disparar, padronizamos a mensagem
            throw new IllegalStateException(
                "Não é possível excluir o Gênero porque há registros relacionados.", e
            );
        }
    }

    public List<Genero> listarTodos() {
        return repo.findAll();
    }

}
