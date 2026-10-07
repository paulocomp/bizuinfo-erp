package com.bizuinfo.usuario.factory;

import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;


@ApplicationScoped
public class UsuarioFactory {

    public static final String SENHA_LOGIN_GOOGLE = "GOOGLE_LOGIN";
    public Usuario criarFuncionario(String nome, String email, String senha) {

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        usuario.setRole(Role.FUNCIONARIO);
        usuario.setEmailVerificado(false);

        return usuario;
    }

    public Usuario criarFuncionarioGoogle(String nome, String email) {

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(SENHA_LOGIN_GOOGLE);
        usuario.setRole(Role.FUNCIONARIO);
        usuario.setEmailVerificado(true);

        return usuario;
    }
}