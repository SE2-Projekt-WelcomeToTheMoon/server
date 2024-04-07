package WebsocketServer;

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

    @Test
    public void testJoinLobby() throws Exception {
        WebSocketSession session = initStompSession();

        //Simulation: Senden einer Nachricht
        JSONObject joinLobbyMessage = new JSONObject()
                .put("action", "joinLobby")
                .put("username", "testUser1");
        session.sendMessage(new TextMessage(joinLobbyMessage.toString()));

        // Erwartete Antwort
        JSONObject expected = new JSONObject("{\"action\":\"joinedLobby\",\"success\":true}");
        JSONObject actual = new JSONObject(messages.poll(5, TimeUnit.SECONDS));
        assertThat(actual.similar(expected)).isTrue();
    }
    @Test
    public void testJoinLobbyDefault() throws Exception{
        WebSocketSession session1 = initStompSession();

        JSONObject joinLobbyDefaultMessage = new JSONObject();
        joinLobbyDefaultMessage.put("action", "ungültig");
        joinLobbyDefaultMessage.put("username", "testUser");

        session1.sendMessage(new TextMessage(joinLobbyDefaultMessage.toString()));

        // Erwarte eine Fehlermeldung als Antwort
        String expectedErrorMessage = "{\"action\":\"ungültig\",\"error\":\"Unbekannte Aktion\"}";
        String actualResponse = messages.poll(5, TimeUnit.SECONDS);

        // Überprüfung, ob die tatsächliche Antwort der erwarteten Fehlermeldung entspricht
        assertThat(actualResponse).isEqualToIgnoringWhitespace(expectedErrorMessage);
    }

    @Test
    public void testJoinLobbyFull()throws Exception{

        WebSocketSession session2 = initStompSession();

        JSONObject resultJSON = new JSONObject();
        resultJSON.put("action", "joinLobby");
        resultJSON.put("username", "testUser");
        session2.sendMessage(new TextMessage(resultJSON.toString()));

        JSONObject expected = new JSONObject("{\"action\":\"joinedLobby\",\"success\":true}");
        JSONObject actual = new JSONObject(messages.poll(5, TimeUnit.SECONDS));
        assertThat(actual.similar(expected)).isTrue();

        WebSocketSession session3 = initStompSession();

        JSONObject resultJSON2 = new JSONObject();
        resultJSON2.put("action", "joinLobby");
        resultJSON2.put("username", "testUser");
        session3.sendMessage(new TextMessage(resultJSON2.toString()));

        JSONObject expected2 = new JSONObject("{\"action\":\"joinedLobby\",\"success\":\"false\",\"error\":\"lobby is full or Username already in use.\"}");
        JSONObject actual2 = new JSONObject(messages.poll(5, TimeUnit.SECONDS));
        assertThat(actual2.similar(expected2)).isFalse();
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