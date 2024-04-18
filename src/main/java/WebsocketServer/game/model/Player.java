package WebsocketServer.game.model;

import WebsocketServer.game.services.GameBoardService;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Getter
public class Player {
    private final GameBoard gameBoard;

    public Player(GameBoardService gameBoardService){
        gameBoard = gameBoardService.createGameBoard();
    }

    public void sendCurrentCardCombination(CardCombination[] currentCombination) {
        //TODO: Implement Network Interface to access client and connect it.
    }
}
