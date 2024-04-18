package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
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
    void testGetFloorAtNegativeIndex(){
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gameBoard.finalizeGameBoard();
            gameBoard.getFloorAtIndex(-1);
        });
    }

    @Test
    public void testIsFinalizedInitiallyFalse() {
        assertFalse(gameBoard.isFinalized());
    }

    @Test
    public void testFinalizeGameBoardChangesIsFinalizedToTrue() {
        gameBoard.finalizeGameBoard();
        assertTrue(gameBoard.isFinalized());
    }

    @Test
    public void testAddFloorAfterGameBoardFinalizationThrowsException() {
        gameBoard.finalizeGameBoard();
        assertThrows(FinalizedException.class, () -> gameBoard.addFloor(floor));
    }

    @Test
    public void testGetFloorAtIndexBeforeGameBoardFinalizationThrowsException() {
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.getFloorAtIndex(0));
    }

    @Test
    public void testFinalizeGameBoardWithAlreadyFinalizedFloorThrowsException() {
        floor.finalizeFloor();
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.finalizeGameBoard());
    }

    @Test
    public void testFinalizeGameBoardTwiceThrowsException() {
        gameBoard.finalizeGameBoard();
        assertThrows(FinalizedException.class, gameBoard::finalizeGameBoard);
    }

    @Test
    public void testSetValueWithinFloorAtIndexBeforeGameBoardFinalizationThrowsException() {
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);
        assertThrows(FinalizedException.class, () -> gameBoard.setValueWithinFloorAtIndex(0, 0, FieldValue.ONE));
    }
}