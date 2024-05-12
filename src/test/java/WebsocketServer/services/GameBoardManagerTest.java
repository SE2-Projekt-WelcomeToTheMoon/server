package WebsocketServer.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.model.FieldUpdateMessage;
import WebsocketServer.game.model.GameBoard;
import WebsocketServer.game.services.GameBoardService;
import WebsocketServer.services.user.CreateUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;


class GameBoardManagerTest {
    private GameBoardManager gameBoardManager;
    private CreateUserService player;
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        gameBoardManager = new GameBoardManager();
        logger = mock(Logger.class);
        this.player = new CreateUserService(mock(WebSocketSession.class), "User");
    }

    @Test
    void testInitGameBoardJSON() {
        GameBoardService gameBoardService = new GameBoardService();
        GameBoard testGameBoard = gameBoardService.createGameBoard();
        ObjectMapper mapper = new ObjectMapper();
        String emptyGameBoardJSON = null;
        try {
            emptyGameBoardJSON = mapper.writeValueAsString(testGameBoard);
            System.out.println(emptyGameBoardJSON);
        } catch (JsonProcessingException e) {
            fail("JSON serialization error");
        }
        assertEquals(emptyGameBoardJSON, gameBoardManager.getEmptyGameBoardJSON());
    }

    @Test
    void testUpdateUser() {
        GameBoardService gameBoardService = new GameBoardService();
        GameBoard gameBoard = gameBoardService.createGameBoard();
        player.setGameBoard(gameBoard);

        assertEquals(FieldValue.NONE, player.getGameBoard().getFloorAtIndex(0).getChamber(0).getField(0).getFieldValue());

        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE);
        ObjectMapper mapper = new ObjectMapper();
        String message = null;
        try {
            message = mapper.writeValueAsString(fieldUpdateMessage);
        } catch (JsonProcessingException e) {
            fail("JSON serialization error");
        }
        gameBoardManager.updateUser(player, message);
        assertEquals(FieldValue.FIVE, player.getGameBoard().getFloorAtIndex(0).getChamber(0).getField(0).getFieldValue());
    }

    @Test
    void testUpdateUserJsonProcessingException() {
        String invalidMessage = "";

        gameBoardManager.setLogger(logger);
        gameBoardManager.updateUser(player, invalidMessage);

        verify(logger).error(eq("JSON deserialization error"), any(JsonProcessingException.class));
    }

    @Test
    void testUpdateUserNullPointerException() {
        player.setGameBoard(null);
        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE);
        ObjectMapper mapper = new ObjectMapper();
        String validJsonMessage = null;
        try {
            validJsonMessage = mapper.writeValueAsString(fieldUpdateMessage);
        } catch (JsonProcessingException e) {
            fail("JSON serialization error");
        }

        gameBoardManager.setLogger(logger);
        gameBoardManager.updateUser(player, validJsonMessage);

        verify(logger).error(eq("Failed to update field value due to null object reference"), any(NullPointerException.class));
    }

    @Test
    void testUpdateClientGameBoard() {
        gameBoardManager.setLogger(logger);
        gameBoardManager.updateClientGameBoard(player, new FieldUpdateMessage(0,0,0,FieldValue.FIVE));

        verify(logger).info("GameBoard Update sent for {}", player.getUsername());
    }

    @Test
    void testUpdateClientGameBoardNullPlayer() {
        gameBoardManager.setLogger(logger);
        gameBoardManager.updateClientGameBoard(null, null);

        verify(logger).warn("Attempted to update game board for null player");
    }

    @Test
    void testSerializeFieldUpdateMessage() {

        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE);
        ObjectMapper mapper = new ObjectMapper();
        String fieldUpdateJSON = null;
        try {
            fieldUpdateJSON = mapper.writeValueAsString(fieldUpdateMessage);
        } catch (JsonProcessingException e) {
            fail("JSON serialization error");
        }
        assertEquals(fieldUpdateJSON, gameBoardManager.serializeFieldUpdateMessage(fieldUpdateMessage));
    }

    @Test
    void testInformClientsAboutStart() {
        List<CreateUserService> players = new ArrayList<>();
        players.add(player);

        gameBoardManager.setLogger(logger);
        gameBoardManager.informClientsAboutStart(players);
        verify(logger).info("Player: {} wird informiert", player.getUsername());
    }

    @Test
    void testFieldUpdateMessage(){
        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0,0,0,FieldValue.FIVE);
        ObjectMapper mapper = new ObjectMapper();
        String message = null;
        try {
            message = mapper.writeValueAsString(fieldUpdateMessage);
        } catch (JsonProcessingException e) {
            fail("JSON serialization error");
        }
        System.out.println(message);
    }

    @Test
    void testUpdateClientGameBoardFromGame(){
        gameBoardManager.setLogger(logger);
        gameBoardManager.updateClientGameBoardFromGame(player, "test");
        verify(logger).info("Rerouted GameBoard Update sent for {}", player.getUsername());
    }

}


