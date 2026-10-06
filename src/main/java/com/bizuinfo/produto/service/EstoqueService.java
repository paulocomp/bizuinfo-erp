package com.bizuinfo.produto.service;

import com.bizuinfo.infra.service.EmailService;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.auditoria.service.LogAuditoriaService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class EstoqueService {

    @Inject
    private ProdutoDAO produtoDAO;

    @EJB
    private LogAuditoriaService logAuditoriaService;

    @EJB
    private EmailService emailService;

    private static final String EMAIL_GERENCIA = "bizuinfo.contato@gmail.com";

    public void movimentarEstoque(Long idProduto, int quantidade, Usuario usuario) {

        Optional<Produto> opt = produtoDAO.buscarPorId(idProduto);

        if (opt.isPresent()) {
            Produto produto = opt.get();
            int novoEstoque = produto.getEstoqueAtual() + quantidade;

            if (novoEstoque < 0) {
                novoEstoque = 0;
            }

            int estoqueAnterior = produto.getEstoqueAtual();

            produto.setEstoqueAtual(novoEstoque);
            produtoDAO.salvar(produto);

            String tipoMovimentacao =
                    quantidade >= 0 ? "Entrada" : "Saída";

            logAuditoriaService.registrar(
                    "MOVIMENTACAO_ESTOQUE",
                    tipoMovimentacao
                            + " de "
                            + Math.abs(quantidade)
                            + " unidade(s) no produto '"
                            + produto.getNome()
                            + "' ("
                            + estoqueAnterior
                            + " → "
                            + novoEstoque
                            + ")",
                    usuario.getNome()
            );

            verificarAlerta(produto);
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
