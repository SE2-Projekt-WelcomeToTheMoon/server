package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FloorSequenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    private Floor floor;
    private Chamber chamberCompatible;
    private Chamber secondChamberCompatible;
    private Chamber chamberIncompatible;
    private Chamber chamberAllNull;

    @BeforeEach
    void setUp() {
        floor = new Floor(FieldCategory.ROBOTER);
        chamberCompatible = new Chamber(FieldCategory.ROBOTER);
        secondChamberCompatible = new Chamber(FieldCategory.ROBOTER);
        chamberIncompatible = new Chamber(FieldCategory.WASSER);
        chamberAllNull = new Chamber(FieldCategory.ROBOTER);


        chamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));

        secondChamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.THREE));
        secondChamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.FOUR));

        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
    }

    @Test
    void testGetFieldCategory(){
        assertEquals(floor.getFieldCategory(), FieldCategory.ROBOTER);
    }

    @Test
    void testAddCompatibleChamber() {
        assertDoesNotThrow(() -> floor.addChamber(chamberCompatible));
        assertEquals(2, floor.getSize());
    }

    @Test
    void testAddIncompatibleChamber() {
        assertThrows(IllegalArgumentException.class, () -> floor.addChamber(chamberIncompatible));
    }

    @Test
    void testGetField() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(secondChamberCompatible);
        Field retrievedFieldTwo = floor.getFieldAtIndex(1);
        Field retrievedFieldFour = floor.getFieldAtIndex(3);

        assertEquals(FieldValue.TWO, retrievedFieldTwo.getFieldValue());
        assertEquals(FieldValue.FOUR, retrievedFieldFour.getFieldValue());
    }

    @Test
    void testGetNonExistingField() {
        floor.addChamber(chamberCompatible);
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getFieldAtIndex(5));
    }

    @Test
    void testSetFieldAtIndexValid() {
        floor.addChamber(chamberCompatible);
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.THREE));
        assertEquals(FieldValue.THREE, floor.getFieldAtIndex(1).getFieldValue());
    }

    @Test
    void testSetFieldAtIndexInvalid() {
        floor.addChamber(chamberCompatible);
        assertThrows(FloorSequenceException.class, () -> floor.setFieldAtIndex(1, FieldValue.ONE));
    }

    @Test
    void testSetFieldAtIndexNone(){
        floor.addChamber(chamberAllNull);
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.NONE));
    }

    @Test
    void testSetFieldAtIndexWithNoneValueAndValidAscendingOrder() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(chamberAllNull);
        floor.addChamber(secondChamberCompatible);
        assertDoesNotThrow(() -> floor.setFieldAtIndex(2, FieldValue.NONE));


        assertDoesNotThrow(() -> floor.setFieldAtIndex(4, FieldValue.FIVE));
    }

    @Test
    void testSettingNoneValueDoesNotBreakSequence() {
        floor.addChamber(chamberAllNull);
        assertDoesNotThrow(() -> floor.setFieldAtIndex(0, FieldValue.ONE));
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.TWO));
    }

    @Test
    void testGetNegativeChamber(){
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getChamber(-1));
    }

    @Test
    void testGetNonExistingChamber(){
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getChamber(3));
    }
}