package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.exceptions.FinalizedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    @Test
    void testFieldConstructorAndDefaultValues() {
        Field field = new Field(FieldCategory.ROBOTER);

        field.finalizeField();

        assertNotNull(field.getFieldCategory());
        assertEquals(FieldCategory.ROBOTER, field.getFieldCategory());
        assertEquals(FieldValue.NONE, field.getFieldValue());
    }

    @Test
    void testSetterAndGetter() {
        Field field = new Field(FieldCategory.ROBOTER);
        field.setFieldCategory(FieldCategory.WASSER);

        field.finalizeField();

        field.setFieldValue(FieldValue.ONE);

        assertEquals(FieldCategory.WASSER, field.getFieldCategory());
        assertEquals(FieldValue.ONE, field.getFieldValue());
    }

    @Test
    void testNullValues() {
        Field field = new Field(null);
        field.finalizeField();

        assertNull(field.getFieldCategory());

        field.setFieldValue(null);
        assertNull(field.getFieldValue());
    }
    @Test
    void testIsFinalized(){
        Field field = new Field(FieldCategory.ROBOTER, FieldValue.NONE);
        assertFalse(field.isFinalized());
        field.finalizeField();
        assertTrue(field.isFinalized());
    }

    @Test
    void testSetFieldCategoryAfterFinalizationThrowsException() {
        Field field = new Field(FieldCategory.ROBOTER);
        field.finalizeField();
        assertThrows(FinalizedException.class, () -> field.setFieldCategory(FieldCategory.WASSER));
    }

    @Test
    void testSetFieldValueBeforeFinalizationThrowsException() {
        Field field = new Field(FieldCategory.ROBOTER);
        assertThrows(FinalizedException.class, () -> field.setFieldValue(FieldValue.ONE));
    }

    @Test
    void testFinalizeFieldTwiceThrowsException() {
        Field field = new Field(FieldCategory.ROBOTER);
        field.finalizeField();
        assertThrows(FinalizedException.class, field::finalizeField);
    }

    @Test
    void testSetFieldValueClient(){
        Field field = new Field(FieldCategory.ROBOTER);
        field.finalizeField();
        field.setFieldValueClient(FieldValue.ONE);
        assertTrue(field.isChanged());
    }

    @Test
    void testSetFieldValueClientBeforeFinalizationThrowsException(){
        Field field = new Field(FieldCategory.ROBOTER);
        assertThrows(FinalizedException.class, () -> field.setFieldValueClient(FieldValue.ONE));
    }
}