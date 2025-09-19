package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.service.AutorService;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listar(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        Page<Autor> pagina = service.listar(filtro, page, 10);

        model.addAttribute("lista", pagina.getContent());
        model.addAttribute("filtro", filtro);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagina.getTotalPages());

        // Thymeleaf (principal.html) deve incluir este fragmento
        model.addAttribute("conteudo", "autor/list :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("autor", new Autor());
        model.addAttribute("conteudo", "autor/form :: conteudo");
        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("autor", service.buscar(id));
        model.addAttribute("conteudo", "autor/form :: conteudo");

        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Integer id, Model model) {
        model.addAttribute("autor", service.buscar(id));
        // Você pode reutilizar o form em modo somente leitura se quiser
        model.addAttribute("somenteLeitura", true);
        model.addAttribute("conteudo", "autor/form :: conteudo");

        return "principal";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("autor") Autor a,
            BindingResult br,
            RedirectAttributes ra,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute("conteudo", "autor/form :: conteudo");
            return "principal";
        }
        service.salvar(a);
        ra.addFlashAttribute("mensagem", "Autor salvo com sucesso.");
        return "redirect:/autor";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("mensagem", "Autor excluído.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ra.addFlashAttribute("erro", ex.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao excluir o registro.");
        }
        return "redirect:/autor";
    }

}
