package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Klasse um die Lobby zu verwalten
 *  - Spieler hinzufügen
 *  - Spieler entfernen
 *  - Spieleranzahl prüfen
 *  - Spielerliste ausgeben
 */
public class LobbyService {


    private Lobby gamelobby;
    private GenerateJSONObjectService generateJSONObjectService;

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

        String username = messageJson.getString("username");

        if(gamelobby.addPlayerToLobby(username)){
            JSONObject response = GenerateJSONObjectService.generateJSONObject("joinedLobby", username, true, "", "");
            session.sendMessage(new TextMessage(response.toString()));
            System.out.println("Erfolgreich zur Lobby hinzugefügt :  "+ session.getId() + " Username: " + messageJson.getString("username"));

        }else{
            JSONObject errorResponse = GenerateJSONObjectService.generateJSONObject("joinLobby", username, false, "", "lobby is full or Username already in use.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
            System.out.println("Nicht zur Lobby hinzugefügt :  "+ session.getId() + " Username: " + messageJson.getString("username"));

        }

//            JSONObject response = new JSONObject();
//            response.put("action", "joinedLobby");
//            response.put("success", true);
//
//            session.sendMessage(new TextMessage(response.toString()));
//        }else{
//            JSONObject errorResponse = new JSONObject();
//            errorResponse.put("action", "joinLobby");
//            errorResponse.put("success", false);
//            errorResponse.put("error", "lobby is full or Username already in use.");
//            session.sendMessage(new TextMessage(errorResponse.toString()));
//        }
    }
}