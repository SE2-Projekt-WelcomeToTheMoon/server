package WebsocketServer.services.userServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that manages all users registered on the server.
 */

public class ManageUserService {

    private HashMap<String, CreateUserService> userList;
    private final Logger logger = LogManager.getLogger(String.valueOf(ManageUserService.class));


    public ManageUserService(){
        userList = new HashMap<>();
    }

    /**
     * Adds a user to the list and checks if the user already exists.
     * @param user User to add.
     */
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

    /**
     * Removes user from list.
     * @param username User to be removed.
     */
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

    /**
     * Returns the user object saved as value behind key username.
     * @param username User to get.
     * @return value of key username
     */
    public CreateUserService getUser(String username){
        return userList.get(username);
    }

    /**
     * Returns a list with all user objects saved in the list.
     * @return List
     */

    public List<CreateUserService> getAllUsers(){
        return new ArrayList<>(userList.values());
    }

    /**
     * Cleans up the list from all users.
     */
    public void cleanUpUserList(){
        userList = null;
        logger.info("User list has been cleaned.");
    }
}
