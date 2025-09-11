package com.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String raiz() {
        return "redirect:/principal";
    }

    // Método para carregar a tela principal com um fragmento inicial
    @GetMapping("/principal")
    public String principal(Model model) {
        // Pode apontar para um fragmento de boas-vindas se quiser
        model.addAttribute("conteudo", "fragments/boasvindas :: conteudo");
        return "principal";
    }
}
