package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.services.json.GenerateJSONObjectService;
import WebsocketServer.services.user.CreateUserService;
import lombok.Getter;
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
     * @throws Exception    Exception for handling errors
     */
    public void handleJoinLobby(WebSocketSession session, JSONObject messageJson) throws Exception {

        logger.info("Versuchen zur Lobby hinzuzufügen: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));


        String username = messageJson.getString(USERNAME_KEY);
        if(gamelobby.addPlayerToLobby(username)){
            JSONObject response = GenerateJSONObjectService.generateJSONObject("joinLobby", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            logger.info("Erfolgreich zur Lobby hinzugefügt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));

        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("joinLobby", username, false, "", "lobby is full or Username already in use.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
            logger.info("Nicht zur Lobby hinzugefügt : {}, {} ", session.getId(), messageJson.getString(USERNAME_KEY));
        }
    }

    /**
     * Remove Player from Lobby
     * @param session  current connection
     * @param messageJson   received string for assignment in HandleMessage
     * @throws Exception    Exception for handling errors
     */
    public void handleLeaveLobby(WebSocketSession session, JSONObject messageJson) throws Exception {

        logger.info("Versuchen aus der Lobby zu entfernen: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));

        String username = messageJson.getString(USERNAME_KEY);

        if(gamelobby.removePlayerFromLobbyByName(username)) {
            JSONObject response = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            logger.info("Erfolgreich aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, false, "", "Username not in Lobby.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
            logger.info("Nicht aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
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


