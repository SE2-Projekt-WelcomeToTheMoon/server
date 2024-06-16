package websocketserver.game.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websocketserver.game.enums.FieldCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FieldCategoryTranslatorTest {

    @Test
    void testTranslateRobot() {
        Assertions.assertEquals(FieldCategory.ROBOTER, FieldCategoryTranslator.translate("ROBOT"));
    }

    @Test
    void testTranslateWater() {
        assertEquals(FieldCategory.WASSER, FieldCategoryTranslator.translate("WATER"));
    }

    @Test
    void testTranslatePlant() {
        assertEquals(FieldCategory.PFLANZE, FieldCategoryTranslator.translate("PLANT"));
    }

    @Test
    void testTranslateEnergy() {
        assertEquals(FieldCategory.ENERGIE, FieldCategoryTranslator.translate("ENERGY"));
    }

    @Test
    void testTranslateSpacesuit() {
        assertEquals(FieldCategory.RAUMANZUG, FieldCategoryTranslator.translate("SPACESUIT"));
    }

    @Test
    void testTranslatePlanning() {
        assertEquals(FieldCategory.PLANUNG, FieldCategoryTranslator.translate("PLANNING"));
    }

    @Test
    void testTranslateWildcard() {
        assertEquals(FieldCategory.ANYTHING, FieldCategoryTranslator.translate("WILDCARD"));
    }

    @Test
    void testTranslateUnknown() {
        assertNull(FieldCategoryTranslator.translate("UNKNOWN"));
    }
}