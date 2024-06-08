package websocketserver.services;

import websocketserver.game.model.*;
import websocketserver.game.services.GameBoardService;
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
    public void updateUser(CreateUserService player, String message) {
        ObjectMapper mapper = new ObjectMapper();
        FieldUpdateMessage fieldUpdateMessage;
        try {
            fieldUpdateMessage = mapper.readValue(message, FieldUpdateMessage.class);
        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return;
        }
        // just for readabilities sake
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
        JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("makeMove", player.getUsername(), true, payload, "");
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        logger.info("GameBoard Update sent for {}", player.getUsername());
    }

    public void updateClientGameBoardFromGame(CreateUserService player, String payload) {
        JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("makeMove", player.getUsername(), true, payload, "");
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
            JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("gameIsStarted", player.getUsername(), true, "", "");
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }
}