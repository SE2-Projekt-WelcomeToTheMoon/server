package WebsocketServer.services.userServices;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateUserService {
    private final Logger logger = LogManager.getLogger(String.valueOf(CreateUserService.class));
    @Getter
    private String username;

    public CreateUserService(String username){
        registerUser(username);
    }

    public boolean checkUserExists(String username){
        return UserListService.userList.getUser(username) == null;
    }

    public void registerUser(String username) {
        if (username != null && !username.isEmpty()) {
            if(checkUserExists(username)){
                this.username = username;
                logger.info("Username {} set.", username);
            }
            else logger.warn("Username {} already exists.", username);
        } else logger.warn("No username has been passed.");
    }
}