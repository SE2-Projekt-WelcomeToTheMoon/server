package WebsocketServer.websocket.handler;

import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WebSockerHandlerConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(new WebSocketHandlerImpl(), "/websocket").setAllowedOrigins("*");
        // "/websocket" ist der Endpunkt für die Clients, um eine Verbindung zum Server herzustellen.
    }
}
