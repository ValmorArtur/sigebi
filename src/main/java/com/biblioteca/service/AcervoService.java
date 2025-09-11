package com.biblioteca.service;

import com.biblioteca.model.*;
import com.biblioteca.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AcervoService {

    @Autowired
    private AcervoRepository repo;
    @Autowired
    private EditoraRepository editoraRepo;
    @Autowired
    private GeneroRepository generoRepo;
    @Autowired
    private CategoriaRepository categoriaRepo;
    @Autowired
    private AutorRepository autorRepo;
    @Autowired
    private ExemplarService exemplarService;

    public Page<Acervo> listar(String filtro, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("tituloAcervo").ascending());
        if (filtro == null || filtro.isBlank()) {
            // antes: repo.findAll(p)
            return repo.pageAll(p); // carrega editora/genero/categoria/autores
        }
        return repo.searchAll(filtro.trim(), p); // também com EntityGraph
    }

    public Acervo buscar(Integer id) {
        return repo.findById(id).orElseThrow();
    }

    public Acervo salvar(Acervo a, List<Integer> idsAutores) {
        // Só atualiza autores se a lista for fornecida
        if (idsAutores != null) {
            Set<Autor> setAutores = new LinkedHashSet<>();
            if (!idsAutores.isEmpty()) {
                setAutores.addAll(autorRepo.findAllById(idsAutores));
            }
            a.setAutores(setAutores);
        }
        return repo.save(a);
    }

    @Transactional // <<< garante atomicidade (excluir exemplares + acervo)
    public void excluir(Integer id) {
        // 1) Excluir todos os exemplares do acervo (respeitando as regras)
        exemplarService.excluirTodosPorAcervo(id); // lança IllegalStateException se bloqueado

        // 2) Excluir o acervo
        repo.deleteById(id);
    }

    // Listas para dropdowns (ordenadas)
    public List<Editora> listarEditoras() {
        return editoraRepo.findAll(Sort.by("nomeEditora").ascending());
    }

    public List<Genero> listarGeneros() {
        return generoRepo.findAll(Sort.by("descricao").ascending());
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepo.findAll(Sort.by("descricao").ascending());
    }

    public List<Autor> listarAutores() {
        return autorRepo.findAll(Sort.by("nomeAutor").ascending());
    }
}
