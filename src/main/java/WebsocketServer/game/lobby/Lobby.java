package WebsocketServer.game.lobby;

import WebsocketServer.services.CardManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse um die Lobby zu verwalten
 * - Spieler hinzufügen
 * - Spieler entfernen
 * - Spielerliste ausgeben
 */
@Component
public class Lobby {

    private static final int MAX_PLAYERS = 4;
    private final List<String> userList;

    private final CardManager cardManager;
    public Lobby() {
        this.userList = new ArrayList<>();
        this.cardManager=new CardManager(this);
    }

    /**
     * add Player to Lobby
     * @param username  Username to add
     * @return  boolean if player was added
     */
    public boolean addPlayerToLobby(String username) {
        if (userList.size() < MAX_PLAYERS && !(userList.contains(username))) {
            userList.add(username);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove Player from Lobby
     * @param username  Username to remove
     * @return  boolean if player was removed
     */
    public boolean removePlayerFromLobby(String username){
        if(userList.contains(username)){
            userList.remove(username);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Return new Arraylist with users in lobby
     */
    public ArrayList<String> getUserListFromLobby() {
        return new ArrayList<>(userList);
    }

    /**
     * Remove all players from lobby
     */
    public void removeAllPlayersFromLobby() {
        userList.clear();
    }

    public void sendNextCard(WebSocketSession session){
        cardManager.drawAndSendNextCard(session);
    }
}