package websocketserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.EndType;
import websocketserver.game.model.Game;
import websocketserver.services.user.CreateUserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static websocketserver.websocket.handler.WebSocketHandlerImpl.gameService;

class GameServiceTest {
    private GameService gameServiceObject;
    @Mock
    Logger loggerObject;
    @Mock
    private Game game;
    @Mock
    private GameBoardManager gameBoardManager;
    @Mock
    private WebSocketSession session;
    @Mock
    private CreateUserService player;
    @Mock
    private CreateUserService cheater;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loggerObject = mock(Logger.class);
        this.gameServiceObject = new GameService();
        this.gameServiceObject.setLogger(loggerObject);
        when(player.getUsername()).thenReturn("player1");
        when(cheater.getUsername()).thenReturn("player2");
        when(player.getSession()).thenReturn(session);
        when(cheater.getSession()).thenReturn(session);
    }

    @Test
    void testUpdateUser() {
        gameServiceObject.updateUser("", "");
        verify(loggerObject).info("GameService updateUser");
    }

    @Test
    void testSendInvalidCombination() {
        gameServiceObject.sendInvalidCombination(null);
        verify(loggerObject).info("GameService sendInvalidCombination");
    }

    @Test
    void testInformPlayersAboutEndOfGame() throws IOException {

        List<CreateUserService> winners = new ArrayList<>();
        winners.add(player);
        winners.add(cheater);
        EndType endType = EndType.ROCKETS_COMPLETED;

        gameServiceObject.informPlayersAboutEndOfGame(winners, endType);

        verify(loggerObject).info("GameService informPlayersAboutEndOfGame");
        verify(session, times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testInformPlayerAboutSystemerror() {
        gameServiceObject.informPlayerAboutSystemerror(null);
        verify(loggerObject).info("GameService informPlayerAboutSystemerror");
    }

    @Test
    void testInformPlayersCheat() {
        gameServiceObject.cheat(null, null);
    }
}
