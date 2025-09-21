package com.biblioteca.controller;

import com.biblioteca.model.Editora;
import com.biblioteca.service.EditoraService;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/editora")
public class EditoraController {

    @Autowired
    private EditoraService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String filtro, Model model) {
        Page<Editora> p = service.listar(filtro, page, 10);
        model.addAttribute("lista", p.getContent());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("filtro", filtro);
        model.addAttribute("conteudo", "editora/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("editora", new Editora());
        model.addAttribute("conteudo", "editora/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("editora", service.buscar(id));
        model.addAttribute("conteudo", "editora/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Editora e,
            BindingResult br,
            RedirectAttributes ra,
            Model model) {

        if (br.hasErrors()) {
            model.addAttribute("conteudo", "editora/form :: conteudo");
            return "principal";
        }

        service.salvar(e); // normaliza e salva no serviço
        ra.addFlashAttribute("mensagem", "Editora salva.");
        return "redirect:/editora";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("mensagem", "Editora excluída.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao excluir o registro.");
        }
        return "redirect:/editora";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ativar/{id}")
    public String ativar(@PathVariable Integer id, RedirectAttributes ra) {
        Editora e = service.buscar(id);
        e.setAtivo(!Boolean.TRUE.equals(e.getAtivo()));
        service.salvar(e);
        ra.addFlashAttribute("mensagem", "Situação atualizada.");
        return "redirect:/editora";
    }
}
