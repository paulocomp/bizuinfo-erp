package com.bizuinfo.acesso.service;

import com.bizuinfo.acesso.model.TipoToken;
import com.bizuinfo.infra.service.EmailService;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class ConfirmarEmailService {

    @Inject
    private LinkMagicoService linkMagicoService;

    @Inject
    private EmailService emailService;

    @Inject
    private UsuarioDAO usuarioDAO;

    /**
     * @param urlConfirmacao URL completa da página que consome o token;
     *                       o token é acrescentado como parâmetro "token"
     */
    public void enviarLink(String email, String urlConfirmacao) {

        Optional<Usuario> optUsuario = usuarioDAO.buscarPorEmail(email);

        if (optUsuario.isEmpty()) {
            return;
        }

        Usuario usuario = optUsuario.get();

        linkMagicoService.gerarToken(
            usuario,
            TipoToken.VERIFICACAO_EMAIL
        );

        usuarioDAO.salvar(usuario);

        String link = urlConfirmacao + "?token=" + usuario.getTokenVerificacao();

        String text = "Clique no link para confirmar seu email da conta BizuInfo:<br><br>"
                    + "<a href='" + link + "'>Confirmar Email</a>";

        emailService.enviarEmail(
            email,
            "Confirmação de Email",
            text
        );
    }
}
