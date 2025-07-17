package com.VarandaCafeteria.config;

import com.VarandaCafeteria.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery(); // Exemplo: token=eyJhbGciOiJIUzI1...

        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6);
            try {
                String idUsuario = String.valueOf(jwtUtil.extractId(token));
                return new StompPrincipal(idUsuario);
            } catch (Exception e) {
                throw new IllegalArgumentException("Token inválido.");
            }
        }

        throw new IllegalArgumentException("Token não encontrado.");
    }
}
