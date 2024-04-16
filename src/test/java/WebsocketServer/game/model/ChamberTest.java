package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChamberTest {
    private Chamber chamber;
    private Field validField;
    private Field invalidField;

    @BeforeEach
    void setUp() {
        chamber = new Chamber(FieldCategory.ROBOTER);
        validField = new Field(FieldCategory.ROBOTER);
        invalidField = new Field(FieldCategory.WASSER);
    }

    @Test
    void testAddValidField() {
        assertDoesNotThrow(() -> chamber.addField(validField));
        chamber.finalizeChamber();
        assertEquals(1, chamber.getSize());
        assertEquals(validField, chamber.getField(0));
    }

    @Test
    void testAddInvalidField() {
        assertThrows(IllegalArgumentException.class, () -> chamber.addField(invalidField));
    }

    @Test
    void testGetValidField() {
        chamber.addField(validField);
        chamber.finalizeChamber();

        assertDoesNotThrow(() -> chamber.getField(0));
        assertEquals(validField, chamber.getField(0));
    }

    @Test
    void testGetMissingField() {
        chamber.finalizeChamber();
        assertThrows(IndexOutOfBoundsException.class, () -> chamber.getField(0));
    }

    @Test
    void testGetNegativeIndex() {
        chamber.finalizeChamber();
        assertThrows(IndexOutOfBoundsException.class, () -> chamber.getField(-1));
    }
    @Test
    void testGetChamberSize() {
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber.finalizeChamber();
        assertEquals(chamber.getSize(),4);
    }

    @Test
    void testInsertatIndexValid() {
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.finalizeChamber();
        chamber.setFieldAtIndex(0,FieldValue.THREE,0);
        chamber.setFieldAtIndex(1,FieldValue.FOUR,0);
        chamber.setFieldAtIndex(2,FieldValue.FIVE,0);
        assertEquals(chamber.getField(2).getFieldValue().getValue(),5);
    }
    @Test
    void testInsertatIndexInValid() {
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber.finalizeChamber();
        chamber.setFieldAtIndex(0,FieldValue.THREE,0);
        chamber.setFieldAtIndex(1,FieldValue.FOUR,0);
        chamber.setFieldAtIndex(2,FieldValue.FIVE,0);
        assertThrows(FloorSequenceException.class, () -> chamber.setFieldAtIndex(3,FieldValue.TWO,0));
    }
    @Test
    public void testIsFinalizedInitiallyFalse() {
        assertFalse(chamber.isFinalized());
    }

    @Test
    public void testFinalizeChamberChangesIsFinalizedToTrue() {
        chamber.finalizeChamber();
        assertTrue(chamber.isFinalized());
    }

    @Test
    public void testAddFieldAfterChamberFinalizationThrowsException() {
        Field field = new Field(FieldCategory.ROBOTER);
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, () -> chamber.addField(field));
    }

    @Test
    public void testGetFieldBeforeChamberFinalizationThrowsException() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        assertThrows(FinalizedException.class, () -> chamber.getField(0));
    }

    @Test
    public void testSomeFieldsAlreadyFinalized() {
        Field field = new Field(FieldCategory.ROBOTER);
        field.finalizeField();
        chamber.addField(field);
        assertThrows(FinalizedException.class, () -> chamber.finalizeChamber());
    }

    @Test
    public void testFinalizeChamberTwiceThrowsException() {
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, chamber::finalizeChamber);
    }
    @Test
    public void testChamberFinalizationWorking() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, chamber::finalizeChamber);
    }
    @Test
    public void testChamberCompletionValid() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();
        chamber.setFieldAtIndex(0,FieldValue.THREE,0);
        chamber.setFieldAtIndex(1,FieldValue.FOUR,0);
        chamber.setFieldAtIndex(2,FieldValue.FIVE,0);
        assertTrue(chamber.checkChamberCompletion(0));
    }
    @Test
    public void testChamberCompletionInValid() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();
        chamber.setFieldAtIndex(0,FieldValue.THREE,0);
        chamber.setFieldAtIndex(1,FieldValue.FOUR,0);
        chamber.setFieldAtIndex(2,FieldValue.FIVE,0);
        assertFalse(chamber.checkChamberCompletion(4));
    }
    @Test
    public void testGetHighestValueinChamberFilled() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();
        chamber.setFieldAtIndex(0,FieldValue.THREE,0);
        chamber.setFieldAtIndex(1,FieldValue.FOUR,0);
        chamber.setFieldAtIndex(2,FieldValue.FIVE,0);
        assertEquals(5,chamber.getHighestValueInChamber());
    }
    @Test
    public void testGetHighestValueinChamberEmpty() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();

        assertEquals(0,chamber.getHighestValueInChamber());
    }

}