package WebsocketServer;

import WebsocketServer.services.GenerateJSONObjectService;
import WebsocketServer.websocket.WebSocketHandlerClientImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String WEBSOCKET_URI = "ws://localhost:%d/welcome-to-the-moon";

    /**
     * Queue of messages from the server.
     */
    BlockingQueue<String> messages = new LinkedBlockingDeque<>();

    @Test
    public void testWebSocketMessageBroker() throws Exception {
        WebSocketSession session = initStompSession();

        // send a message to the server
        String message = "Test message";
        session.sendMessage(new TextMessage(message));

        var expectedResponse = "echo from handler: " + message;
        assertThat(messages.poll(1, TimeUnit.SECONDS)).isEqualTo(expectedResponse);
    }

    /**
     * Testet die Methode handleJoinLobby aus dem LobbyService
     */
    @Test
    public void testJoinLobby() throws Exception {
        WebSocketSession session = initStompSession();

        //Senden einer Nachricht an den Server
        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                        "joinLobby", "User1234", true, "", "");
        session.sendMessage(new TextMessage(jsonMsg.toString()));

        // Erwartete Antwort
        JSONObject expected = new JSONObject("{\"action\":\"joinedLobby\",\"success\":true,\"username\":\"User1234\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
    }

    /**
     * Testet die Methode handleJoinLobby aus dem LobbyService
     */
    @Test
    public void testJoinLobbyDefault() throws Exception{
        WebSocketSession session = initStompSession();

        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                        "ungültig", "testUser", null, "", "Unbekannte Aktion"
        );
        session.sendMessage(new TextMessage(jsonMsg.toString()));

        // Erwarte Antwort
        JSONObject expected = new JSONObject("{\"action\":\"ungültig\",\"error\":\"Unbekannte Aktion\"}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));
    }

    /**
     * Testet die Methode handleJoinLobby aus dem LobbyService
     */
    @Test
    public void testJoinLobbyDuplicateUsername()throws Exception{

        WebSocketSession session = initStompSession();

        JSONObject jsonMsg = GenerateJSONObjectService.generateJSONObject(
                        "joinLobby", "testUser", null, "", "");

        session.sendMessage(new TextMessage(jsonMsg.toString()));

        JSONObject expected = new JSONObject("{\"action\":\"joinedLobby\",\"username\":\"testUser\",\"success\":true}");
        JSONObject actual = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual.similar(expected));


        WebSocketSession session2 = initStompSession();

        JSONObject jsonMsg2 = GenerateJSONObjectService.generateJSONObject(
                        "joinLobby", "testUser", null, "", "lobby is full or Username already in use.");
        session2.sendMessage(new TextMessage(jsonMsg2.toString()));

        JSONObject expected2 = new JSONObject("{\"action\":\"joinLobby\",\"username\":\"testUser\",\"success\":false,\"error\":\"lobby is full or Username already in use.\"}");

        JSONObject actual2 = new JSONObject(messages.poll(1, TimeUnit.SECONDS));

        assertTrue(actual2.similar(expected2));
    }

    /**
     * @return The basic session for the WebSocket connection.
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

}