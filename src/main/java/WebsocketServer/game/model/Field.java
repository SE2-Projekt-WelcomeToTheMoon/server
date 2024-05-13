package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class Field {
    @JsonIgnore
    private FieldCategory fieldCategory;
    private FieldValue fieldValue;
    @JsonIgnore
    private boolean isFinalized = false;
    private boolean isChanged = false;

    public Field(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        this.fieldValue = FieldValue.NONE;
    }

    public Field(FieldCategory fieldCategory, FieldValue fieldValue) {
        this.fieldCategory = fieldCategory;
        this.fieldValue = fieldValue;
    }

    public void setFieldCategory(FieldCategory fieldCategory) {
        if (isFinalized) {
            throw new FinalizedException("Chamber already finalized.");
        }

        this.fieldCategory = fieldCategory;
    }

    public void setFieldValue(FieldValue fieldValue) {
        if (!isFinalized) {
            throw new FinalizedException("Chamber must be finalized.");
        }
        this.fieldValue = fieldValue;
    }

    public void finalizeField() {
        if (isFinalized) {
            throw new FinalizedException("Field already finalized.");
        } else {
            isFinalized = true;

        }
    }

    /**
     * So that Client won't change it again
     * @param fieldValue
     */
    public void setFieldValueClient(FieldValue fieldValue) {
        if (!isFinalized) {
            throw new FinalizedException("Field must be finalized.");
        }
        this.fieldValue = fieldValue;
        isChanged = true;
    }
}
