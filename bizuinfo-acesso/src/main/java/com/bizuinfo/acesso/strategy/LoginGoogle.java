package com.bizuinfo.acesso.strategy;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.usuario.factory.UsuarioFactory;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Login via Google: devolve o usuário com esse email e, se ainda não
 * existir, cadastra um funcionário já com o email verificado.
 */
@ApplicationScoped
public class LoginGoogle extends AutenticacaoStrategy {

    @Inject
    private UsuarioFactory usuarioFactory;

    @Override
    public LoginResultado autenticar(CredenciaisLogin credenciais) {

        Optional<Usuario> opt = usuarioDAO.buscarPorEmail(credenciais.email());

        if (opt.isPresent()) {
            return new LoginResultado(ResultadoLogin.SUCESSO, opt.get());
        }

        Usuario usuario = usuarioFactory.criarFuncionarioGoogle(
                credenciais.nome(),
                credenciais.email()
        );

        usuarioDAO.salvar(usuario);

        return new LoginResultado(ResultadoLogin.SUCESSO, usuario);
    }
}