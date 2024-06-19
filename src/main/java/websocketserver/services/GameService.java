package websocketserver.services;

import lombok.Setter;
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
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    public final Game game;
    public final GameBoardManager gameBoardManager;
    private final CardManager cardManager;
    boolean gameStarted = false;

    @Setter
    private Logger logger = LoggerFactory.getLogger(GameService.class);
    private List<CreateUserService> players;

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
        logger.info("GameService sendNewCardCombinationToPlayer");
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
        logger.info("GameService notifyClients about {}", action);
        gameBoardManager.notifySingleClient(player, action);
    }

    public void informPlayersAboutEndOfGame(List<CreateUserService> winners, EndType endType) {
        logger.info("GameService informPlayersAboutEndOfGame");
        for (CreateUserService player : winners) {
            JSONObject msg = new GenerateJSONObjectService("endGame", player.getUsername(), true, "Game is finished"+ endType.toString(), "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), msg);
        }
    }

    public void sendUserAndRocketCount(WebSocketSession session, JSONObject message) {
        logger.info("Case winnerScreen(sendUserAndRocketCount): {}{} ", session.getId(), message);

        List<CreateUserService> gamePlayers = game.getPlayers();
        logger.info("players im aktuellen Spiel: {}", gamePlayers.size());

        JSONArray playersInfoArr = new JSONArray();
        for (CreateUserService player : gamePlayers) {
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
        SendMessageService.sendSingleMessage(session, response);
        logger.info("Users in lobby sent: {} {}", session.getId(), playersInfoArr);
    }

    public void informPlayerAboutSystemerror(CreateUserService createUserService) {
        logger.info("GameService informPlayerAboutSystemerror");

        int errors = createUserService.getGameBoard().getSystemErrors();
        JSONObject msg = new GenerateJSONObjectService("systemError", createUserService.getUsername(), true, "system error informplayer", "").generateJSONObject();
        msg.put("points", errors);
        SendMessageService.sendSingleMessage(createUserService.getSession(), msg);
        logger.info("Systemerror sent to player: {}", msg);
    }

    public void updateUser(String username, String message) {
        logger.info("GameService makeMove");
        game.updateUser(username, message);
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

    public void cheat(WebSocketSession session, String username) {
        logger.info("GameService cheat");
        game.cheat(session, username);
        gameBoardManager.informClientsAboutCheat(game.getPlayers(), username);
    }

    public boolean detectCheat(WebSocketSession session, String username, String cheater) {
        logger.info("GameService detectCheat");
        boolean hasCheated = game.detectCheat(session, username, cheater);
        gameBoardManager.informClientsAboutDetectedCheat(game.getPlayers(), username, hasCheated);
        return hasCheated;
    }

    public void updateCurrentCards(String username) {
        logger.info("GameService updateCurrentCards");
        cardManager.updateUserAboutCurrentCards(game.getUserByUsername(username));
    }

    public void addRocketToPlayer(CreateUserService player, int rocketCount) {
        logger.info("GameService addRocket");
        gameBoardManager.addRocketToPlayer(player, rocketCount);
    }
}
