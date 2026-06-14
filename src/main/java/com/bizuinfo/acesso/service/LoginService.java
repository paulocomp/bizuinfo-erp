package com.bizuinfo.acesso.service;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.auditoria.service.LogAuditoriaService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Stateless
public class LoginService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private LogAuditoriaService logAuditoriaService;

    public LoginResultado autenticar(String email, String senha) {

        Optional<Usuario> opt = usuarioDAO.buscarPorEmail(email);

        if (opt.isEmpty()) {

            logAuditoriaService.registrar(
                    "LOGIN_FALHA",
                    "Email não encontrado",
                    email
            );

            return new LoginResultado(
                    ResultadoLogin.EMAIL_NAO_ENCONTRADO,
                    null
            );
        }

        Usuario usuario = opt.get();

        boolean senhaCorreta;

        try {
            senhaCorreta = BCrypt.checkpw(senha, usuario.getSenha());
        } catch (Exception e) {
            senhaCorreta = false;
        }

        if (!senhaCorreta) {

            logAuditoriaService.registrar(
                    "LOGIN_FALHA",
                    "Senha inválida",
                    email
            );

            return new LoginResultado(
                    ResultadoLogin.SENHA_INVALIDA,
                    null
            );
        }

        if (!usuario.getEmailVerificado()) {

            return new LoginResultado(
                    ResultadoLogin.EMAIL_NAO_CONFIRMADO,
                    usuario
            );
        }

        logAuditoriaService.registrar(
                "LOGIN",
                "Usuário logou",
                usuario.getNome()
        );

        return new LoginResultado(
                ResultadoLogin.SUCESSO,
                usuario
        );
    }
}