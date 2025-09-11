package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Usuario;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

        // EmprestimoRepository.java
        @Query("""
                        select e from Emprestimo e
                        join fetch e.exemplar x
                        join fetch x.acervo a
                        join fetch e.usuario u
                        where e.dataDevolucao is null
                          and ( :filtro is null or :filtro = ''
                                or lower(x.idTombo) like lower(concat('%', :filtro, '%'))
                                or lower(u.loginUsuario) like lower(concat('%', :filtro, '%')) )
                        """)
        Page<Emprestimo> pageAtivos(@Param("filtro") String filtro, Pageable pageable);

        @Query("""
                        select e from Emprestimo e
                        left join fetch e.exemplar x
                        left join fetch x.acervo
                        left join fetch e.usuario
                        where e.idEmprestimo = :id
                        """)
        Optional<Emprestimo> detalhado(@Param("id") Integer id);

        @Query("""
                        select e from Emprestimo e
                        join fetch e.exemplar x
                        join fetch x.acervo a
                        join fetch e.usuario u
                        where e.dataDevolucao is not null
                          and ( :filtro is null or :filtro = ''
                                or lower(x.idTombo) like lower(concat('%', :filtro, '%'))
                                or lower(u.loginUsuario) like lower(concat('%', :filtro, '%'))
                                or lower(a.tituloAcervo) like lower(concat('%', :filtro, '%')) )
                        order by e.dataDevolucao desc
                        """)
        Page<Emprestimo> pageDevolvidos(@Param("filtro") String filtro, Pageable pageable);

        boolean existsByExemplarIdTomboAndDataDevolucaoIsNull(String idTombo);

        List<Emprestimo> findByUsuarioAndDataDevolucaoIsNull(Usuario usuario);

        Optional<Emprestimo> findByIdEmprestimoAndDataDevolucaoIsNull(Integer idEmprestimo);

        long countByExemplarIdTomboAndDataDevolucaoIsNull(String idTombo);

        Optional<Emprestimo> findFirstByExemplar_IdTomboAndDataDevolucaoIsNull(String idTombo);
}
