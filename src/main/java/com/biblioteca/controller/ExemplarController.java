package com.biblioteca.controller;

import com.biblioteca.model.Exemplar;
import com.biblioteca.model.enums.SituacaoExemplar;
import com.biblioteca.repository.AcervoRepository;
import com.biblioteca.service.ExemplarService;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/exemplar")
public class ExemplarController {

    @Autowired
    private ExemplarService exemplarService;

    @Autowired
    private AcervoRepository acervoRepository;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Exemplar> pagina = exemplarService.listar(q, page, size);

        model.addAttribute("pagina", pagina);
        model.addAttribute("q", q); // mantém o termo no input

        // layout já usado no seu projeto
        model.addAttribute("tituloPagina", "Exemplares");
        model.addAttribute("iconeModulo", "/img/icon-exemplar.png");
        model.addAttribute("conteudo", "exemplar/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("exemplar", new Exemplar());
        model.addAttribute("acervos", acervoRepository.findAll());
        model.addAttribute("situacoes", SituacaoExemplar.values());
        model.addAttribute("tituloPagina", "Novo Exemplar");
        model.addAttribute("iconeModulo", "/img/icon-exemplar.png");

        model.addAttribute("conteudo", "exemplar/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model, RedirectAttributes ra) {
        return exemplarService.buscar(id)
                .map(e -> {
                    model.addAttribute("exemplar", e);
                    model.addAttribute("acervos", acervoRepository.findAll());
                    model.addAttribute("situacoes", SituacaoExemplar.values());
                    model.addAttribute("tituloPagina", "Editar Exemplar");
                    model.addAttribute("iconeModulo", "/img/icon-exemplar.png");
                    model.addAttribute("conteudo", "exemplar/form :: conteudo");
                    return "principal";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Exemplar não encontrado.");
                    return "redirect:/exemplar";
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Exemplar exemplar,
            BindingResult br,
            RedirectAttributes ra,
            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("acervos", acervoRepository.findAll());
            model.addAttribute("situacoes", SituacaoExemplar.values());
            model.addAttribute("iconeModulo", "/img/icon-exemplar.png");
            model.addAttribute("conteudo", "exemplar/form :: conteudo");
            return "principal";
        }

        try {
            // <<< garante que o Acervo postado é válido e gerenciado
            if (exemplar.getAcervo() == null || exemplar.getAcervo().getIdAcervo() == null) {
                throw new IllegalArgumentException("Acervo é obrigatório.");
            }
            exemplar.setAcervo(
                    acervoRepository.findById(exemplar.getAcervo().getIdAcervo())
                            .orElseThrow(() -> new IllegalArgumentException("Acervo inválido.")));

            exemplarService.salvar(exemplar);
            ra.addFlashAttribute("sucesso", "Exemplar salvo com sucesso!");
            return "redirect:/exemplar";
        } catch (IllegalStateException | IllegalArgumentException ex) {
            // Regras de negócio ou validação
            model.addAttribute("acervos", acervoRepository.findAll());
            model.addAttribute("situacoes", SituacaoExemplar.values());
            model.addAttribute("iconeModulo", "/img/icon-exemplar.png");
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("conteudo", "exemplar/form :: conteudo");
            return "principal";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable String id, RedirectAttributes ra) {
        try {
            exemplarService.excluir(id);
            ra.addFlashAttribute("sucesso", "Exemplar excluído.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/exemplar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/proximo-tombo", produces = "application/json")
    @ResponseBody
    public Map<String, Object> proximoTombo(@RequestParam Integer idAcervo) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String proximo = exemplarService.gerarProximoTombo(idAcervo); // método público no service
            resp.put("ok", true);
            resp.put("proximoTombo", proximo);
        } catch (Exception e) {
            resp.put("ok", false);
            resp.put("erro", e.getMessage());
        }
        return resp;
    }
}
