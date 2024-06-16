package websocketserver.game.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import websocketserver.game.util.FieldCategoryDeserializer;

@JsonDeserialize(using = FieldCategoryDeserializer.class)
public enum FieldCategory {

    ROBOTER, WASSER, PFLANZE, ENERGIE, RAUMANZUG, PLANUNG, ANYTHING;

}
