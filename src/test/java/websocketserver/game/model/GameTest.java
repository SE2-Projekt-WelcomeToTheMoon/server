package websocketserver.game.model;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.ChosenCardCombination;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.GameState;
import websocketserver.game.exceptions.GameStateException;
import websocketserver.game.services.GameBoardService;
import websocketserver.services.CardManager;
import websocketserver.services.GameBoardManager;
import websocketserver.services.GameService;
import websocketserver.services.user.CreateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

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
    Game mockedGame;
    @Mock
    GameService mockedGameService;
    @Mock
    CardManager mockedCardManager;
    @Mock
    GameBoard mockedGameBoard1;
    @Mock
    GameBoard mockedGameBoard2;
    @Mock
    GameBoardManager mockedGameBoardManager;


    @Mock
    CreateUserService player1;
    @Mock
    CreateUserService player2;

    @Autowired
    GameBoardService gameBoardService;

    @InjectMocks
    private Game game;

    @Mock
    private GameService gameService;

    @Mock
    private GameBoardManager gameBoardManager;

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

    @BeforeEach
    public void setUp() {
        // Clearing players list and adding mock players

        MockitoAnnotations.openMocks(this);
        gameObject.getPlayers().clear();
        gameObject.addPlayer(player1);
        when(player1.getUsername()).thenReturn("Player1");
        when(player2.getUsername()).thenReturn("Player2");

        doNothing().when(player1).createGameBoard();
        doNothing().when(player2).createGameBoard();

        when(player1.getGameBoard()).thenReturn(gameBoardService.createGameBoard());
        when(player2.getGameBoard()).thenReturn(gameBoardService.createGameBoard());

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
    void testWrongStateForStart(){


    }
    @Test
    void testStartGameSuccess() throws InterruptedException, ExecutionException {
        gameObject.addPlayer(player2);
        gameObject.startGame();

        player1.getGameBoard().addRockets(35);

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            gameObject.receiveSelectedCombinationOfPlayer(player1, ChosenCardCombination.ONE);
            gameObject.receiveSelectedCombinationOfPlayer(player2, ChosenCardCombination.TWO);
        });

        future1.get(); // Wait until all players have made their choice

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            gameObject.receiveValueAtPositionOfPlayer(player1, 1, 1, FieldValue.ONE);
            gameObject.receiveValueAtPositionOfPlayer(player2, 1, 1, FieldValue.TWO);
        });

        future2.get(); // Wait until all players have set their values

        Thread.sleep(100);

        assertEquals(GameState.FINISHED, gameObject.getGameState());

        assertThrows(GameStateException.class, () -> gameObject.startGame());
    }


    @Test
    void testWrongStateForRound() {
        assertThrows(GameStateException.class, () -> gameObject.receiveSelectedCombinationOfPlayer(player1, ChosenCardCombination.ONE));
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
    void testUpdateUser() {
        gameObject.setGameBoardManager(mockedGameBoardManager);
        gameObject.addPlayer(player2);

        gameObject.updateUser("Player1", "message");

        verify(mockedGameBoardManager, times(1)).updateUser(any(), any());
        verify(mockedGameBoardManager, times(1)).updateClientGameBoardFromGame(any(), any());
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
}