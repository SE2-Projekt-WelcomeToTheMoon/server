package websocketserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;
import websocketserver.services.user.CreateUserService;

import static org.mockito.Mockito.*;

class GameServiceTest {
    private GameService gameService;
    @Mock
    Logger logger;
    @Mock
    CreateUserService mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger = mock(Logger.class);
        mockPlayer = mock(CreateUserService.class);
        this.gameService = new GameService();
        this.gameService.setLogger(logger);
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
