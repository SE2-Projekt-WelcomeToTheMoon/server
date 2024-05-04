package WebsocketServer.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.model.FieldUpdateMessage;
import WebsocketServer.game.model.GameBoard;
import WebsocketServer.game.model.Player;
import WebsocketServer.game.services.GameBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class GameBoardManagerTest {
    @Mock
    private WebSocketSession session;
    @Mock
    private GameBoardService gameBoardService;
    @Mock
    private Player player;
    @Mock
    private GameBoard gameBoard;
    FieldUpdateMessage testMessage;

    private GameBoardManager gameBoardManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        gameBoardManager = new GameBoardManager(session);
        when(gameBoardService.createGameBoard()).thenReturn(gameBoard);
        testMessage = new FieldUpdateMessage(1,1,1,FieldValue.ONE);

        gameBoard = mock(GameBoard.class);
        player = mock(Player.class);
        when(player.getGameBoard()).thenReturn(gameBoard);
        when(player.getPlayerId()).thenReturn(UUID.randomUUID());
    }

    @Test
    void shouldSuccessfullyUpdateGameBoardWhenJsonValid() {
        ObjectMapper mapper = new ObjectMapper();
        String message = null;
        try {
            message = mapper.writeValueAsString(testMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        FieldUpdateMessage updateMessage = null;
        try {
            updateMessage = mapper.readValue(message, FieldUpdateMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertTrue(gameBoardManager.updateUser(player, message));
        verify(gameBoard, times(1)).setValueWithinFloorAtIndex(updateMessage.floor(), updateMessage.chamber(), updateMessage.fieldValue());
    }

    @Test
    void shouldReturnFalseWhenJsonMalformed() {
        String malformedJson = "{bad_json}";

        assertFalse(gameBoardManager.updateUser(player, malformedJson));
    }

    @Test
    void shouldReturnFalseWhenGameBoardUpdateFails() {
        String message = "{\"floor\":1, \"chamber\":2, \"field\":1 \"fieldValue\":ONE}";
        assertFalse(gameBoardManager.updateUser(player, message));
    }

    @Test
    void shouldReturnFalseWhenPlayerIsNull(){
        player = null;

        assertFalse(gameBoardManager.updateUser(player, "dingdong"));
    }

    @Test
    void shouldReturnString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(gameBoard);

        String test = gameBoardManager.getGameBoardUser(player);
        assertEquals(result,test);
    }

    @Test
    void shouldReturnNullForNullPlayerInGetGameBoardUser() {
        assertNull(gameBoardManager.getGameBoardUser(null));
    }

    @Test
    void testInitGameBoardsTrue(){
        List<Player> playerList = new ArrayList<>();
        playerList.add(player);

        assertTrue(gameBoardManager.initGameBoards(playerList));
    }

    @Test
    void testInitGameBoardsFalse(){
        List<Player> playerList = new ArrayList<>();

        assertFalse(gameBoardManager.initGameBoards(playerList));
    }

    @Test
    void testInitGameBoardsFalse2(){
        List<Player> playerList = null;

        assertFalse(gameBoardManager.initGameBoards(playerList));
    }

    @Test
    void testInitGameBoardsFalse3(){
        List<Player> playerList = new ArrayList<>();
        playerList.add(null);

        assertFalse(gameBoardManager.initGameBoards(playerList));
    }

    @Test
    void testSerializeGameBoard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(gameBoard);
        String test = gameBoardManager.serializeGameBoard(gameBoard);

        assertEquals(result, test);
    }
}


