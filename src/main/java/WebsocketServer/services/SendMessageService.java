package WebsocketServer.services;

import WebsocketServer.services.user.CreateUserService;
import WebsocketServer.websocket.handler.WebSocketHandlerImpl;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

/**
 * Service to send messages to clients.
 */
public class SendMessageService {
    private static final Logger logger = LogManager.getLogger(String.valueOf(SendMessageService.class));

    /**
     * Method to send one message to a specific client.
     * @param session Client to send the message.
     * @param messageToSend Message to send to client.
     */
    @SneakyThrows
    public static void sendSingleMessage(WebSocketSession session, JSONObject messageToSend){
        if(checkMessage(messageToSend)) {
            session.sendMessage(new TextMessage(messageToSend.toString()));
            logger.info("Message sent.");
        }else logger.warn("Message incomplete. Message not sent.");
    }

    /**
     * Method sends messages to all users registered on the server and registered in a lobby.
     * @param messageToSend Message to send to all users.
     */
    @SneakyThrows
    public static void sendMessagesToAllUsers(JSONObject messageToSend){
        if(checkMessage(messageToSend)) {
            ArrayList<CreateUserService> users = WebSocketHandlerImpl.lobbyService.getUsersInLobby();
            for(CreateUserService user : users){
                WebSocketSession session = user.getSession();
                session.sendMessage(new TextMessage(messageToSend.toString()));
            }
            logger.info("Message sent to all users.");
        }else logger.warn("Message incomplete. Message not sent.");
    }

    /**
     * Checks if must have keys are in the message.
     * @param messageToCheck Message to check.
     * @return Boolean value if message to send has needed keys or not.
     */
    private static boolean checkMessage(JSONObject messageToCheck){
        return ((messageToCheck.getString("action") != null) & !(messageToCheck.getString("action").isEmpty()));
    }
}
