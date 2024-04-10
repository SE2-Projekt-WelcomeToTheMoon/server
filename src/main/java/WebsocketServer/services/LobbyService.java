package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Klasse um User zur Lobby hinzuzufügen
 *  - Spieler hinzufügen
 *  - Spieler entfernen
 *  - Spieleranzahl prüfen
 *  - Spielerliste ausgeben
 */
public class LobbyService {

    private Lobby gamelobby;
    private static final String USERNAME_KEY = "username";

    private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);



    public LobbyService(){
        this.gamelobby = new Lobby();
    }

    /**
     * Methode um einen Spieler zur Lobby hinzuzufügen
     *
     * @param session       aktuelle Verbindung
     * @param messageJson   empfangener String um Zuordnung in der HnadleMessage zu machen
     * @throws Exception    Fehlerbehandlung
     */
    public void handleJoinLobby(WebSocketSession session, JSONObject messageJson) throws Exception {

        logger.info("Versuchen zur Lobby hinzuzufügen : " + session.getId() + " Username: " + messageJson.getString(USERNAME_KEY));

        String username = messageJson.getString(USERNAME_KEY);
        if(gamelobby.addPlayerToLobby(username)){
            JSONObject response = GenerateJSONObjectService.generateJSONObject("joinedLobby", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            logger.info("Erfolgreich zur Lobby hinzugefügt :  "+ session.getId() + " Username: " + messageJson.getString(USERNAME_KEY));
        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("joinLobby", username, false, "", "lobby is full or Username already in use.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
            logger.info("Nicht zur Lobby hinzugefügt :  "+ session.getId() + " Username: " + messageJson.getString(USERNAME_KEY));
        }
    }
}