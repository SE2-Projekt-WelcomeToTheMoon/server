package websocketserver.game.model;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.ChosenCardCombination;
import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.GameState;
import websocketserver.game.exceptions.GameStateException;
import websocketserver.game.services.GameBoardService;
import websocketserver.game.util.FieldUpdateMessage;
import websocketserver.services.CardManager;
import websocketserver.services.GameService;
import websocketserver.services.user.CreateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
class GameTest {

    @Autowired
    Game gameObject;

    @Mock
    Logger logger;

    @Mock
    CreateUserService player1;
    @Mock
    CreateUserService player2;

    @Autowired
    GameBoardService gameBoardService;

    @InjectMocks
    private Game game;

    @Mock
    private WebSocketSession session;

    @Mock
    private CreateUserService player;

    @Mock
    private CreateUserService cheater;

    @Mock
    private GameBoard playerGameBoard;

    @Mock
    private GameBoard cheaterGameBoard;

    @Mock
    private CardManager mockCardManager;

    CardCombination[] combinations = {
            new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.RAUMANZUG, FieldValue.ONE),
            new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.ENERGIE, FieldValue.TWO),
            new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.ENERGIE, FieldValue.THREE)
    };
    @Autowired
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        // Clearing players list and adding mock players

        MockitoAnnotations.openMocks(this);

        gameObject.setLogger(logger);
        gameObject.getPlayers().clear();
        gameObject.addPlayer(player1);
        when(player1.getUsername()).thenReturn("Player1");
        when(player2.getUsername()).thenReturn("Player2");

        doNothing().when(player1).createGameBoard();
        doNothing().when(player2).createGameBoard();

        when(player1.getGameBoard()).thenReturn(gameBoardService.createGameBoard());
        when(player2.getGameBoard()).thenReturn(gameBoardService.createGameBoard());

        when(player1.getSession()).thenReturn(session);
        when(player2.getSession()).thenReturn(session);

        when(mockCardManager.getCurrentCombination()).thenReturn(combinations);

        when(player.getUsername()).thenReturn("player1");
        when(cheater.getUsername()).thenReturn("player2");
        when(player.getGameBoard()).thenReturn(playerGameBoard);
        when(cheater.getGameBoard()).thenReturn(cheaterGameBoard);

        List<CreateUserService> players = new ArrayList<>();
        players.add(player);
        players.add(cheater);
        game.players = players;

    }

    @Test
    void testGetPlayerList() {
        assertThat(gameObject.getPlayers()).hasSize(1);
    }

    @Test
    void testStartGameSuccess() throws InterruptedException, ExecutionException {
        gameObject.addPlayer(player2);
        gameObject.startGame();

        player1.getGameBoard().addRockets(35);

        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.RAUMANZUG, FieldValue.ONE);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            gameObject.receiveValueAtPositionOfPlayer(player1, 1, 1, cardCombination);
            gameObject.receiveValueAtPositionOfPlayer(player2, 1, 1, cardCombination);
        });

        future.get(); // Wait until all players have set their values

        assertEquals(GameState.FINISHED, gameObject.getGameState());

        assertThrows(GameStateException.class, () -> gameObject.startGame());
    }

    @Test
    void testDoRoundOneWrongGameState() {
        Game game1 = new Game(null, null);
        assertThrows(GameStateException.class, game1::doRoundOne);
    }

    @Test
    void testGetUserByUserName() {
        assertNotNull(gameObject.getUserByUsername("Player1"));
        assertNull(gameObject.getUserByUsername("Player3"));
    }

    @Test
    void testCheatSuccessful() throws IOException, ParseException {
        when(player.getUsername()).thenReturn("player1");

        game.cheat(session, "player1");

        verify(playerGameBoard).cheat();
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        TextMessage sentMessage = captor.getValue();
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject response = (JSONObject) parser.parse(sentMessage.getPayload());
        assertEquals("cheat", response.getAsString("action"));
        assertEquals("player1", response.getAsString("username"));
    }

    @Test
    void testCheatPlayerNotFound() throws IOException {
        when(player.getUsername()).thenReturn("player2");

        game.cheat(session, "player1");

        verify(playerGameBoard, never()).cheat();
        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testDetectCheatSuccessful() throws IOException, ParseException {
        when(player.getUsername()).thenReturn("player1");
        when(cheater.getUsername()).thenReturn("player2");
        when(cheaterGameBoard.isHasCheated()).thenReturn(true);

        boolean result = game.detectCheat(session, "player1", "player2");

        assertTrue(result);
        verify(cheaterGameBoard).isHasCheated();
        verify(playerGameBoard).addRockets(1);
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        TextMessage sentMessage = captor.getValue();
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject response = (JSONObject) parser.parse(sentMessage.getPayload());
        assertEquals("detectCheat", response.getAsString("action"));
        assertEquals("player1", response.getAsString("username"));
    }

    @Test
    void testDetectCheatNotSuccessful() throws IOException, ParseException {
        when(player.getUsername()).thenReturn("player1");
        when(cheater.getUsername()).thenReturn("player2");
        when(cheaterGameBoard.isHasCheated()).thenReturn(false);

        boolean result = game.detectCheat(session, "player1", "player2");

        assertFalse(result);
        verify(cheaterGameBoard).isHasCheated();
        verify(playerGameBoard).addRockets(-1);
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        TextMessage sentMessage = captor.getValue();
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject response = (JSONObject) parser.parse(sentMessage.getPayload());
        assertEquals("detectCheat", response.getAsString("action"));
        assertEquals("player1", response.getAsString("username"));
    }

    @Test
    void testFindCorrectCombinationOne() {
        game = new Game(mockCardManager, null);
        when(mockCardManager.getCurrentCombination()).thenReturn(combinations);
        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.RAUMANZUG, FieldValue.ONE);
        assertEquals(ChosenCardCombination.ONE, game.findCorrectCombination(cardCombination));
    }

    @Test
    void testFindCorrectCombinationTwo() {
        game = new Game(mockCardManager, null);
        when(mockCardManager.getCurrentCombination()).thenReturn(combinations);
        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.ENERGIE, FieldValue.TWO);
        assertEquals(ChosenCardCombination.TWO, game.findCorrectCombination(cardCombination));
    }

    @Test
    void testFindCorrectCombinationThree() {
        game = new Game(mockCardManager, null);
        when(mockCardManager.getCurrentCombination()).thenReturn(combinations);
        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.ENERGIE, FieldValue.THREE);
        assertEquals(ChosenCardCombination.THREE, game.findCorrectCombination(cardCombination));
    }

    @Test
    void testFindCorrectCombinationNone() {
        game = new Game(mockCardManager, null);
        when(mockCardManager.getCurrentCombination()).thenReturn(combinations);
        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.ENERGIE, FieldValue.FIVE);
        assertNull(game.findCorrectCombination(cardCombination));
    }

    @Test
    void testGetServerCoordinatesFloorZeroOneFour() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(0);
        when(mockMessage.field()).thenReturn(5);
        assertArrayEquals(new int[]{0, 5}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorTwoThreeSixSeven() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(2);
        when(mockMessage.field()).thenReturn(5);
        when(mockMessage.chamber()).thenReturn(3);
        assertArrayEquals(new int[]{2, 11}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorFiveChamberZero() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(5);
        when(mockMessage.field()).thenReturn(5);
        when(mockMessage.chamber()).thenReturn(0);
        assertArrayEquals(new int[]{5, 5}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorFiveChamberOne() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(5);
        when(mockMessage.field()).thenReturn(5);
        when(mockMessage.chamber()).thenReturn(1);
        assertArrayEquals(new int[]{5, 10}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorFiveChamberOther() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(5);
        when(mockMessage.field()).thenReturn(5);
        when(mockMessage.chamber()).thenReturn(2);
        assertArrayEquals(new int[]{5, 12}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorOther() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(9);
        assertArrayEquals(new int[]{0, 0}, game.getServerCoordinates(mockMessage));
    }
}

