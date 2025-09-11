package com.biblioteca.controller;

import com.biblioteca.model.Endereco;
import com.biblioteca.repository.EnderecoRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoRepository repository;

    public EnderecoController(EnderecoRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Endereco> listarTodos() {
        return repository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Endereco criar(@Valid @RequestBody Endereco endereco) {
        return repository.save(endereco);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @Valid @RequestBody Endereco novoEndereco) {
        return repository.findById(id)
                .map(endereco -> {
                    endereco.setRua(novoEndereco.getRua());
                    endereco.setCep(novoEndereco.getCep());
                    endereco.setNumero(novoEndereco.getNumero());
                    endereco.setBairro(novoEndereco.getBairro());
                    endereco.setNomeCidade(novoEndereco.getNomeCidade());
                    endereco.setSiglaEstado(novoEndereco.getSiglaEstado());
                    endereco.setUsuario(novoEndereco.getUsuario());
                    return ResponseEntity.ok(repository.save(endereco));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<Void>>map(endereco -> {
                    repository.delete(endereco);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
