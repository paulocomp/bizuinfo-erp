package com.bizuinfo.venda.service;

import com.bizuinfo.infra.persistencia.Transacional;
import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.infra.service.EmailService;
import com.bizuinfo.infra.persistencia.ContextoPersistencia;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.venda.dao.ItemVendaDAO;
import com.bizuinfo.venda.dao.PagamentoDAO;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.auditoria.service.LogAuditoriaService;
import com.bizuinfo.infra.exception.RegraNegocioException;
import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.venda.dao.VendaDAO;
import com.bizuinfo.venda.model.ItemVenda;
import com.bizuinfo.venda.model.Pagamento;
import com.bizuinfo.venda.model.Venda;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Transacional
public class VendaService {

    @Inject
    private LogAuditoriaService logAuditoriaService;

    @Inject
    private EmailService emailService;

    @Inject
    private VendaDAO vendaDAO;

    @Inject
    private ItemVendaDAO itemVendaDAO;

    @Inject
    private PagamentoDAO pagamentoDAO;

    @Inject
    private ProdutoDAO produtoDAO;

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private ContextoPersistencia contexto;

    private static final String EMAIL_GERENCIA = "bizuinfo.contato@gmail.com";

    /**
     * Gerente e admin veem todas as vendas; funcionário vê só as próprias.
     */
    private boolean podeVerTodas(Usuario usuario) {
        return usuario.getRole().temPermissao(Role.GERENTE);
    }

    public List<Venda> listarVisiveisPara(Usuario usuario) {
        return podeVerTodas(usuario)
                ? vendaDAO.buscarTodas()
                : vendaDAO.buscarPorUsuario(usuario.getId());
    }

    public List<Venda> listarPorPeriodoVisiveisPara(Usuario usuario,
                                                    LocalDateTime inicio,
                                                    LocalDateTime fim) {

        List<Venda> vendas = vendaDAO.buscarPorPeriodo(inicio, fim);

        if (podeVerTodas(usuario)) {
            return vendas;
        }

        return vendas.stream()
                .filter(v -> v.getUsuario().getId().equals(usuario.getId()))
                .toList();
    }

    public List<Venda> listarParaDashboard(Usuario usuario) {
        return vendaDAO.buscarVendasParaDashboard(
                podeVerTodas(usuario) ? null : usuario.getId()
        );
    }

    public Venda buscarReciboPara(Long vendaId, Usuario usuario) {

        Venda venda = vendaDAO.buscarCompletamente(vendaId)
                .orElseThrow(() -> new RegraNegocioException("Venda não encontrada"));

        if (!podeVerTodas(usuario) && !venda.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Acesso negado.");
        }

        return venda;
    }

    public Venda finalizarVenda(
            Venda venda,
            Pagamento pagamento,
            List<ItemVenda> itens
    ) {

        if (venda == null) {
            throw new RegraNegocioException("Venda não informada.");
        }

        if (venda.getUsuario() == null) {
            throw new RegraNegocioException("Usuário da venda não foi informado.");
        }

        try {

            double valorTotal = 0.0;

            for (ItemVenda item : itens) {

                Produto produto = produtoDAO.buscarPorId(item.getProduto().getId())
                        .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));

                if (produto.getEstoqueAtual() < item.getQuantidade()) {
                    throw new RegraNegocioException("Estoque insuficiente para: " + produto.getNome());
                }

                double subtotal = item.getQuantidade() * produto.getPreco();

                item.setValorUnitario(produto.getPreco());
                item.setSubtotal(subtotal);

                valorTotal += subtotal;

                // produto está gerenciado: a baixa é gravada no commit
                produto.setEstoqueAtual(produto.getEstoqueAtual() - item.getQuantidade());

                contexto.aposCommit(() -> verificarAlerta(produto));
            }

            venda.setValorTotal(valorTotal);

            venda.setUsuario(
                    usuarioDAO.buscarPorId(venda.getUsuario().getId())
                            .orElseThrow(() -> new RegraNegocioException("Usuário da venda não encontrado."))
            );

            vendaDAO.inserir(venda);

            for (ItemVenda item : itens) {
                item.setVenda(venda);
                itemVendaDAO.salvar(item);
            }

            pagamento.setVenda(venda);
            pagamentoDAO.inserir(pagamento);

            logAuditoriaService.registrar(
                    "VENDA_FINALIZADA",
                    String.format(
                            "Venda #%d finalizada no valor de R$ %.2f contendo %d item(ns)",
                            venda.getId(),
                            venda.getValorTotal(),
                            itens.size()
                    ),
                    venda.getUsuario().getNome()
            );

            return venda;

        } catch (RegraNegocioException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao finalizar venda.", e);
        }
    }

    private void verificarAlerta(Produto produto) {
        if (produto.getEstoqueAtual() <= produto.getEstoqueMinimo()) {
            String assunto = "ALERTA ERP - Estoque Baixo: " + produto.getNome();
            String mensagem = String.format(
                    "<h3>Aviso de Estoque Mínimo Atingido</h3>" +
                            "<p>O produto <b>%s</b> atingiu ou está abaixo do limite de segurança.</p>" +
                            "<ul>" +
                            "<li><b>Estoque Atual:</b> %d unidades</li>" +
                            "<li><b>Estoque Mínimo:</b> %d unidades</li>" +
                            "</ul>" +
                            "<p>Por favor, providencie o reabastecimento junto ao fornecedor.</p>",
                    produto.getNome(), produto.getEstoqueAtual(), produto.getEstoqueMinimo()
            );

            String[] emailsDestino = {EMAIL_GERENCIA, "miguel.rspp@gmail.com", "202320637511@uezo.edu.com"};

            for (String email : emailsDestino) {
                emailService.enviarEmail(email, assunto, mensagem);
            }
        }
    }
}