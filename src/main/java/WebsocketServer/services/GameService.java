package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.Game;
import WebsocketServer.game.services.CardController;
import WebsocketServer.game.services.GameBoardService;
import WebsocketServer.services.user.CreateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GameService {

    public final Game game;
    public final GameBoardManager gameBoardManager;
    CardController cardController;
    boolean gameStarted = false;

    private static final String USERNAME_KEY = "username";
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    public GameService() {
        cardController = new CardController();
        game = new Game(cardController);
        gameBoardManager = new GameBoardManager(null);
    }

    public void handleStartGame(Map<String, CreateUserService> players) {
        if(!gameStarted){
            logger.info("GameService fügt player hinzu");
            players.values().forEach(CreateUserService::createGameBoard);
            game.addPlayers(players);
            gameStarted = true;
        }
    }


}
