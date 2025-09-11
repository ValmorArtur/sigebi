package com.biblioteca.service;

import com.biblioteca.model.Exemplar;
import com.biblioteca.model.enums.SituacaoExemplar;
import com.biblioteca.repository.ExemplarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.biblioteca.repository.AcervoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;

@Service
public class ExemplarService {

    @Autowired
    private ExemplarRepository repository;

    @Autowired
    private AcervoRepository acervoRepo;

    @Autowired
    private EmprestimoService emprestimoService;

    @Transactional(readOnly = true)
    public Page<Exemplar> listar(String q, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by("idTombo").descending());

        if (q == null || q.isBlank()) {
            return repository.pageAll(pageable);
        }

        SituacaoExemplar situacao = inferirSituacaoDeTexto(q); // [ALTERAÇÃO]
        return repository.search(q, situacao, pageable); // [ALTERAÇÃO]
    }

    // --------------------------------------------------------------------
    // Heurística para mapear o texto digitado para um Enum:
    // - ignora acentos e caixa
    // - casa com label (ex.: "Disponível") ou nome do enum (DISPONIVEL)
    // - aceita prefixos com >= 4 chars (ex.: "empre" → EMPRESTADO)
    // --------------------------------------------------------------------
    private SituacaoExemplar inferirSituacaoDeTexto(String q) {
        if (q == null)
            return null;
        String norm = normalizar(q);

        for (SituacaoExemplar s : SituacaoExemplar.values()) {
            String nome = s.name().toLowerCase(Locale.ROOT);
            String label = normalizar(s.getLabel());

            if (norm.equals(label) || norm.equals(nome)) {
                return s;
            }
            if (norm.length() >= 1 && (label.startsWith(norm) || nome.startsWith(norm))) {
                return s;
            }
        }
        return null;
    }

    private String normalizar(String v) {
        String n = Normalizer.normalize(v, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    @Transactional(readOnly = true)
    public Optional<Exemplar> buscar(String id) {
        return repository.findById(id); // não lança; só retorna Optional
    }

    @Transactional
    public Exemplar salvar(Exemplar atualizando) {
        if (atualizando.getAcervo() == null || atualizando.getAcervo().getIdAcervo() == null) {
            throw new IllegalArgumentException("Acervo é obrigatório.");
        }
        Integer idAcervo = atualizando.getAcervo().getIdAcervo();

        // valida acervo
        if (!acervoRepo.existsById(idAcervo)) {
            throw new NoSuchElementException("Acervo inexistente: " + idAcervo);
        }

        // >>> Inclusão (idTombo nulo): gerar o PK no formato <idAcervo>-NNNN
        if (atualizando.getIdTombo() == null || atualizando.getIdTombo().isBlank()) {

            if (atualizando.getSituacao() == SituacaoExemplar.EMPRESTADO) { // NOVO
                throw new IllegalStateException("Não é permitido incluir exemplar já na situação 'Emprestado'."); // NOVO
            }
            atualizando.setIdTombo(gerarProximoTombo(idAcervo));
        } else {
            // >>> Atualização: validar regras de transição de situação
            Exemplar existente = repository.findById(atualizando.getIdTombo())
                    .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado."));

            if (atualizando.getSituacao() == SituacaoExemplar.EMPRESTADO
                    && existente.getSituacao() != SituacaoExemplar.EMPRESTADO) {
                throw new IllegalStateException("Para emprestar, use o módulo 'Empréstimos'.");
            }
            if (existente.getSituacao() == SituacaoExemplar.EMPRESTADO
                    && atualizando.getSituacao() == SituacaoExemplar.DISPONIVEL) {
                emprestimoService.devolverAtivoDoTombo(existente.getIdTombo());
                atualizando.setSituacao(SituacaoExemplar.DISPONIVEL);
            }

            if (atualizando.getSituacao() != null &&
                    existente.getSituacao() != atualizando.getSituacao()) {

                if (atualizando.getSituacao() == SituacaoExemplar.DISPONIVEL &&
                        (existente.getSituacao() == SituacaoExemplar.RESERVADO)) {

                    throw new IllegalStateException(
                            "Não é possível marcar como 'Disponível' enquanto houver reserva ativa.");
                }
            }
        }

        // default de situação
        if (atualizando.getSituacao() == null) {
            atualizando.setSituacao(SituacaoExemplar.DISPONIVEL);
        }

        return repository.save(atualizando);
    }

    public Exemplar buscarOuFalhar(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Exemplar não encontrado: " + id));
    }

    @Transactional
    public Exemplar atualizar(String id, Exemplar payload) {
        Exemplar atual = buscarOuFalhar(id);

        if (payload.getAcervo() != null && payload.getAcervo().getIdAcervo() != null) {
            Integer novoIdAcervo = payload.getAcervo().getIdAcervo();
            acervoRepo.findById(novoIdAcervo).ifPresent(atual::setAcervo);
            // >>> NÃO GERAR novo idTombo: PK não muda em edição
        }

        if (payload.getSituacao() != null) {
            // regras básicas já aplicadas em salvar(), mas aqui não precisamos da transição
            // complexa
            atual.setSituacao(payload.getSituacao());
        }

        atual.setObservacao(payload.getObservacao());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(String id) {
        Exemplar e = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado."));

        // regra: travar exclusão com empréstimo ou reserva
        if (e.getSituacao() == SituacaoExemplar.EMPRESTADO ||
                e.getSituacao() == SituacaoExemplar.RESERVADO) {
            throw new IllegalStateException(
                    "Exclusão bloqueada: o exemplar está Emprestado ou Reservado.");
        }
        repository.delete(e);
    }

    @Transactional(readOnly = true)
    public boolean hasEmprestadoOuReservado(Integer idAcervo) {
        return repository.existsByAcervo_IdAcervoAndSituacaoIn(
                idAcervo,
                Arrays.asList(SituacaoExemplar.EMPRESTADO, SituacaoExemplar.RESERVADO));
    }

    /**
     * Ajusta a situação de TODOS os exemplares de um acervo.
     * - Se alvo = DESCONTINUADO e strict=true -> falha se houver
     * EMPRESTADO/RESERVADO.
     * - Se alvo = DISPONIVEL -> só em quem não estiver EMPRESTADO/RESERVADO.
     */
    @Transactional
    public void alterarSituacaoPorAcervo(Integer idAcervo,
            SituacaoExemplar situacaoAlvo,
            boolean strictWhenDiscontinuing) {

        List<Exemplar> exemplares = repository.findByAcervo_IdAcervo(idAcervo);

        if (situacaoAlvo == SituacaoExemplar.DESCONTINUADO) {
            if (strictWhenDiscontinuing && hasEmprestadoOuReservado(idAcervo)) {
                throw new IllegalStateException(
                        "Não é possível inativar o acervo: há exemplar Emprestado ou Reservado.");
            }
            for (Exemplar ex : exemplares) {
                if (ex.getSituacao() != SituacaoExemplar.EMPRESTADO &&
                        ex.getSituacao() != SituacaoExemplar.RESERVADO) {
                    ex.setSituacao(SituacaoExemplar.DESCONTINUADO);
                    repository.save(ex);
                }
            }
        } else if (situacaoAlvo == SituacaoExemplar.DISPONIVEL) {
            for (Exemplar ex : exemplares) {
                if (ex.getSituacao() != SituacaoExemplar.EMPRESTADO &&
                        ex.getSituacao() != SituacaoExemplar.RESERVADO) {
                    ex.setSituacao(SituacaoExemplar.DISPONIVEL);
                    repository.save(ex);
                }
            }
        } else {
            throw new IllegalArgumentException("Situação alvo não suportada para operação em lote: " + situacaoAlvo);
        }
    }

    /**
     * Exclui todos os exemplares do acervo, respeitando as regras (não apaga se
     * EMPRESTADO/RESERVADO).
     */
    @Transactional
    public void excluirTodosPorAcervo(Integer idAcervo) {
        List<Exemplar> exemplares = repository.findByAcervo_IdAcervo(idAcervo);
        for (Exemplar ex : exemplares) {
            excluir(ex.getIdTombo()); // >>> agora o id é String
        }
    }

    /**
     * >>> Tornado público para o endpoint de prévia (/exemplar/proximo-tombo).
     * Calcula o próximo idTombo para um dado acervo, no formato <idAcervo>-NNNN.
     */
    @Transactional(readOnly = true)
    public String gerarProximoTombo(Integer idAcervo) {
        String prefixo = idAcervo + "-";
        Optional<Exemplar> ultimo = repository
                .findFirstByAcervo_IdAcervoAndIdTomboStartingWithOrderByIdTomboDesc(idAcervo, prefixo);

        int proximo = 1;
        if (ultimo.isPresent()) {
            String idTombo = ultimo.get().getIdTombo(); // ex.: "15-0027"
            String sufixo = idTombo.substring(idTombo.indexOf('-') + 1);
            proximo = Integer.parseInt(sufixo) + 1;
        }
        return prefixo + "%04d".formatted(proximo);
    }
}
