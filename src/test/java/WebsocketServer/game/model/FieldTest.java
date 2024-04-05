package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
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
}