package websocketserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GameServiceTest {
    private GameService gameService;
    @Mock
    Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger = mock(Logger.class);
        this.gameService = new GameService();
        this.gameService.setLogger(logger);
    }

    @Test
    void testUpdateUser() {
        gameService.updateUser("", "");
        verify(logger).info("GameService updateUser");
    }

    @Test
    void testSendInvalidCombination() {
        gameService.sendInvalidCombination(null);
        verify(logger).info("GameService sendInvalidCombination");
    }

    @Test
    void testInformPlayersAboutEndOfGame() {
        gameService.informPlayersAboutEndOfGame(null, null);
        verify(logger).info("GameService informPlayersAboutEndOfGame");
    }

    @Test
    void testInformPlayerAboutSystemerror() {
        gameService.informPlayerAboutSystemerror(null);
        verify(logger).info("GameService informPlayerAboutSystemerror");
    }

}
