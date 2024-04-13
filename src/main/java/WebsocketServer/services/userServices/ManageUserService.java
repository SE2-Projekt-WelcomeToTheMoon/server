package WebsocketServer.services.userServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManageUserService {

    private HashMap<String, CreateUserService> userList;
    private final Logger logger = LogManager.getLogger(String.valueOf(ManageUserService.class));


    public ManageUserService(){
        userList = new HashMap<>();
    }

    public void addUser(CreateUserService user){
        String username = user.getUsername();
        try {
            if(userList.containsKey(username)){
                logger.warn("Username {} is already in use", username);
            }
            else {
                userList.put(user.getUsername(), user);
                logger.info("User {} now managed.", username);
            }
        }catch(Exception e){
            logger.error("User {} could not be added. Error: {}", username, e.getMessage());
        }
    }

    public void deleteUser(String username){
        try {
            if(userList.containsKey(username)){
                userList.remove(username);
                logger.info("User {} removed.", username);
            }
            else logger.warn("User {} could not be removed, key {} does not exist.", username, username);

        }catch (Exception e){
            logger.error("User {} could not be deleted. Error: {}", username, e.getMessage());
        }
    }

    public CreateUserService getUser(String username){
        return userList.get(username);
    }

    public List<CreateUserService> getAllUsers(){
        return new ArrayList<>(userList.values());
    }

    public void cleanUpUserList(){
        userList = null;
        logger.info("User list has been cleaned.");
    }
}
