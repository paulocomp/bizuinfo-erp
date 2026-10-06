package com.bizuinfo.web;

import com.bizuinfo.usuario.model.Role;

import java.util.Map;

public final class Permissoes {

    private Permissoes() {
    }

    private static final Map<String, Role> PERMISSOES = Map.ofEntries(

            // Dashboards
            Map.entry(Paginas.DASHBOARD_ADMIN, Role.ADMIN),
            Map.entry(Paginas.DASHBOARD_GERENTE, Role.GERENTE),
            Map.entry(Paginas.DASHBOARD_FUNCIONARIO, Role.FUNCIONARIO),

            // Auditoria
            Map.entry(Paginas.LOGS_AUDITORIA, Role.ADMIN),

            // Usuários
            Map.entry(Paginas.USUARIOS_ADMIN, Role.ADMIN),
            Map.entry(Paginas.USUARIOS_GERENTE, Role.GERENTE),
            Map.entry(Paginas.PERFIL_USUARIO, Role.FUNCIONARIO),

            // Produtos
            Map.entry(Paginas.CONSULTAR_ESTOQUE, Role.FUNCIONARIO),
            Map.entry(Paginas.GERENCIAR_ESTOQUE, Role.GERENTE),
            Map.entry(Paginas.GERENCIAR_CATEGORIAS, Role.GERENTE),
            Map.entry(Paginas.DETALHES_PRODUTO, Role.FUNCIONARIO),

            // Vendas
            Map.entry(Paginas.CONSULTAR_PRODUTOS_VENDA, Role.FUNCIONARIO),
            Map.entry(Paginas.CHECKOUT, Role.FUNCIONARIO),
            Map.entry(Paginas.MINHAS_VENDAS, Role.FUNCIONARIO),
            Map.entry(Paginas.RECIBO_VENDA, Role.FUNCIONARIO)
    );

    public static Role obterPermissao(String pagina) {
        return PERMISSOES.get(pagina);
    }
}