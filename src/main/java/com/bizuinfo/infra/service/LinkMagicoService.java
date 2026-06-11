package com.bizuinfo.infra.service;

import com.bizuinfo.acesso.model.TipoToken;
import com.bizuinfo.usuario.model.Usuario;

import jakarta.ejb.Stateless;

import java.time.LocalDateTime;
import java.util.UUID;

@Stateless
public class LinkMagicoService {

    private static final int EXPIRACAO_MINUTOS = 15;

    public void gerarToken(Usuario usuario, TipoToken tipo) {

        String token = UUID.randomUUID().toString();
        LocalDateTime expiracao = LocalDateTime.now().plusMinutes(EXPIRACAO_MINUTOS);

        switch (tipo) {

            case VERIFICACAO_EMAIL:
                usuario.setTokenVerificacao(token);
                usuario.setTokenExpiracao(expiracao);
                break;

            case RECUPERACAO_ACESSO:
                usuario.setTokenReset(token);
                break;
        }

    }

    public boolean tokenValido(Usuario u, String token, TipoToken t) {

        if (token == null) {
            return false;
        }

        return switch (t) {

            case VERIFICACAO_EMAIL ->
                token.equals(u.getTokenVerificacao())
                    && tokenNaoExpirado(u.getTokenExpiracao());

            case RECUPERACAO_ACESSO ->
                token.equals(u.getTokenReset());
        };
    }

    public void invalidarToken(Usuario usuario, TipoToken tipo) {
        switch (tipo) {

            case VERIFICACAO_EMAIL:
                usuario.setTokenVerificacao(null);
                usuario.setTokenExpiracao(null);
                break;

            case RECUPERACAO_ACESSO:
                usuario.setTokenReset(null);
                break;
        }
    }

    private boolean tokenNaoExpirado(LocalDateTime expiracao) {
        return expiracao != null && !LocalDateTime.now().isAfter(expiracao);
    }
}