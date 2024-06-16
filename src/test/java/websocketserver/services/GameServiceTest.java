package websocketserver.services;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;
import websocketserver.game.enums.GameState;
import websocketserver.services.user.CreateUserService;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.EndType;
import websocketserver.game.model.Game;
import websocketserver.game.model.GameBoard;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Mock
    CreateUserService mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockPlayer = mock(CreateUserService.class);

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
        when(game.getGameState()).thenReturn(GameState.INITIAL);
        setGameInGameService(gameServiceObject, game);
    }

    @Test
    void testMakeMove() {
        gameServiceObject.updateUser("", "");
        verify(loggerObject).info("GameService makeMove");
    }

    @Test
    void testInformPlayersAboutEndOfGame() {

        List<CreateUserService> winners = new ArrayList<>();
        winners.add(player);
        winners.add(cheater);
        EndType endType = EndType.ROCKETS_COMPLETED;

        gameServiceObject.informPlayersAboutEndOfGame(winners, endType);

        verify(loggerObject).info("GameService informPlayersAboutEndOfGame");
    }

    @Test
    void testInformPlayerAboutSystemerror() {
        when(player.getSession()).thenReturn(session);
        when(player.getGameBoard()).thenReturn(mock(GameBoard.class));
        gameServiceObject.informPlayerAboutSystemerror(player);
        verify(loggerObject).info("GameService informPlayerAboutSystemerror");
    }

    @Test
    void testInformPlayersCheat() {
        gameServiceObject.cheat(null, null);
        verify(loggerObject).info("GameService cheat");
    }

    @Test
    void testSendUserAndRocketCount() throws Exception {
        JSONObject message = new JSONObject();
        message.put("action", "testAction");

        gameServiceObject.sendUserAndRocketCount(session, message);

        verify(loggerObject).info("Case winnerScreen(sendUserAndRocketCount): {}{} ", session.getId(), message);
        verify(loggerObject).info("players im aktuellen Spiel: {}", 2);
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

    @Test
    void testHandleStartGame() {
        Map<String, CreateUserService> players = new HashMap<>();
        players.put("player1", player);

        gameServiceObject.handleStartGame(players);
        verify(loggerObject).info("GameService fügt player hinzu");
    }

    @Test
    void testInformClientsAboutStart() {
        gameServiceObject.informClientsAboutStart();
        verify(loggerObject).info("Player werden über game start informiert");
    }

    @Test
    void testSendNewCardCombinationToPlayer() {
        gameServiceObject.sendNewCardCombinationToPlayer();
        verify(loggerObject).info("GameService sendNewCardCombinationToPlayer");
    }

    @Test
    void testNotifyAllClients() {
        gameServiceObject.notifyAllClients("testAction");
        verify(loggerObject).info("GameService notifyClients about {}", "testAction");
    }

    @Test
    void testNotifySingleClient() {
        gameServiceObject.notifySingleClient("testAction", player);
        verify(loggerObject).info("GameService notifyClients about {}", "testAction");
    }

    @Test
    void testInformClientsAboutGameState() {
        gameServiceObject.informClientsAboutGameState();
        verify(loggerObject).info("GameService informClientsAboutGameState");
    }
    @Test
    void testUpdateUserAboutCurrentCards() {
        gameServiceObject.updateCurrentCards(player.getUsername());
        verify(loggerObject).info("GameService updateCurrentCards");
    }
}
