package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {
    private FieldCategory fieldCategory;
    private FieldValue fieldValue;

    public Field(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        this.fieldValue = FieldValue.NONE;
    }

    public Field(FieldCategory fieldCategory, FieldValue fieldValue) {
        this.fieldCategory = fieldCategory;
        this.fieldValue = fieldValue;
    }
}
