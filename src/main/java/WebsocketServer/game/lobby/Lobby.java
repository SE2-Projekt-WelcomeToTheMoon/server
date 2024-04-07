package WebsocketServer.game.lobby;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private static final int MAX_PLAYERS = 4;
    private List<String> userList;

    public Lobby(){
        this.userList = new ArrayList<>();
    }


    /**
     * Lobby Klasse mit basic implementierung
     * Max Anzahl = 4
     */
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