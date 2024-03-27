package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FloorSequenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard();
    }

    @Test
    void testAddFloor() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        gameBoard.addFloor(floor);

        assertEquals(1, gameBoard.getSize());
        assertDoesNotThrow(() -> gameBoard.getFloorAtIndex(0));
        assertEquals(floor, gameBoard.getFloorAtIndex(0));
    }


    @Test
    void testGetFloorNotPresent() {
        assertThrows(IndexOutOfBoundsException.class, () -> gameBoard.getFloorAtIndex(0));
    }

    @Test
    void testSetAndGetFieldValueWithinFloorAtIndex() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        Field field = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        chamber.addField(field);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        assertDoesNotThrow(() -> gameBoard.setValueWithinFloorAtIndex(0, 0, FieldValue.TWO));
        assertEquals(FieldValue.TWO, gameBoard.getFloorAtIndex(0).getFieldAtIndex(0).getFieldValue());
    }

    @Test
    void testSetInvalidValueWithinFloorAtIndex() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamber = new Chamber(FieldCategory.ROBOTER);
        Field field1 = new Field(FieldCategory.ROBOTER, FieldValue.ONE);
        Field field2 = new Field(FieldCategory.ROBOTER, FieldValue.THREE);
        chamber.addField(field1);
        chamber.addField(field2);
        floor.addChamber(chamber);
        gameBoard.addFloor(floor);

        assertThrows(FloorSequenceException.class, () ->
                gameBoard.setValueWithinFloorAtIndex(0, 0, FieldValue.FOUR));

    }

    @Test
    void testGetFloorAtNegativeIndex(){
        assertThrows(IndexOutOfBoundsException.class, () -> gameBoard.getFloorAtIndex(-1));
    }
}