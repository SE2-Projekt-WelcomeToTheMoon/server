package websocketserver.game.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import websocketserver.game.enums.FieldCategory;

public class FieldCategoryDeserializer extends JsonDeserializer<FieldCategory> {
    @Override
    public FieldCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String englishCategory = p.getText().toUpperCase();
        return FieldCategoryTranslator.translate(englishCategory);
    }
}
