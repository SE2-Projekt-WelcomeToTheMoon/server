package websocketserver.services;

import org.json.JSONArray;
import websocketserver.game.enums.EndType;
import websocketserver.game.model.Game;
import websocketserver.game.model.MissionCard;
import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;

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

    private static Logger logger = LoggerFactory.getLogger(GameService.class);
    public List<CreateUserService> players;

    public GameService() {
        cardManager = new CardManager();
        game = new Game(cardManager, this);
        gameBoardManager = new GameBoardManager();
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

    public void informClientsAboutGameState() {
        logger.info("GameService informClientsAboutGameState");
        gameBoardManager.informClientsAboutGameState(game.getPlayers(), game.getGameState().toString());
    }


    public void notifyAllClients(String action) {
        logger.info("GameService notifyClients about {}", action);
        gameBoardManager.notifyAllClients(game.getPlayers(), action);
    }

    public void notifySingleClient(String action, CreateUserService player) {
        // this is crashing app lol
        logger.info("GameService notifyClients about {}", action);
        gameBoardManager.notifySingleClient(player, action);
    }

    public void informPlayersAboutEndOfGame(List<CreateUserService> winners, EndType endType) {
        logger.info("GameService informPlayersAboutEndOfGame");
        for(CreateUserService player: winners){
            JSONObject msg = GenerateJSONObjectService.generateJSONObject("endGame", player.getUsername(), true, "Game is finished", "");
            SendMessageService.sendSingleMessage(player.getSession(), msg);
        }
    }

    public void sendUserAndRocketCount(WebSocketSession session, JSONObject message) {
        logger.info("Case winnerScreen(sendUserAndRocketCount): {}{} ", session.getId(), message.toString());

        List<CreateUserService> players = game.getPlayers();
        logger.info("players im aktuellen Spiel: {}", players.size());

        JSONArray playersInfoArr = new JSONArray();
        for (CreateUserService player : players) {
            JSONObject playerinfo = new JSONObject();
            playerinfo.put("username", player.getUsername());
            playerinfo.put("points", player.getGameBoard().getRocketCount());
            playersInfoArr.put(playerinfo);
        }
        logger.info("playersInfoArr: {}", playersInfoArr);

        JSONObject response = new JSONObject();
        response.put("action", "winnerScreen");
        response.put("users", playersInfoArr);
        response.put("success", true);

        logger.info("response: {}", response);

        try {
            SendMessageService.sendSingleMessage(session, response);
            logger.info("Users in lobby sent: {} {}", session.getId(), playersInfoArr);
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
        }
    }

    public void informPlayerAboutSystemerror(CreateUserService createUserService) {
        logger.info("GameService informPlayerAboutSystemerror");
        //TODO: If new card combination and player can't find a spot
    }

    public void updateUser(String username, String message) {
        logger.info("GameService makeMove");
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
                logger.info("Message sent to player: {}", message);
            }
        } catch (Exception e) {
            logger.error("Failed to send message to player: {}", e.getMessage());
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
            logger.error(e.getMessage());
        }

        for (CreateUserService player : players) {
            SendMessageService.sendSingleMessage(player.getSession(), message);
        }
    }
    for (CreateUserService player : players) {
        SendMessageService.sendSingleMessage(player.getSession(), message);
    }
}

    public void cheat(WebSocketSession session, String username) {
        game.cheat(session, username);
        gameBoardManager.informClientsAboutCheat( game.getPlayers(), username);
    }

    public boolean detectCheat(WebSocketSession session, String username, String cheater) {
        boolean hasCheated = game.detectCheat(session, username, cheater);
        gameBoardManager.informClientsAboutDetectedCheat( game.getPlayers(), username, hasCheated);
        return hasCheated;
    }
}
