package WebsocketServer.services;

import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.model.Game;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.user.CreateUserService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    public final Game game;
    public final GameBoardManager gameBoardManager;
    CardController cardController;
    boolean gameStarted = false;

    private static final String USERNAME_KEY = "username";
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private List<CreateUserService> players;

    public GameService() {
        cardController = new CardController();
        game = new Game(cardController, this);
        gameBoardManager = new GameBoardManager(null);
        this.players = players;
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

    public void sendNewCardCombinationToPlayer(CardCombination[] currentCombination) {
    }

    public void notifyAllPlayers(String message) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("type", "notification");
        jsonMessage.put("message", message);

        players.forEach(player -> sendMessageToPlayer(player.getSession(), jsonMessage));
    }

    private void sendMessageToPlayer(WebSocketSession session, JSONObject message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message.toString()));
                System.out.println("Message sent to player: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send message to player: " + e.getMessage());
        }
    }
}
