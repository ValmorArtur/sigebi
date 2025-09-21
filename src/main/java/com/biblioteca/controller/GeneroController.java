// src/main/java/com/biblioteca/controller/GeneroController.java
package com.biblioteca.controller;

import com.biblioteca.model.Genero;
import com.biblioteca.service.GeneroService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/genero")
public class GeneroController {

    private final GeneroService service;

    public GeneroController(GeneroService service) {
        this.service = service;
    }

    // LISTAGEM COM FILTRO + PAGINAÇÃO
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(required = false) String filtro,
                         Model model) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("descricao").ascending());

        // -> O service deve aceitar filtro opcional + pageable (igual Autor/Categoria).
        // Implementação sugerida no service: listarPaginado(String filtro, Pageable pageable)
        Page<Genero> resultado = service.listarPaginado(filtro, pageable);

        model.addAttribute("lista", resultado.getContent());
        model.addAttribute("currentPage", resultado.getNumber());
        model.addAttribute("totalPages", resultado.getTotalPages());
        model.addAttribute("filtro", filtro);

        model.addAttribute("conteudo", "genero/list :: conteudo");
        return "principal";
    }

    // NOVO
    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("genero", new Genero());
        model.addAttribute("conteudo", "genero/form :: conteudo");
        return "principal";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Genero genero = service.buscarPorId(id);
        if (genero == null) {
            ra.addFlashAttribute("erro", "Gênero não encontrado.");
            return "redirect:/genero";
        }
        model.addAttribute("genero", genero);
        model.addAttribute("conteudo", "genero/form :: conteudo");
        return "principal";
    }

    // VISUALIZAR (usa o mesmo form; se quiser, pode tratar como somente-leitura no template)
    @GetMapping("/visualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String visualizar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Genero genero = service.buscarPorId(id);
        if (genero == null) {
            ra.addFlashAttribute("erro", "Gênero não encontrado.");
            return "redirect:/genero";
        }
        model.addAttribute("genero", genero);
        model.addAttribute("visualizar", true); // se quiser desabilitar inputs no form
        model.addAttribute("conteudo", "genero/form :: conteudo");
        return "principal";
    }

    // SALVAR
    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute Genero genero,
                         org.springframework.validation.BindingResult br,
                         Model model,
                         RedirectAttributes ra) {

        if (br.hasErrors()) {
            // volta para o form dentro do layout principal
            model.addAttribute("conteudo", "genero/form :: conteudo");
            return "principal";
        }

        service.salvar(genero);
        ra.addFlashAttribute("mensagem", "Gênero salvo com sucesso.");
        return "redirect:/genero";
    }

    // EXCLUIR
    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("mensagem", "Gênero excluído com sucesso.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao excluir o Gênero.");
        }
        return "redirect:/genero";
    }
}
