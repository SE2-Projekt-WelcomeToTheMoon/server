package websocketserver.services;

import websocketserver.game.model.*;
import websocketserver.game.services.GameBoardService;
import websocketserver.game.util.FieldUpdateMessage;
import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;

/**
 * Manages the GameBoard for all Users in a given Game
 */
public class GameBoardManager {

    private GameBoard gameBoardRocket;
    /**
     * -- GETTER --
     * Just for testing purposes
     */
    @Getter
    private String emptyGameBoardJSON;
    /**
     * -- SETTER --
     * For Testing purposes
     */
    @Setter
    private Logger logger = LogManager.getLogger(GameBoardManager.class);

    public GameBoardManager() {
        GameBoardService gameBoardService = new GameBoardService();
        this.gameBoardRocket = gameBoardService.createGameBoard();
        initGameBoardJSON();
    }

    public void initGameBoardJSON() {
        GameBoardService gameBoardService = new GameBoardService();
        gameBoardRocket = gameBoardService.createGameBoard();
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.emptyGameBoardJSON = mapper.writeValueAsString(gameBoardRocket);
        } catch (JsonProcessingException e) {
            logger.error("JSON serialization error", e);
        }
    }

    /**
     * gets FieldUpdateMessage from Client
     */
    public void updateUser(CreateUserService player, FieldUpdateMessage fieldUpdateMessage) {
        try {
            GameBoard gameBoard = player.getGameBoard();
            Floor floor = gameBoard.getFloorAtIndex(fieldUpdateMessage.floor());
            Chamber chamber = floor.getChamber(fieldUpdateMessage.chamber());
            Field field = chamber.getField(fieldUpdateMessage.field());

            field.setFieldValue(fieldUpdateMessage.fieldValue());
        } catch (NullPointerException e) {
            logger.error("Failed to update field value due to null object reference", e);
        }
    }

    public void updateClientGameBoard(CreateUserService player, FieldUpdateMessage fieldUpdateMessage) {
        if (player == null) {
            logger.warn("Attempted to update game board for null player");
            return;
        }
        String payload = serializeFieldUpdateMessage(fieldUpdateMessage);
        JSONObject jsonObject = new GenerateJSONObjectService("makeMove", player.getUsername(), true, payload, "").generateJSONObject();
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        logger.info("GameBoard Update sent for {}", player.getUsername());
    }

    public void updateClientGameBoardFromGame(CreateUserService player, String payload) {
        JSONObject jsonObject = new GenerateJSONObjectService("makeMove", player.getUsername(), true, payload, "").generateJSONObject();
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        logger.info("Rerouted GameBoard Update sent for {}", player.getUsername());
    }

    String serializeFieldUpdateMessage(FieldUpdateMessage fieldUpdateMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(fieldUpdateMessage);
        } catch (JsonProcessingException e) {
            logger.error("JSON serialization error", e);
            return null;
        }
    }

    public void informClientsAboutStart(List<CreateUserService> players) {
        for (CreateUserService player : players) {
            logger.info("Player: {} wird informiert", player.getUsername());
            JSONObject jsonObject = new GenerateJSONObjectService("gameIsStarted", player.getUsername(), true, "", "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }

    public void informClientsAboutGameState(List<CreateUserService> players, String currentGameState) {
        for (CreateUserService player : players) {
            logger.info("Player: {} wird informiert (GameState)", player.getUsername());
            JSONObject jsonObject = new GenerateJSONObjectService("notifyGameState", player.getUsername(), true, currentGameState, "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }

    public void notifyAllClients(List<CreateUserService> players, String action) {
        logger.info("Notifying multiple player about {}", "DingDong");
        for (CreateUserService player : players) {
            logger.info("Notify Player {} about {}", player.getUsername(), action);
            JSONObject jsonObject = new GenerateJSONObjectService(action, player.getUsername(), true, "", "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }

    public void notifySingleClient(CreateUserService player, String action) {
        logger.info("Notify Player {} about {}", player.getUsername(), action);
        JSONObject jsonObject = new GenerateJSONObjectService(action, player.getUsername(), true, "", "").generateJSONObject();
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
    }

    public void informClientsAboutCheat(List<CreateUserService> players, String username) {
        logger.info("Player werden über cheat informiert");
        for (CreateUserService player : players) {
            logger.info("Player: {} wird über cheat informiert", player.getUsername());
            JSONObject jsonObject = new GenerateJSONObjectService("playerHasCheated", player.getUsername(), true, username , "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }

    public void informClientsAboutDetectedCheat(List<CreateUserService> players, String username, boolean hasCheated) {
        for (CreateUserService player : players) {
            logger.info("Player: {} wird über detect cheat informiert", player.getUsername());
            JSONObject jsonObject = new GenerateJSONObjectService(
                    hasCheated ? "playerDetectedCheatCorrect" : "playerDetectedCheatWrong",
                    player.getUsername(),
                    true,
                    username,
                    "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }

    public void addRocketToPlayer(CreateUserService player, int rocketCount) {
        logger.info("Player: {} gets {} Rockets", player.getUsername(), rocketCount);
        JSONObject jsonObject = new GenerateJSONObjectService("addRocket", player.getUsername(), true, String.valueOf(rocketCount), "").generateJSONObject();
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
    }
}