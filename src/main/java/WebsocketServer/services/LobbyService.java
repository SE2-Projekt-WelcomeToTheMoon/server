package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.services.json.GenerateJSONObjectService;
import WebsocketServer.services.user.CreateUserService;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Map;

/**
 * Klasse um User zur Lobby hinzuzufügen
 *  - Spieler hinzufügen
 *  - Spieler entfernen
 *  - Spieleranzahl prüfen
 *  - Spielerliste ausgeben
 */
@Component
public class LobbyService {

    @Getter
    private boolean gameStarted = false;
    public final Lobby gamelobby;
    private static final String USERNAME_KEY = "username";
    private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);

    public LobbyService() {
        gamelobby = new Lobby();
    }

    /**
     * Add Player to Lobby
     * @param session       current connection
     * @param messageJson   received string for assignment in HandleMessage
     */
    public void handleJoinLobby(WebSocketSession session, JSONObject messageJson) {

        logger.info("Versuchen zur Lobby hinzuzufügen: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));


        String username = messageJson.getString(USERNAME_KEY);
        if(gamelobby.addPlayerToLobby(username)){
            JSONObject response = GenerateJSONObjectService.generateJSONObject("joinLobby", username, true, "", "");
            SendMessageService.sendMessageToAllUsersBySession(response);
            logger.info("Erfolgreich zur Lobby hinzugefügt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));

        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("joinLobby", username, false, "", "lobby is full or Username already in use.");
            SendMessageService.sendSingleMessage(session, errorResponse);
            logger.info("Nicht zur Lobby hinzugefügt : {}, {} ", session.getId(), messageJson.getString(USERNAME_KEY));
        }
    }


    /**
     * Remove Player from Lobby
     * @param session  current connection
     * @param messageJson   received string for assignment in HandleMessage
     */
    public void handleLeaveLobby(WebSocketSession session, JSONObject messageJson) {

        logger.info("Versuchen aus der Lobby zu entfernen: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));

        String username = messageJson.getString(USERNAME_KEY);

        if(gamelobby.userListMap.containsKey(username)) {
            JSONObject response = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, true, "", "");
            SendMessageService.sendMessageToAllUsersBySession(response);
            gamelobby.removePlayerFromLobbyByName(username);
            logger.info("Erfolgreich aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, false, "", "Username not in Lobby.");
            SendMessageService.sendSingleMessage(session, errorResponse);
            logger.info("Nicht aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        }
    }
    public void handleRequestLobbyUser(WebSocketSession session) {
        logger.info("Requesting users in lobby: {}", session.getId());

        ArrayList<CreateUserService> userlist = getUsersInLobby();

        JSONArray userListJSONArr = new JSONArray();
        for (CreateUserService user : userlist) {
            if (user != null) {
                userListJSONArr.put(user.getUsername());
            }
        }
        JSONObject response = new JSONObject();
        response.put("action", "requestLobbyUser");
        response.put("users", userListJSONArr);
        response.put("success", true);

        try {
            SendMessageService.sendSingleMessage(session, response);
            if(userlist.isEmpty()){
                logger.info("No users in lobby.");
            }else {
                logger.info("Users in lobby sent: {} {}", session.getId(), userListJSONArr);
            }
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
        }
    }



    public void removeAllUsersFromLobby() {
        gamelobby.removeAllPlayersFromLobby();
    }
    public ArrayList<CreateUserService> getUsersInLobby(){
        return gamelobby.getUserListFromLobby();
    }

    public Map<String, CreateUserService> handleStartGame(WebSocketSession session, JSONObject messageJson) throws Exception{
        logger.info("Versuchen Game zu starten");

        String username = messageJson.getString(USERNAME_KEY);

        if(!gameStarted){
            gameStarted = true;
            JSONObject response = GenerateJSONObjectService.generateJSONObject("startGame", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            logger.info("Lobby returned UserList");
            return gamelobby.getUserListMap();
        }
        logger.info("Game schon gestartet");
        return null;
    }
}