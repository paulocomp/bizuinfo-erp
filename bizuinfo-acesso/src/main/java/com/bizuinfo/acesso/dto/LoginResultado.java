package com.bizuinfo.acesso.dto;

import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.usuario.model.Usuario;

public class LoginResultado {

    private final ResultadoLogin resultado;
    private final Usuario usuario;

    public LoginResultado(ResultadoLogin resultado, Usuario usuario) {
        this.resultado = resultado;
        this.usuario = usuario;
    }

    public ResultadoLogin getResultado() {
        return resultado;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}