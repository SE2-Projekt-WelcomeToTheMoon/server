package WebsocketServer.game.model;

import WebsocketServer.game.enums.ChoosenCardCombination;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.GameStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
class GameTest {

    @Autowired
    Game game;

    @Autowired
    Player player1;
    @Autowired
    Player player2;

    @BeforeEach
    public void setUp() {
        game.getPlayerList().clear();
        game.addPlayer(player1);
    }

    @Test
    void testGetPlayerList() {
        assertThat(game.getPlayerList()).hasSize(1);
    }

    @Test
    @DirtiesContext
    void testStartGameSuccess() throws InterruptedException {
        game.addPlayer(player2);
        game.startGame();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            game.receiveSelectedCombinationOfPlayer(player1, ChoosenCardCombination.ONE);
            game.receiveSelectedCombinationOfPlayer(player2, ChoosenCardCombination.TWO);
        });
        future.join();

        //Timeout to allow waiting for async
        sleep(100);

        future = CompletableFuture.runAsync(() -> {
            game.receiveValueAtPositionOfPlayer(player1, 1, 1, FieldValue.ONE);
            game.receiveValueAtPositionOfPlayer(player2, 1, 1, FieldValue.TWO);
        });
        future.join();
        //Timeout to allow waiting for async
        sleep(100);

        //Currently nothing happens within the rounds therefore it should run straight through akk rounds
        assertEquals(GameState.FINISHED, game.getGameState());
    }


    @Test
    @DirtiesContext
    void testWrongStateForRound() throws InterruptedException {
        game.startGame();

        //Skip waiting for client response in doRoundTwo
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            game.receiveSelectedCombinationOfPlayer(player1, ChoosenCardCombination.ONE);
        });
        future.join();
        //Timeout to allow waiting for async
        sleep(100);

        //Skip waiting for client response in doRoundThree
        future = CompletableFuture.runAsync(() -> {
            game.receiveValueAtPositionOfPlayer(player1, 1, 1, FieldValue.ONE);
        });
        future.join();
        //Timeout to allow waiting for async
        sleep(100);

        assertThrows(GameStateException.class, () -> game.startGame());
        assertThrows(GameStateException.class, () -> game.doRoundOne());
        assertThrows(GameStateException.class, () -> game.doRoundTwo());
        assertThrows(GameStateException.class, () -> game.doRoundThree());
        assertThrows(GameStateException.class, () -> game.doRoundFour());
        assertThrows(GameStateException.class, () -> game.doRoundFive());
        assertThrows(GameStateException.class, () -> game.doRoundSix());
    }


}