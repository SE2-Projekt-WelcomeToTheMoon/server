package WebsocketServer.game.model;

import WebsocketServer.game.services.GameBoardService;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Player {
    private final GameBoard gameBoard;

    public Player(GameBoardService gameBoardService){
        gameBoard = gameBoardService.createGameBoard();
    }
}
