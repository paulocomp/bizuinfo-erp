package com.bizuinfo.acesso.service;

import com.bizuinfo.acesso.model.TipoToken;
import com.bizuinfo.infra.service.EmailService;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class RecuperarAcessoService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @EJB
    private LinkMagicoService linkMagicoService;

    @EJB
    private EmailService emailService;

    /**
     * @param urlRecuperacao URL completa da página que consome o token;
     *                       o token é acrescentado como parâmetro "token"
     */
    public void enviarLink(String email, String urlRecuperacao) {

        Optional<Usuario> optUsuario =
                usuarioDAO.buscarPorEmail(email);

        if (optUsuario.isEmpty()) {
            return;
        }

        Usuario usuario = optUsuario.get();

        linkMagicoService.gerarToken(
                usuario,
                TipoToken.RECUPERACAO_ACESSO
        );

        usuarioDAO.salvar(usuario);

        String link = urlRecuperacao + "?token=" + usuario.getTokenReset();

        String conteudo = "Clique no link para recuperar acesso:<br><br>"
                        + "<a href='" + link + "'>Recuperar acesso</a>";

        emailService.enviarEmail(
                usuario.getEmail(),
                "Recuperação de acesso",
                conteudo
        );
    }
}