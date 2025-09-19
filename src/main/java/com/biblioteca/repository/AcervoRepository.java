package com.biblioteca.repository;

import com.biblioteca.model.Acervo;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AcervoRepository extends JpaRepository<Acervo, Integer> {

    @EntityGraph(attributePaths = { "editora", "genero", "categoria", "autores" })
    @Query("SELECT a FROM Acervo a")
    Page<Acervo> pageAll(Pageable pageable);

    @EntityGraph(attributePaths = { "editora", "genero", "categoria", "autores" })
    @Query(value = """
        SELECT DISTINCT a
          FROM Acervo a
          LEFT JOIN a.editora e
          LEFT JOIN a.genero g
          LEFT JOIN a.categoria c
          LEFT JOIN a.autores au
         WHERE (:filtro IS NULL OR :filtro = '' OR
                LOWER(a.isbn)                         LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(a.tituloAcervo)                 LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(a.subTituloAcervo)              LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                CAST(a.anoPublicacaoAcervo AS string) LIKE CONCAT('%', :filtro, '%') OR
                CAST(a.numEdicaoAcervo AS string)     LIKE CONCAT('%', :filtro, '%') OR
                LOWER(e.nomeEditora)                  LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(g.descricao)                    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(c.descricao)                    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(au.nomeAutor)                   LIKE LOWER(CONCAT('%', :filtro, '%'))
         )
        """,
        countQuery = """
        SELECT COUNT(DISTINCT a)
          FROM Acervo a
          LEFT JOIN a.editora e
          LEFT JOIN a.genero g
          LEFT JOIN a.categoria c
          LEFT JOIN a.autores au
         WHERE (:filtro IS NULL OR :filtro = '' OR
                LOWER(a.isbn)                         LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(a.tituloAcervo)                 LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(a.subTituloAcervo)              LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                CAST(a.anoPublicacaoAcervo AS string) LIKE CONCAT('%', :filtro, '%') OR
                CAST(a.numEdicaoAcervo AS string)     LIKE CONCAT('%', :filtro, '%') OR
                LOWER(e.nomeEditora)                  LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(g.descricao)                    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(c.descricao)                    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
                LOWER(au.nomeAutor)                   LIKE LOWER(CONCAT('%', :filtro, '%'))
         )
        """)
    Page<Acervo> searchAll(@Param("filtro") String filtro, Pageable pageable);

    // Conta quantos itens do acervo possuem o autor informado
    long countByAutores_IdAutor(Integer idAutor);

}

