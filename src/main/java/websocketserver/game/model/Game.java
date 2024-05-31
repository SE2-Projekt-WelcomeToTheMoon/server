package websocketserver.game.model;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import websocketserver.game.enums.ChosenCardCombination;
import websocketserver.game.enums.EndType;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.GameState;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.game.exceptions.GameStateException;
import websocketserver.services.CardManager;
import websocketserver.game.services.CardController;
import websocketserver.services.GameBoardManager;
import websocketserver.services.GameService;
import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    CardManager cardManager;
    @Setter
    GameBoardManager gameBoardManager;
    CardController cardController;
    HashMap<CreateUserService, ChosenCardCombination> currentPlayerChoices;

    private final AtomicInteger clientResponseReceived = new AtomicInteger(0);
    private CompletableFuture<Void> allClientResponseReceivedFuture = new CompletableFuture<>();

    private final Logger logger = LogManager.getLogger(Game.class);

    public Game(CardManager cardManager, GameService gameService) {
        this.gameState = GameState.INITIAL;
        this.cardManager = cardManager;
        this.players = new ArrayList<>();
        currentPlayerChoices = new HashMap<>();
        this.gameService = gameService;
        this.gameBoardManager = new GameBoardManager();
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

        cardManager.drawNextCard();

        for(CreateUserService createUserService : players){
            if(!createUserService.getGameBoard().checkCardCombination(cardManager.getCurrentCombination())) {
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
        gameService.sendNewCardCombinationToPlayer();
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

    protected void receiveSelectedCombinationOfPlayer(CreateUserService player, ChosenCardCombination chosenCardCombination) {
        if (this.gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Invalid game state for selecting card combinations");
        }

        //TODO: Insert check if combination is valid
        if(player.getGameBoard().checkCardCombination(new CardCombination[]{cardManager.getCurrentCombination()[chosenCardCombination.ordinal()]})){
            currentPlayerChoices.put(player, chosenCardCombination);
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
    public CreateUserService getUserByUsername(String username) {
        for (CreateUserService player : players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    /**
     * when client sends update to server, and it gets approved, also reroutes the message to all other clients
     * @param username
     * @param message
     */
    public void updateUser(String username, String message) {
        logger.info("Game updateUser for {}", username);
        gameBoardManager.updateUser(getUserByUsername(username), message);
        for (CreateUserService player : players) {
            if (!player.getUsername().equals(username)) {
                logger.info("Rerouting GameBoard Update from {} to {}", username, player.getUsername());
                gameBoardManager.updateClientGameBoardFromGame(player, message);
            }
        }
    }

    public void cheat(WebSocketSession session, String username) {
        for(CreateUserService player : players){
            if(player.getUsername().equals(username)){
                player.getGameBoard().cheat();
                JSONObject response = GenerateJSONObjectService.generateJSONObject("cheat", username, true, "", "");
                try {
                    session.sendMessage(new TextMessage(response.toString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                logger.info("Cheated one rocket for {}", username);
            }
        }
    }
}
