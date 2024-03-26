package WebsocketServer.websocket.handler;


import WebsocketServer.messaging.dtos.OutputMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;

import java.util.LinkedList;

public class WebSocketHandlerImpl implements WebSocketHandler {

    LinkedList<WebSocketSession> connection = new LinkedList();


    /**
     * aufgerufen, wenn client sich versucht zu verbinden
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        connection.add(session);
        System.out.println("Mit Server verbunden: "+ session.getId());

    }

    /**
     * wird aufgereufen, wenn Server eine nachricht bekommt
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO handle the messages here
        String json = (String) message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        OutputMessage outputMessage = mapper.readValue(json, OutputMessage.class);


        session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        connection.remove(session);
        System.out.println("Verbindung geschlossen: "+ session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
