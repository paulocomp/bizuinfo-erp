package com.bizuinfo.acesso.service;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.strategy.AutenticacaoStrategy;
import com.bizuinfo.acesso.strategy.CredenciaisLogin;
import com.bizuinfo.acesso.strategy.LoginGoogle;
import com.bizuinfo.acesso.strategy.LoginToken;
import com.bizuinfo.acesso.strategy.LoginTradicional;
import com.bizuinfo.infra.persistencia.Transacional;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Transacional
public class LoginService {

    @Inject
    private LoginTradicional loginTradicional;

    @Inject
    private LoginGoogle loginGoogle;

    @Inject
    private LoginToken loginToken;

    /** Login com email e senha. */
    public LoginResultado autenticar(String email, String senha) {
        return executar(loginTradicional, CredenciaisLogin.comSenha(email, senha));
    }

    /** Login com email e token de verificação. */
    public LoginResultado autenticarComToken(String email, String token) {
        return executar(loginToken, CredenciaisLogin.comToken(email, token));
    }

    /**
     * Login via Google: devolve o usuário com esse email e, se ainda não
     * existir, cadastra um funcionário já com o email verificado.
     */
    public Usuario entrarComGoogle(String email, String nome) {
        return executar(loginGoogle, CredenciaisLogin.comGoogle(email, nome)).getUsuario();
    }

    private LoginResultado executar(AutenticacaoStrategy estrategia, CredenciaisLogin credenciais) {
        return estrategia.autenticar(credenciais);
    }
}