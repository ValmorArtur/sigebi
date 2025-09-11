
package com.biblioteca.controller;

import com.biblioteca.model.Acervo;
import com.biblioteca.model.Autor;
import com.biblioteca.model.enums.SituacaoExemplar;
import com.biblioteca.repository.ExemplarRepository;
import com.biblioteca.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/acervo")
public class AcervoController {

    @Autowired
    private AcervoService acervoService;
    @Autowired
    private AutorService autorService;
    @Autowired
    private EditoraService editoraService;
    @Autowired
    private GeneroService generoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private ExemplarService exemplarService;
    @Autowired 
    private ExemplarRepository exemplarRepo;

    private void carregarDominiosBasicos(Model model) {
        model.addAttribute("editoras", editoraService.listarTodos());
        model.addAttribute("generos", generoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodos());
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String filtro,
            HttpServletRequest request,
            Model model) {
        Page<Acervo> p = acervoService.listar(filtro, page, 10);
        model.addAttribute("lista", p.getContent());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("filtro", filtro);

        String qs = request.getQueryString();
        String urlBack = request.getRequestURI() + ((qs != null && !qs.isBlank()) ? ("?" + qs) : "");
        model.addAttribute("urlBack", urlBack);            

        model.addAttribute("conteudo", "acervo/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        Acervo ac = new Acervo();
        model.addAttribute("acervo", ac);
        carregarDominiosBasicos(model);
        model.addAttribute("autores", autorService.buscarTodos());
        model.addAttribute("conteudo", "acervo/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @RequestParam(required = false) String back, Model model) {
        Acervo ac = acervoService.buscar(id);
        model.addAttribute("acervo", ac);
        carregarDominiosBasicos(model);
        model.addAttribute("autores", autorService.buscarTodos());
        model.addAttribute("autoresSelecionados", ac.getAutores()); // marca no form
        model.addAttribute("conteudo", "acervo/form :: conteudo");
        model.addAttribute("qtdDisp", exemplarRepo.countByAcervo_IdAcervoAndSituacao(ac.getIdAcervo(), SituacaoExemplar.DISPONIVEL));
        model.addAttribute("back", back);
        return "principal";
    }

    @PreAuthorize("isAuthenticated()") // ou remova esta linha se quiser público
    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Integer id, Model model) {
        Acervo ac = acervoService.buscar(id);
        model.addAttribute("acervo", ac);
        // Se quiser mostrar nomes de relacionamentos no template
        carregarDominiosBasicos(model);
        model.addAttribute("autoresSelecionados", ac.getAutores());
        // carrega um fragmento próprio de visualização (abaixo)
        model.addAttribute("conteudo", "acervo/visualizar :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("acervo") Acervo ac,
            BindingResult br,
            @RequestParam(required = false) List<Integer> idsAutores,
            @RequestParam(required = false) String back,
            RedirectAttributes ra,
            Model model) {

        if (br.hasErrors()) {
            carregarDominiosBasicos(model);
            model.addAttribute("autores", autorService.buscarTodos());
            model.addAttribute("conteudo", "acervo/form :: conteudo");
            model.addAttribute("back", back);
            return "principal";
        }

        // monta o Set<Autor> a partir dos IDs enviados pelo <select multiple>
        Set<Autor> autoresSel = new LinkedHashSet<>();
        if (idsAutores != null && !idsAutores.isEmpty()) {
            autoresSel.addAll(autorService.buscarPorIds(idsAutores));
        }
        ac.setAutores(autoresSel);

        try {
            acervoService.salvar(ac, idsAutores);
            ra.addFlashAttribute("mensagem", "Acervo salvo com sucesso!");
            return "redirect:/acervo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar o acervo: " + e.getMessage());
            carregarDominiosBasicos(model);
            model.addAttribute("autores", autorService.buscarTodos());
            model.addAttribute("conteudo", "acervo/form :: conteudo");
            model.addAttribute("back", back);
            return "principal";
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ativar/{id}")
    public String ativarOuInativar(@PathVariable Integer id, RedirectAttributes ra) {
        Acervo ac = acervoService.buscar(id);

        boolean vaiInativar = Boolean.TRUE.equals(ac.getAtivo()); // se está ativo, a ação é inativar

        try {
            if (vaiInativar) {
                // Se houver EMPRÉSTIMO/RESERVA, bloqueia inativação
                if (exemplarService.hasEmprestadoOuReservado(id)) {
                    ra.addFlashAttribute("erro",
                            "Não é possível inativar o acervo: existe exemplar Emprestado ou Reservado.");
                    return "redirect:/acervo";
                }
            }
            ac.setAtivo(!Boolean.TRUE.equals(ac.getAtivo())); // inverte o status
            acervoService.salvar(ac, null); // pode passar null se não quiser alterar autores
            // Propaga situação aos exemplares
            if (ac.getAtivo()) {
                exemplarService.alterarSituacaoPorAcervo(id, SituacaoExemplar.DISPONIVEL, false);
                ra.addFlashAttribute("mensagem",
                        "Acervo ativado e exemplares ajustados para 'Disponível' (quando aplicável).");
            } else {
                exemplarService.alterarSituacaoPorAcervo(id, SituacaoExemplar.DESCONTINUADO, true);
                ra.addFlashAttribute("mensagem", "Acervo inativado e exemplares marcados como 'Descontinuado'.");
            }
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        }
        ra.addFlashAttribute("mensagem", "Status alterado com sucesso.");
        return "redirect:/acervo";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            acervoService.excluir(id);
            ra.addFlashAttribute("mensagem", "Acervo e exemplares excluídos.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/acervo";
    }
}
