package websocketserver.websocket.handler;

import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import websocketserver.services.GameService;
import websocketserver.services.SendMessageService;
import websocketserver.services.json.ActionValues;
import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;
import websocketserver.services.LobbyService;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import websocketserver.services.user.UserListService;
import org.json.JSONObject;
import org.springframework.web.socket.*;
import java.util.concurrent.TimeUnit;

import java.util.Map;

public class WebSocketHandlerImpl implements WebSocketHandler {

    public static GameService gameService;
    public static LobbyService lobbyService;
    @Getter
    public static JSONObject responseMessage;
    private static final Logger logger = LogManager.getLogger(WebSocketHandlerImpl.class);
    private CreateUserService user;
    private int reconnTry;

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

                case "reconnect":
                    logger.info("Case reconnect: {} ", username);
                    reconnTry++;
                    if(reconnectUser(session, username)) {
                        logger.info("User {} reconnected.", username);
                    }
                    logger.error("User {} not reconnected.", username);
                    if(reconnTry == 5){
                        logger.error("User {} reconnect timed out.", username);
                        break;
                    }
                    break;

                case "disconnect":
                    logger.info("User {} is disconnecting from server.", username);
                    if(disconnectUser(session, username)){
                        logger.info("User {} disconnected from server.", username);
                    }
                    else logger.error("User {} not disconnected.", username);
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
        logger.error("Connection to User interrupted, attempting reconnect...");

//        if (UserListService.userList.getUserBySessionID(session.getId()) != null) {
//            //Removes user from lobby
//            if (removeUserFromLobby(session)) {
//                logger.info("User nicht mehr in der Lobby vorhanden(ConnectionCloses).{}", session.getId());
//            }
//            else logger.error("User konnte nicht aus der Lobby entfernt werden.");
//
//            if(removeUserFromServer(session)) logger.info("User gelöscht.");
//            else logger.error("User konnte nicht gelöscht werden.");
//        }
//        logger.info(" Verbindung getrennt.: {} ", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private boolean reconnectUser(WebSocketSession session, String username){
        if(UserListService.userList.getUserByUsername(username) != null &&
                UserListService.userList.getUserByUsername(username).getUsername().equals(username)){
            UserListService.userList.getUserByUsername(username).updateSession(session);
            responseMessage = GenerateJSONObjectService.generateJSONObject(ActionValues.RECONNECT.getValue(), username,
                    true, "", "");
            SendMessageService.sendSingleMessage(session, responseMessage);
            return true;

        }
        return false;
    }

    @SneakyThrows
    private boolean disconnectUser(WebSocketSession session, String username){
        if(removeUserFromLobby(session) && removeUserFromServer(session)){
                responseMessage = GenerateJSONObjectService.generateJSONObject(ActionValues.DISCONNECT.getValue(),
                        username, true, "", "");
                SendMessageService.sendSingleMessage(session, responseMessage);
                TimeUnit.SECONDS.sleep(2);
                session.close();
                return true;
            }

        return false;
    }

    private boolean removeUserFromLobby(WebSocketSession session){
        if(UserListService.userList.getUserBySessionID(session.getId()) != null){
            String username = UserListService.userList.getUserBySessionID(session.getId()).getUsername();
            lobbyService.gamelobby.removePlayerFromLobbyByName(username);
            return true;
        }
        else return false;
    }
    private boolean removeUserFromServer(WebSocketSession session){
        if(UserListService.userList.getUserBySessionID(session.getId()) != null){
            UserListService.userList.deleteUser(session.getId());
            return true;
        }
        else return false;
    }
}