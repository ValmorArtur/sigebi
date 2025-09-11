package com.biblioteca.repository;

import com.biblioteca.model.Editora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface EditoraRepository extends JpaRepository<Editora, Integer> {

    // Busca em todas as colunas exibidas na listagem + ativo (Sim/Não)
    @Query(value = """
        SELECT e FROM Editora e
        WHERE
             LOWER(e.nomeEditora)             LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.foneEditora, ''))   LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.siteEditora, ''))   LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.ruaEditora, ''))    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.numeroEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.bairroEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.cidadeEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.estadoEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.cepEditora, ''))    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             (:filtroBool IS NOT NULL AND e.ativo = :filtroBool)
        """,
        countQuery = """
        SELECT COUNT(e) FROM Editora e
        WHERE
             LOWER(e.nomeEditora)             LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.foneEditora, ''))   LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.siteEditora, ''))   LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.ruaEditora, ''))    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.numeroEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.bairroEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.cidadeEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.estadoEditora, '')) LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             LOWER(COALESCE(e.cepEditora, ''))    LIKE LOWER(CONCAT('%', :filtro, '%')) OR
             (:filtroBool IS NOT NULL AND e.ativo = :filtroBool)
        """
    )
    Page<Editora> searchAll(String filtro, Boolean filtroBool, Pageable pageable);

    // (opcional) você pode manter o método antigo, se quiser
    Page<Editora> findByNomeEditoraContainingIgnoreCase(String filtro, Pageable p);
}
