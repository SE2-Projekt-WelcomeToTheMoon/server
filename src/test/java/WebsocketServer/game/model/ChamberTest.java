package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.exceptions.FinalizedException;
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
}