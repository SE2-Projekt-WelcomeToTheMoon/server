package WebsocketServer.services;

import WebsocketServer.game.model.Game;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.user.CreateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameService {

    public final Game game;
    public final GameBoardManager gameBoardManager;
    CardController cardController;
    boolean gameStarted = false;

    private static final String USERNAME_KEY = "username";
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    public GameService() {
        cardController = new CardController();
        game = new Game(cardController, this);
        gameBoardManager = new GameBoardManager(null);
    }

    public void handleStartGame(Map<String, CreateUserService> players) {
        if(!gameStarted){
            logger.info("GameService fügt player hinzu");
            players.values().forEach(CreateUserService::createGameBoard);
            game.addPlayers(players);
            game.startGame();

            gameStarted = true;
        }
    }


    public void informClientsAboutStart() {
        logger.info("Player werden über game start informiert");
        gameBoardManager.informClientsAboutStart(game.getPlayers());
    }
}
