package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameBoardManager {

    private final Lobby gamelobby;
    private final Logger logger = LogManager.getLogger(String.valueOf(GameBoardManager.class));

    public GameBoardManager(Lobby gameLobby) {
        this.gamelobby = gameLobby;
    }

    public String getGameBoardUser(String username) {
        if (!gamelobby.getUser(username)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        GameBoard gameBoard = gamelobby.getGameBoard(username);
        try {
            return mapper.writeValueAsString(gameBoard);
        } catch (Exception e) {
            logger.warn("badabing badabum geht nit", e);
        }
        return null;
    }
}