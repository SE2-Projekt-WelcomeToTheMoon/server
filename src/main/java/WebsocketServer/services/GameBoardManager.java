package WebsocketServer.services;

import WebsocketServer.game.model.FieldUpdateMessage;
import WebsocketServer.game.model.GameBoard;
import WebsocketServer.game.model.Player;
import WebsocketServer.game.services.GameBoardService;
import WebsocketServer.services.json.GenerateJSONObjectService;
import WebsocketServer.services.user.CreateUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Manages the GameBoard for all Users in a given Game
 */
public class GameBoardManager {

    private final WebSocketSession session;
    private final String gameBoardRocketJSON;
    private final GameBoard gameBoardRocket;
    private final Logger logger = LogManager.getLogger(GameBoardManager.class);

    public GameBoardManager(WebSocketSession session) {
        this.session = session;
        GameBoardService gameBoardService = new GameBoardService();
        this.gameBoardRocket = gameBoardService.createGameBoard();
        this.gameBoardRocketJSON = initGameBoardRocket();
    }

    /**
     * This is the Main GameBoard than wil get sent on Startup of the Game.
     */
    private String initGameBoardRocket() {
        return serializeGameBoard(this.gameBoardRocket);
    }

    /**
     * Tries so send the Blank GameBoard to every user in given Game
     * TODO
     * replace player.getPlayerId().toString() with player.getUsername when available
     *
     * @return success
     */
    public boolean initGameBoards(List<Player> players) {
        if (players == null || players.isEmpty()){
            return false;
        }

        for (Player player : players) {
            if (player == null){
                logger.warn("Player is null");
                return false;
            }
            JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("initUser", player.getPlayerId().toString(), true, this.gameBoardRocketJSON, "");
            SendMessageService.sendSingleMessage(this.session, jsonObject);
        }
        return true;
    }

    /**
     * Gets the GameBoard instance of a given Username and processes into a JSON format
     *
     * @return the Formatted String and null if it fails
     */
    public String getGameBoardUser(Player player) {
        if (player == null) {
            logger.warn("Attempted to get game board for null player");
            return null;
        }

        return serializeGameBoard(player.getGameBoard());
    }


    /**
     * Client sends Message in FieldUpdateMessage Format
     * Gets Parsed and then passed onto GameBoard
     */
    public boolean updateUser(Player player, String message) {
        if (player == null || player.getGameBoard() == null) {
            logger.error("Player or game board is null");
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            FieldUpdateMessage fieldUpdateMessage = mapper.readValue(message, FieldUpdateMessage.class);
            player.getGameBoard().setValueWithinFloorAtIndex(
                    fieldUpdateMessage.floor(),
                    fieldUpdateMessage.chamber(),
                    fieldUpdateMessage.fieldValue());
            logger.info("Game board updated successfully for user: {}", player.getPlayerId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to parse JSON or update game board for user: {}", player.getPlayerId(), e);
            return false;
        }
    }

    String serializeGameBoard(GameBoard gameBoard) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(gameBoard);
        } catch (JsonProcessingException e) {
            logger.error("JSON serialization error", e);
            return null;
        }
    }

    public void informClientsAboutStart(List<CreateUserService> players) {
        for(CreateUserService player : players){
            logger.info("Player: {} wird informiert", player.getUsername());
            JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("gameIsStarted", player.getUsername(), true, this.gameBoardRocketJSON, "");
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }
}