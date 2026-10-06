package com.bizuinfo.web;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

public class Paginas {


    // Prefixos de acesso
    private static final String RESTRITO = "/restrito/";
    private static final String PUBLICO = "/publico/";


    // Módulo
    private static final String ACESSO    = "acesso/";
    private static final String PRODUTO   = "produto/";
    private static final String USUARIO   = "usuario/";
    private static final String ESTOQUE   = "estoque/";
    private static final String AUDITORIA = "auditoria/";
    private static final String DASHBOARD = "dashboard/";
    private static final String VENDA = "venda/";


    // público                 NOME                         ACESSO    MÓDULO   PÁGINA
    public static final String LOGIN                      = PUBLICO + ACESSO + "login.xhtml";
    public static final String CADASTRO                   = PUBLICO + ACESSO + "cadastro.xhtml";
    public static final String ACESSO_NEGADO              = PUBLICO + ACESSO + "acesso_negado.xhtml";
    public static final String CONFIRMAR_EMAIL            = PUBLICO + ACESSO + "confirmar_email.xhtml";
    public static final String RECUPERAR_ACESSO           = PUBLICO + ACESSO + "recuperar_acesso.xhtml";
    public static final String CONSUMIR_TOKEN_CONFIRMACAO = PUBLICO + ACESSO + "consumir_token_confirmacao.xhtml";
    public static final String CONSUMIR_TOKEN_RECUPERACAO = PUBLICO + ACESSO + "consumir_token_recuperacao.xhtml";


    // restrito                NOME                        ACESSO     MÓDULO      PÁGINA
    public static final String LOGS_AUDITORIA           = RESTRITO + AUDITORIA + "logs.xhtml";
    public static final String DASHBOARD_ADMIN          = RESTRITO + DASHBOARD + "dashboard_admin.xhtml";
    public static final String DASHBOARD_FUNCIONARIO    = RESTRITO + DASHBOARD + "dashboard_funcionario.xhtml";
    public static final String DASHBOARD_GERENTE        = RESTRITO + DASHBOARD + "dashboard_gerente.xhtml";
    public static final String DETALHES_PRODUTO         = RESTRITO + PRODUTO   + "detalhes_produto.xhtml";
    public static final String GERENCIAR_CATEGORIAS     = RESTRITO + PRODUTO   + "gerenciar_categorias.xhtml";
    public static final String CONSULTAR_ESTOQUE        = RESTRITO + ESTOQUE   + "consultar_estoque.xhtml";
    public static final String GERENCIAR_ESTOQUE        = RESTRITO + ESTOQUE   + "gerenciar_estoque.xhtml";
    public static final String PERFIL_USUARIO           = RESTRITO + USUARIO   + "perfil_usuario.xhtml";
    public static final String USUARIOS_ADMIN           = RESTRITO + USUARIO   + "usuarios_admin.xhtml";
    public static final String USUARIOS_GERENTE         = RESTRITO + USUARIO   + "usuarios_gerente.xhtml";
    public static final String CONSULTAR_PRODUTOS_VENDA = RESTRITO + VENDA     + "consultar_produtos_venda.xhtml";
    public static final String CHECKOUT                 = RESTRITO + VENDA     + "checkout.xhtml";
    public static final String MINHAS_VENDAS            = RESTRITO + VENDA     + "minhas_vendas.xhtml";
    public static final String RECIBO_VENDA             = RESTRITO + VENDA     + "recibo_venda.xhtml";

    /**
     * Monta a URL completa de uma página a partir da requisição atual,
     * por exemplo http://localhost:8080/bizuinfo_erp_war_exploded/publico/acesso/login.xhtml.
     * Usada nos links enviados por email, que precisam funcionar fora do navegador.
     */
    public static String urlAbsoluta(String pagina) {

        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

        String scheme = ctx.getRequestScheme();
        int porta = ctx.getRequestServerPort();
        boolean portaPadrao = ("http".equals(scheme) && porta == 80)
                || ("https".equals(scheme) && porta == 443);

        return scheme + "://" + ctx.getRequestServerName()
                + (portaPadrao ? "" : ":" + porta)
                + ctx.getRequestContextPath()
                + pagina;
    }
}