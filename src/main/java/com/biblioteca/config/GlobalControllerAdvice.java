package com.biblioteca.config;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @ModelAttribute("primeiroNomeUsuario")
    public String getPrimeiroNomeUsuario(Principal principal) {
        if (principal == null)
            return "Usuário";

        return usuarioRepository
                .findByLoginUsuarioOrCpfOrEmailUsuario(principal.getName(), principal.getName(), principal.getName())
                .map(Usuario::getNomeUsuario)
                .map(nome -> {
                    String primeiroNome = nome.trim().split(" ")[0];
                    return Character.toUpperCase(primeiroNome.charAt(0)) + primeiroNome.substring(1).toLowerCase();
                })
                .orElse("Usuário");
    }
}

