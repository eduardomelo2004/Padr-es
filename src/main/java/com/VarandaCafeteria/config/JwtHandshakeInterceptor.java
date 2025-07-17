package com.VarandaCafeteria.config;

import com.VarandaCafeteria.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, org.springframework.http.server.ServerHttpResponse response,
                                   org.springframework.web.socket.WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpRequest = servletRequest.getServletRequest();
                String token = httpRequest.getParameter("token");
                if (token != null) {
                    Long userId = jwtUtil.extractId(token);
                    attributes.put("user", new StompPrincipal(String.valueOf(userId)));
                }
            }
            return super.beforeHandshake(request, response, wsHandler, attributes);
        }catch (Exception e) {
            e.printStackTrace(); // ou logue adequadamente
            return false; // impede o handshake
        }
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> auth = accessor.getNativeHeader("Authorization");
            if (auth == null || auth.isEmpty()) {
                auth = accessor.getNativeHeader("token"); // alternativa
            }
            if (auth != null && !auth.isEmpty()) {
                String token = auth.get(0).replace("Bearer ", "");
                try {
                    Long userId = jwtUtil.extractId(token);
                    accessor.setUser(new StompPrincipal(String.valueOf(userId)));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid token");
                }
            }
        } else {
            if (accessor.getUser() == null && accessor.getSessionAttributes() != null) {
                Object user = accessor.getSessionAttributes().get("user");
                if (user instanceof Principal principal) {
                    accessor.setUser(principal);
                }
            }
        }
        return message;
    }

}