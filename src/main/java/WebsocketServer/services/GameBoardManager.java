package WebsocketServer.services;

import WebsocketServer.game.model.*;
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
    private GameBoard gameBoardRocket;
    private String emptyGameBoardJSON;
    private final Logger logger = LogManager.getLogger(GameBoardManager.class);

    public GameBoardManager(WebSocketSession session) {
        this.session = session;
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

    public boolean updateFromUser(CreateUserService player, String message) {
        ObjectMapper mapper = new ObjectMapper();
        FieldUpdateMessage fieldUpdateMessage;
        try {
            fieldUpdateMessage = mapper.readValue(message, FieldUpdateMessage.class);
        } catch (JsonProcessingException e) {
            logger.error("JSON deserialization error", e);
            return false;
        }
        // just for readbilities sake
        try {
            GameBoard gameBoard = player.getGameBoard();
            Floor floor = gameBoard.getFloorAtIndex(fieldUpdateMessage.floor());
            Chamber chamber = floor.getChamber(fieldUpdateMessage.chamber());
            Field field = chamber.getField(fieldUpdateMessage.field());

            field.setFieldValue(fieldUpdateMessage.fieldValue());
        } catch (NullPointerException e) {
            logger.error("Failed to update field value due to null object reference", e);
            return false;
        }

        return true;
    }

    public boolean updateClientGameBoard(CreateUserService player, GameBoard gameBoard) {
        if (player == null) {
            logger.warn("Attempted to update game board for null player");
            return false;
        }
        String payload = serializeGameBoard(gameBoard);
        JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("updateGameBoard", player.getUsername(), true, payload, "");
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);

        return true;
    }

    public String getGameBoardUser(CreateUserService player) {
        if (player == null) {
            logger.warn("Attempted to get game board for null player");
            return null;
        }

        return serializeGameBoard(player.getGameBoard());
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
        for (CreateUserService player : players) {
            logger.info("Player: {} wird informiert", player.getUsername());
            JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("gameIsStarted", player.getUsername(), true, this.emptyGameBoardJSON, "");
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        }
    }
}