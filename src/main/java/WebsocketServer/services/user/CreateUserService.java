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
    private String username;

    public CreateUserService(String username){
        registerUser(username);
    }

    /**
     * Checks if a user with the passed username already exists.
     * @param username Username to create user.
     * @return boolean according to if user exists or not.
     */
    public boolean checkUserExists(String username){
        return UserListService.userList.getUser(username) == null;
    }

    /**
     * Sets global variables for username and more tbd.
     * @param username Username to be set.
     */
    public void registerUser(String username) {
        if (!username.isEmpty()) {
            if(checkUserExists(username)){
                this.username = username;
                logger.info("Username {} set.", username);
            }
            else logger.warn("Username {} already exists.", username);
        } else logger.warn("No username has been passed.");
    }
}