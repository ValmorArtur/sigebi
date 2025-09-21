package com.biblioteca.service;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import java.util.List;
import com.biblioteca.repository.AcervoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private AcervoRepository acervoRepo;


    public Page<Categoria> listar(String filtro, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("descricao").ascending());
        return (filtro == null || filtro.isBlank())
                ? categoriaRepo.findAll(p)
                : categoriaRepo.findByDescricaoContainingIgnoreCase(filtro, p);
    }

    public List<Categoria> listarTodos() {
        return categoriaRepo.findAll();
    }

    public Categoria buscar(Integer id) {
        return categoriaRepo.findById(id).orElseThrow();
    }

    public Categoria salvar(Categoria c) {
        return categoriaRepo.save(c);
    }

    @Transactional
    public void excluir(Integer id) {
        long qtd = acervoRepo.countByCategoria_IdCategoria(id);
        if (qtd > 1) {
            // Mensagem clara para o usuário (vai para o flash no controller)
            throw new IllegalStateException(
                "Não é possível excluir a categoria: existem " + qtd + " itens do acervo relacionados a ela."
            );
        } else if (qtd == 1) {
            // Mensagem clara para o usuário (vai para o flash no controller)
            throw new IllegalStateException(
                "Não é possível excluir a categoria: existe " + qtd + " item do acervo relacionados a ela."
            );
        }

        try {
            categoriaRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Se alguma outra FK/constraint disparar, padronizamos a mensagem
            throw new IllegalStateException(
                "Não é possível excluir a categoria porque há registros relacionados.", e
            );
        }
    }

}
