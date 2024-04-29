package WebsocketServer.services;


import WebsocketServer.services.json.GenerateJSONObjectService;
import WebsocketServer.websocket.WebSocketHandlerClientImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

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


        //Senden einer Nachricht an den Server
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "joinLobby", "User12345", true, "", "");
        lobbyService.handleJoinLobby(session, jsonMsg);

        // Erwartete Antwort
        JSONObject expected = new JSONObject("{\"action\":\"joinLobby\",\"success\":true,\"username\":\"User12345\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
        assertEquals(1, lobbyService.gamelobby.userListMap.size());
        assertEquals(1, lobbyService.getUsersInLobby().size());

        JSONObject jsonMsgLeave = GenerateJSONObjectService.generateJSONObject(
                "leaveLobby", "User12345", true, "", "");
        lobbyService.handleLeaveLobby(session, jsonMsgLeave);
        JSONObject expectedLeave = new JSONObject("{\"action\":\"leaveLobby\",\"success\":true,\"username\":\"User12345\"}");
        JSONObject actualLeave = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actualLeave.similar(expectedLeave));
        assertEquals(0, lobbyService.gamelobby.getUserListFromLobby().size());

    }

    @Test
    void testLeaveLobbyFail() throws Exception {

        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "leaveLobby", "UserNotInLobby", false, null, "Username not in Lobby.");
        lobbyService.handleLeaveLobby(session, jsonMsg);

        JSONObject expected = new JSONObject("{\"action\":\"leaveLobby\",\"success\":false,\"username\":\"UserNotInLobby\",\"error\":\"Username not in Lobby.\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
        assertEquals(0, lobbyService.gamelobby.getUserListFromLobby().size());
    }

    @Test
    void testJoinLobbyDefault() throws Exception{

        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "ungültig", "testUser", null, "", "Unbekannte Aktion"
        );
        session.sendMessage(new TextMessage(jsonMsg.toString()));

        JSONObject expected = new JSONObject("{\"action\":\"ungültig\",\"error\":\"Unbekannte Aktion\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
    }

    /**
     * Testet die Methode handleJoinLobby aus dem LobbyService
     */
    @Test
    void testJoinLobbyDuplicateUsername()throws Exception{

        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                "joinLobby", "testUser", true, null, null);

        lobbyService.handleJoinLobby(session, jsonMsg);

        JSONObject expected = new JSONObject("{\"action\":\"joinLobby\",\"success\":true,\"username\":\"testUser\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
        assertEquals(1, lobbyService.gamelobby.getUserListFromLobby().size());

        WebSocketSession session2 = initStompSession();

        JSONObject jsonMsg2 = GenerateJSONObjectService.generateJSONObject(
                "joinLobby", "testUser", false, null, null);
        lobbyService.handleJoinLobby(session2, jsonMsg2);

        JSONObject expected2 = new JSONObject("{\"action\":\"joinLobby\",\"success\":false,\"username\":\"testUser\",\"error\":\"lobby is full or Username already in use.\"}");
        JSONObject actual2 = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual2.similar(expected2));
        assertEquals(1, lobbyService.gamelobby.getUserListFromLobby().size());
    }

    public WebSocketSession initStompSession() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();

        // connect client to the websocket server
        WebSocketSession session = client.execute(new WebSocketHandlerClientImpl(messages), // pass the message list
                        String.format(WEBSOCKET_URI, port))
                // wait 1 sec for the client to be connected
                .get(1, TimeUnit.SECONDS);

        return session;
    }
}

