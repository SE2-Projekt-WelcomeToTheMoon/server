package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.GameBoard;
import WebsocketServer.game.services.GameBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Manages the GameBoard for all Users in a given Lobby
 */
public class GameBoardManager {

    private final Lobby gamelobby;
    private final String gameBoardRocketJSON;
    private final GameBoard gameBoardRocket;
    private final Logger logger = LogManager.getLogger(GameBoardManager.class);

    public GameBoardManager(Lobby gameLobby) {
        this.gamelobby = gameLobby;
        this.gameBoardRocketJSON = initGameBoardRocket();
        GameBoardService gameBoardService = new GameBoardService();
        this.gameBoardRocket = gameBoardService.createGameBoard();
    }

    /**
     * This is the Main GameBoard than wil get sent on Startup of the Lobby.
     */
    private String initGameBoardRocket() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameBoardRocket);
        } catch (Exception e) {
            logger.warn("Error serializing game board ", e);
        }
        return null;
    }

    /**
     * Tries so send the Blank GameBoard to every user in given Lobby, also sets the Server-Side GameBoard to the "Blank" one
     *
     * @return success
     */
    public boolean initGameBoards() {
        List<String> userList = gamelobby.getUserListFromLobby();

        for (String user : userList) {
            // actually need to send
            GenerateJSONObjectService.generateJSONObject("initUser", user, true, this.gameBoardRocketJSON, "");
            gamelobby.setGameBoardUser(user, gameBoardRocket);
        }
        return true;
    }

    /**
     * Gets the GameBoard instance of a given Username and processes into a JSON format
     *
     * @param username of the User of which GameBoard one needs
     * @return the Formatted String and null if it fails
     */

    public String getGameBoardUser(String username) {
        if (!gamelobby.findUser(username)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        GameBoard gameBoard = gamelobby.getGameBoard(username);
        try {
            return mapper.writeValueAsString(gameBoard);
        } catch (Exception e) {
            logger.warn("Error serializing game board ", e);
        }
        return null;
    }

    /**
     * Updates a given User with GameBoard Information sent by the Client
     *
     * @param username the User that will get the Update
     * @param message  the JSON formatted String that will be parsed to a GameBoard Class again
     * @return success
     */
    public boolean updateUser(String username, String message) {
        if (!gamelobby.findUser(username)) {
            return false;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            GameBoard updatedGameBoard = mapper.readValue(message, GameBoard.class);
            gamelobby.setGameBoardUser(username, updatedGameBoard);
            logger.info("Game board updated successfully for user: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Failed to parse JSON or update game board for user: {}", username, e);
            return false;
        }
    }
}
