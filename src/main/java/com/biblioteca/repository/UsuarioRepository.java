package com.biblioteca.repository;

import com.biblioteca.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLoginUsuarioOrCpfOrEmailUsuario(String loginusuario, String cpf, String emailusuario);

    List<Usuario> findAllByLoginUsuarioOrCpfOrEmailUsuario(String loginusuario, String cpf, String emailusuario);

    List<Usuario> findByAtivoTrueOrderByNomeUsuarioAsc();
    
    Optional<Usuario> findByLoginUsuario(String loginUsuario);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmailUsuario(String emailUsuario);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.enderecos WHERE u.idUsuario = :id")
    Optional<Usuario> findByIdWithEnderecos(@Param("id") Integer id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.enderecos WHERE u.loginUsuario = :login OR u.cpf = :login OR u.emailUsuario = :login")
    List<Usuario> findAllByLoginCpfEmailWithEnderecos(@Param("login") String login);

    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.nomeUsuario) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.emailUsuario) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.foneUsuario) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.loginUsuario) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(u.cpf) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    Page<Usuario> buscarPorFiltro(@Param("filtro") String filtro, Pageable pageable);

    Page<Usuario> findAllByAtivoTrue(Pageable pageable);

    Page<Usuario> findAllByAtivoFalse(Pageable pageable);

    Page<Usuario> findAllByAdministradorTrue(Pageable pageable);

    Page<Usuario> findAllByAdministradorFalse(Pageable pageable);

}
