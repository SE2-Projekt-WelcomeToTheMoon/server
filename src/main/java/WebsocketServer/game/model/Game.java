package WebsocketServer.game.model;

import WebsocketServer.game.enums.ChoosenCardCombination;
import WebsocketServer.game.enums.EndType;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.FloorSequenceException;
import WebsocketServer.game.exceptions.GameStateException;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.GameBoardManager;
import WebsocketServer.services.GameService;
import WebsocketServer.services.user.CreateUserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Component

public class Game {

    @Getter
    private GameState gameState;

    @Getter
    List<CreateUserService> players;
    GameService gameService;
    GameBoardManager gameBoardManager;
    CardController cardController;
    HashMap<CreateUserService, ChoosenCardCombination> currentPlayerChoices;

    private final AtomicInteger clientResponseReceived = new AtomicInteger(0);
    private CompletableFuture<Void> allClientResponseReceivedFuture = new CompletableFuture<>();


    public Game(CardController cardController, GameService gameService) {
        this.gameState = GameState.INITIAL;
        this.cardController = cardController;
        this.players = new ArrayList<>();
        currentPlayerChoices = new HashMap<>();
        this.gameService = gameService;
    }

    public void startGame() {
        if (gameState != GameState.INITIAL) {
            throw new GameStateException("Game must be in state INITIAL to be started");
        }

        gameState = GameState.ROUND_ONE;

        gameService.informClientsAboutStart();

        doRoundOne();
    }


    /**
     * Draw new card and check if players can find place for new value. If not add System error and check whether the
     * game is lost.
     */
    protected void doRoundOne() {
        if (gameState != GameState.ROUND_ONE) {
            throw new GameStateException("Game must be in state ROUND_ONE");
        }

        cardController.drawNextCard();

        for(CreateUserService createUserService : players){
            if(!createUserService.getGameBoard().checkCardCombination(cardController.getLastCardCombination())) {
                if(createUserService.getGameBoard().addSystemError()){
                    gameService.informPlayersAboutEndOfGame(null, EndType.SYSTEM_ERROR_EXCEEDED);
                }else{
                    gameService.informPlayerAboutSystemerror(createUserService);
                }
            }
        }

        sendNewCardCombinationToPlayer();

        gameState = GameState.ROUND_TWO;
        doRoundTwo();
    }

    private void sendNewCardCombinationToPlayer() {
        CardCombination[] currentCombination = cardController.getLastCardCombination();
        gameService.sendNewCardCombinationToPlayer(currentCombination);
    }

    protected void doRoundTwo() {
        if (gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Game must be in state ROUND_TWO");
        }

        //Logic for round two where player choose their combination
        clientResponseReceived.set(0);
        allClientResponseReceivedFuture = new CompletableFuture<>();

        allClientResponseReceivedFuture.thenRunAsync(() -> {
            gameState = GameState.ROUND_THREE;
            doRoundThree();
        });
    }

    protected void receiveSelectedCombinationOfPlayer(CreateUserService player, ChoosenCardCombination choosenCardCombination) {
        if (this.gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Invalid game state for selecting card combinations");
        }

        //TODO: Insert check if combination is valid
        if(player.getGameBoard().checkCardCombination(new CardCombination[]{cardController.getLastCardCombination()[choosenCardCombination.ordinal()]})){
            currentPlayerChoices.put(player, choosenCardCombination);
        }else {
            //TODO: Return failure to client as there is no spot for the chosen combination
        }

        if (clientResponseReceived.incrementAndGet() == players.size()) {
            allClientResponseReceivedFuture.complete(null);
        }
    }

    protected void doRoundThree() {
        if (gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Game must be in state ROUND_THREE");
        }

        //Logic for round three where player enter their number
        clientResponseReceived.set(0);
        allClientResponseReceivedFuture = new CompletableFuture<>();

        allClientResponseReceivedFuture.thenRunAsync(() -> {
            gameState = GameState.ROUND_FOUR;
            doRoundFour();
        });
    }

    protected void receiveValueAtPositionOfPlayer(CreateUserService player, int floor, int field, FieldValue fieldValue) {
        if (this.gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Invalid game state for setting field values");
        }

        for(CreateUserService currentPlayer : players){
            if(currentPlayer.equals(player)){
                try{
                    currentPlayer.getGameBoard().setValueWithinFloorAtIndex(floor, field, fieldValue);
                }catch (FloorSequenceException e){
                    gameService.sendInvalidCombination(player);
                }
            }
        }

        if (clientResponseReceived.incrementAndGet() == players.size()) {
            allClientResponseReceivedFuture.complete(null);
        }
    }

    protected void doRoundFour() {
        if (gameState != GameState.ROUND_FOUR) {
            throw new GameStateException("Game must be in state ROUND_FOUR");
        }

        //Logic for round four where player optional do their action

        for (CreateUserService player : players) {
            for (CreateUserService otherPlayer : players) {
                if (!player.equals(otherPlayer)) {
                    gameService.updateClientGameBoard(player, otherPlayer.getGameBoard());
                }
            }
        }

        gameState = GameState.ROUND_FIVE;
        doRoundFive();
    }

    protected void doRoundFive() {
        if (gameState != GameState.ROUND_FIVE) {
            throw new GameStateException("Game must be in state ROUND_FIVE");
        }

        //Logic for round two where affects of player moves are calculated

        gameState = GameState.ROUND_SIX;
        doRoundSix();
    }

    protected void doRoundSix() {
        if (gameState != GameState.ROUND_SIX) {
            throw new GameStateException("Game must be in state ROUND_SIX");
        }

        //Logic for round six where missions can be completed, and it will be
        //checked whether the game is finished or not.
        List<CreateUserService> winners = checkIfGameIsFinished();


        if (!winners.isEmpty()) {
            gameState = GameState.FINISHED;
            gameService.informPlayersAboutEndOfGame(winners, EndType.ROCKETS_COMPLETED);
        } else {
            gameState = GameState.ROUND_ONE;
            doRoundOne();
        }
    }

    protected List<CreateUserService> checkIfGameIsFinished() {
        //Check if game is finished
        List<CreateUserService> winners = new ArrayList<>();

        for(CreateUserService createUserService : players){
            if(createUserService.getGameBoard().hasWon()){
                winners.add(createUserService);
            }
        }

        return winners;
    }

    public void addPlayers(Map<String, CreateUserService> players) {
        this.players.addAll(players.values());
    }

    public void addPlayer(CreateUserService player) {
        players.add(player);
    }

}
