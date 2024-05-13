package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard gameBoard;
    private Floor floor;

    @BeforeEach
    void setUp() {
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
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        Field field = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER);
        chamber.addField(field);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertDoesNotThrow(() -> gameBoard.setValueWithinFloorAtIndex(0, 1, FieldValue.TWO));
        assertEquals(FieldValue.TWO, gameBoard.getFloorAtIndex(0).getFieldAtIndex(1).getFieldValue());
    }

    @Test
    void testSetInvalidValueWithinFloorAtIndex() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        Field field1 = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER, FieldValue.THREE);
        chamber.addField(field1);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        gameBoard.finalizeGameBoard();

        assertThrows(FloorSequenceException.class, () ->
                gameBoard.setValueWithinFloorAtIndex(0, 0, FieldValue.FOUR));

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
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.setValueWithinFloorAtIndex(0, 0, FieldValue.ONE));
    }
}