package websocketserver.services;


import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;
import websocketserver.websocket.WebSocketHandlerClientImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;


import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LobbyServiceTests {

    @LocalServerPort
    private int port;

    @Autowired
    private LobbyService lobbyService;

    private final String WEBSOCKET_URI = "ws://localhost:%d/welcome-to-the-moon";

    BlockingQueue<String> messages = new LinkedBlockingDeque<>();

    private WebSocketSession session;


    @BeforeEach
    void setup() throws Exception {
        this.session = initStompSession();
    }

    @AfterEach
    void tearDown() {
        lobbyService.removeAllUsersFromLobby();
        lobbyService.gamelobby.userListMap.clear();
    }


    @Test
    void testJoinLobbyAndLeave() throws Exception {
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "joinLobby", "User12345", true, "", "");
        lobbyService.handleJoinLobby(session, jsonMsg);
        assertEquals(1, lobbyService.gamelobby.getUserListFromLobby().size());
        lobbyService.handleJoinLobby(session, jsonMsg);
        assertEquals(1, lobbyService.gamelobby.getUserListFromLobby().size());
        lobbyService.handleLeaveLobby(session, jsonMsg);
        assertEquals(0, lobbyService.gamelobby.getUserListFromLobby().size());
        lobbyService.handleLeaveLobby(session, jsonMsg);
        assertEquals(0, lobbyService.gamelobby.getUserListFromLobby().size());
    }

    @Test
    void testHandleRequestLobby() throws Exception {
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "requestLobbyUser", "User12345", true, "", "");
        lobbyService.handleRequestLobbyUser(session);
        assertEquals(0, lobbyService.gamelobby.getUserListFromLobby().size());
        lobbyService.handleJoinLobby(session, jsonMsg);
        lobbyService.handleRequestLobbyUser(session);
        assertEquals(1, lobbyService.gamelobby.getUserListFromLobby().size());
    }


    /**
     * Testet die Methode handleJoinLobby aus dem LobbyService
     */


    public WebSocketSession initStompSession() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();

        // connect client to the websocket server
        WebSocketSession session = client.execute(new WebSocketHandlerClientImpl(messages), // pass the message list
                        String.format(WEBSOCKET_URI, port))
                // wait 1 sec for the client to be connected
                .get(1, TimeUnit.SECONDS);

        return session;
    }

    @Test
    void testStartGameSuccessfully() throws Exception {
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "startGame", "User123", true, "", "");


        lobbyService.handleStartGame(session, jsonMsg);

        assertTrue(lobbyService.isGameStarted());
    }

    @Test
    void testStartGameFailAlreadyStarted() throws Exception {
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "startGame", "User123", true, "", "");

        lobbyService.handleStartGame(session, jsonMsg);

        Map<String, CreateUserService> result = lobbyService.handleStartGame(session, jsonMsg);

        assertNull(result);
    }
}