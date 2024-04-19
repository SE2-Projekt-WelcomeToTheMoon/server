package WebsocketServer.websocket.handler;

import WebsocketServer.services.user.CreateUserService;
import WebsocketServer.services.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import WebsocketServer.services.user.UserListService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {

    private final LobbyService lobbyService;
    private static final Logger logger = LogManager.getLogger(WebSocketHandlerImpl.class);

    public WebSocketHandlerImpl(){
        this.lobbyService = new LobbyService();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Verbindung erfolgreich hergestellt: {} " ,  session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("Nachricht erhalten: {} " , message.getPayload());

        if (message.getPayload().equals("Test message")) {
            session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
        } else {
            JSONObject messageJson = new JSONObject(message.getPayload().toString());

            String username = messageJson.getString("username");
            String action = messageJson.getString("action");

            //Checks which action was requested by client.
            switch (action) {
                case "registerUser":
                    logger.info("Setting Username...");
                    JSONObject responseMessage = UserListService.userList.addUser(new CreateUserService(messageJson.getString("username")));
                    session.sendMessage(new TextMessage(responseMessage.toString()));
                    break;
                case "joinLobby":
                    logger.info("Case joinLobby: {} ",  username );
                    lobbyService.handleJoinLobby(session, messageJson);
                    break;
                case "leaveLobby":
                    logger.info("Case leaveLobby: {} ",  username );
                    lobbyService.handleLeaveLobby(session, messageJson);
                    break;
                default:
                    JSONObject response = new JSONObject();
                    response.put("error", "Unbekannte Aktion");
                    response.put("action", action);
                    session.sendMessage(new TextMessage(response.toString()));
                    logger.info("Unbekannte Aktion erhalten: {}, {}", action, messageJson.getString(username));
                    break;
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        //TODO handle transport error
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        logger.info("Verbindung getrennt: {} ",  session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}