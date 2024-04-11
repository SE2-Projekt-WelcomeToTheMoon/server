package WebsocketServer.game.model;

import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.GameStateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
class GameTest {

    @Autowired
    Game game;


    @Test
    @DirtiesContext
    void testStartGameSuccess() {
        game.startGame();

        //Currently nothing happens within the rounds therefore it should run straight through akk rounds
        assertEquals(GameState.FINISHED, game.getGameState());
    }


    @Test
    @DirtiesContext
    void testWrongStateForRound() {
        game.startGame();
        assertThrows(GameStateException.class, () -> game.startGame());
        assertThrows(GameStateException.class, () -> game.doRoundOne());
        assertThrows(GameStateException.class, () -> game.doRoundTwo());
        assertThrows(GameStateException.class, () -> game.doRoundThree());
        assertThrows(GameStateException.class, () -> game.doRoundFour());
        assertThrows(GameStateException.class, () -> game.doRoundFive());
        assertThrows(GameStateException.class, () -> game.doRoundSix());
    }


}