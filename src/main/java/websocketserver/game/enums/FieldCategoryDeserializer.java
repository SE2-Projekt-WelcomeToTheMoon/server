package websocketserver.game.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * this is because --someone-- decided to use german names for the enum
 * translates and returns the correct enum to properly deserialize it using jackson
 */
class FieldCategoryDeserializer extends JsonDeserializer<FieldCategory> {
    @Override
    public FieldCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase();
        return switch (value) {
            case "PLANT", "PFLANZE" -> FieldCategory.PFLANZE;
            case "ENERGY", "ENERGIE" -> FieldCategory.ENERGIE;
            case "WATER", "WASSER" -> FieldCategory.WASSER;
            case "ROBOT", "ROBOTER" -> FieldCategory.ROBOTER;
            case "SPACE_SUIT", "RAUMANZUG" -> FieldCategory.RAUMANZUG;
            case "PLANNING", "PLANUNG" -> FieldCategory.PLANUNG;
            case "WILDCARD", "ANYTHING" -> FieldCategory.ANYTHING;
            default -> null;
        };
    }
}