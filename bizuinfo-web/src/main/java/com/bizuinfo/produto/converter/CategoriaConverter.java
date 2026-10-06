package com.bizuinfo.produto.converter;

import com.bizuinfo.produto.service.CategoriaService;
import com.bizuinfo.produto.model.Categoria;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import java.util.Optional;

@FacesConverter(value = "categoriaConverter", managed = true)
public class CategoriaConverter implements Converter<Categoria> {

    @Inject
    private CategoriaService categoriaService;

    @Override
    public Categoria getAsObject(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        Optional<Categoria> optCategoria = categoriaService.buscarPorId(Long.valueOf(value));

        return optCategoria.orElse(null);
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            Categoria categoria) {

        if (categoria == null || categoria.getId() == null) {
            return "";
        }

        return categoria.getId().toString();
    }
}