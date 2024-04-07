package WebsocketServer.services;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;

public class UserClientService {
    private static HashMap<Short, String> userClients = new HashMap<>();
    private static short clientID = 0;
    public UserClientService(){
        this.clientID = 0;
    }
    public static String registerUser(WebSocketSession session, JSONObject message) throws IOException {
        JSONObject response = new JSONObject();
        if (message.getString("Username") != null) {
            if (!userClients.containsValue(message.getString("Username"))) {
                userClients.put(clientID++, message.getString("Username"));
                return "Username set.";
            } else return "Username already in use, please take another one.";
        } else return "No username passed, please provide an username.";
    }

    public String getUserClient(short key) {
        return this.userClients.get(key);
    }
    public short getClientID() {
        return this.clientID;
    }
}