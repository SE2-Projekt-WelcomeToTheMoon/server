package WebsocketServer.game.model;

import WebsocketServer.game.services.CardController;
import WebsocketServer.game.services.GameBoardService;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
@Getter
public class Player {
    private final GameBoard gameBoard;
    UUID playerId;

    public Player(GameBoardService gameBoardService){
        gameBoard = gameBoardService.createGameBoard();
        playerId = UUID.randomUUID();
    }

    //TODO Add sending to Server
    public void sendCurrentCardCombination(CardCombination[] currentCombination) {
        //TODO: Implement Network Interface to access client and connect it.
        String messageString= CardController.getCurrentCardMessage(currentCombination);

    }
}
