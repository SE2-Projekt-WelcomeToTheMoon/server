package WebsocketServer.services.user;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class creates user object to handle users logged on the server.
 */
public class CreateUserService {

    private final Logger logger = LogManager.getLogger(String.valueOf(CreateUserService.class));

    @Getter
    private String sessionID;
    @Getter
    private String username;

    public CreateUserService(String sessionID, String username){
        registerUser(sessionID, username);
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
     * Sets global variables for sessionID, username and more tbd.
     * @param sessionID SessionID to be set.
     * @param username Username to be set.
     */
    public void registerUser(String sessionID, String username) {
        if (!username.isEmpty() && !sessionID.isEmpty()) {
            if(checkUserExists(sessionID)){
                this.sessionID = sessionID;
                this.username = username;
                logger.info("SessionID {} and Username {} set. User created.", sessionID, username);
            }
            else logger.warn("Username {} already exists. User not created.", username);
        } else logger.warn("No username has been passed. User not created.");
    }
}