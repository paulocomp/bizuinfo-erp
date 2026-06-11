package com.bizuinfo.produto.service;

import com.bizuinfo.produto.model.Produto;
import jakarta.ejb.Stateful;

import java.util.ArrayList;
import java.util.List;

@Stateful
public class ProdutoFiltroService {

    public List<Produto> filtrarPorNomeOuCategoria(
            List<Produto> produtos,
            String filtro
    ) {

        if (filtro == null || filtro.isBlank()) {
            return new ArrayList<>(produtos);
        }

        String termo = filtro.toLowerCase();

        return produtos.stream()
                .filter(p ->

                        (p.getNome() != null
                                && p.getNome().toLowerCase().contains(termo)) ||
                                (p.getCategoria() != null
                                        && p.getCategoria().getNome() != null
                                        && p.getCategoria().getNome().toLowerCase().contains(termo))
                )
                .toList();
    }
}