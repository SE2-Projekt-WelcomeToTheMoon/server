package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.services.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Arrays;


class GameBoardTest {
    private GameBoard gameBoard;
    private Floor floor;

    private List<Reward> rewards;
    @BeforeEach
    void setUp() {
        rewards= List.of(new Reward[]{new Reward(RewardCategory.PLANING), new Reward(RewardCategory.ROCKET, 5)});
        gameBoard = new GameBoard();
        floor = new Floor(FieldCategory.ROBOTER);
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

    @Test()
    void testHasWonWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.hasWon());
    }

    @Test()
    void testGetBarometerPointsWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.getRocketBarometerPoints());
    }

    @Test()
    void testAddRocketsWhenNotFinalized() {
        assertThrows(FinalizedException.class, () -> gameBoard.addRockets(5));
    }

    @Test()
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
        Chamber chamber = new Chamber(FieldCategory.ROBOTER,rewards,0);
        Field field = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER);
        chamber.addField(field);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertDoesNotThrow(() -> gameBoard.setValueWithinFloorAtIndex(0, 1, new CardCombination(floor.getFieldCategory(),floor.getFieldCategory(),FieldValue.TWO)));
        assertEquals(FieldValue.TWO, gameBoard.getFloorAtIndex(0).getFieldAtIndex(1).getFieldValue());
    }

    @Test
    void testSetInvalidValueWithinFloorAtIndex() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER,rewards,0);
        Field field1 = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER, FieldValue.THREE);
        chamber.addField(field1);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertThrows(FloorSequenceException.class, () ->
                gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(floor.getFieldCategory(),floor.getFieldCategory(),FieldValue.FOUR)));

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
        Chamber chamber = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.setValueWithinFloorAtIndex(0, 0, new CardCombination(floor.getFieldCategory(),floor.getFieldCategory(),FieldValue.ONE)));
    }

    @Test
    void testInitializeMissionCards() {
        List<MissionCard> cards = gameBoard.initializeMissionCards();

        assertEquals(3, cards.size(), "Should initialize exactly three mission cards");
        assertTrue(cards.stream().allMatch(card -> card.getMissionDescription().startsWith("Mission ")), "Mission descriptions should be correctly set");
        assertTrue(cards.stream().allMatch(card -> Arrays.asList(2, 3).contains(card.getReward().getNumberRockets())), "Rewards should be correctly set between 2 and 3 rockets");
    }

    @Test
    void testCheckAndFlipMissionCards() {
        GameService mockGameService = mock(GameService.class);
        gameBoard.setGameService(mockGameService); 

        MissionCard card1 = new MissionCard("Mission A1", new Reward(RewardCategory.ROCKET, 3));
        MissionCard card2 = new MissionCard("Mission A2", new Reward(RewardCategory.ROCKET, 3));
        gameBoard.setMissionCards(Arrays.asList(card1, card2));

        gameBoard.checkAndFlipMissionCards("Mission A1");
        assertTrue(card1.isFlipped(), "Mission A1 should be flipped");
        assertFalse(card2.isFlipped(), "Mission A2 should not be flipped");
        verify(mockGameService).notifyPlayersMissionFlipped(card1);
    }

    @Test
    void testAreAllFieldsNumberedAllNumbered() {
        GameBoard gameBoard = new GameBoard();
        Floor floor1 = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);

        assertTrue(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER), 
            "Should return true as all fields in the specified category are numbered.");
    }

    @Test
    void testAreAllFieldsNumberedSomeUnnumbered() {
        GameBoard gameBoard = new GameBoard();
        Floor floor1 = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE)); 
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);

        assertFalse(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER),
            "Should return false as not all fields in the specified category are numbered.");
    }

    @Test
    void testAreAllFieldsNumberedNoRelevantFloors() {
        GameBoard gameBoard = new GameBoard();
        Floor floor1 = new Floor(FieldCategory.PLANUNG);
        Chamber chamber1 = new Chamber(FieldCategory.PLANUNG,rewards,0);
        chamber1.addField(new Field(FieldCategory.PLANUNG, FieldValue.ONE));
        floor1.addChamber(chamber1);

        gameBoard.addFloor(floor1);

        assertTrue(gameBoard.areAllFieldsNumbered(FieldCategory.ROBOTER),
            "Should return true because there are no floors of the specified category.");
    }

     @Test
    void testCheckMissions() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setMissionCards(Arrays.asList(
            new MissionCard("Mission A1", new Reward(RewardCategory.ROCKET, 3)),
            new MissionCard("Mission B1", new Reward(RewardCategory.ROCKET, 3))
        ));

        GameBoard spyGameBoard = spy(gameBoard);
        GameService mockGameService = mock(GameService.class);
        spyGameBoard.setGameService(mockGameService);

        doReturn(true).when(spyGameBoard).areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER);
        doReturn(false).when(spyGameBoard).areAllFieldsNumbered(FieldCategory.ENERGIE);

        spyGameBoard.checkMissions();

        verify(mockGameService, times(1)).notifyPlayersMissionFlipped(any(MissionCard.class));
        verify(mockGameService, never()).notifyPlayersMissionFlipped(new MissionCard("Mission B1", new Reward(RewardCategory.ROCKET, 3)));
    }

    @Test
    void testCheat(){
        gameBoard.finalizeGameBoard();
        assertEquals(0, gameBoard.getRocketCount());
        gameBoard.cheat();
        assertEquals(1, gameBoard.getRocketCount());
    }
}