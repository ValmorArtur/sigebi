package com.biblioteca.repository;
import com.biblioteca.model.Categoria;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Page<Categoria> findByDescricaoContainingIgnoreCase(String filtro, Pageable p);
}
