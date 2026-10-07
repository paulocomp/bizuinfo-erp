package com.bizuinfo.acesso.strategy;

/**
 * Dados de entrada de uma tentativa de login. Cada estratégia usa
 * apenas os campos que lhe interessam.
 *
 * @param email   email do usuário (usado por todas as estratégias)
 * @param segredo senha (login tradicional) ou token (login por token)
 * @param nome    nome informado pelo provedor (login Google)
 */
public record CredenciaisLogin(String email, String segredo, String nome) {

    public static CredenciaisLogin comSenha(String email, String senha) {
        return new CredenciaisLogin(email, senha, null);
    }

    public static CredenciaisLogin comGoogle(String email, String nome) {
        return new CredenciaisLogin(email, null, nome);
    }

    public static CredenciaisLogin comToken(String email, String token) {
        return new CredenciaisLogin(email, token, null);
    }
}