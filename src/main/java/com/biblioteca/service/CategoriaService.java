package com.biblioteca.service;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import java.util.List;


@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    public Page<Categoria> listar(String filtro, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("descricao").ascending());
        return (filtro == null || filtro.isBlank())
                ? repo.findAll(p)
                : repo.findByDescricaoContainingIgnoreCase(filtro, p);
    }

    public List<Categoria> listarTodos() {
        return repo.findAll();
    }

    public Categoria buscar(Integer id) {
        return repo.findById(id).orElseThrow();
    }

    public Categoria salvar(Categoria c) {
        return repo.save(c);
    }

    public void excluir(Integer id) {
        repo.deleteById(id);
    }
}
