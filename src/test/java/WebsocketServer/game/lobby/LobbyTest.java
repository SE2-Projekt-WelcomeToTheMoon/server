package WebsocketServer.game.lobby;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    private Lobby lobby;

    @BeforeEach
    public void setUp(){
        lobby = new Lobby();
    }

    @Test
    void addPlayer_Successful() {
        assertTrue(lobby.addPlayerToLobby("Spieler1"));
        assertTrue(lobby.addPlayerToLobby("Spieler2"));
        assertTrue(lobby.addPlayerToLobby("Spieler3"));
        assertTrue(lobby.addPlayerToLobby("Spieler4"));
    }
    @Test
    void addPLayer_LobbyFull() {
        assertTrue(lobby.addPlayerToLobby("Spieler1"));
        assertTrue(lobby.addPlayerToLobby("Spieler2"));
        assertTrue(lobby.addPlayerToLobby("Spieler3"));
        assertTrue(lobby.addPlayerToLobby("Spieler4"));
        assertFalse(lobby.addPlayerToLobby("Spieler5"));
    }

    @Test
    void removerPLayer(){
        lobby.addPlayerToLobby("Spieler1");
        assertEquals(1, lobby.getUserListFromLobby().size());
        lobby.removePlayerFromLobbyByName("Spieler1");
        assertEquals(0, lobby.getUserListFromLobby().size());
        lobby.removePlayerFromLobbyByName("Spieler1");
        assertFalse(lobby.removePlayerFromLobbyByName("Spieler1"));
    }
    @Test
    void removeAllPlayerFromLobby(){
        lobby.addPlayerToLobby("Spieler1");
        lobby.addPlayerToLobby("Spieler2");
        lobby.addPlayerToLobby("Spieler3");
        lobby.addPlayerToLobby("Spieler4");
        assertEquals(4, lobby.getUserListFromLobby().size());
        lobby.removeAllPlayersFromLobby();
        assertEquals(0, lobby.getUserListFromLobby().size());
    }
}