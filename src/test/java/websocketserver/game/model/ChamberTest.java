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

class ChamberTest {
    private Chamber chamber;
    private Field validField;
    private Field invalidField;
    private List<Reward> rewards;

    @BeforeEach
    void setUp() {
        rewards= List.of(new Reward[]{new Reward(RewardCategory.PLANING), new Reward(RewardCategory.ROCKET, 5)});
        chamber = new Chamber(FieldCategory.ROBOTER,rewards,0);
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
        assertEquals(4,chamber.getSize());
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
        assertEquals(5,chamber.getField(2).getFieldValue().getValue());
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
    void testIsFinalizedInitiallyFalse() {
        assertFalse(chamber.isFinalized());
    }

    @Test
    void testFinalizeChamberChangesIsFinalizedToTrue() {
        chamber.finalizeChamber();
        assertTrue(chamber.isFinalized());
    }

    @Test
    void testAddFieldAfterChamberFinalizationThrowsException() {
        Field field = new Field(FieldCategory.ROBOTER);
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, () -> chamber.addField(field));
    }

    @Test
    void testGetFieldBeforeChamberFinalizationThrowsException() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        assertThrows(FinalizedException.class, () -> chamber.getField(0));
    }

    @Test
    void testSomeFieldsAlreadyFinalized() {
        Field field = new Field(FieldCategory.ROBOTER);
        field.finalizeField();
        chamber.addField(field);
        assertThrows(FinalizedException.class, () -> chamber.finalizeChamber());
    }

    @Test
    void testFinalizeChamberTwiceThrowsException() {
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, chamber::finalizeChamber);
    }
    @Test
    void testChamberFinalizationWorking() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();
        assertThrows(FinalizedException.class, chamber::finalizeChamber);
    }
    @Test
    void testChamberCompletionValid() {
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
    void testChamberCompletionInValid() {
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
    void testGetHighestValueinChamberFilled() {
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
    void testGetHighestValueInChamberEmpty() {
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.addField(new Field(FieldCategory.ROBOTER));
        chamber.finalizeChamber();

        assertEquals(0,chamber.getHighestValueInChamber());
    }

    @Test
    void testGetFieldsList(){
        List<Field> fields = new ArrayList<>();
        Field field = new Field(FieldCategory.ENERGIE);
        fields.add(field);

        Chamber chamberTest = new Chamber(FieldCategory.ENERGIE,rewards,0);
        chamberTest.addField(field);

        assertEquals(fields, chamberTest.getFields());
    }

    @Test
    void testGetRewardList(){
        List<Reward> rewardsTest = new ArrayList<>();
        Reward reward = new Reward(RewardCategory.ROCKET);
        rewardsTest.add(reward);
        Chamber chamber = new Chamber(FieldCategory.ENERGIE, rewardsTest, 1);

        assertEquals(rewardsTest, chamber.getRewards());
    }
}