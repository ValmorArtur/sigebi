package com.biblioteca.controller;

import com.biblioteca.model.*;
import com.biblioteca.repository.*;
import com.biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.biblioteca.model.enums.SituacaoExemplar;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Controller
public class EmprestimoController {

    @Autowired
    private EmprestimoService service;
    @Autowired
    private ExemplarRepository exemplarRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private EmprestimoRepository emprestimoRepo;

    // ===== Listas (ativos) =====
    @GetMapping({ "/emprestimo" })
    public String listarAtivos(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String filtro,
            Model model) {
        var pagina = service.listarAtivos(filtro, page, size);
        model.addAttribute("lista", pagina.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("filtro", filtro);
        model.addAttribute("titulo", "Empréstimos Ativos");
        model.addAttribute("conteudo", "emprestimo/list :: conteudo");
        return "principal";
    }

    // Mapeia Devoluções como a mesma lista, apenas muda o título na view
    @GetMapping({ "/devolucao" })
    public String listarDevolucoes(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String filtro,
            Model model) {

        var pagina = service.listarDevolvidos(filtro, page, size);
        model.addAttribute("lista", pagina.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("filtro", filtro);
        model.addAttribute("pageSize", size);

        model.addAttribute("titulo", "Devoluções");
        model.addAttribute("conteudo", "devolucao/list :: conteudo");
        return "principal";
    }

    @GetMapping("/devolucao/visualizar/{id}")
    public String visualizarDevolucao(@PathVariable Integer id,
            @RequestParam(required = false) String back,
            Model model,
            RedirectAttributes ra) {
        return emprestimoRepo.detalhado(id)
                .map(e -> {
                    model.addAttribute("emprestimo", e);
                    model.addAttribute("conteudo", "devolucao/visualizar :: conteudo");
                    model.addAttribute("back", back);
                    return "principal";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Devolução não encontrada.");
                    return "redirect:/devolucao";
                });
    }

    @PostMapping("/devolucao/{id}/remover")
    public String removerDevolucao(@PathVariable Integer id,
            @RequestParam(value = "back", required = false) String back,
            RedirectAttributes ra) {
        try {
            service.removerDevolucao(id);
            ra.addFlashAttribute("mensagem", "Devolução removida. Empréstimo reaberto.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return redirectBack(back, "/devolucao");
    }

    // ===== Formulário Novo =====
    @GetMapping("/emprestimo/novo")
    public String novo(@RequestParam(required = false) Integer idAcervo,
            @RequestParam(required = false) String tombo,
            @RequestParam(required = false) String back,
            Model model) {
        Emprestimo form = new Emprestimo();
        form.setDataPrevistaDevolucao(LocalDate.now().plusDays(7));

        List<Exemplar> exemplares;
        if (tombo != null) {
            exemplares = exemplarRepo.findAllById(List.of(tombo));
        } else if (idAcervo != null) {
            exemplares = exemplarRepo.findByAcervo_IdAcervoAndSituacao(idAcervo, SituacaoExemplar.DISPONIVEL);
        } else {
            exemplares = exemplarRepo.findBySituacao(SituacaoExemplar.DISPONIVEL);
        }

        model.addAttribute("emprestimo", form);
        model.addAttribute("exemplares", exemplares);
        model.addAttribute("usuarios", usuarioRepo.findByAtivoTrueOrderByNomeUsuarioAsc());
        model.addAttribute("conteudo", "emprestimo/form :: conteudo");
        model.addAttribute("back", back);
        return "principal";
    }

    @PostMapping("/emprestimo/salvar")
    public String salvar(@ModelAttribute("emprestimo") Emprestimo form,
            BindingResult br,
            @RequestParam(required = false) String back,
            RedirectAttributes ra,
            Model model) {

        if (br.hasErrors()) {
            // Recarrega dados mínimos para o form (ajuste se quiser usar o mesmo filtro do
            // GET/novo)
            model.addAttribute("exemplares", exemplarRepo.findBySituacao(SituacaoExemplar.DISPONIVEL));
            model.addAttribute("usuarios", usuarioRepo.findByAtivoTrueOrderByNomeUsuarioAsc());
            model.addAttribute("conteudo", "emprestimo/form :: conteudo");
            model.addAttribute("back", back);
            return "principal";
        }

        try {
            service.emprestar(
                    form.getExemplar().getIdTombo(),
                    form.getUsuario().getIdUsuario(),
                    form.getDataPrevistaDevolucao(),
                    form.getObservacao());

            ra.addFlashAttribute("mensagem", "Empréstimo registrado com sucesso!");
            // Volta para a origem (lista de Exemplares ou de Empréstimos)
            return redirectBack(back, "/emprestimo");

        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());

            // Monta a query string preservando 'back' (ENCODADO) e, se possível, o 'tombo'
            // selecionado
            StringBuilder qs = new StringBuilder();
            if (StringUtils.hasText(back) && back.startsWith("/")) {
                qs.append(qs.length() == 0 ? "?" : "&")
                        .append("back=")
                        .append(UriUtils.encodeQueryParam(back, StandardCharsets.UTF_8));
            }
            if (form.getExemplar() != null && StringUtils.hasText(form.getExemplar().getIdTombo())) {
                qs.append(qs.length() == 0 ? "?" : "&")
                        .append("tombo=")
                        .append(UriUtils.encodeQueryParam(form.getExemplar().getIdTombo(), StandardCharsets.UTF_8));
            }

            return "redirect:/emprestimo/novo" + qs;
        }
    }

    // Tela de Renovação (pré-carrega dados do empréstimo ativo)
    @GetMapping("/emprestimo/{id}/renovar")
    public String renovarForm(@PathVariable Integer id,
            @RequestParam(required = false) String back,
            Model model,
            RedirectAttributes ra) {
        return emprestimoRepo.findByIdEmprestimoAndDataDevolucaoIsNull(id)
                .map(e -> {
                    // Sugestão de data: +7 dias a partir da data prevista atual, senão hoje+7
                    LocalDate sugerida = (e.getDataPrevistaDevolucao() != null)
                            ? e.getDataPrevistaDevolucao().plusDays(7)
                            : LocalDate.now().plusDays(7);

                    model.addAttribute("emprestimo", e);
                    model.addAttribute("novaPrevista", sugerida);
                    model.addAttribute("titulo", "Renovar Empréstimo");
                    model.addAttribute("back", back);
                    model.addAttribute("conteudo", "emprestimo/renovar :: conteudo");
                    return "principal";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Somente empréstimos ativos podem ser renovados.");
                    return "redirect:/emprestimo";
                });
    }

    // Efetiva a renovação: devolve o atual e abre um novo
    @PostMapping("/emprestimo/{id}/renovar")
    public String renovarSalvar(@PathVariable Integer id,
            @RequestParam(name = "dataPrevistaDevolucao", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaPrevista,
            @RequestParam(required = false) String observacao,
            @RequestParam(required = false) String back,
            RedirectAttributes ra) {
        try {
            Emprestimo novo = service.renovar(id, novaPrevista, observacao);
            ra.addFlashAttribute("sucesso", "Empréstimo renovado com sucesso! Novo ID: " + novo.getIdEmprestimo());
        } catch (Exception ex) {
            ra.addFlashAttribute("erro", "Não foi possível renovar: " + ex.getMessage());
        }
        return (StringUtils.hasText(back)
                ? "redirect:" + UriUtils.encodePath(back, StandardCharsets.UTF_8)
                : "redirect:/emprestimo");
    }

    @PostMapping("/emprestimo/{id}/devolver")
    public String devolver(@PathVariable Integer id,
            @RequestParam(value = "back", required = false) String back,
            RedirectAttributes ra) {
        try {
            service.devolver(id);
            ra.addFlashAttribute("mensagem", "Devolução registrada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return redirectBack(back, "/emprestimo");
    }

    @PostMapping("/emprestimo/{id}/excluir")
    public String excluir(@PathVariable Integer id,
            @RequestParam(value = "back", required = false) String back,
            RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("mensagem", "Empréstimo excluído.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return redirectBack(back, "/emprestimo");
    }

    private String redirectBack(String back, String fallback) {
        return (StringUtils.hasText(back) && back.startsWith("/"))
                ? "redirect:" + back
                : "redirect:" + fallback;
    }

    @GetMapping("/emprestimo/visualizar/{id}")
    public String visualizar(@PathVariable Integer id,
            @RequestParam(required = false) String back,
            Model model,
            RedirectAttributes ra) {
        return emprestimoRepo.detalhado(id) // pode trocar por o método com fetch (abaixo)
                .map(e -> {
                    model.addAttribute("emprestimo", e);
                    model.addAttribute("conteudo", "emprestimo/visualizar :: conteudo");
                    model.addAttribute("back", back);
                    return "principal";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Empréstimo não encontrado.");
                    return "redirect:/emprestimo";
                });
    }

}
