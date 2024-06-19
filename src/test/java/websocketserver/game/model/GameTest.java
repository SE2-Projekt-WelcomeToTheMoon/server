package websocketserver.game.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private GameService mockGameService;

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

        gameObject = new Game(mockCardManager, gameService);

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
    void testLoop() throws InterruptedException, ExecutionException {
        gameObject.addPlayer(player2);
        gameObject.startGame();

        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.RAUMANZUG, FieldValue.ONE);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            gameObject.receiveValueAtPositionOfPlayer(player1, 1, 1, cardCombination);
            gameObject.receiveValueAtPositionOfPlayer(player2, 1, 1, cardCombination);
        });

        future.get(); // Wait until all players have set their values

        verify(logger).info("Round SIX finished, starting new round");
        assertEquals(GameState.ROUND_THREE, gameObject.getGameState());

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
        CardCombination cardCombination = new CardCombination(FieldCategory.PFLANZE, FieldCategory.ENERGIE, FieldValue.FIVE);
        assertNull(game.findCorrectCombination(cardCombination));
    }

    @Test
    void testGetServerCoordinatesFloorZeroOneFourFive() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(0);
        when(mockMessage.field()).thenReturn(5);
        assertArrayEquals(new int[]{0, 5}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorTwoThreeSevenEight() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(2);
        when(mockMessage.field()).thenReturn(1);
        when(mockMessage.chamber()).thenReturn(3);
        assertArrayEquals(new int[]{2, 7}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorSixChamberZero() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(6);
        when(mockMessage.field()).thenReturn(2);
        when(mockMessage.chamber()).thenReturn(0);
        assertArrayEquals(new int[]{6, 2}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorSixChamberOne() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(6);
        when(mockMessage.field()).thenReturn(1);
        when(mockMessage.chamber()).thenReturn(1);
        assertArrayEquals(new int[]{6, 6}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorSixChamberOther() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(6);
        when(mockMessage.field()).thenReturn(1);
        when(mockMessage.chamber()).thenReturn(2);
        assertArrayEquals(new int[]{6, 8}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testGetServerCoordinatesFloorOther() {
        game = new Game(null, null);
        FieldUpdateMessage mockMessage = mock(FieldUpdateMessage.class);
        when(mockMessage.floor()).thenReturn(9);
        assertArrayEquals(new int[]{0, 0}, game.getServerCoordinates(mockMessage));
    }

    @Test
    void testReceiveValueAtPositionInvalidMove() {

        gameObject.startGame();
        gameObject.addPlayer(player2);
        CardCombination cardCombination = new CardCombination(FieldCategory.RAUMANZUG, FieldCategory.RAUMANZUG, FieldValue.ONE);

        gameObject.receiveValueAtPositionOfPlayer(player2, 5, 1, cardCombination);
        assertFalse(gameObject.currentPlayerDraw.containsKey(player2));
    }

    @Test
    void testCheckChamberCompletion() {
        Floor floor = new Floor(FieldCategory.ENERGIE);

        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward(RewardCategory.ROCKET, 5));
        rewards.add(new Reward(RewardCategory.SYSTEMERROR, 1));
        Chamber chamber = new Chamber(FieldCategory.ENERGIE, rewards, 1);
        Field field = chamber.getFields().get(0);
        chamber.finalizeChamber();
        field.setFieldValue(FieldValue.ONE);

        floor.addChamber(chamber);

        when(player.getGameBoard().getFloorAtIndex(anyInt())).thenReturn(floor);
        doNothing().when(mockGameService).informPlayerAboutSystemerror(any());
        doNothing().when(mockGameService).addRocketToPlayer(any(), anyInt());

        game.checkChamberCompletion(player, 0);

        verify(logger).info("Chamber completed, handling Rewards for {}", player.getUsername());

        game.checkChamberCompletion(player, 0);

        verify(logger).info("Rewards already handled");
    }

    @Test
    void testReturnFieldUpdateMessage() {
        CardCombination combination = new CardCombination(FieldCategory.ENERGIE, FieldCategory.ENERGIE, FieldValue.ONE);
        FieldUpdateMessage fieldUpdateMessage = new FieldUpdateMessage(1, 1, 1, FieldValue.ONE, "owner", combination);
        ObjectMapper objectMapper = new ObjectMapper();
        String result;
        try {
            result = objectMapper.writeValueAsString(fieldUpdateMessage);
        } catch (IOException e) {
            result = "";
        }
        // cant simply assertEquals cause Combination gets Created; therefore we need to compare individual fields

        FieldUpdateMessage convertedFieldUpdateMessage = game.returnFieldUpdateMessage(result);
        assertEquals(fieldUpdateMessage.floor(), convertedFieldUpdateMessage.floor());
        assertEquals(fieldUpdateMessage.field(), convertedFieldUpdateMessage.field());
        assertEquals(fieldUpdateMessage.chamber(), convertedFieldUpdateMessage.chamber());
        assertEquals(fieldUpdateMessage.fieldValue(), convertedFieldUpdateMessage.fieldValue());
        assertEquals(fieldUpdateMessage.userOwner(), convertedFieldUpdateMessage.userOwner());
    }

    @Test
    void testReceiveValueNoCombination(){
        game.setGameState(GameState.ROUND_THREE);
        game.receiveValueAtPositionOfPlayer(player, 1, 1, null);
        verify(logger).error("CardCombination is null");
    }

    @Test
    void testRoundOneWrongState(){
        game.setGameState(GameState.ROUND_TWO);
        assertThrows(GameStateException.class, game::doRoundOne);
    }

    @Test
    void testRoundTwoWrongState(){
        game.setGameState(GameState.ROUND_THREE);
        assertThrows(GameStateException.class, game::doRoundTwo);
    }

    @Test
    void testRoundThreeWrongState(){
        game.setGameState(GameState.ROUND_FOUR);
        assertThrows(GameStateException.class, game::doRoundThree);
    }

    @Test
    void testRoundFourWrongState(){
        game.setGameState(GameState.ROUND_FIVE);
        assertThrows(GameStateException.class, game::doRoundFour);
    }

    @Test
    void testRoundFiveWrongState(){
        game.setGameState(GameState.ROUND_SIX);
        assertThrows(GameStateException.class, game::doRoundFive);
    }

    @Test
    void testRoundSixWrongState(){
        game.setGameState(GameState.FINISHED);
        assertThrows(GameStateException.class, game::doRoundSix);
    }

    @Test
    void testAddPlayers(){
        Map<String, CreateUserService> players = new HashMap<>();
        players.put("player1", player);
        game.addPlayers(players);
        assertEquals(3, game.getPlayers().size());
        assertEquals(game.getPlayers().get(2), player);
    }
}

