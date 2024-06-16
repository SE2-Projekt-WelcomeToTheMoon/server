package websocketserver.game.util;

import websocketserver.game.enums.FieldCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * this is because --someone-- decided to use german names for the enum
 * translates and returns the correct enum to properly deserialize it using jackson
 */
public class FieldCategoryTranslator {

    private FieldCategoryTranslator() {
    }

    private static final Map<String, FieldCategory> englishToGermanMap = new HashMap<>();

    static {
        englishToGermanMap.put("ROBOT", FieldCategory.ROBOTER);
        englishToGermanMap.put("WATER", FieldCategory.WASSER);
        englishToGermanMap.put("PLANT", FieldCategory.PFLANZE);
        englishToGermanMap.put("ENERGY", FieldCategory.ENERGIE);
        englishToGermanMap.put("SPACESUIT", FieldCategory.RAUMANZUG);
        englishToGermanMap.put("PLANNING", FieldCategory.PLANUNG);
        englishToGermanMap.put("WILDCARD", FieldCategory.ANYTHING);
    }

    public static FieldCategory translate(String englishCategory) {
        return englishToGermanMap.get(englishCategory);
    }
}