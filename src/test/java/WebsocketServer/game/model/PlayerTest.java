package WebsocketServer.game.model;

import WebsocketServer.game.services.GameBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerTest {
    @Autowired
    Player player;
    @Autowired
    GameBoardService gameBoardService;

    @Test
    void testCreatePlayer(){
        assertNotNull(player.getGameBoard());
        assertTrue(player.getGameBoard().isFinalized());
    }

    @Test
    void getPlayerId(){
        assertNotNull(player.getPlayerId());
    }
}