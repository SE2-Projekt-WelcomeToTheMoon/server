package websocketserver.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;
import websocketserver.services.user.CreateUserService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.EndType;
import websocketserver.game.model.Game;
import websocketserver.game.model.GameBoard;
import websocketserver.services.user.CreateUserService;

import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

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
        loggerObject = mock(Logger.class);
        this.gameServiceObject = new GameService();
        this.gameServiceObject.setLogger(loggerObject);
        when(player.getUsername()).thenReturn("player1");
        when(cheater.getUsername()).thenReturn("player2");
        when(player.getSession()).thenReturn(session);
        when(cheater.getSession()).thenReturn(session);
        when(player.getGameBoard()).thenReturn(mock(GameBoard.class));
        when(cheater.getGameBoard()).thenReturn(mock(GameBoard.class));

        when(player.getGameBoard().getRocketCount()).thenReturn(5);
        when(cheater.getGameBoard().getRocketCount()).thenReturn(3);

        List<CreateUserService> players = new ArrayList<>();
        players.add(player);
        players.add(cheater);

        when(game.getPlayers()).thenReturn(players);
        setGameInGameService(gameServiceObject, game);
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


    @Test
    void testSendUserAndRocketCount() throws Exception {

        org.json.JSONObject message = new org.json.JSONObject();
        message.put("action", "testAction");

        gameServiceObject.sendUserAndRocketCount(session, message);

        verify(loggerObject).info("Case winnerScreen(sendUserAndRocketCount): {}{} ", session.getId(), message.toString());
        verify(loggerObject).info("players im aktuellen Spiel: {}", 2);
        verify(session).sendMessage(any(TextMessage.class));
    }

    private void setGameInGameService(GameService gameService, Game game) {
        try {
            java.lang.reflect.Field gameField = GameService.class.getDeclaredField("game");
            gameField.setAccessible(true);
            gameField.set(gameService, game);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
