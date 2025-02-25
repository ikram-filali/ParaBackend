package parapharmacie.para.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure le broker de messages pour /topic
        config.enableSimpleBroker("/topic");
        // Préfixe pour les endpoints d'application
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entrée WebSocket avec support fallback SockJS
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200") // URL du frontend Angular
                .withSockJS();
    }
}