package websocketserver.game.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.MissionType;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.services.GameService;
import websocketserver.services.user.CreateUserService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameBoardTest {

    @Mock
    private GameService gameService;

    @Mock
    private MissionCard missionCard;

    private List<MissionCard> missionCards;

    private GameBoard gameBoard;
    private Floor floor;

    private List<Reward> rewards;

    @BeforeEach
    void setUp() {
        rewards = List.of(new Reward[]{new Reward(RewardCategory.SYSTEMERROR), new Reward(RewardCategory.ROCKET, 5)});
        MockitoAnnotations.openMocks(this);
        gameBoard = spy(new GameBoard());
        floor = new Floor(FieldCategory.ROBOTER);
        missionCards = new ArrayList<>();
        gameBoard.setMissionCards(missionCards);
        gameBoard.setGameService(gameService);
    }

    @Test
    void testAddFloor() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        assertEquals(1, gameBoard.getSize());
        assertDoesNotThrow(() -> gameBoard.getFloorAtIndex(0));
        assertEquals(floor, gameBoard.getFloorAtIndex(0));
    }

    @Test
    void testGetFloorNotPresent() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gameBoard.finalizeGameBoard();
            gameBoard.getFloorAtIndex(0);
        });
    }

    @Test
    void testHasWonWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.hasWon());
    }

    @Test
    void testGetBarometerPointsWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.getRocketBarometerPoints());
    }

    @Test
    void testAddRocketsWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.addRockets(5));
    }

    @Test
    void testGetRocketCountWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.getRocketCount());
    }

    @Test
    void testHasWon() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        assertFalse(gameBoard.hasWon());
    }

    @Test
    void testAddRocket() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        gameBoard.addRockets(5);
        assertEquals(5, gameBoard.getRocketCount());
    }

    @Test
    void testGetBarometerPoints() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        gameBoard.addRockets(5);
        assertEquals(15, gameBoard.getRocketBarometerPoints());
    }

    @Test
    void testAddSystemErrorsNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.addSystemError());
    }

    @Test
    void testAddSystemErrors() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        assertEquals(8, gameBoard.getRemainingErrors());
        assertDoesNotThrow(() -> gameBoard.addSystemError());
        assertEquals(7, gameBoard.getRemainingErrors());

        for (int i = 0; i < 6; i++) {
            assertFalse(gameBoard.addSystemError());
            assertEquals(6 - i, gameBoard.getRemainingErrors());
        }

        assertTrue(gameBoard.addSystemError());
        assertEquals(0, gameBoard.getRemainingErrors());
    }

    @Test
    void testHasLost() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        assertFalse(gameBoard.hasLost());
    }

    @Test
    void testGetRemainingSystemErrorsNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.getRemainingErrors());
    }

    @Test
    void testHasLostNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.hasLost());
    }

    @Test
    void testSetAndGetFieldValueWithinFloorAtIndex() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        Field field = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER);
        chamber.addField(field);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertDoesNotThrow(() -> gameBoard.setValueWithinFloorAtIndex(0, 1, new CardCombination(floor.getFieldCategory(), floor.getFieldCategory(), FieldValue.TWO)));
        assertEquals(FieldValue.TWO, gameBoard.getFloorAtIndex(0).getFieldAtIndex(1).getFieldValue());
    }

    @Test
    void testSetInvalidValueWithinFloorAtIndex() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        Field field1 = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER, FieldValue.THREE);
        chamber.addField(field1);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertThrows(FloorSequenceException.class, () ->
                gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(floor.getFieldCategory(), floor.getFieldCategory(), FieldValue.FOUR)));
    }

    @Test
    void testGetFloorAtNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gameBoard.finalizeGameBoard();
            gameBoard.getFloorAtIndex(-1);
        });
    }

    @Test
    void testIsFinalizedInitiallyFalse() {
        assertFalse(gameBoard.isFinalized());
    }

    @Test
    void testFinalizeGameBoardChangesIsFinalizedToTrue() {
        gameBoard.finalizeGameBoard();
        assertTrue(gameBoard.isFinalized());
    }

    @Test
    void testAddFloorAfterGameBoardFinalizationThrowsException() {
        gameBoard.finalizeGameBoard();
        assertThrows(FinalizedException.class, () -> gameBoard.addFloor(floor));
    }

    @Test
    void testGetFloorAtIndexBeforeGameBoardFinalizationThrowsException() {
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.getFloorAtIndex(0));
    }

    @Test
    void testFinalizeGameBoardWithAlreadyFinalizedFloorThrowsException() {
        floor.finalizeFloor();
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.finalizeGameBoard());
    }

    @Test
    void testFinalizeGameBoardTwiceThrowsException() {
        gameBoard.finalizeGameBoard();
        assertThrows(FinalizedException.class, gameBoard::finalizeGameBoard);
    }

    @Test
    void testSetValueWithinFloorAtIndexBeforeGameBoardFinalizationThrowsException() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(floor.getFieldCategory(), floor.getFieldCategory(), FieldValue.ONE)));
    }

    @Test
    void testInitializeMissionCards() {
        List<MissionCard> cards = gameBoard.initializeMissionCards();

        assertEquals(3, cards.size(), "Should initialize exactly three mission cards");
        assertTrue(cards.stream().allMatch(card -> card.getMissionType().name().matches("A[12]|B[12]|C[12]")), "Mission types should be correctly set");
        assertTrue(cards.stream().allMatch(card -> Arrays.asList(2, 3).contains(card.getReward().getNumberRockets())), "Rewards should be correctly set between 2 and 3 rockets");
    }

    @Test
    void testCheckAndFlipMissionCards() {
        MissionCard card1 = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        MissionCard card2 = new MissionCard(MissionType.A2, new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(Arrays.asList(card1, card2));
        gameBoard.setGameService(gameService);

        gameBoard.checkAndFlipMissionCards(MissionType.A1);
        assertTrue(card1.isFlipped(), "Mission A1 should be flipped");
        assertFalse(card2.isFlipped(), "Mission A2 should not be flipped");
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(card1));
    }

    @Test
    void testAreAllFieldsNumberedAllNumbered() {
        Floor floor1 = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);

        assertTrue(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER),
                "Should return true as all fields in the specified category are numbered.");
    }

    @Test
    void testAreAllFieldsNumberedSomeUnnumbered() {
        Floor floor1 = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);
        gameBoard.finalizeGameBoard();

        assertFalse(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER),
                "Should return false as not all fields in the specified category are numbered.");
    }

    @Test
    void testAreAllFieldsNumberedNoRelevantFloors() {
        Floor floor1 = new Floor(FieldCategory.PLANUNG);
        Chamber chamber1 = new Chamber(FieldCategory.PLANUNG, rewards, 0);
        chamber1.addField(new Field(FieldCategory.PLANUNG, FieldValue.ONE));
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);
        gameBoard.finalizeGameBoard();

        assertTrue(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER),
                "Should return true because there are no floors of the specified category.");
    }

    @Test
    void testCheckMissions() {
        gameBoard.setMissionCards(Arrays.asList(
                new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3)),
                new MissionCard(MissionType.B1, new Reward(RewardCategory.ROCKET, 3))
        ));
        gameBoard.setGameService(gameService);

        GameBoard spyGameBoard = spy(new GameBoard());
        spyGameBoard.setMissionCards(gameBoard.getMissionCards());
        spyGameBoard.setGameService(gameService);

        doReturn(true).when(spyGameBoard).areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER);
        doReturn(false).when(spyGameBoard).areAllFieldsNumbered(FieldCategory.ENERGIE);

        spyGameBoard.checkMissions(gameService, new ArrayList<>());

        verify(gameService, times(1)).notifyPlayersMissionFlipped(anyList(), any(MissionCard.class));
    }

    @Test
    void testMissionA1() {
        when(missionCard.getMissionType()).thenReturn(MissionType.A1);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        doReturn(true).when(gameBoard).areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }

    @Test
    void testMissionA2() {
        when(missionCard.getMissionType()).thenReturn(MissionType.A2);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        doReturn(true).when(gameBoard).areAllFieldsNumbered(FieldCategory.ROBOTER, FieldCategory.PLANUNG);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }

    @Test
    void testMissionB1() {
        when(missionCard.getMissionType()).thenReturn(MissionType.B1);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        doReturn(true).when(gameBoard).areAllFieldsNumbered(FieldCategory.ENERGIE);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }

    @Test
    void testMissionB2() {
        when(missionCard.getMissionType()).thenReturn(MissionType.B2);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        doReturn(true).when(gameBoard).areAllFieldsNumbered(FieldCategory.PFLANZE, FieldCategory.ANYTHING);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }

    @Test
    void testSetFieldWithinFloorBeforeGameBoardFinalizationThrowsException() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.setFieldWithinFloor(0, 0, new CardCombination(floor.getFieldCategory(), floor.getFieldCategory(), FieldValue.ONE)));
    }

    @Test
    void testSetValueWithinFloorAtIndexWrongCategory() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 3);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();
        assertFalse(gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(FieldCategory.WASSER, FieldCategory.WASSER, FieldValue.TWO)));
    }

    @Test
    void testSetValueWithinFloorAtIndexAnything() {
        Chamber chamber = new Chamber(FieldCategory.ANYTHING, rewards, 3);
        Floor anythingFloor = new Floor(FieldCategory.ANYTHING);
        anythingFloor.addChamber(chamber);
        gameBoard.addFloor(anythingFloor);
        gameBoard.finalizeGameBoard();
        assertTrue(gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(FieldCategory.WASSER, FieldCategory.WASSER, FieldValue.TWO)));
    }

    @Test
    void testAddMissionCard() {
        MissionCard card = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        missionCards.add(card);
        gameBoard.setMissionCards(missionCards);
        assertEquals(1, gameBoard.getMissionCards().size(), "Mission card should be added to the game board.");
    }

    @Test
    void testGetMissionCards() {
        assertNotNull(gameBoard.getMissionCards(), "Mission cards should be retrievable from the game board.");
    }

    @Test
    void testCheckAndFlipMissionCardsWhenNoMatch() {
        gameBoard.setGameService(gameService);

        MissionCard card1 = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(Arrays.asList(card1));

        gameBoard.checkAndFlipMissionCards(MissionType.A2);
        assertFalse(card1.isFlipped(), "Mission A1 should not be flipped when checking for A2.");
        verify(gameService, never()).notifyPlayersMissionFlipped(anyList(), eq(card1));
    }

    @Test
    void testMissionCardRewardDecrease() {
        MissionCard card = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        card.flipCard();
        card.applyRewardDecrease();
        assertEquals(2, card.getReward().getNumberRockets(), "The reward should decrease by 1 after the round in which the card is flipped.");
    }

    @Test
    void testNotifyPlayersInitialMissionCards() {
        gameBoard.setGameService(gameService);

        MissionCard card1 = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(Arrays.asList(card1));

        gameBoard.notifyPlayersInitialMissionCards();

        verify(gameService).notifyPlayersInitialMissionCards(anyList(), anyList());
    }

    @Test
    void testCheckMissionsWithAllFieldsNumbered() {
        MissionCard card = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(List.of(card));
        gameBoard.setGameService(gameService);

        doReturn(true).when(gameBoard).areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(gameService, times(1)).notifyPlayersMissionFlipped(anyList(), eq(card));
    }

    @Test
    void testCheckMissionsWithSomeFieldsUnnumbered() {
        MissionCard card = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(List.of(card));
        gameBoard.setGameService(gameService);

        doReturn(false).when(gameBoard).areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER);

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(gameService, never()).notifyPlayersMissionFlipped(anyList(), eq(card));
    }

    @Test
    void testCheatAddsRocket() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        gameBoard.cheat();

        assertEquals(1, gameBoard.getRocketCount());
        assertTrue(gameBoard.isHasCheated());
    }

    @Test
    void testSetValueWithinFloorAtIndexInvalidCategory() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER, rewards, 0);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();

        assertFalse(gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(FieldCategory.WASSER, FieldCategory.WASSER, FieldValue.ONE)));
    }

    @Test
    void testCheckCardCombinationReturnsFalse() {
        Floor floor1 = new Floor(FieldCategory.ROBOTER);
        floor1.addChamber(new Chamber(FieldCategory.ROBOTER, rewards, 0));
        gameBoard.addFloor(floor1);
        gameBoard.finalizeGameBoard();

        CardCombination[] combinations = {new CardCombination(FieldCategory.WASSER, FieldCategory.WASSER, FieldValue.ONE)};
        assertFalse(gameBoard.checkCardCombination(combinations));
    }

    @Test
    void testCheckMissionsWithIllegalArgumentException() {
        MissionCard card = mock(MissionCard.class);
        when(card.getMissionType()).thenThrow(IllegalArgumentException.class);

        gameBoard.setMissionCards(List.of(card));
        gameBoard.setGameService(gameService);

        assertThrows(IllegalArgumentException.class, () -> invokeCheckMissions(gameBoard, gameService));
    }

    public void invokeCheckMissions(GameBoard gameBoard, GameService gameService) {
        gameBoard.checkMissions(gameService, new ArrayList<>());
    }

    @Test
    void testHandleMissionReward() throws Exception {
        missionCard = new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3));
        CreateUserService player1 = mock(CreateUserService.class);
        CreateUserService player2 = mock(CreateUserService.class);

        GameBoard player1Board = mock(GameBoard.class);
        GameBoard player2Board = mock(GameBoard.class);

        when(player1.getGameBoard()).thenReturn(player1Board);
        when(player2.getGameBoard()).thenReturn(player2Board);

        List<CreateUserService> players = List.of(player1, player2);

        Method handleMissionRewardMethod = GameBoard.class.getDeclaredMethod("handleMissionReward", MissionCard.class, GameService.class, List.class);
        handleMissionRewardMethod.setAccessible(true);

        handleMissionRewardMethod.invoke(gameBoard, missionCard, gameService, players);

        verify(player1Board).addRockets(3);
        verify(player2Board).addRockets(3);
        verify(gameService, times(2)).addRocketToPlayer(any(CreateUserService.class), eq(3));
    }

    @Test
    void testMissionC1() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();
        when(missionCard.getMissionType()).thenReturn(MissionType.C1);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }

    @Test
    void testMissionC2() {
        gameBoard.addFloor(floor);
        gameBoard.finalizeGameBoard();
        when(missionCard.getMissionType()).thenReturn(MissionType.C2);
        missionCards.add(missionCard);
        gameBoard.setMissionCards(missionCards);
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();
        gameBoard.addSystemError();

        gameBoard.checkMissions(gameService, new ArrayList<>());

        verify(missionCard).flipCard();
        verify(gameService).notifyPlayersMissionFlipped(anyList(), eq(missionCard));
    }
}
