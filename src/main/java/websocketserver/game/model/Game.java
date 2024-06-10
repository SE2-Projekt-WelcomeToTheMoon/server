package websocketserver.game.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import websocketserver.game.enums.ChosenCardCombination;
import websocketserver.game.enums.EndType;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.GameState;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.game.exceptions.GameStateException;
import websocketserver.services.CardManager;
import websocketserver.services.GameBoardManager;
import websocketserver.services.GameService;
import websocketserver.services.user.CreateUserService;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
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
    HashMap<CreateUserService, ChosenCardCombination> currentPlayerChoices;
    HashMap<CreateUserService, String> currentPlayerDraw;

    private final AtomicInteger clientResponseReceived = new AtomicInteger(0);
    private CompletableFuture<Void> allClientResponseReceivedFuture = new CompletableFuture<>();

    private final Logger logger = LogManager.getLogger(Game.class);

    public Game(CardManager cardManager, GameService gameService) {
        this.gameState = GameState.INITIAL;
        this.cardManager = cardManager;
        this.players = new ArrayList<>();
        this.currentPlayerChoices = new HashMap<>();
        this.gameService = gameService;
        this.gameBoardManager = new GameBoardManager();
        this.currentPlayerDraw = new HashMap<>();
    }

    public void startGame() {
        if (gameState != GameState.INITIAL) {
            throw new GameStateException("Game must be in state INITIAL to be started");
        }

        gameState = GameState.ROUND_ONE;
        gameService.informClientsAboutStart();
        logger.info("Now in state ROUND_ONE");

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

        currentPlayerDraw.clear();
        cardManager.drawNextCard();

        for (CreateUserService createUserService : players) {
            if (!createUserService.getGameBoard().checkCardCombination(cardManager.getCurrentCombination())) {
                if (createUserService.getGameBoard().addSystemError()) {
                    gameService.informPlayersAboutEndOfGame(null, EndType.SYSTEM_ERROR_EXCEEDED);
                } else {
                    gameService.informPlayerAboutSystemerror(createUserService);
                }
            }
        }

        sendNewCardCombinationToPlayer();

        gameState = GameState.ROUND_TWO;
        doRoundTwo();
        logger.info("Now in state ROUND_TWO");
    }

    private void sendNewCardCombinationToPlayer() {
        gameService.sendNewCardCombinationToPlayer();
    }

    protected void doRoundTwo() {
        if (gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Game must be in state ROUND_TWO");
        }

        gameService.informClientsAboutGameState();

        gameState = GameState.ROUND_THREE;
        doRoundThree();
        logger.info("Now in state ROUND_THREE");
    }

    protected void receiveSelectedCombinationOfPlayer(CreateUserService player, ChosenCardCombination chosenCardCombination) {
        if (this.gameState != GameState.ROUND_TWO) {
            throw new GameStateException("Invalid game state for selecting card combinations");
        }

        //TODO: Insert check if combination is valid
        if (player.getGameBoard().checkCardCombination(new CardCombination[]{cardManager.getCurrentCombination()[chosenCardCombination.ordinal()]})) {
            currentPlayerChoices.put(player, chosenCardCombination);
        } else {
            logger.info("Player {} combination was incorrect or invalid, removing from Current Draw", player.getUsername());
            currentPlayerDraw.remove(player);
            gameService.notifySingleClient("invalidCombination", player);
        }

        if (clientResponseReceived.incrementAndGet() == players.size()) {
            allClientResponseReceivedFuture.complete(null);
        }
    }

    protected void doRoundThree() {
        if (gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Game must be in state ROUND_THREE");
        }

        gameService.informClientsAboutGameState();

        //Logic for round three where player enter their number
        clientResponseReceived.set(0);
        allClientResponseReceivedFuture = new CompletableFuture<>();

        allClientResponseReceivedFuture.thenRun(() -> {
            synchronized (this) {
                gameState = GameState.ROUND_FOUR;
                doRoundFour();
                logger.info("Now in state ROUND_FOUR");
            }
        });
    }

    protected void receiveValueAtPositionOfPlayer(CreateUserService player, int floor, int field, FieldValue fieldValue) {
        if (this.gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Invalid game state for setting field values");
        }

        for (CreateUserService currentPlayer : players) {
            if (currentPlayer.equals(player)) {
                try {
                    currentPlayer.getGameBoard().setValueWithinFloorAtIndex(floor, field, fieldValue);
                    logger.info("Move was valid, rerouting move to other Players {}", player.getUsername());
                    gameService.notifySingleClient("alreadyMoved", currentPlayer);
                    for (CreateUserService otherPlayer : players) {
                        logger.info("Sending validMove from Player {} to {}", player.getUsername(), otherPlayer.getUsername());
                        gameBoardManager.updateClientGameBoardFromGame(otherPlayer, currentPlayerDraw.get(player));
                    }
                } catch (FloorSequenceException e) {
                    logger.info("Player {} move was incorrect or invalid, removing from Current Draw", player.getUsername());
                    currentPlayerDraw.remove(player);
                    gameService.notifySingleClient("invalidMove", player);
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

        gameService.informClientsAboutGameState();

        //Logic for round four where player optional do their action

        gameState = GameState.ROUND_FIVE;
        logger.info("Now in state ROUND_FIVE");
        doRoundFive();
    }

    protected void doRoundFive() {
        if (gameState != GameState.ROUND_FIVE) {
            throw new GameStateException("Game must be in state ROUND_FIVE");
        }

        gameService.informClientsAboutGameState();

        //Logic for round two where affects of player moves are calculated

        gameState = GameState.ROUND_SIX;
        doRoundSix();
        logger.info("Now in state ROUND_SIX");
    }

    protected void doRoundSix() {
        if (gameState != GameState.ROUND_SIX) {
            throw new GameStateException("Game must be in state ROUND_SIX");
        }

        gameService.informClientsAboutGameState();

        //Logic for round six where missions can be completed, and it will be
        //checked whether the game is finished or not.
        List<CreateUserService> winners = checkIfGameIsFinished();


        if (!winners.isEmpty()) {
            gameState = GameState.FINISHED;
            gameService.informPlayersAboutEndOfGame(winners, EndType.ROCKETS_COMPLETED);
        } else {
            gameState = GameState.ROUND_ONE;
            logger.info("RoundSIX finished, starting new round");
            doRoundOne();
        }
    }

    protected List<CreateUserService> checkIfGameIsFinished() {
        //Check if game is finished
        List<CreateUserService> winners = new ArrayList<>();

        for (CreateUserService createUserService : players) {
            if (createUserService.getGameBoard().hasWon()) {
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
     *
     * @param username
     * @param message
     */
    public void updateUser(String username, String message) {
        logger.info("Game makeMove for {}", username);

        if (currentPlayerDraw.containsKey(getUserByUsername(username))){
            logger.error("Player {} already made a move this Round", username);
            return;
        }

        FieldUpdateMessage fieldUpdateMessage = returnFieldUpdateMessage(message);
        if (fieldUpdateMessage == null) {
            return;
        }

        logger.info("Received Move from {}", username);
        currentPlayerDraw.put(getUserByUsername(username), message);

        //check if the choosen combination exists
        logger.info("Checking if the chosen combination exists");
        ChosenCardCombination chosenCardCombination = findOutCorrectCombination(fieldUpdateMessage.cardCombination());
        if (chosenCardCombination == null) {
            logger.error("Chosen combination does not exist");
        }
        //receiveSelectedCombinationOfPlayer(getUserByUsername(username), chosenCardCombination);

        // modify coords and check if we can set them (logically)
        logger.info("Checking if the chosen field is valid");
        int[] coords = getServerCoordinates(fieldUpdateMessage);
        receiveValueAtPositionOfPlayer(getUserByUsername(username), coords[0], coords[1], fieldUpdateMessage.fieldValue());
    }

    public ChosenCardCombination findOutCorrectCombination(CardCombination cardCombination) {
        CardCombination[] combinations = cardManager.getCurrentCombination();
        for (int i = 0; i < combinations.length; i++) {
            if (combinations[i].getCurrentSymbol() == cardCombination.getCurrentSymbol() &&
                    combinations[i].getCurrentNumber() == cardCombination.getCurrentNumber()) {
                if (i == 0) return ChosenCardCombination.ONE;
                if (i == 1) return ChosenCardCombination.TWO;
                if (i == 2) return ChosenCardCombination.THREE;
            }
        }
        return null;
    }

    public int[] getServerCoordinates(FieldUpdateMessage fieldUpdateMessage) {
        int floor = fieldUpdateMessage.floor();
        switch (floor) {
            case 0, 1, 4:
                return new int[]{fieldUpdateMessage.floor(), fieldUpdateMessage.field()};
            case 2, 3, 6, 7:
                return new int[]{fieldUpdateMessage.floor(), fieldUpdateMessage.field() + (2 * fieldUpdateMessage.chamber())};
            case 5:
                if (fieldUpdateMessage.chamber() == 0) {
                    return new int[]{fieldUpdateMessage.floor(), fieldUpdateMessage.field()};
                }
                if (fieldUpdateMessage.chamber() == 1) {
                    return new int[]{fieldUpdateMessage.floor(), fieldUpdateMessage.field() + 5};
                }
                return new int[]{fieldUpdateMessage.floor(), fieldUpdateMessage.field() + 7};
            default:
                return new int[]{0, 0};
        }
    }


    private FieldUpdateMessage returnFieldUpdateMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        FieldUpdateMessage fieldUpdateMessage;
        try {
            fieldUpdateMessage = mapper.readValue(message, FieldUpdateMessage.class);
        } catch (Exception e) {
            logger.error("JSON deserialization error", e);
            return null;
        }
        return fieldUpdateMessage;
    }
}
