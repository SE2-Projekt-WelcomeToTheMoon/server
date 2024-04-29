package WebsocketServer.websocket.handler;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.services.SendMessageService;
import WebsocketServer.services.user.CreateUserService;
import WebsocketServer.services.LobbyService;
import lombok.Getter;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import WebsocketServer.services.user.UserListService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {

    public static LobbyService lobbyService;
    @Getter
    public static JSONObject responseMessage;
    private static final Logger logger = LogManager.getLogger(WebSocketHandlerImpl.class);
    private CreateUserService user;

    public WebSocketHandlerImpl(){
        Lobby gameLobby = new Lobby();
        lobbyService = new LobbyService(gameLobby);
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
                    logger.info("Creating user...");
                    user = new CreateUserService(session, username);
                    if(responseMessage.getBoolean("success")) UserListService.userList.addUser(user);
                    SendMessageService.sendSingleMessage(session, responseMessage);
                    responseMessage = null;
                    break;
                case "joinLobby":
                    logger.info("Case joinLobby: {} ",  username );
                    lobbyService.handleJoinLobby(session, messageJson);
                    break;
                case "leaveLobby":
                    logger.info("Case leaveLobby: {} ",  username );
                    lobbyService.handleLeaveLobby(session, messageJson);
                    break;
                case "getNextCard":
                    logger.info("Case getNextCard: {}",username);
                    lobbyService.handleCardDraw(session,messageJson);
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        //Deletes registered user
        if(UserListService.userList.getUserBySessionID(session.getId()) != null){
            UserListService.userList.deleteUser(session.getId());
            logger.info("User gelöscht.");
        }
        //Removes user from lobby
        lobbyService.removeFromLobbyAfterConnectionClosed(session.getId());
        logger.info("User nicht mehr in der Lobby vorhanden.");

        logger.info("Verbindung getrennt: {} ",  session.getId());

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}