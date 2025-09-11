package com.biblioteca.repository;

import com.biblioteca.model.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Integer> {

    // >>> Usado na tela de listagem com filtro (Controller/Service)
    @Query("""
           SELECT a
             FROM Autor a
            WHERE (:filtro IS NULL OR :filtro = '' OR
                   LOWER(a.nomeAutor)      LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                   LOWER(a.descricaoAutor) LIKE LOWER(CONCAT('%', :filtro, '%'))
            )
           """)
    Page<Autor> listar(@Param("filtro") String filtro, Pageable pageable);

    // >>> Usados pelo endpoint /api/autores (Tom Select)
    List<Autor> findTop20ByOrderByNomeAutorAsc();
    List<Autor> findTop20ByNomeAutorContainingIgnoreCaseOrderByNomeAutorAsc(String q);
    
}
