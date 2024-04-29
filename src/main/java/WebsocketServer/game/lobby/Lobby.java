package WebsocketServer.game.lobby;

import WebsocketServer.services.LobbyService;
import WebsocketServer.services.user.CreateUserService;
import WebsocketServer.services.user.UserListService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasse um die Lobby zu verwalten
 * - Spieler hinzufügen
 * - Spieler entfernen
 * - Spielerliste ausgeben
 */
@Component
public class Lobby {

    private static final int MAX_PLAYERS = 4;

    @Getter
    public Map<String, CreateUserService> userListMap;
    private static final Logger logger = LoggerFactory.getLogger(LobbyService.class);

    public Lobby() {
        userListMap = new HashMap<>();
    }

    /**
     * add Player to Lobby
     * @param username  Username to add
     * @return  boolean if player was added
     */
    public boolean addPlayerToLobby(String username) {
        CreateUserService user = UserListService.userList.getUserByUsername(username);
        if(userListMap.size() < MAX_PLAYERS && !(userListMap.containsKey(username))) {
            userListMap.put(username, user);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Remove Player from Lobby
     * @param username  Username to remove
     * @return  boolean if player was removed
     */

    public boolean removePlayerFromLobbyByName(String username){
        CreateUserService user = UserListService.userList.getUserByUsername(username);
        if(userListMap.containsKey(username)){
            userListMap.remove(username, user);
            return true;
        }else{
            return false;
        }
    }
    public void removePlayerFromLobbyBySessionID(String sessionID){
        CreateUserService user = UserListService.userList.getUserBySessionID(sessionID);
        if(userListMap.containsKey(user.getUsername())){
            userListMap.remove(user.getUsername(), user);
            logger.info("User {} removed from lobby: {}", user.getUsername(), user.getSessionID());
        }else{
            logger.warn("User {} not in lobby: {}", user.getUsername(), user.getSessionID());
        }
    }

    /**
     * Return new Arraylist with users in lobby
     */
    public ArrayList<CreateUserService> getUserListFromLobby() {
        return new ArrayList<>(userListMap.values());
    }

    /**
     * Remove all players from lobby
     */
    public void removeAllPlayersFromLobby() {
        userListMap.clear();
    }
}