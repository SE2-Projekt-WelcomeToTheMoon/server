package WebsocketServer.services;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.HashMap;

public class UserClientService {
    private static HashMap<Short, String> userClients = new HashMap<>();
    private static short clientID = 0;
    public UserClientService(){
        this.clientID = 0;
    }
    public static void registerUser(WebSocketSession session, JSONObject message) throws IOException {
        JSONObject response = new JSONObject();
        if (message.getString("Username") != null) {
            if (UserClientService.setUsername(message.getString("Username"))) {
                session.sendMessage(new TextMessage("Username set. Please continue."));
            } else session.sendMessage(new TextMessage("Username already in use, please take another one."));
        } else session.sendMessage(new TextMessage("No username passed, please provide an username."));
    }
    public static boolean setUsername(String usrName){
        if (!userClients.containsValue(usrName)){
            userClients.put(clientID++, usrName);
            return true;
        }
        else return false;
    }

    public String getUserClient(short key) {
        return this.userClients.get(key);
    }
    public short getClientID() {
        return this.clientID;
    }
}