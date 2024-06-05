package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    private Floor floor;
    private Chamber chamberCompatible;
    private Chamber secondChamberCompatible;
    private Chamber chamberIncompatible;
    private Chamber chamberAllNull;
    private Chamber chamber;
    private List<Reward> rewards;

    @BeforeEach
    void setUp() {
        rewards= List.of(new Reward[]{new Reward(RewardCategory.PLANING), new Reward(RewardCategory.ROCKET, 5)});
        floor = new Floor(FieldCategory.ROBOTER);
        chamberCompatible = new Chamber(FieldCategory.ROBOTER,rewards,0);
        secondChamberCompatible = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamberIncompatible = new Chamber(FieldCategory.WASSER,rewards,0);
        chamberAllNull = new Chamber(FieldCategory.ROBOTER,rewards,0);

        chamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));


        secondChamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.THREE));
        secondChamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.FOUR));

        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamberAllNull.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));

        chamber = new Chamber(FieldCategory.ROBOTER,rewards,0);
    }

    @Test
    void testGetFieldCategory(){
        assertEquals(FieldCategory.ROBOTER, floor.getFieldCategory());
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
        chamberCompatible.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamberCompatible);
        floor.finalizeFloor();
        assertDoesNotThrow(() -> floor.setFieldAtIndex(2, FieldValue.THREE));
        assertEquals(FieldValue.THREE, floor.getFieldAtIndex(2).getFieldValue());
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
        assertThrows(FloorSequenceException.class,() -> floor.setFieldAtIndex(1, FieldValue.NONE));
    }

    @Test
    void testSetFieldAtIndexWithNoneValueAndValidAscendingOrder() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(chamberAllNull);
        floor.addChamber(secondChamberCompatible);

        floor.finalizeFloor();

        assertThrows(FloorSequenceException.class,() -> floor.setFieldAtIndex(2, FieldValue.NONE));
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
    void testIsFinalizedInitiallyFalse() {
        assertFalse(floor.isFinalized());
    }

    @Test
    void testFinalizeFloorChangesIsFinalizedToTrue() {
        floor.finalizeFloor();
        assertTrue(floor.isFinalized());
    }

    @Test
    void testAddChamberAfterFloorFinalizationThrowsException() {
        floor.finalizeFloor();
        assertThrows(FinalizedException.class, () -> floor.addChamber(chamber));
    }

    @Test
    void testGetFieldAtIndexBeforeFloorFinalizationThrowsException() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.getFieldAtIndex(0));
    }

    @Test
    void testGetChamberBeforeFloorFinalizationThrowsException() {
        floor.addChamber(new Chamber(FieldCategory.ROBOTER,rewards,0));
        assertThrows(FinalizedException.class, () -> floor.getChamber(0));
    }

    @Test
    void testFinalizeFloorWithAlreadyFinalizedChamberThrowsException() {
        chamber.finalizeChamber();
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.finalizeFloor());
    }

    @Test
    void testFinalizeFloorTwiceThrowsException() {
        floor.finalizeFloor();
        assertThrows(FinalizedException.class, floor::finalizeFloor);
    }

    @Test
    void testSetFieldAtIndexBeforeFloorFinalizationThrowsException() {
        floor.addChamber(chamber);
        assertThrows(FinalizedException.class, () -> floor.setFieldAtIndex(0, FieldValue.ONE));
    }
    @Test
    void testChamberCompletionWorking() {
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
    @Test
    void testChamberCompletionNotCompleted() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));

        floor.addChamber(chamber);

        floor.finalizeFloor();
        floor.setFieldAtIndex(0,FieldValue.ONE);
        floor.setFieldAtIndex(1,FieldValue.TWO);
        floor.setFieldAtIndex(3,FieldValue.FIVE);
        floor.setFieldAtIndex(4,FieldValue.SIX);
        assertFalse(floor.checkFloorCompletion());
    }

    @Test
    void testInsertionNotInOrder() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));

        floor.addChamber(chamber);

        floor.finalizeFloor();
        floor.setFieldAtIndex(0,FieldValue.ONE);
        floor.setFieldAtIndex(1,FieldValue.TWO);
        floor.setFieldAtIndex(3,FieldValue.FIVE);

        assertThrows(FloorSequenceException.class, () -> floor.setFieldAtIndex(4,FieldValue.FOUR));
    }

    @Test
    void testGetChambersList(){
        List<Chamber> chambers = new ArrayList<>();
        Chamber chamber1 = new Chamber(FieldCategory.ENERGIE,rewards,0);
        chambers.add(chamber1);

        Floor floor1 = new Floor(FieldCategory.ENERGIE);
        floor1.addChamber(chamber1);

        assertEquals(chambers, floor1.getChambers());
    }

    @Test
    void testCanInsertValueThrowsIfNotFinalized() {
        assertThrows(FinalizedException.class, () -> floor.canInsertValue(FieldValue.ONE));
    }

    @Test
    void testCanInsertValueValidSimpleInsert() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(chamberAllNull);
        floor.finalizeFloor();
        assertTrue(floor.canInsertValue(FieldValue.THREE));
    }

    @Test
    void testCanInsertValueValidAcrossChambers() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(secondChamberCompatible);
        secondChamberCompatible.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        floor.finalizeFloor();
        assertTrue(floor.canInsertValue(FieldValue.FIVE));
    }

    @Test
    void testCanInsertValueInvalidDueToNextValue() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(secondChamberCompatible);
        floor.finalizeFloor();
        assertFalse(floor.canInsertValue(FieldValue.SIX));
    }

    @Test
    void testCanInsertValueInvalidDueToLowValue() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(secondChamberCompatible);
        floor.finalizeFloor();
        assertFalse(floor.canInsertValue(FieldValue.ONE));
    }

    @Test
    void testCanInsertValueValidAtStart() {
        floor.addChamber(chamberAllNull);
        floor.addChamber(secondChamberCompatible);
        floor.finalizeFloor();
        assertTrue(floor.canInsertValue(FieldValue.ONE));
    }

    @Test
    void testCanInsertValueInvalidAtEndDueToNextValue() {
        floor.addChamber(chamberCompatible);
        floor.addChamber(secondChamberCompatible);
        floor.addChamber(chamberAllNull);
        floor.finalizeFloor();
        assertFalse(floor.canInsertValue(FieldValue.THREE));
    }

    @Test
    void testCanInsertValueWithNextValueNull() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamberTest = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamberTest.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamberTest.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));
        chamberTest.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));

        floor.addChamber(chamberTest);
        floor.finalizeFloor();

        assertTrue(floor.canInsertValue(FieldValue.THREE));
    }

    @Test
    void testCanInsertValueLessThanNextValue() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));

        Chamber chamber2 = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber2.addField(new Field(FieldCategory.ROBOTER, FieldValue.NONE));
        chamber2.addField(new Field(FieldCategory.ROBOTER, FieldValue.FIVE));

        floor.addChamber(chamber1);
        floor.addChamber(chamber2);
        floor.finalizeFloor();

        assertTrue(floor.canInsertValue(FieldValue.THREE));
    }

    @Test
    void testCanInsertValueNotGreaterThanCurrentMax() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamberTest = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamberTest.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamberTest.addField(new Field(FieldCategory.ROBOTER, FieldValue.FOUR));

        floor.addChamber(chamberTest);
        floor.finalizeFloor();

        assertFalse(floor.canInsertValue(FieldValue.THREE));
    }

    @Test
    void testCanInsertValueNotLessThanNextValue() {
        Floor floor = new Floor(FieldCategory.ROBOTER);
        Chamber chamber1 = new Chamber(FieldCategory.ROBOTER,rewards, 0);
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.ONE));
        chamber1.addField(new Field(FieldCategory.ROBOTER, FieldValue.TWO));

        Chamber chamber2 = new Chamber(FieldCategory.ROBOTER,rewards,0);
        chamber2.addField(new Field(FieldCategory.ROBOTER, FieldValue.THREE));

        floor.addChamber(chamber1);
        floor.addChamber(chamber2);
        floor.finalizeFloor();

        assertFalse(floor.canInsertValue(FieldValue.FOUR));
    }

    @Test
    void testGetFloorSize(){
        assertEquals(0, floor.getFloorSize());
        floor.addChamber(chamberCompatible);
        assertEquals(2, floor.getFloorSize());
    }

    @Test
    void testGetNumOfChambers(){
        floor.addChamber(chamberCompatible);
        assertEquals(1, floor.getNumberOfChambers());
    }

    @Test
    void testGetCorrectChamber(){
        floor.addChamber(chamberCompatible);
        floor.finalizeFloor();
        assertEquals(chamberCompatible, floor.getChamber(0));
    }
}