package WebsocketServer.game.model;

import WebsocketServer.game.enums.ChoosenCardCombination;
import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.GameState;
import WebsocketServer.game.exceptions.GameStateException;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.GameService;
import WebsocketServer.services.user.CreateUserService;
import lombok.Getter;

import org.json.JSONObject;
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
    CardController cardController;
    HashMap<CreateUserService, ChoosenCardCombination> currentPlayerChoices;

    private final AtomicInteger clientResponseReceived = new AtomicInteger(0);
    private CompletableFuture<Void> allClientResponseReceivedFuture = new CompletableFuture<>();
    private GameBoard gameBoard;


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

    protected void doRoundOne() {
        if (gameState != GameState.ROUND_ONE) {
            throw new GameStateException("Game must be in state ROUND_ONE");
        }

        cardController.drawNextCard();

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

        currentPlayerChoices.put(player, choosenCardCombination);

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
                currentPlayer.getGameBoard().setValueWithinFloorAtIndex(floor, field, fieldValue);
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

        if (checkIfGameIsFinished()) {
            gameState = GameState.FINISHED;
        } else {
            gameState = GameState.ROUND_ONE;
            doRoundOne();
        }
    }

    protected boolean checkIfGameIsFinished() {
        //Check if game is finished

        return true;
    }

    public void addPlayers(Map<String, CreateUserService> players) {
        this.players.addAll(players.values());
    }

    public void addPlayer(CreateUserService player) {
        players.add(player);
    }

    public void checkMissions() {
        if (checkAllRobotsTasksCompleted()) {
            gameBoard.checkAndFlipMissionCards("Complete all robot tasks");
            gameService.notifyAllPlayers("Mission 'Complete all robot tasks' completed and card flipped.");
        }
    }

    private boolean checkAllRobotsTasksCompleted() {
        return gameBoard.getFloors().stream()
            .filter(floor -> floor.getFieldCategory() == FieldCategory.ROBOTER)
            .allMatch(floor -> floor.getChambers().stream()
                .allMatch(chamber -> chamber.getFields().stream()
                    .allMatch(field -> field.getFieldValue().getValue() >= 1)));
    }
}
