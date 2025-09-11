package com.biblioteca.service;

import com.biblioteca.model.Endereco;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvar(Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Preservar dados que não vieram da tela "Meus Dados"
            if (usuario.getLoginUsuario() == null || usuario.getLoginUsuario().isBlank()) {
                usuario.setLoginUsuario(existente.getLoginUsuario());
            }
            if (usuario.getSenhaUsuario() == null || usuario.getSenhaUsuario().isBlank()) {
                usuario.setSenhaUsuario(existente.getSenhaUsuario());
            } else {
                usuario.setSenhaUsuario(processarSenha(usuario.getSenhaUsuario()));
            }

            if (usuario.getAdministrador() == null) {
                usuario.setAdministrador(existente.getAdministrador());
            }

            if (usuario.getNomeUsuario() == null) {
                usuario.setNomeUsuario(existente.getNomeUsuario());
            }
            if (usuario.getCpf() == null) {
                usuario.setCpf(existente.getCpf());
            }

            if (usuario.getAtivo() == null) {
                usuario.setAtivo(existente.getAtivo());
            }
        } else {
            // Novo usuário

            usuario.setSenhaUsuario(processarSenha(usuario.getSenhaUsuario()));

        }

        // Associar endereço corretamente
        if (usuario.getEnderecos() != null && !usuario.getEnderecos().isEmpty()) {
            Endereco endereco = usuario.getEnderecos().get(0);

            boolean todosCamposVazios = (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) &&
                    (endereco.getRua() == null || endereco.getRua().trim().isEmpty()) &&
                    (endereco.getNumero() == null || endereco.getNumero() == null) &&
                    (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) &&
                    (endereco.getNomeCidade() == null || endereco.getNomeCidade().trim().isEmpty()) &&
                    (endereco.getSiglaEstado() == null || endereco.getSiglaEstado().trim().isEmpty());

            boolean algumCampoPreenchido = !(todosCamposVazios);

            if (todosCamposVazios) {
                // Não salvar endereço algum
                usuario.setEnderecos(null);
            } else if (algumCampoPreenchido) {
                // Valida se todos os campos obrigatórios foram preenchidos
                if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()
                        || endereco.getRua() == null || endereco.getRua().trim().isEmpty()
                        || endereco.getNumero() == null || endereco.getNumero() == null
                        || endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()
                        || endereco.getNomeCidade() == null || endereco.getNomeCidade().trim().isEmpty()
                        || endereco.getSiglaEstado() == null || endereco.getSiglaEstado().trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Se algum campo do endereço for preenchido, todos os campos são obrigatórios.");
                }

                // Associa o endereço ao usuário
                endereco.setUsuario(usuario);
            }
        }

        usuarioRepository.save(usuario);
    }

    // AUTENTICAR usuário para login (se ativo e senha correta)
    public Optional<Usuario> autenticar(String loginUsuario, String senha) {
        List<Usuario> usuarios = usuarioRepository.findAllByLoginUsuarioOrCpfOrEmailUsuario(
                loginUsuario, loginUsuario, loginUsuario);

        for (Usuario usuario : usuarios) {
            if (Boolean.TRUE.equals(usuario.getAtivo()) &&
                    passwordEncoder.matches(senha, usuario.getSenhaUsuario())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }

    // LISTAR todos os usuários
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // BUSCAR usuário por ID
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findByIdWithEnderecos(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // EXCLUIR usuário por ID
    public void excluir(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public Page<Usuario> listarComPaginacao(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return usuarioRepository.findAll(pageable);
        }

        filtro = filtro.trim().toLowerCase();

        // Mapeia "ativo"/"inativo" para true/false
        if (filtro.equals("ativo")) {
            return usuarioRepository.findAllByAtivoTrue(pageable);
        } else if (filtro.equals("inativo")) {
            return usuarioRepository.findAllByAtivoFalse(pageable);
        } else if (filtro.equals("sim")) {
            return usuarioRepository.findAllByAdministradorTrue(pageable);
        } else if (filtro.equals("não") || filtro.equals("nao")) {
            return usuarioRepository.findAllByAdministradorFalse(pageable);
        }

        // Caso contrário, faz busca genérica
        return usuarioRepository.buscarPorFiltro(filtro, pageable);
    }

    private String processarSenha(String senhaAtual) {
        if (senhaAtual == null || senhaAtual.isBlank()) {
            return null;
        }
        return senhaAtual.startsWith("$2a$") ? senhaAtual : passwordEncoder.encode(senhaAtual);
    }

}
