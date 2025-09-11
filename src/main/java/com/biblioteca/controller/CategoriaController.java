package com.biblioteca.controller;

import com.biblioteca.model.Categoria;
import com.biblioteca.service.CategoriaService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

  @Autowired
  private CategoriaService service;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public String listar(@RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false) String filtro,
      Model model) {
    Page<Categoria> p = service.listar(filtro, page, 10);
    model.addAttribute("lista", p.getContent());
    model.addAttribute("totalPages", p.getTotalPages());
    model.addAttribute("currentPage", page);
    model.addAttribute("filtro", filtro);
    model.addAttribute("conteudo", "categoria/list :: conteudo");
    return "principal";
  }
  
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/novo")
  public String novo(Model model) {
    model.addAttribute("categoria", new Categoria());
    model.addAttribute("conteudo", "categoria/form :: conteudo");
    return "principal";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/editar/{id}")
  public String editar(@PathVariable Integer id, Model model) {
    model.addAttribute("categoria", service.buscar(id));
    model.addAttribute("conteudo", "categoria/form :: conteudo");
    return "principal";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/salvar")
  public String salvar(@Valid @ModelAttribute Categoria c,
      org.springframework.validation.BindingResult br,
      Model model,
      RedirectAttributes ra) {
    if (br.hasErrors()) {
      model.addAttribute("categoria", c);
      model.addAttribute("conteudo", "categoria/form :: conteudo");
      return "principal";
    }
    service.salvar(c);
    ra.addFlashAttribute("mensagem", "Categoria salva com sucesso.");
    return "redirect:/categoria";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/excluir/{id}")
  public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
    service.excluir(id);
    ra.addFlashAttribute("mensagem", "Excluído.");
    return "redirect:/categoria";
  }
}
