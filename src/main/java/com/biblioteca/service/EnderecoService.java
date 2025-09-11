package com.biblioteca.service;

import com.biblioteca.model.Endereco;
import com.biblioteca.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    public List<Endereco> listarTodos() {
        return repository.findAll();
    }

    public Optional<Endereco> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Endereco salvar(Endereco endereco) {
        return repository.save(endereco);
    }

    public Optional<Endereco> atualizar(Long id, Endereco novo) {
        return repository.findById(id).map(endereco -> {
            endereco.setRua(novo.getRua());
            endereco.setCep(novo.getCep());
            endereco.setNumero(novo.getNumero());
            endereco.setBairro(novo.getBairro());
            endereco.setNomeCidade(novo.getNomeCidade());
            endereco.setSiglaEstado(novo.getSiglaEstado());
            endereco.setUsuario(novo.getUsuario());
            return repository.save(endereco);
        });
    }

    public boolean deletar(Long id) {
        return repository.findById(id).map(endereco -> {
            repository.delete(endereco);
            return true;
        }).orElse(false);
    }
}
