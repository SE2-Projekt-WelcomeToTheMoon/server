package WebsocketServer.services;

import WebsocketServer.game.enums.EndType;
import WebsocketServer.game.model.Game;
import WebsocketServer.game.model.MissionCard;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.user.CreateUserService;

import org.json.JSONException;
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
    private final CardManager cardManager;
    boolean gameStarted = false;

    private static final String USERNAME_KEY = "username";
    private static Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private List<CreateUserService> players;

    public GameService() {
        cardManager = new CardManager();
        game = new Game(cardManager, this);
        gameBoardManager = new GameBoardManager();
        gameBoardManager = new GameBoardManager(null);
        this.players = players;
    }

    public void handleStartGame(Map<String, CreateUserService> players) {
        if (!gameStarted) {
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

    public void sendNewCardCombinationToPlayer() {
        cardManager.drawNextCard();
        if (!cardManager.sendCurrentCardsToPlayers(game.getPlayers())) logger.error("Error sending cards to players");
    }

    public void sendInvalidCombination(CreateUserService player) {
        logger.info("GameService sendInvalidCombination");
        //TODO: If Player sends invalid selection use this method, to return failure.
    }

    public void informPlayersAboutEndOfGame(List<CreateUserService> winners, EndType endType) {
        logger.info("GameService informPlayersAboutEndOfGame");
        //TODO: If Player has won, game will call this Method to send information to players.
    }

    public void informPlayerAboutSystemerror(CreateUserService createUserService) {
        logger.info("GameService informPlayerAboutSystemerror");
        //TODO: If new card combination and player can't find a spot
    }

    public void updateUser(String username, String message) {
        logger.info("GameService updateUser");
        game.updateUser(username, message);
    }

    // for testing purposes
    public void setLogger(Logger logger) {
        GameService.logger = logger;
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

    public void notifyPlayersMissionFlipped(MissionCard card) {
    JSONObject message = new JSONObject();
    try {
        message.put("action", "missionFlipped");
        message.put("missionDescription", card.getMissionDescription());
        message.put("newReward", card.getReward().getNumberRockets());
        message.put("flipped", true);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    for (CreateUserService player : players) {
        SendMessageService.sendSingleMessage(player.getSession(), message);
    }
}
}
