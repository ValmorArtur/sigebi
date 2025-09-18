package com.biblioteca.service;

import com.biblioteca.model.*;
import com.biblioteca.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.model.enums.SituacaoExemplar;

import java.time.LocalDate;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepo;
    @Autowired
    private ExemplarRepository exemplarRepo;
    @Autowired
    private UsuarioRepository usuarioRepo; // supondo que exista

    public Page<Emprestimo> listarAtivos(String filtro, int page, int size) {
        Pageable p = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("dataEmprestimo").descending());
        return emprestimoRepo.pageAtivos(filtro, p);
    }

    public Page<Emprestimo> listarDevolvidos(String filtro, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return emprestimoRepo.pageDevolvidos(filtro, pageable);
    }

    @Transactional
    public Emprestimo emprestar(String idTombo, Integer idUsuario, LocalDate prevista, String observacao) {
        Exemplar ex = exemplarRepo.findById(idTombo)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));
        if (ex.getSituacao() != SituacaoExemplar.DISPONIVEL) {
            throw new IllegalStateException("Exemplar não está disponível para empréstimo");
        }
        if (emprestimoRepo.existsByExemplarIdTomboAndDataDevolucaoIsNull(idTombo)) {
            throw new IllegalStateException("Já existe empréstimo ativo para este tombo");
        }
        Usuario user = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (prevista == null || !prevista.isAfter(LocalDate.now().minusDays(1))) {
            prevista = LocalDate.now().plusDays(7);
        }

        // atualiza exemplar
        ex.setSituacao(SituacaoExemplar.EMPRESTADO);
        exemplarRepo.save(ex);

        Emprestimo emp = new Emprestimo();
        emp.setExemplar(ex);
        emp.setUsuario(user);
        emp.setDataPrevistaDevolucao(prevista);
        emp.setObservacao(observacao);
        return emprestimoRepo.save(emp);
    }

    @Transactional
    public void devolver(Integer idEmprestimo) {
        Emprestimo emp = emprestimoRepo.findByIdEmprestimoAndDataDevolucaoIsNull(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não ativo"));
        emp.setDataDevolucao(LocalDate.now());
        emprestimoRepo.save(emp);

        // volta exemplar para disponível
        Exemplar ex = emp.getExemplar();
        ex.setSituacao(SituacaoExemplar.DISPONIVEL);
        exemplarRepo.save(ex);
    }

    @Transactional
    public void excluir(Integer idEmprestimo) {
        // por regra, só permitimos excluir empréstimos ENCERRADOS
        Emprestimo emp = emprestimoRepo.findById(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));
        if (emp.getDataDevolucao() == null) {
            throw new IllegalStateException("Não é possível excluir empréstimo ativo");
        }
        emprestimoRepo.delete(emp);
    }

    @Transactional
    public void devolverAtivoDoTombo(String idTombo) {
        Emprestimo emp = emprestimoRepo
                .findFirstByExemplar_IdTomboAndDataDevolucaoIsNull(idTombo)
                .orElseThrow(() -> new IllegalStateException("Não há empréstimo ativo para este exemplar."));
        devolver(emp.getIdEmprestimo()); // usa seu método já existente
    }

    @Transactional
    public void removerDevolucao(Integer idEmprestimo) {
        Emprestimo e = emprestimoRepo.findById(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado."));

        if (e.getDataDevolucao() == null) {
            throw new IllegalStateException("Este empréstimo ainda não foi devolvido.");
        }

        // ⚠️ Regra: só reabrir se NÃO houver outro empréstimo ATIVO do mesmo tombo
        String idTombo = e.getExemplar().getIdTombo();

        emprestimoRepo.findFirstByExemplar_IdTomboAndDataDevolucaoIsNull(idTombo)
                .filter(ativo -> !ativo.getIdEmprestimo().equals(e.getIdEmprestimo()))
                .ifPresent(ativo -> {
                    throw new IllegalStateException(
                            "Não é possível remover a devolução do empréstimo #" + idEmprestimo +
                                    " porque já existe empréstimo ativo (#" + ativo.getIdEmprestimo() +
                                    ") para o tombo " + idTombo + ". Devolva/encerre o empréstimo ativo antes.");
                });

        // Desfaz a devolução (reabre este empréstimo)
        e.setDataDevolucao(null);

        // Ajusta situação do exemplar para EMPRESTADO
        Exemplar ex = e.getExemplar();
        if (ex != null) {
            ex.setSituacao(SituacaoExemplar.EMPRESTADO);
            exemplarRepo.save(ex);
        }

        emprestimoRepo.save(e);
    }

    // Renova um empréstimo ativo: encerra o atual e abre um novo para o
    // mesmo exemplar/usuário
    @Transactional
    public Emprestimo renovar(Integer idEmprestimo, LocalDate novaPrevista, String observacao) {
        // Garante que o empréstimo a renovar está ATIVO
        Emprestimo atual = emprestimoRepo.findByIdEmprestimoAndDataDevolucaoIsNull(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não está ativo."));

        // Dados do empréstimo atual
        String idTombo = atual.getExemplar().getIdTombo();
        Integer idUsuario = atual.getUsuario().getIdUsuario();

        // Devolve o empréstimo atual (isso marca dataDevolucao = hoje e coloca
        // exemplar como DISPONIVEL)
        devolver(idEmprestimo);

        // Cria um NOVO empréstimo para o mesmo exemplar/usuário (emprestar já setará
        // EMPRESTADO)
        return emprestar(idTombo, idUsuario, novaPrevista, observacao);
    }

}
