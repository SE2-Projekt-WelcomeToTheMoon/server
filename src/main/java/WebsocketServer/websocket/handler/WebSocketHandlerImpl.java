package WebsocketServer.websocket.handler;

import WebsocketServer.services.UserClientService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Verbindung erfolgreich hergestellt: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO handle the messages here
        System.out.println("Nachricht erhalten: " + message.getPayload());
        JSONObject messsage = (JSONObject) message.getPayload();
        if (messsage.get("Username") != null && messsage.get("setUserButtonId") == "2") {
            if (UserClientService.setUsername(messsage.get("Username").toString())) {
                session.sendMessage(new TextMessage("Username set. Please continue."));
            } else session.sendMessage(new TextMessage("Username already in use, please take another one."));
        }
        session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Verbindung getrennt: "+ session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}