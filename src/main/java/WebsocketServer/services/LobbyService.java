package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import lombok.Getter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse um User zur Lobby hinzuzufügen
 *  - Spieler hinzufügen
 *  - Spieler entfernen
 *  - Spieleranzahl prüfen
 *  - Spielerliste ausgeben
 *  - Karten an Spieler schicken
 */
@Component
public class LobbyService {

    private final Lobby gamelobby;
    private CardManager cardManager;
    private static final String USERNAME_KEY = "username";

    private static final String MESSAGE_KEY="message";
    @Getter
    private final Map<String, String> sessionUserMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);



    public LobbyService(Lobby gameLobby){
        this.gamelobby = gameLobby;
        this.cardManager=new CardManager(gameLobby);
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
            sessionUserMap.put(session.getId(), username);
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

        if(gamelobby.removePlayerFromLobby(username)) {
            sessionUserMap.remove(session.getId(), username);
            JSONObject response = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            logger.info("Erfolgreich aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("leaveLobby", username, false, "", "Username not in Lobby.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
            logger.info("Nicht aus der Lobby entfernt: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        }
    }


    /***
     * Draws the next card and sends that information to the player
     * @param session  current connection
     * @param messageJson   received string for assignment in HandleMessage
     */
    public void handleCardDraw(WebSocketSession session, JSONObject messageJson) {
        logger.info("Versuche Nächste Karte zu schicken: {}, {}", session.getId(), messageJson.getString(USERNAME_KEY));
        cardManager.drawAndSendNextCard(session);
    }
  
    public void removeFromLobbyAfterConnectionClosed(String sessionId) {
        String username = sessionUserMap.get(sessionId);
        if (username != null) {
            gamelobby.removePlayerFromLobby(username);
            sessionUserMap.remove(sessionId, username);
            logger.info("User aus der Lobby entfernt nach Verbindungsschluss: {}, {}", sessionId, username);
        } else {
            logger.info("Kein User gefunden für Session-ID: {}", sessionId);
        }
    }

    public void removeAllUsersFromLobby() {
        gamelobby.removeAllPlayersFromLobby();
        sessionUserMap.clear();
        logger.info("Alle User aus der Lobby entfernt");
    }
}