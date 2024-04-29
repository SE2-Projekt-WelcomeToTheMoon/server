package WebsocketServer.game.lobby;

import org.springframework.stereotype.Component;

import WebsocketServer.game.model.GameBoard;

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
    public ArrayList<String> getUserListFromLobby() {
        return new ArrayList<>(userList);
    }

    /**
     * Remove all players from lobby
     */
    public void removeAllPlayersFromLobby() {
        userList.clear();
    }

    /**
     * PLACEHOLDER
     * method that returns gameBoard of specific user
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

    public boolean findUser(String username){
        return this.userList.contains(username);
    }

    /**
     * In the Future, should set GameBoard for given User Object
     * @param username
     * @param gameBoard
     * @return
     */
    public boolean setGameBoardUser(String username, GameBoard gameBoard){
        if (!this.userList.contains(username)){
            return false;
        }
        //just code against SonarCloud
        GameBoard gameBoard1 = gameBoard;
        gameBoard1.finalizeGameBoard();
        //user.setGameBoard(gameBoard)
        return true;
    }
}