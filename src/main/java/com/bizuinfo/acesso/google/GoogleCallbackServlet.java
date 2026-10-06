package com.bizuinfo.acesso.google;

import com.bizuinfo.acesso.bean.SessaoBean;
import com.bizuinfo.acesso.service.LoginService;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

@WebServlet("/google/callback")
public class GoogleCallbackServlet extends HttpServlet {

    @EJB
    private LoginService loginService;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String code = request.getParameter("code");

        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");

        String redirectUri = "http://localhost:8080/bizuinfo_erp_war_exploded/google/callback";

        System.out.println("CODE = " + code);
        System.out.println("CODE = " + code);
        System.out.println("CODE = " + code);

        if (code == null) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/publico/acesso/login.xhtml");
            return;
        }

        String body = "code=" + URLEncoder.encode(code, UTF_8)
                + "&client_id=" + URLEncoder.encode(clientId, UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, UTF_8)
                + "&grant_type=authorization_code";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {

            HttpResponse<String> tokenResponse = client.send(
                    tokenRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("STATUS = " + tokenResponse.statusCode());

            try (JsonReader reader = Json.createReader(
                    new java.io.StringReader(tokenResponse.body()))) {

                JsonObject json = reader.readObject();

                String accessToken = json.getString("access_token");

                HttpRequest userInfoRequest = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "https://www.googleapis.com/oauth2/v3/userinfo"))
                        .header(
                                "Authorization",
                                "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> userInfoResponse = client.send(
                        userInfoRequest,
                        HttpResponse.BodyHandlers.ofString()
                );

                System.out.println("USER INFO STATUS = "
                        + userInfoResponse.statusCode());

                System.out.println(userInfoResponse.body());

                try (JsonReader userReader = Json.createReader(
                        new java.io.StringReader(userInfoResponse.body()))) {

                    JsonObject userJson = userReader.readObject();

                    String email = userJson.getString("email");
                    String nome = userJson.getString("given_name");

                    System.out.println("EMAIL = " + email);
                    System.out.println("NOME = " + nome);

                    Usuario usuario = loginService.entrarComGoogle(email, nome);

                    // LOGIN
                    HttpSession session = request.getSession(true);

                    session.setAttribute(SessaoBean.ATRIBUTO_USUARIO, usuario);

                    String dashboard = switch (usuario.getRole()) {
                        case ADMIN -> "/restrito/dashboard/dashboard_admin.xhtml";
                        case GERENTE -> "/restrito/dashboard/dashboard_gerente.xhtml";
                        default -> "/restrito/dashboard/dashboard_funcionario.xhtml";
                    };

                    response.sendRedirect(
                            request.getContextPath() + dashboard
                    );

                    return;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
