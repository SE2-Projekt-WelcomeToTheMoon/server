package WebsocketServer.game.model;

import WebsocketServer.game.enums.ChoosenCardCombination;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.GameStateException;
import WebsocketServer.game.services.CardController;
import WebsocketServer.game.services.GameBoardService;
import WebsocketServer.services.GameService;
import WebsocketServer.services.user.CreateUserService;
import org.hamcrest.core.IsAnything;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
class GameTest {

    @Autowired
    Game game;
    @Mock
    Game mockedGame;
    @Mock
    GameService mockedGameService;
    @Mock
    CardController mockedCardController;
    @Mock
    GameBoard mockedGameBoard1;
    @Mock
    GameBoard mockedGameBoard2;


    @Mock
    CreateUserService player1;
    @Mock
    CreateUserService player2;

    @Autowired
    GameBoardService gameBoardService;

    @BeforeEach
    public void setUp() {
        // Clearing players list and adding mock players

        MockitoAnnotations.openMocks(this);
        game.getPlayers().clear();
        game.addPlayer(player1);
        when(player1.getUsername()).thenReturn("Player1");
        when(player2.getUsername()).thenReturn("Player2");

        doNothing().when(player1).createGameBoard();
        doNothing().when(player2).createGameBoard();

        when(player1.getGameBoard()).thenReturn(gameBoardService.createGameBoard());
        when(player2.getGameBoard()).thenReturn(gameBoardService.createGameBoard());

    }

    @Test
    void testGetPlayerList() {
        assertThat(game.getPlayers()).hasSize(1);
    }

    @Test
    void testWrongStateForStart(){


    }
    @Test
    void testStartGameSuccess() throws InterruptedException, ExecutionException, ExecutionException {
        game.addPlayer(player2);
        game.startGame();

        player1.getGameBoard().addRockets(35);

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            game.receiveSelectedCombinationOfPlayer(player1, ChoosenCardCombination.ONE);
            game.receiveSelectedCombinationOfPlayer(player2, ChoosenCardCombination.TWO);
        });

        future1.get(); // Wait until all players have made their choice

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            game.receiveValueAtPositionOfPlayer(player1, 1, 1, FieldValue.ONE);
            game.receiveValueAtPositionOfPlayer(player2, 1, 1, FieldValue.TWO);
        });

        future2.get(); // Wait until all players have set their values

        Thread.sleep(100);

        assertEquals(GameState.FINISHED, game.getGameState());

        assertThrows(GameStateException.class, () -> game.startGame());
    }


    @Test
    void testWrongStateForRound() throws InterruptedException {
        assertThrows(GameStateException.class, () -> game.receiveSelectedCombinationOfPlayer(player1, ChoosenCardCombination.ONE));
    }

    @Test
    void testDoRoundOneWrongGameState() {
        // Set game state to something other than ROUND_ONE
        GameBoardService gameBoardService1 = new GameBoardService();
        GameBoard gameBoard = gameBoardService1.createGameBoard();


        // Attempt to run doRoundOne and expect a GameStateException
        ReflectionTestUtils.setField(mockedGame, "cardController", mockedCardController);
        ReflectionTestUtils.setField(mockedGame, "gameService", mockedGameService);
        when(mockedGame.getGameState()).thenReturn(GameState.ROUND_ONE);

        mockedGame.addPlayer(player1);
        mockedGame.addPlayer(player2);

        doNothing().when(mockedCardController).drawNextCard();

        when(player1.getGameBoard()).thenReturn(mockedGameBoard1);
        when(player2.getGameBoard()).thenReturn(mockedGameBoard2);
        when(mockedGameBoard1.addSystemError()).thenReturn(true);
        when(mockedGameBoard2.addSystemError()).thenReturn(false);
        when(mockedGameBoard1.checkCardCombination(any())).thenReturn(true);
        when(mockedGameBoard2.checkCardCombination(any())).thenReturn(true);

        mockedGame.doRoundOne();
    }

}