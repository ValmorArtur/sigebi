package com.biblioteca.repository;
import com.biblioteca.model.Genero;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {
    Page<Genero> findByDescricaoContainingIgnoreCase(String filtro, Pageable p);
}
