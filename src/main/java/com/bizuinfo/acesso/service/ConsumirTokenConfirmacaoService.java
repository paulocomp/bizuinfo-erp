package com.bizuinfo.acesso.service;

import com.bizuinfo.acesso.model.TipoToken;
import com.bizuinfo.infra.service.LinkMagicoService;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class ConsumirTokenConfirmacaoService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private LinkMagicoService linkMagicoService;

    public Usuario validarToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        Optional<Usuario> opt = usuarioDAO.buscarPorToken(token);

        if (opt.isEmpty()) {
            return null;
        }

        Usuario usuario = opt.get();

        boolean tokenValido = linkMagicoService.tokenValido(
            usuario,
            token,
            TipoToken.VERIFICACAO_EMAIL
        );

        if (!tokenValido) {
            return null;
        }

        usuario.setEmailVerificado(true);

        linkMagicoService.invalidarToken(
            usuario,
            TipoToken.VERIFICACAO_EMAIL
        );

        usuarioDAO.salvar(usuario);

        return usuario;
    }
}