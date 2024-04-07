package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class LobbyService {

    private Lobby gamelobby;

    public LobbyService(){
        this.gamelobby = new Lobby();
    }

    /**
     * LobbyService zur Erstellung eines JSON, damit weniger code im WebsocketHandlerImpl und kompakter
     * action --> definiert das Ziel, welches angesteuert werden will (für client wichtig + bitte userName mitsenden)
     * @param session aktuelle Verbindung
     * @param messageJson empfangener String um Zuordnung in der HnadleMessage zu machen
     */
    public void handleJoinLobby(WebSocketSession session, JSONObject messageJson) throws Exception {

        String username = messageJson.getString("Username");

        if(gamelobby.addPlayerToLobby(username)){
            JSONObject response = new JSONObject();
            response.put("Action", "joinedLobby");
            response.put("Success", true);

            session.sendMessage(new TextMessage(response.toString()));
        }else{
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("Action", "joinLobby");
            errorResponse.put("Success", false);
            errorResponse.put("Error", "lobby is full or Username already in use.");
            session.sendMessage(new TextMessage(errorResponse.toString()));
        }
    }
}