package com.bizuinfo.acesso.filter;

import com.bizuinfo.acesso.bean.SessaoBean;
import com.bizuinfo.usuario.model.Usuario;

import com.bizuinfo.web.Paginas;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/restrito/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Usuario usuario = null;

        if (session != null) {
            usuario = (Usuario) session.getAttribute(SessaoBean.ATRIBUTO_USUARIO);
        }

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + Paginas.LOGIN);
            return;
        }

        chain.doFilter(request, response);
    }
}