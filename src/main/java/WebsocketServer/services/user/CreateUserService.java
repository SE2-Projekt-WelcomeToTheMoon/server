package WebsocketServer.services.user;

import WebsocketServer.game.model.GameBoard;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.WebSocketSession;

/**
 * Class creates user object to handle users logged on the server.
 */
public class CreateUserService {

    private final Logger logger = LogManager.getLogger(String.valueOf(CreateUserService.class));

    @Getter
    private String sessionID;
    @Getter
    private String username;
    @Getter
    private WebSocketSession session;
    @Getter
    private GameBoard gameBoard;

    public CreateUserService(WebSocketSession session, String username){
        registerUser(session, username);
    }

    /**
     * Checks if a user with the passed sessionID already exists.
     * @param sessionID SessionID to create user.
     * @return boolean according to if user exists or not.
     */
    public boolean checkUserExists(String sessionID){
        return UserListService.userList.getUser(sessionID) == null;
    }

    /**
     * Sets global variables for sessionID, username, session and more tbd.
     * @param session Session to be set.
     * @param username Username to be set.
     */
    public void registerUser(WebSocketSession session, String username) {
        if (!username.isEmpty() && (session != null)) {
            String sessionID = session.getId();
            if(checkUserExists(sessionID)){
                this.sessionID = sessionID;
                this.username = username;
                this.session = session;
                logger.info("SessionID {} and Username {} set. User created.", sessionID, username);
            }
            else logger.warn("Username {} already exists. User not created.", username);
        } else logger.warn("No username has been passed. User not created.");
    }
}