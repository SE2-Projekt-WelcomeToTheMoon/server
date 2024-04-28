package WebsocketServer.services;

import WebsocketServer.services.user.UserListService;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

public class SendMessageService {
    private static final Logger logger = LogManager.getLogger(String.valueOf(SendMessageService.class));

    @SneakyThrows
    public static void sendSingleMessage(WebSocketSession session, JSONObject messageToSend){
        if(checkMessage(messageToSend)) {
            session.sendMessage(new TextMessage(messageToSend.toString()));
            logger.info("Message sent.");
        }else logger.warn("Message incomplete. Message not sent.");
    }

    @SneakyThrows
    public static void sendMessagesToAllUsers(JSONObject messageToSend){
        if(checkMessage(messageToSend)) {
            ArrayList<String> usernames = LobbyService.gameLobby.getUserListFromLobby();
            for(String username : usernames){
                WebSocketSession session = UserListService.userList.getUserByUsername(username).getSession();
                session.sendMessage(new TextMessage(messageToSend.toString()));
            }
            logger.info("Message sent to all users.");
        }else logger.warn("Message incomplete. Message not sent.");
    }

    private static boolean checkMessage(JSONObject messageToCheck){
        return ((messageToCheck.getString("action") != null) & !(messageToCheck.getString("action").isEmpty()));
    }
}
