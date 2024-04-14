package WebsocketServer.game.lobby;

import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    Game game;

    public Lobby() {
        this.userList = new ArrayList<>();
    }

    public boolean addPlayerToLobby(String username) {
        if (userList.size() < MAX_PLAYERS && !(userList.contains(username))) {
            userList.add(username);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromLobbyByName(String username) {
        userList.remove(username);

    }

    public List<String> getUserListFromLobby() {
        return new ArrayList<>(userList);
    }

    public void sendNewCardCombination(CardCombination[] currentCombinations) {
        //TODO Implement connection to player

        //TODO If all Player have done their decision
        game.setWaitingForUserResponse(false);
    }
}