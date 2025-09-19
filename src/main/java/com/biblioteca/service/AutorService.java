package com.biblioteca.service;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AcervoRepository;
import com.biblioteca.repository.AutorRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository repo;

    @Autowired
    private AcervoRepository acervoRepo;

    public Page<Autor> listar(String filtro, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("nomeAutor").ascending());
        if (filtro == null || filtro.isBlank()) {
            return repo.findAll(p); // sem filtro -> pagina tudo
        }
        return repo.listar(filtro.trim(), p); // com filtro -> usa JPQL do repositório
    }

    public Autor buscar(Integer id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Autor não encontrado: " + id));
    }

    public Autor salvar(Autor a) {
        return repo.save(a);
    }

    @Transactional
    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do autor inválido.");
        }
    
        long qtd = acervoRepo.countByAutores_IdAutor(id);
        if (qtd == 1) {
            // Bloqueia a exclusão e informa quantos vínculos existem
            throw new IllegalStateException(
                "Não é possível excluir: há " + qtd + " item do acervo vinculado à este autor."
            );
        } else if (qtd > 1){
            throw new IllegalStateException(
                "Não é possível excluir: há " + qtd + " itens do acervo vinculados à este autor."
            ); 
        }
    
        // Sem vínculos -> pode excluir
        repo.deleteById(id);
    }

    public List<Autor> buscarTodos() {
        return repo.findAll(Sort.by("nomeAutor").ascending());
    }

    public List<Autor> buscarPorIds(Iterable<Integer> ids) {
        return repo.findAllById(ids);
    }

}
