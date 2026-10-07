package com.bizuinfo.acesso.strategy;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/** Login com email e senha. */
@ApplicationScoped
public class LoginTradicional extends AutenticacaoStrategy {

    @Override
    public LoginResultado autenticar(CredenciaisLogin credenciais) {

        String email = credenciais.email();

        Optional<Usuario> opt = usuarioDAO.buscarPorEmail(email);

        if (opt.isEmpty()) {
            return falha(ResultadoLogin.EMAIL_NAO_ENCONTRADO, "Email não encontrado", email);
        }

        Usuario usuario = opt.get();

        boolean senhaCorreta;

        try {
            senhaCorreta = BCrypt.checkpw(credenciais.segredo(), usuario.getSenha());
        } catch (Exception e) {
            senhaCorreta = false;
        }

        if (!senhaCorreta) {
            return falha(ResultadoLogin.SENHA_INVALIDA, "Senha inválida", email);
        }

        if (!usuario.getEmailVerificado()) {
            return new LoginResultado(ResultadoLogin.EMAIL_NAO_CONFIRMADO, usuario);
        }

        return sucesso(usuario);
    }
}