package com.biblioteca.service;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class AutorService {

    @Autowired
    private AutorRepository repo;

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

    public void excluir(Integer id) {
        repo.deleteById(id);
    }

    public List<Autor> buscarTodos() {
        return repo.findAll(Sort.by("nomeAutor").ascending());
    }

    public List<Autor> buscarPorIds(Iterable<Integer> ids) {
        return repo.findAllById(ids);
    }

}
