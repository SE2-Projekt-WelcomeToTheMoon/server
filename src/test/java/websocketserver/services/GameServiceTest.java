package websocketserver.services;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.mockito.Mock;
import websocketserver.game.enums.GameState;
import websocketserver.game.enums.MissionType;
import websocketserver.game.model.MissionCard;
import websocketserver.services.user.CreateUserService;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.EndType;
import websocketserver.game.model.Game;
import websocketserver.game.model.GameBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.*;

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
    @Mock
    private CreateUserService user;

    @Mock
    private GameBoard gameBoard;

    private GameService gameService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockPlayer = mock(CreateUserService.class);
        gameService = new GameService();

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
        when(game.detectCheat(any(WebSocketSession.class), anyString(),anyString())).thenReturn(true);
        setGameInGameService(gameServiceObject, game);

        Field gameBoardManagerField = GameService.class.getDeclaredField("gameBoardManager");
        gameBoardManagerField.setAccessible(true);
        gameBoardManagerField.set(gameService, gameBoardManager);
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
    void testSendUserAndRocketCount() {
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

    @Test
    void testDetectCheat(){
        assertTrue(gameServiceObject.detectCheat(session, "name1", "name2"));
    }

    @Test
    void testAddRocketToPlayer(){
        gameServiceObject.addRocketToPlayer(player, 1);
        verify(loggerObject).info("GameService addRocket");
    }

    @Test
    void testInitializeMissionCards() {
        when(gameBoardManager.getGameBoard()).thenReturn(gameBoard);
        List<MissionCard> missionCards = Collections.singletonList(mock(MissionCard.class));
        when(gameBoard.initializeMissionCards()).thenReturn(missionCards);

        gameService.initializeMissionCards(session, "testUser");

        ArgumentCaptor<List> playersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> missionCardsCaptor = ArgumentCaptor.forClass(List.class);

        verify(gameBoardManager).notifyPlayersInitialMissionCards(playersCaptor.capture(), missionCardsCaptor.capture());

        assertEquals(1, playersCaptor.getValue().size());
        assertEquals(missionCards, missionCardsCaptor.getValue());
    }

    @Test
    void testFlipMissionCard() {
        when(gameBoardManager.getGameBoard()).thenReturn(gameBoard);
        MissionCard missionCard = new MissionCard(MissionType.A1, null);
        missionCard.flipCard();
        when(gameBoard.getMissionCards()).thenReturn(Collections.singletonList(missionCard));

        gameService.flipMissionCard(session, "testUser", "A1");

        ArgumentCaptor<List> playersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<MissionCard> missionCardCaptor = ArgumentCaptor.forClass(MissionCard.class);

        verify(gameBoardManager).notifyPlayersMissionFlipped(playersCaptor.capture(), missionCardCaptor.capture());

        assertEquals(1, playersCaptor.getValue().size());
        assertEquals(missionCard, missionCardCaptor.getValue());
    }

    @Test
    void testSendMissionCards() {
        when(gameBoardManager.getGameBoard()).thenReturn(gameBoard);
        List<MissionCard> missionCards = Collections.singletonList(mock(MissionCard.class));
        when(gameBoard.getMissionCards()).thenReturn(missionCards);

        gameService.sendMissionCards(session, "testUser");

        ArgumentCaptor<List> playersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> missionCardsCaptor = ArgumentCaptor.forClass(List.class);

        verify(gameBoardManager).notifyPlayersInitialMissionCards(playersCaptor.capture(), missionCardsCaptor.capture());

        assertEquals(1, playersCaptor.getValue().size());
        assertEquals(missionCards, missionCardsCaptor.getValue());
    }

    @Test
    void testNotifyPlayersInitialMissionCards() {
        List<CreateUserService> players = Collections.singletonList(user);
        List<MissionCard> missionCards = Collections.singletonList(mock(MissionCard.class));

        gameService.notifyPlayersInitialMissionCards(players, missionCards);

        ArgumentCaptor<List> playersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> missionCardsCaptor = ArgumentCaptor.forClass(List.class);

        verify(gameBoardManager).notifyPlayersInitialMissionCards(playersCaptor.capture(), missionCardsCaptor.capture());

        assertEquals(players, playersCaptor.getValue());
        assertEquals(missionCards, missionCardsCaptor.getValue());
    }

    @Test
    void testNotifyPlayersMissionFlipped() {
        List<CreateUserService> players = Collections.singletonList(user);
        MissionCard missionCard = mock(MissionCard.class);

        gameService.notifyPlayersMissionFlipped(players, missionCard);

        ArgumentCaptor<List> playersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<MissionCard> missionCardCaptor = ArgumentCaptor.forClass(MissionCard.class);

        verify(gameBoardManager).notifyPlayersMissionFlipped(playersCaptor.capture(), missionCardCaptor.capture());

        assertEquals(players, playersCaptor.getValue());
        assertEquals(missionCard, missionCardCaptor.getValue());
    }

}
