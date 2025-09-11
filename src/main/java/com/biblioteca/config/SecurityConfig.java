package com.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.model.Usuario;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers(
                                "/login",
                                "/usuario/cadastro_login",
                                "/usuario/salvar_login",
                                "/usuario/esqueci_senha",
                                "/webjars/**",
                                "/css/**", "/js/**", "/img/**",
                                "/error/**")
                        .permitAll()

                        // Rotas "me" (usuário autenticado)
                        .requestMatchers("/usuario/editar/me", "/usuario/salvar/me").authenticated()

                        // Usuario (admin p/ operações administrativas)
                        .requestMatchers("/usuario/salvar").hasRole("ADMIN")
                        .requestMatchers("/usuario/novo",
                                "/usuario",
                                "/usuario/listar",
                                "/usuario/editar/**",
                                "/usuario/excluir/**",
                                "/usuario/ativar/**")
                        .hasRole("ADMIN")

                        // Endereço e principal (autenticado)
                        .requestMatchers("/endereco/**", "/principal").authenticated()

                        // *** Exemplar: primeiro as rotas administrativas ***
                        .requestMatchers("/exemplar/novo", "/exemplar/salvar",
                                "/exemplar/editar/**", "/exemplar/excluir/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/exemplar/**").authenticated()

                        // Editora: tudo ADMIN (inclui lista/tela)
                        .requestMatchers("/editora/**").hasRole("ADMIN")

                        // Acervo/Autor/Gênero/Categoria: admin nas operações de escrita, leitura p/
                        // autenticados
                        .requestMatchers("/acervo/novo", "/acervo/salvar",
                                "/acervo/editar/**", "/acervo/excluir/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/emprestimo/**", "/devolucao/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/emprestimo/**").hasRole("ADMIN")

                        .requestMatchers("/acervo/**").authenticated()

                        .requestMatchers("/autor/novo", "/autor/salvar",
                                "/autor/editar/**", "/autor/excluir/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/autor/**").authenticated()

                        .requestMatchers("/genero/novo", "/genero/salvar",
                                "/genero/editar/**", "/genero/excluir/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/genero/**").authenticated()

                        .requestMatchers("/categoria/novo", "/categoria/salvar",
                                "/categoria/editar/**", "/categoria/excluir/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/categoria/**").authenticated()

                        // fallback
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/principal", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // >>> envia 403 para sua página
                .exceptionHandling(e -> e.accessDeniedPage("/error/403"));

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return identificador -> {
            Usuario usuario = usuarioRepository
                    .findByLoginUsuarioOrCpfOrEmailUsuario(identificador, identificador, identificador)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                throw new UsernameNotFoundException("Usuário inativo");
            }

            return User.builder()
                    .username(usuario.getLoginUsuario()) // exibido no header
                    .password(usuario.getSenhaUsuario())
                    .roles(usuario.getAdministrador() ? "ADMIN" : "CLIENTE")
                    .build();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
