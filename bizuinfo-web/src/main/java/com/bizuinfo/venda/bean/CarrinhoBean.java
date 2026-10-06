package com.bizuinfo.venda.bean;

import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.venda.model.ItemVenda;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
@SessionScoped
public class CarrinhoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<ItemVenda> itens = new ArrayList<>();

    public void adicionarProduto(Produto produto, int quantidade) {

        if (quantidade <= 0) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Quantidade inválida",
                            "Informe uma quantidade maior que zero."
                    )
            );

            return;
        }

        Optional<ItemVenda> itemExistente =
                itens.stream()
                        .filter(i ->
                                i.getProduto().getId()
                                        .equals(produto.getId()))
                        .findFirst();

        int quantidadeAtual =
                itemExistente
                        .map(ItemVenda::getQuantidade)
                        .orElse(0);

        int quantidadeFinal = quantidadeAtual + quantidade;

        if (quantidadeFinal > produto.getEstoqueAtual()) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_WARN,
                            "Estoque insuficiente",
                            "Estoque disponível para "
                                    + produto.getNome()
                                    + ": "
                                    + produto.getEstoqueAtual()
                    )
            );

            return;
        }

        if (itemExistente.isPresent()) {

            ItemVenda item = itemExistente.get();

            item.setQuantidade(quantidadeFinal);

            item.setSubtotal(
                    item.getQuantidade()
                            * item.getValorUnitario()
            );

        } else {

            ItemVenda item = new ItemVenda();

            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setValorUnitario(produto.getPreco());

            item.setSubtotal(
                    quantidade * produto.getPreco()
            );

            itens.add(item);
        }

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Produto adicionado",
                        produto.getNome() + " adicionado ao carrinho."
                )
        );
    }

    public void removerItem(ItemVenda item) {

        if (item.getQuantidade() > 1) {

            item.setQuantidade(
                    item.getQuantidade() - 1
            );

            item.setSubtotal(
                    item.getQuantidade()
                            * item.getValorUnitario()
            );

            return;
        }

        itens.remove(item);
    }

    public void limparCarrinho() {
        itens.clear();
    }

    public void atualizarQuantidade(ItemVenda item) {

        if (item.getQuantidade() <= 0) {

            removerItem(item);
            return;
        }

        item.setSubtotal(
                item.getQuantidade()
                        * item.getValorUnitario()
        );
    }

    public double getValorTotal() {

        return itens.stream()
                .mapToDouble(ItemVenda::getSubtotal)
                .sum();
    }

    public int getQuantidadeItens() {

        return itens.stream()
                .mapToInt(ItemVenda::getQuantidade)
                .sum();
    }

    public boolean isCarrinhoVazio() {
        return itens.isEmpty();
    }

    public List<ItemVenda> getItens() {
        return itens;
    }
}