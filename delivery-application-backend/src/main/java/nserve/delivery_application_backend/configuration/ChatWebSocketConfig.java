package nserve.delivery_application_backend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class ChatWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Địa chỉ để gửi tin nhắn đến các client
        config.setApplicationDestinationPrefixes("/app"); // Địa chỉ cho client gửi tin nhắn
        config.setUserDestinationPrefix("/user"); // Thiết lập prefix cho user queue
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    private Principal determineUser(
                            HttpServletRequest request,
                            org.springframework.web.socket.WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {
                        String userId = request.getParameter("userId");
                        if (userId != null) {
                            System.out.println("User connected with userId: " + userId);
                        } else {
                            System.out.println("User connected without userId");
                        }
                        return () -> userId; // Set `userId` làm `Principal`
                    }
                })

                .withSockJS();
    }


}


