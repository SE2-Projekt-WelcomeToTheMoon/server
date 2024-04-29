package WebsocketServer.game.lobby;

import WebsocketServer.game.model.Game;
import WebsocketServer.game.model.GameBoard;

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

    public Lobby() {
        this.userList = new ArrayList<>();
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
    public List<String> getUserListFromLobby() {
        return new ArrayList<>(userList);
    }

    /**
     * PLACEHOLDER
     * method that returns gameboard of specific user
     */
    public GameBoard getGameBoard(String username){

        // PLACEHOLDER
        GameBoard gameBoard = new GameBoard();

        if (!userList.contains(username)){
            return null;
        }
        // return username.getGameBoard
        return gameBoard;
    }

    public boolean getUser(String username){
        if (userList.contains(username)){
            // TODO
            // return User Object
            return true;
        }
        return false;
    }
}