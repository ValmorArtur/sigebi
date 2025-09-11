package com.biblioteca.controller;

import com.biblioteca.model.Endereco;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    private boolean existeOutroUsuarioComMesmoLoginCpfOuEmail(Usuario usuario) {
        List<Usuario> existentes = usuarioRepository.findAllByLoginUsuarioOrCpfOrEmailUsuario(
                usuario.getLoginUsuario(), usuario.getCpf(), usuario.getEmailUsuario());

        return existentes.stream()
                .anyMatch(u -> !u.getIdUsuario().equals(usuario.getIdUsuario()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("conteudo", "usuario/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listar(@RequestParam(required = false) String filtro,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("nomeUsuario").ascending()); // 10 itens por página
        Page<Usuario> pagina = usuarioService.listarComPaginacao(filtro, pageable);

        model.addAttribute("usuarios", pagina.getContent());
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("filtro", filtro);
        model.addAttribute("conteudo", "usuario/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("edicaoRestrita", false);
        model.addAttribute("conteudo", "usuario/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, RedirectAttributes ra, Model model) {
        if (existeOutroUsuarioComMesmoLoginCpfOuEmail(usuario)) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", "Login, CPF ou e-mail já estão sendo usados por outro usuário.");

            if (Boolean.TRUE.equals(usuario.getAdministrador()) || Boolean.FALSE.equals(usuario.getAdministrador())) {
                // edição completa (usuário comum pelo menu lateral)
                model.addAttribute("edicaoRestrita", false);
                model.addAttribute("conteudo", "usuario/form :: conteudo");
                return "principal";
            } else {
                // edição restrita (meus dados)
                model.addAttribute("edicaoRestrita", true);
                model.addAttribute("conteudo", "usuario/form :: conteudo");
                return "principal";
            }
        }

        if (usuario.getIdUsuario() == null) {
            usuario.setAtivo(true);
            if (usuario.getAdministrador() == null) {
                usuario.setAdministrador(false);
            }
        }

        if (usuario.getEnderecos() != null) {
            for (Endereco endereco : usuario.getEnderecos()) {
                endereco.setUsuario(usuario);
            }
        }

        usuarioService.salvar(usuario);
        ra.addFlashAttribute("mensagem", "Usuário salvo com sucesso.");
        return "redirect:/usuario";
    }

    // EDITAR (carrega usuário pelo ID)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("edicaoRestrita", false);
            model.addAttribute("conteudo", "usuario/form :: conteudo");
            return "principal";
        } else {
            return "redirect:/usuario?erro=naoencontrado";
        }
    }

    // EXCLUIR usuário
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        usuarioService.excluir(id);
        ra.addFlashAttribute("mensagem", "Usuário excluído com sucesso.");
        return "redirect:/usuario";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/esqueci_senha")
    public String exibirFormularioEsqueciSenha(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/esqueci_senha";
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/esqueci_senha")
    public String processarAlteracaoSenha(@RequestParam String loginUsuario,
            @RequestParam String novaSenha,
            RedirectAttributes ra) {

        List<Usuario> encontrados = usuarioRepository.findAllByLoginUsuarioOrCpfOrEmailUsuario(
                loginUsuario, loginUsuario, loginUsuario);

        if (!encontrados.isEmpty()) {
            Usuario usuario = encontrados.get(0); // pega o primeiro

            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                ra.addFlashAttribute("erro", "Usuário inativo. Não é possível alterar a senha.");
                return "redirect:/usuario/esqueci_senha";
            }

            usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
            usuarioRepository.save(usuario);
            ra.addFlashAttribute("mensagem", "Senha alterada com sucesso!");
            return "redirect:/login";
        } else {
            ra.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuario/esqueci_senha";
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/cadastro_login")
    public String exibirCadastroLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro_login";
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/salvar_login")
    public String salvarCadastroLogin(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        if (usuario.getSenhaUsuario() == null || usuario.getSenhaUsuario().isEmpty()) {
            ra.addFlashAttribute("erro", "Senha obrigatória");
            return "redirect:/usuario/cadastro_login";
        }

        List<Usuario> existentes = usuarioRepository.findAllByLoginUsuarioOrCpfOrEmailUsuario(
                usuario.getLoginUsuario(), usuario.getCpf(), usuario.getEmailUsuario());

        if (!existentes.isEmpty()) {
            ra.addFlashAttribute("erro", "Login, CPF ou e-mail já cadastrados!");
            return "redirect:/usuario/cadastro_login";
        }

        usuario.setAdministrador(false);
        usuario.setAtivo(true);
        usuarioService.salvar(usuario);
        ra.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça o login.");
        return "redirect:/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/editar/me")
    public String editarMe(Model model, Principal principal) {
        List<Usuario> encontrados = usuarioRepository.findAllByLoginCpfEmailWithEnderecos(
                principal.getName());

        if (!encontrados.isEmpty()) {
            model.addAttribute("usuario", encontrados.get(0));
            model.addAttribute("edicaoRestrita", true);
            model.addAttribute("conteudo", "usuario/form :: conteudo");
            return "principal";
        } else {
            model.addAttribute("erro", "Usuário não encontrado.");
            return "redirect:/principal";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/salvar/me")
    public String salvarMe(@ModelAttribute("usuario") Usuario usuarioForm,
            Principal principal,
            RedirectAttributes ra,
            Model model) {
        Usuario usuarioLogado = usuarioRepository
                .findByLoginUsuarioOrCpfOrEmailUsuario(principal.getName(), principal.getName(), principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Verifica se é o mesmo usuário
        if (!usuarioForm.getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
            throw new AccessDeniedException("Você não pode alterar os dados de outro usuário.");
        }

        // Preserva campos que o usuário comum não pode alterar
        usuarioForm.setAdministrador(usuarioLogado.getAdministrador());
        usuarioForm.setLoginUsuario(usuarioLogado.getLoginUsuario());
        usuarioForm.setCpf(usuarioLogado.getCpf());
        usuarioForm.setSenhaUsuario(usuarioLogado.getSenhaUsuario()); // mantemos a senha atual
        usuarioForm.setAtivo(usuarioLogado.getAtivo());

        // Garante vínculo do endereço
        if (usuarioForm.getEnderecos() != null) {
            for (Endereco endereco : usuarioForm.getEnderecos()) {
                endereco.setUsuario(usuarioForm);
            }
        }

        usuarioService.salvar(usuarioForm);
        ra.addFlashAttribute("mensagem", "Seus dados foram atualizados com sucesso!");
        return "redirect:/usuario/editar/me";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ativar/{id}")
    public String ativarOuInativar(@PathVariable Integer id, RedirectAttributes ra,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String filtro) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setAtivo(!Boolean.TRUE.equals(usuario.getAtivo()));
        usuarioService.salvar(usuario);
        ra.addFlashAttribute("mensagem", "Situação atualizada com sucesso.");
        return "redirect:/usuario?page=" + page + (filtro != null ? "&filtro=" + filtro : "");
    }

}
