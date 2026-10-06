package com.bizuinfo.usuario.model;

import org.jspecify.annotations.NonNull;

public enum Role {
    FUNCIONARIO(1),
    GERENTE(2),
    ADMIN(3);

    private final int nivel;

    Role(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean temPermissao(@NonNull Role roleExigido) {
        return this.nivel >= roleExigido.nivel;
    }
}
