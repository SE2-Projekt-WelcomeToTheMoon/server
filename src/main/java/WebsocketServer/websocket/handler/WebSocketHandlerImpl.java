package WebsocketServer.websocket.handler;

import WebsocketServer.services.GameService;
import WebsocketServer.services.SendMessageService;
import WebsocketServer.services.user.CreateUserService;
import WebsocketServer.services.LobbyService;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import WebsocketServer.services.user.UserListService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

import java.util.Map;

public class WebSocketHandlerImpl implements WebSocketHandler {

    public static GameService gameService;
    public static LobbyService lobbyService;
    @Getter
    public static JSONObject responseMessage;
    private static final Logger logger = LogManager.getLogger(WebSocketHandlerImpl.class);
    private CreateUserService user;

    public WebSocketHandlerImpl() {
        lobbyService = new LobbyService();
        gameService = new GameService();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Verbindung erfolgreich hergestellt: {} ", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.info("Nachricht erhalten: {} ", message.getPayload());

        if (message.getPayload().equals("Test message")) {
            session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
        } else {
            JSONObject messageJson = new JSONObject(message.getPayload().toString());

            String username = null;
            if (messageJson.has("username")) {
                username = messageJson.getString("username");
            }
            String action = messageJson.getString("action");

            //Checks which action was requested by client.
            switch (action) {
                case "registerUser":
                    logger.info("Creating user...");
                    user = new CreateUserService(session, username);
                    if (responseMessage.getBoolean("success")) UserListService.userList.addUser(user);
                    SendMessageService.sendSingleMessage(session, responseMessage);
                    responseMessage = null;
                    break;
                case "joinLobby":
                    logger.info("Case joinLobby: {} ", username);
                    lobbyService.handleJoinLobby(session, messageJson);
                    break;
                case "leaveLobby":
                    logger.info("Case leaveLobby: {} ", username);
                    lobbyService.handleLeaveLobby(session, messageJson);
                    break;
                case "requestLobbyUser":
                    logger.info("Case requestLobbyUser.");
                    lobbyService.handleRequestLobbyUser(session);
                    break;
                case "startGame":
                    logger.info("Case startGame: {} ", username);
                    Map<String, CreateUserService> players = lobbyService.handleStartGame(session, messageJson);
                    gameService.handleStartGame(players);
                    break;
                case "updateUser":
                    logger.info("Case updateGameBoard: {} ", username);
                    gameService.updateUser(username, messageJson.getString("message"));
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
        if (UserListService.userList.getUserBySessionID(session.getId()) != null) {

            //Removes user from lobby
            String username = UserListService.userList.getUserBySessionID(session.getId()).getUsername();
            lobbyService.gamelobby.removePlayerFromLobbyByName(username);
            logger.info("User nicht mehr in der Lobby vorhanden(ConnectionCloses).{}", session.getId());

            UserListService.userList.deleteUser(session.getId());
            logger.info("User gelöscht.");
        }
        logger.info(" Verbindung getrennt.: {} ", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}