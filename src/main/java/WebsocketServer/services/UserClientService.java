package WebsocketServer.services;

import lombok.Getter;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;
import java.util.HashMap;

/**
 * Class that adds user to a hashmap. Only adds non duplicated usernames.
 */
public class UserClientService {
    private final HashMap<Short, String> userClients = new HashMap<>();
    /**
     * -- GETTER --
     *  Returns current amount of registered users.
     * @return Short of registered users.
     */
    @Getter
    private short clientID = 0;

    /**
     * Checks username and adds it to hashmap.
     * @param session Session of webSocket connection.
     * @param message Message received from client to get username from.
     * @return Boolean value if username was set, already in use or key username in message is empty.
     */
    public String registerUser(WebSocketSession session, JSONObject message) {
        String username = message.getString("username");
        if (username != null) {
            if (!userClients.containsValue(username)) {
                userClients.put(this.clientID++, username);
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
}
