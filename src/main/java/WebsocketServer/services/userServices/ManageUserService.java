package WebsocketServer.services.userServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import WebsocketServer.services.GenerateJSONObjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

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
     * Adds a user to the list and checks if the user already exists. Returns a JSONObject message to send back to user
     * for verbose output.
     * @param user User to add.
     * return Response as JSON Object
     */
    public JSONObject addUser(CreateUserService user){
        String username = user.getUsername();
        try {
            if(!(userList.containsKey(username))){
                userList.put(user.getUsername(), user);
                logger.info("User {} now managed.", username);
                return GenerateJSONObjectService.generateJSONObject("registerUser", username, true, ("User " + username + " now managed."), "");
            }
            else {
                logger.warn("Username {} is already in use", username);
                return GenerateJSONObjectService.generateJSONObject("registerUser", username, false, ("Username " + username + " is already in use"), "");
            }
        }catch(Exception e){
            logger.error("User {} could not be added. Error: {}", username, e.getMessage());
            return GenerateJSONObjectService.generateJSONObject("registerUser", username, false, ("User " + username + " could not be added"), e.getMessage());
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
     * @return value of key username or null if user does not exist.
     */
    public CreateUserService getUser(String username){
        if(userList.containsKey(username)){
            return userList.get(username);
        }else return null;
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
