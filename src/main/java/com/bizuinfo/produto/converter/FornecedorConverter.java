package com.bizuinfo.produto.converter;

import com.bizuinfo.produto.dao.FornecedorDAO;
import com.bizuinfo.produto.model.Categoria;
import com.bizuinfo.produto.model.Fornecedor;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import java.util.Optional;

@FacesConverter("fornecedorConverter")
public class FornecedorConverter implements Converter<Fornecedor> {

    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();

    @Override
    public Fornecedor getAsObject(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        Optional<Fornecedor> optFornecedor = fornecedorDAO.buscarPorId(Long.valueOf(value));

        return optFornecedor.orElse(null);
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Fornecedor fornecedor) {

        if (fornecedor == null || fornecedor.getId() == null) {
            return "";
        }

        return fornecedor.getId().toString();
    }
}