package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
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
}