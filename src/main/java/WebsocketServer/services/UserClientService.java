package WebsocketServer.services;

import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class that adds user to a hashmap. Only adds non duplicated usernames.
 */
public class UserClientService {
    private static final HashMap<Short, String> userClients = new HashMap<>();
    private static short clientID = 0;
    public UserClientService(){
        clientID = 0;
    }

    /**
     * Checks username and adds it to hashmap.
     * @param session Session of webSocket connection.
     * @param message Message received from client to get username from.
     * @return Boolean value if username was set, already in use or key username in message is empty.
     * @throws IOException
     */
    public String registerUser(WebSocketSession session, JSONObject message) throws IOException {
        JSONObject response = new JSONObject();
        if (message.getString("username") != null) {
            if (!userClients.containsValue(message.getString("username"))) {
                userClients.put(clientID++, message.getString("username"));
                return "Username set.";
            } else return "Username already in use, please take another one.";
        } else return "No username passed, please provide an username.";
    }

    /**
     * Gets username.
     * @param key Key value of username value.
     * @return
     */
    public String getUserClient(short key) {
        return userClients.get(key);
    }

    /**
     * Returns current ammount of registered users.
     * @return Short of registered users.
     */
    public short getClientID() {
        return clientID;
    }
}