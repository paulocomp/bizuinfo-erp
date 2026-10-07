package com.bizuinfo.acesso.strategy;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Login com email e token de verificação (o mesmo enviado por email).
 * O token é de uso único: ao ser aceito, é consumido e o email do
 * usuário passa a constar como verificado.
 */
@ApplicationScoped
public class LoginToken extends AutenticacaoStrategy {

    @Override
    public LoginResultado autenticar(CredenciaisLogin credenciais) {

        String email = credenciais.email();
        String token = credenciais.segredo();

        Optional<Usuario> opt = usuarioDAO.buscarPorEmail(email);

        if (opt.isEmpty()) {
            return falha(ResultadoLogin.EMAIL_NAO_ENCONTRADO, "Email não encontrado", email);
        }

        Usuario usuario = opt.get();

        if (!tokenConfere(token, usuario.getTokenVerificacao())) {
            return falha(ResultadoLogin.TOKEN_INVALIDO, "Token inválido", email);
        }

        LocalDateTime expiracao = usuario.getTokenExpiracao();

        if (expiracao == null || expiracao.isBefore(LocalDateTime.now())) {
            return falha(ResultadoLogin.TOKEN_EXPIRADO, "Token expirado", email);
        }

        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacao(null);
        usuario.setTokenExpiracao(null);

        usuarioDAO.salvar(usuario);

        return sucesso(usuario);
    }

    /** Comparação em tempo constante, para não vazar o token por timing. */
    private boolean tokenConfere(String informado, String esperado) {

        if (informado == null || esperado == null) {
            return false;
        }

        return MessageDigest.isEqual(
                informado.getBytes(StandardCharsets.UTF_8),
                esperado.getBytes(StandardCharsets.UTF_8)
        );
    }
}