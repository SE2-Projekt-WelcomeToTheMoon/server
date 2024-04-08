package WebsocketServer.game.lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse um die Lobby zu verwalten
 * - Spieler hinzufügen
 * - Spieler entfernen
 * - Spielerliste ausgeben
 */
public class Lobby {

    private static final int MAX_PLAYERS = 4;
    private final List<String> userList;

    public Lobby(){
        this.userList = new ArrayList<>();
    }
    public boolean addPlayerToLobby(String username){
        if(userList.size() < MAX_PLAYERS && !(userList.contains(username))){
            userList.add(username);
            return true;
        }else{
            return false;
        }
    }

    public void removePlayerFromLobbyByName(String username){
        userList.remove(username);

    }

    public List<String> getUserListFromLobby(){
        return new ArrayList<>(userList);
    }

}