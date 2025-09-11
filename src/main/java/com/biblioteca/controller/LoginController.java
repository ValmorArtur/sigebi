package com.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model, 
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @ModelAttribute String mensagem) {

        // Já logado? Vai para principal.
        //if (auth != null && auth.isAuthenticated()) {
        //    return "redirect:/principal";
        //}
        
        if (error != null)
            model.addAttribute("erro", "Usuário ou senha inválidos.");
        if (logout != null)
            model.addAttribute("mensagem", "Logout realizado com sucesso.");
        if (mensagem != null)
            model.addAttribute("mensagem", mensagem);
        return "login";
    }

    @GetMapping("/error/403")
    public String acessoNegado() {
        return "error/403";
    }
}