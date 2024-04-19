package WebsocketServer;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.services.GenerateJSONObjectService;
import WebsocketServer.services.LobbyService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.*;

public class LobbyIntTest {
    private LobbyService lobbyService;
    private Lobby gameLobby;
    private WebSocketSession session;
    private JSONObject messageJson;

    @BeforeEach
    void setUp() {
        gameLobby = mock(Lobby.class);
        lobbyService = new LobbyService(gameLobby); // Pass the mocked Lobby instance to the LobbyService
        session = mock(WebSocketSession.class);
        messageJson = new JSONObject();
    }

    @Test
    void testHandleJoinLobbySuccess() throws Exception {
        when(session.getId()).thenReturn("1");
        messageJson.put("username", "testUser");

        // Mock the behavior of addPlayerToLobby
        when(gameLobby.addPlayerToLobby(anyString())).thenReturn(true);

        lobbyService.handleJoinLobby(session, messageJson);

        // Verify that addPlayerToLobby was called
        verify(gameLobby, times(1)).addPlayerToLobby("testUser");

        // Generate the expected JSON response
        JSONObject expectedResponse = GenerateJSONObjectService.generateJSONObject("joinedLobby", "testUser", true, "", "");

        // Verify that the correct message was sent
        verify(session, times(1)).sendMessage(new TextMessage(expectedResponse.toString()));
    }

    @Test
    void testHandleJoinLobbyFailure() throws Exception {
        when(session.getId()).thenReturn("1");
        messageJson.put("username", "testUser");

        // Mock the behavior of addPlayerToLobby
        when(gameLobby.addPlayerToLobby(anyString())).thenReturn(false);

        lobbyService.handleJoinLobby(session, messageJson);

        // Verify that addPlayerToLobby was called
        verify(gameLobby, times(1)).addPlayerToLobby("testUser");

        // Generate the expected JSON response
        JSONObject expectedResponse = GenerateJSONObjectService.generateJSONObject("joinedLobby", "testUser", false, "", "lobby is full or Username already in use.");

        // Verify that the correct message was sent
        verify(session, times(1)).sendMessage(any(TextMessage.class));}

    @Test
    void testHandleLeaveLobbySuccess() throws Exception {
        when(session.getId()).thenReturn("1");
        messageJson.put("username", "testUser");

        // Mock the behavior of removePlayerFromLobby
        when(gameLobby.removePlayerFromLobby(anyString())).thenReturn(true);

        lobbyService.handleLeaveLobby(session, messageJson);

        // Verify that removePlayerFromLobby was called
        verify(gameLobby, times(1)).removePlayerFromLobby("testUser");

        // Generate the expected JSON response
        JSONObject expectedResponse = GenerateJSONObjectService.generateJSONObject("leftLobby", "testUser", true, "", "");

        // Verify that the correct message was sent
        verify(session, times(1)).sendMessage(new TextMessage(expectedResponse.toString()));
    }

    @Test
    void testHandleLeaveLobbyFailure() throws Exception {
        when(session.getId()).thenReturn("1");
        messageJson.put("username", "testUser");

        // Mock the behavior of removePlayerFromLobby
        when(gameLobby.removePlayerFromLobby(anyString())).thenReturn(false);

        lobbyService.handleLeaveLobby(session, messageJson);

        // Verify that removePlayerFromLobby was called
        verify(gameLobby, times(1)).removePlayerFromLobby("testUser");

        // Generate the expected JSON response
        JSONObject expectedResponse = GenerateJSONObjectService.generateJSONObject("leaveLobby", "testUser", false, "", "Username not in Lobby.");

        // Verify that the correct message was sent
        verify(session, times(1)).sendMessage(new TextMessage(expectedResponse.toString()));
    }
}