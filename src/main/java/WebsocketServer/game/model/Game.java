package WebsocketServer.game.model;

import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.GameStateException;
import WebsocketServer.game.services.CardController;
import lombok.Getter;

import java.util.List;

@Getter
public class Game {

    private GameState gameState;

    List<Player> playerList;
    CardController cardController;

    public Game(CardController cardController) {
        this.gameState = GameState.INITIAL;
        this.cardController = cardController;
    }

    public void startGame() {
        if (gameState != GameState.INITIAL) {
            throw new GameStateException("Game must be in state INITIAL to be started");
        }

        gameState = GameState.ROUND_ONE;
        doRoundOne();
    }

    private void doRoundOne() {
        if (gameState != GameState.ROUND_ONE) {
            throw new GameStateException("Game must be in state ROUND_ONE");
        }

        cardController.drawNextCard();

        gameState = GameState.ROUND_TWO;
        doRoundTwo();
    }

    private void doRoundTwo() {
        if (gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Game must be in state ROUND_TWO");
        }

        //Logic for round two where player choose their combination

        gameState = GameState.ROUND_THREE;
        doRoundThree();
    }

    private void doRoundThree() {
        if (gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Game must be in state ROUND_THREE");
        }

        //Logic for round three where player enter their number

        gameState = GameState.ROUND_FOUR;
        doRoundFour();
    }

    private void doRoundFour() {
        if (gameState != GameState.ROUND_FOUR) {
            throw new GameStateException("Game must be in state ROUND_FOUR");
        }

        //Logic for round four where player optional do their action

        gameState = GameState.ROUND_FIVE;
        doRoundFive();
    }

    private void doRoundFive() {
        if (gameState != GameState.ROUND_FIVE) {
            throw new GameStateException("Game must be in state ROUND_FIVE");
        }

        //Logic for round two where affects of player moves are calculated

        gameState = GameState.ROUND_SIX;
        doRoundSix();
    }

    private void doRoundSix() {
        if (gameState != GameState.ROUND_FIVE) {
            throw new GameStateException("Game must be in state ROUND_FIVE");
        }

        //Logic for round six where missions can be completed, and it will be
        //checked whether the game is finished or not.

        if(checkIfGameIsFinished()){
            gameState = GameState.FINISHED;
        }else {
            gameState = GameState.ROUND_ONE;
            doRoundOne();
        }
    }

    private boolean checkIfGameIsFinished() {
        //Check if game is finished

        return true;
    }
}
