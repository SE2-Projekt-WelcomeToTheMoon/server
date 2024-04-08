package WebsocketServer.websocket.handler;


import jakarta.websocket.ContainerProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandlerImpl(), "/welcome-to-the-moon")
                .setAllowedOrigins("*");
        ContainerProvider.getWebSocketContainer().setAsyncSendTimeout(10_000);
    }
}