package websocketserver.game.model;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import websocketserver.game.enums.ChosenCardCombination;
import websocketserver.game.enums.EndType;
import websocketserver.game.enums.GameState;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.game.exceptions.GameStateException;
import websocketserver.game.util.FieldUpdateMessage;
import websocketserver.services.CardManager;
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
    private int clientCantMakeResponse = 0;
    private CompletableFuture<Void> allClientResponseReceivedFuture = new CompletableFuture<>();

    @Setter
    private Logger logger = LogManager.getLogger(Game.class);

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

        clientCantMakeResponse = 0;

        for (CreateUserService createUserService : players) {
            if (!createUserService.getGameBoard().checkCardCombination(cardManager.getCurrentCombination())) {
                if (createUserService.getGameBoard().addSystemError()) {
                    gameService.informPlayersAboutEndOfGame(new ArrayList<>(), EndType.SYSTEM_ERROR_EXCEEDED);
                } else {
                    // if a player cant make a move we have to ensure that we don't get stuck on round 3
                    gameService.informPlayerAboutSystemerror(createUserService);
                    clientCantMakeResponse++;
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

    protected void doRoundThree() {
        if (gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Game must be in state ROUND_THREE");
        }

        gameService.informClientsAboutGameState();

        //Logic for round three where player enter their number
        clientResponseReceived.set(0);
        allClientResponseReceivedFuture = new CompletableFuture<>();

        // increments the future for every user that can't make a move this round, so we can continue
        for (int i = 0; i < clientCantMakeResponse;i++){
            clientResponseReceived.incrementAndGet();
        }

        allClientResponseReceivedFuture.thenRun(() -> {
            synchronized (this) {
                gameState = GameState.ROUND_FOUR;
                doRoundFour();
                logger.info("Now in state ROUND_FOUR");
            }
        });
    }

    protected void receiveValueAtPositionOfPlayer(CreateUserService player, int floor, int field, CardCombination combination) {
        if (this.gameState != GameState.ROUND_THREE) {
            throw new GameStateException("Invalid game state for setting field values");
        }

        for (CreateUserService currentPlayer : players) {
            if (currentPlayer.equals(player)) {
                try {
                    if(currentPlayer.getGameBoard().getFloorAtIndex(floor).isValidMove(combination,field)){
                        currentPlayer.getGameBoard().setFieldWithinFloor(floor, field, combination);
                        logger.info("Move was valid, rerouting move to other Players {}", player.getUsername());
                        gameService.notifySingleClient("validMove", currentPlayer);
                        // if move was valid, check if the chamber is complete
                        checkChamberCompletion(currentPlayer, floor);
                        for (CreateUserService otherPlayer : players) {
                            logger.info("Sending validMove from Player {} to {}", player.getUsername(), otherPlayer.getUsername());
                            gameBoardManager.updateClientGameBoardFromGame(otherPlayer, currentPlayerDraw.get(player));
                        }
                    }else{
                        logger.error("Player {} move was invalid, removing from currentDraw", player.getUsername());
                        currentPlayerDraw.remove(player);
                        gameService.notifySingleClient("invalidMove", player);
                        return;
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

    private void checkChamberCompletion(CreateUserService player, int floor){
        for (Chamber chamber : player.getGameBoard().getFloorAtIndex(floor).getChambers()) {
            if (chamber.checkChamberCompletion(0)) {
                logger.info("Chamber completed, handling Rewards for {}", player.getUsername());
                if(chamber.isRewardsDone()){
                    logger.info("Rewards already handled");
                    continue;
                }
                handleRewards(chamber.getRewards(), player);
                // sets it to true, so that the rewards are only handled once
                chamber.setRewardsDone(true);
            }
        }
    }

    private void handleRewards(List<Reward> rewards, CreateUserService player){
        for (Reward reward : rewards) {
            switch(reward.getCategory()){
                case ROCKET -> {
                    player.getGameBoard().addRockets(reward.getNumberRockets());
                    gameService.addRocketToPlayer(player, reward.getNumberRockets());
                }
                case SYSTEMERROR -> {
                    for (CreateUserService currentPlayer : players) {
                        if (!currentPlayer.equals(player)) {
                            currentPlayer.getGameBoard().addSystemError();
                            gameService.informPlayerAboutSystemerror(currentPlayer);
                        }
                    }
                }
                default -> logger.error("Reward Category not found");
            }
        }
    }

    protected void doRoundFour() {
        if (gameState != GameState.ROUND_FOUR) {
            throw new GameStateException("Game must be in state ROUND_FOUR");
        }

        //Logic for round four where player optional do their action

        gameState = GameState.ROUND_FIVE;
        logger.info("Now in state ROUND_FIVE");
        doRoundFive();
    }

    protected void doRoundFive() {
        if (gameState != GameState.ROUND_FIVE) {
            throw new GameStateException("Game must be in state ROUND_FIVE");
        }

        //Logic for round two where affects of player moves are calculated

        gameState = GameState.ROUND_SIX;
        doRoundSix();
        logger.info("Now in state ROUND_SIX");
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
            gameService.informPlayersAboutEndOfGame(this.players, EndType.ROCKETS_COMPLETED);
        } else {
            gameState = GameState.ROUND_ONE;
            logger.info("Round SIX finished, starting new round");
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
     */
    public void updateUser(String username, String message) {
        logger.info("Game makeMove for {}", username);

        if (currentPlayerDraw.containsKey(getUserByUsername(username))) {
            logger.error("Player {} already made a move this Round", username);
            gameService.notifySingleClient("alreadyMoved", getUserByUsername(username));
            return;
        }

        FieldUpdateMessage fieldUpdateMessage = returnFieldUpdateMessage(message);
        if (fieldUpdateMessage == null) {
            logger.error("FieldUpdateMessage is null");
            return;
        }

        logger.info("Received Move from {}", username);
        currentPlayerDraw.put(getUserByUsername(username), message);

        if (findCorrectCombination(fieldUpdateMessage.cardCombination()) == null){
            logger.error("CardCombination is not valid");
            return;
        }

        // modify coords and check if we can set them (logically)
        logger.info("Checking if the chosen field is valid");
        int[] coords = getServerCoordinates(fieldUpdateMessage);

        receiveValueAtPositionOfPlayer(getUserByUsername(username), coords[0], coords[1], fieldUpdateMessage.cardCombination());
    }

    public ChosenCardCombination findCorrectCombination(CardCombination cardCombination) {
        CardCombination[] combinations = cardManager.getCurrentCombination();
        for (int i = 0; i < combinations.length; i++) {
            if (combinations[i].getCurrentSymbol().equals(cardCombination.getCurrentSymbol()) &&
                    combinations[i].getCurrentNumber() == cardCombination.getCurrentNumber() &&
                    combinations[i].getNextSymbol().equals(cardCombination.getNextSymbol())) {

                return switch (i) {
                    case 0 -> ChosenCardCombination.ONE;
                    case 1 -> ChosenCardCombination.TWO;
                    case 2 -> ChosenCardCombination.THREE;
                    default -> null;
                };
            }
        }
        return null;
    }

    /**
     * Converts Client Coordinates to Server Coordinates
     * Client uses index floor, chamber, field
     * Server uses index floor, field meaning just indexing on the whole floor
     */
    public int[] getServerCoordinates(FieldUpdateMessage fieldUpdateMessage) {
        int floor = fieldUpdateMessage.floor();
        int field = fieldUpdateMessage.field();
        int chamber = fieldUpdateMessage.chamber();

        return switch (floor) {
            case 0, 1, 4, 5 -> new int[]{floor, field};
            case 2, 3, 7, 8 -> new int[]{floor, field + (2 * chamber)};
            case 6 -> {
                int fieldOffset = switch (chamber) {
                    case 0 -> 0;
                    case 1 -> 5;
                    default -> 7;
                };
                yield new int[]{floor, field + fieldOffset};
            }
            default -> new int[]{0, 0};
        };
    }


    private FieldUpdateMessage returnFieldUpdateMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        FieldUpdateMessage fieldUpdateMessage;
        try {
            mapper.findAndRegisterModules();
            fieldUpdateMessage = mapper.readValue(message, FieldUpdateMessage.class);
        } catch (Exception e) {
            logger.error("JSON deserialization error", e);
            return null;
        }
        return fieldUpdateMessage;
    }


    public void cheat(WebSocketSession session, String username) {
        for(CreateUserService player : players){
            if(player.getUsername().equals(username)){
                player.getGameBoard().cheat();
                JSONObject response = new GenerateJSONObjectService("cheat", username, true, "", "").generateJSONObject();
                try {
                    session.sendMessage(new TextMessage(response.toString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                logger.info("Cheated one rocket for {}", username);
            }
        }
    }

    public boolean detectCheat(WebSocketSession session, String username, String cheater) {
        CreateUserService detector = null;
        CreateUserService suspect = null;
        for(CreateUserService player : players){
            if(player.getUsername().equals(username)){
                detector = player;
            }else if(player.getUsername().equals(cheater)){
                suspect = player;
            }
        }

        JSONObject response = new GenerateJSONObjectService("detectCheat", username, true, "", "").generateJSONObject();
        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert suspect != null;
        assert detector != null;

        if(suspect.getGameBoard().isHasCheated()){
            logger.info("Has cheated  {}", cheater);
            detector.getGameBoard().addRockets(1);
            return true;
        }else {
            logger.info("Has not cheated  {}", cheater);
            detector.getGameBoard().addRockets(-1);
            return false;
        }
    }
}
