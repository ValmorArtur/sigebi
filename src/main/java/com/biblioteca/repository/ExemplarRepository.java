package com.biblioteca.repository;

import com.biblioteca.model.Exemplar;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.biblioteca.model.enums.SituacaoExemplar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, String> {

        @EntityGraph(attributePaths = { "acervo" })
        @Query("""
                        select e from Exemplar e
                        where
                            ( :q is null
                              or lower(e.idTombo) like lower(concat('%', :q, '%'))
                              or lower(e.acervo.tituloAcervo) like lower(concat('%', :q, '%'))
                            )
                            or ( :situacao is not null and e.situacao = :situacao )
                        """)
        Page<Exemplar> search(@Param("q") String q,
                        @Param("situacao") SituacaoExemplar situacao,
                        Pageable pageable);

        @EntityGraph(attributePaths = { "acervo" })
        @Query("select e from Exemplar e")
        Page<Exemplar> pageAll(Pageable pageable);

        // Buscar todos os exemplares de um acervo
        List<Exemplar> findByAcervo_IdAcervo(Integer idAcervo);

        // Existe exemplar do acervo em uma das situações informadas?
        boolean existsByAcervo_IdAcervoAndSituacaoIn(Integer idAcervo, Collection<SituacaoExemplar> situacoes);

        @EntityGraph(attributePaths = { "acervo" })
        Page<Exemplar> findByAcervo_TituloAcervoContainingIgnoreCase(String titulo, Pageable pageable);

        @EntityGraph(attributePaths = { "acervo" })
        Page<Exemplar> findBySituacao(SituacaoExemplar situacao, Pageable pageable);

        @EntityGraph(attributePaths = { "acervo" })
        Page<Exemplar> findByIdTombo(String idTombo, Pageable pageable);

        // pega o MAIOR tombo existente para um acervo (para gerar o próximo)
        // Ex.: prefixo "15-" → retorna "15-0027" se for o último; ordenação
        // lexicográfica funciona
        Optional<Exemplar> findFirstByAcervo_IdAcervoAndIdTomboStartingWithOrderByIdTomboDesc(Integer idAcervo,
                        String prefixo);

        long countByAcervo_IdAcervoAndSituacao(Integer idAcervo, SituacaoExemplar situacao);

        List<Exemplar> findByAcervo_IdAcervoAndSituacao(Integer idAcervo, SituacaoExemplar situacao);

        List<Exemplar> findBySituacao(SituacaoExemplar situacao);

}
