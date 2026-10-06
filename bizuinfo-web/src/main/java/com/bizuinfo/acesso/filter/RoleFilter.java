package com.bizuinfo.acesso.filter;

import com.bizuinfo.acesso.bean.SessaoBean;
import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.web.Permissoes;
import com.bizuinfo.web.Paginas;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/restrito/*")
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + Paginas.LOGIN);
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute(SessaoBean.ATRIBUTO_USUARIO);

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + Paginas.LOGIN);
            return;
        }

        String paginaAtual = req.getRequestURI().replace(req.getContextPath(), "");
        Role permissaoNecessaria = Permissoes.obterPermissao(paginaAtual);

        if (permissaoNecessaria != null && !usuario.getRole().temPermissao(permissaoNecessaria)) {
            res.sendRedirect(req.getContextPath() + Paginas.ACESSO_NEGADO);
            return;
        }

        chain.doFilter(request, response);
    }
}