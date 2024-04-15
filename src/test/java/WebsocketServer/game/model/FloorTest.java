package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
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
    private Chamber chamber;

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

        chamber = new Chamber(FieldCategory.ROBOTER);
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
        floor.finalizeFloor();

        Field retrievedFieldTwo = floor.getFieldAtIndex(1);
        Field retrievedFieldFour = floor.getFieldAtIndex(3);

        assertEquals(FieldValue.TWO, retrievedFieldTwo.getFieldValue());
        assertEquals(FieldValue.FOUR, retrievedFieldFour.getFieldValue());
    }

    @Test
    void testGetNonExistingField() {
        floor.addChamber(chamberCompatible);
        floor.finalizeFloor();
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getFieldAtIndex(5));
    }

    @Test
    void testSetFieldAtIndexValid() {
        floor.addChamber(chamberCompatible);
        floor.finalizeFloor();
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.THREE));
        assertEquals(FieldValue.THREE, floor.getFieldAtIndex(1).getFieldValue());
    }

    @Test
    void testSetFieldAtIndexInvalid() {
        floor.addChamber(chamberCompatible);
        floor.finalizeFloor();
        assertThrows(FloorSequenceException.class, () -> floor.setFieldAtIndex(1, FieldValue.ONE));
    }

    @Test
    void testSetFieldAtIndexNone(){
        floor.addChamber(chamberAllNull);
        floor.finalizeFloor();
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.NONE));
    }

    @Test
    void testSetFieldAtIndexWithNoneValueAndValidAscendingOrder() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(chamberAllNull);
        floor.addChamber(secondChamberCompatible);

        floor.finalizeFloor();

        assertDoesNotThrow(() -> floor.setFieldAtIndex(2, FieldValue.NONE));


        assertDoesNotThrow(() -> floor.setFieldAtIndex(4, FieldValue.FIVE));
    }

    @Test
    void testSettingNoneValueDoesNotBreakSequence() {
        floor.addChamber(chamberAllNull);
        floor.finalizeFloor();
        assertDoesNotThrow(() -> floor.setFieldAtIndex(0, FieldValue.ONE));
        assertDoesNotThrow(() -> floor.setFieldAtIndex(1, FieldValue.TWO));
    }

    @Test
    void testGetNegativeChamber(){
        floor.finalizeFloor();
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getChamber(-1));
    }

    @Test
    void testGetNonExistingChamber(){
        floor.finalizeFloor();
        assertThrows(IndexOutOfBoundsException.class, () -> floor.getChamber(3));
    }

    @Test
    public void testIsFinalizedInitiallyFalse() {
        assertFalse(floor.isFinalized());
    }

    @Test
    public void testFinalizeFloorChangesIsFinalizedToTrue() {
        floor.finalizeFloor();
        assertTrue(floor.isFinalized());
    }

    @Test
    public void testAddChamberAfterFloorFinalizationThrowsException() {
        floor.finalizeFloor();
        assertThrows(FinalizedException.class, () -> floor.addChamber(chamber));
    }

    @Test
    public void testGetFieldAtIndexBeforeFloorFinalizationThrowsException() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.getFieldAtIndex(0));
    }

    @Test
    public void testGetChamberBeforeFloorFinalizationThrowsException() {
        floor.addChamber(new Chamber(FieldCategory.ROBOTER));
        assertThrows(FinalizedException.class, () -> floor.getChamber(0));
    }

    @Test
    public void testFinalizeFloorWithAlreadyFinalizedChamberThrowsException() {
        chamber.finalizeChamber();
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.finalizeFloor());
    }

    @Test
    public void testFinalizeFloorTwiceThrowsException() {
        floor.finalizeFloor();
        assertThrows(FinalizedException.class, floor::finalizeFloor);
    }

    @Test
    public void testSetFieldAtIndexBeforeFloorFinalizationThrowsException() {
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.setFieldAtIndex(0, FieldValue.ONE));
    }
    @Test
    public void testChamberCompletionWorking() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));

        floor.addChamber(chamber);

        floor.finalizeFloor();
        floor.setFieldAtIndex(0,FieldValue.ONE);
        floor.setFieldAtIndex(1,FieldValue.TWO);
        floor.setFieldAtIndex(2,FieldValue.THREE);
        floor.setFieldAtIndex(3,FieldValue.FIVE);
        floor.setFieldAtIndex(4,FieldValue.SIX);
        assertTrue(floor.checkFloorCompletion());
    }

}