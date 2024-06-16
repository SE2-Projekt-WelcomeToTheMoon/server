package websocketserver.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.GameState;
import websocketserver.game.util.FieldUpdateMessage;
import websocketserver.game.model.GameBoard;
import websocketserver.game.services.GameBoardService;
import websocketserver.services.user.CreateUserService;
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
    private List<CreateUserService> players;
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameBoardManager = new GameBoardManager();
        logger = mock(Logger.class);
        gameBoardManager.setLogger(logger);

        this.player = new CreateUserService(mock(WebSocketSession.class), "User");
        this.players = new ArrayList<>();
        players.add(player);
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

        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE, "", null);
        gameBoardManager.updateUser(player, fieldUpdateMessage);
        assertEquals(FieldValue.FIVE, player.getGameBoard().getFloorAtIndex(0).getChamber(0).getField(0).getFieldValue());
    }

    @Test
    void testUpdateUserNullPointerException() {
        player.setGameBoard(null);
        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE, "", null);

        gameBoardManager.setLogger(logger);
        gameBoardManager.updateUser(player, fieldUpdateMessage);

        verify(logger).error(eq("Failed to update field value due to null object reference"), any(NullPointerException.class));
    }

    @Test
    void testUpdateClientGameBoard() {
        gameBoardManager.setLogger(logger);
        gameBoardManager.updateClientGameBoard(player, new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE, "", null));

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

        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE, "", null);
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
        gameBoardManager.informClientsAboutStart(players);
        verify(logger).info("Player: {} wird informiert", player.getUsername());
    }

    @Test
    void testFieldUpdateMessage() {
        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(0, 0, 0, FieldValue.FIVE, "test", null);
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
    void testUpdateClientGameBoardFromGame() {
        gameBoardManager.updateClientGameBoardFromGame(player, "test");
        verify(logger).info("Rerouted GameBoard Update sent for {}", player.getUsername());
    }

    @Test
    void testInformClientsAboutCheat() {
        gameBoardManager.informClientsAboutCheat(players, "player1");

        verify(logger).info("Player: {} wird über cheat informiert", player.getUsername());
    }

    @Test
    void testInformClientsAboutDetectedCheat() {
        gameBoardManager.informClientsAboutDetectedCheat(players, "player1", true);

        verify(logger).info("Player: {} wird über detect cheat informiert", player.getUsername());
    }

    @Test
    void testInformClientsAboutGameState() {
        gameBoardManager.informClientsAboutGameState(players, GameState.INITIAL.toString());

        verify(logger).info("Player: {} wird informiert (GameState)", player.getUsername());
    }

    @Test
    void testNotifyAllClients() {
        gameBoardManager.notifyAllClients(players, "DingDong");

        verify(logger).info("Notifying multiple player about {}", "DingDong");
        verify(logger).info("Notify Player {} about {}", player.getUsername(), "DingDong");
    }

    @Test
    void testNotifySingleClient() {
        gameBoardManager.notifySingleClient(player, "DingDong");

        verify(logger).info("Notify Player {} about {}", player.getUsername(), "DingDong");
    }
}