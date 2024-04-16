package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


public class Chamber {
    private final List<Field> fields;
    @Getter
    private final FieldCategory fieldCategory;
    @Getter
    private boolean isFinalized = false;

    @Getter
    private Reward rewards;

    public Chamber(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        fields = new ArrayList<>();

    }
    public Chamber(FieldCategory fieldCategory, Reward rewards) {
        this.fieldCategory = fieldCategory;
        fields = new ArrayList<>();
        this.rewards=rewards;
    }

    public Field getField(int index) {
        if (!isFinalized) {
            throw new FinalizedException("Chamber must be finalized.");
        }

        if (index >= 0 && index < fields.size()) {
            return fields.get(index);
        } else {
            throw new IndexOutOfBoundsException("Field not present");
        }
    }

    public void addField(Field field) {
        if (isFinalized) {
            throw new FinalizedException("Chamber already finalized.");
        }

        if (field.getFieldCategory().equals(fieldCategory)) {
            fields.add(field);
        } else {
            throw new IllegalArgumentException("Field must have same Category as Chamber");
        }
    }

    public int getSize() {
        return fields.size();
    }

    public void finalizeChamber() {
        if (isFinalized) {
            throw new FinalizedException("Chamber already finalized.");
        } else {
            try {
                for (Field field : fields) {
                    field.finalizeField();
                }
                isFinalized = true;
            } catch (FinalizedException e) {
                throw new FinalizedException("Some Fields already finalized.");
            }
        }
    }
    public boolean checkChamberCompletion(int highestBefore){
        int previousNumber =highestBefore;
        for (Field field: fields) {
            if(field.getFieldValue().getValue()<previousNumber||field.getFieldValue()== FieldValue.NONE)return false;
            previousNumber=field.getFieldValue().getValue();
        }

        return true;
    }
    public int getHighestValueInChamber(){
        if(fields.isEmpty())throw new FloorSequenceException();
        int highest=0;
        for (Field field: fields) {
            highest= Math.max(field.getFieldValue().getValue(), highest);
        }
        return highest;
    }

    /***
     * Checks if the value can be set at the index, then returns the highest value of the chamber
     * @param index the index
     * @param value the Fieldvalue
     * @return highest value in chamber
     */
    public int setFieldAtIndex(int index, FieldValue value){
        int currentIndex=0;
        int currentMax=0;
        Field fieldToChange = null;
        for (int i = 0; i < getSize(); i++) {
            Field field = getField(i);
            if (currentIndex == index) {
                if (value.getValue() > 0 && value.getValue() > currentMax) {
                    currentMax = value.getValue();
                    fieldToChange = field;
                } else if (value.getValue() == 0) {
                    continue;
                } else {
                    throw new FloorSequenceException("Values within Floor must be in ascending order");
                }
            } else {
                if (field.getFieldValue().getValue() > 0 && field.getFieldValue().getValue() > currentMax) {
                    currentMax = field.getFieldValue().getValue();
                } else if (field.getFieldValue().getValue() == 0) {
                    continue;
                } else {
                    throw new FloorSequenceException("Values within Floor must be in ascending order");
                }
            }
            currentIndex++;
        }
        if (fieldToChange != null) {
        fieldToChange.setFieldValue(value);
    }
        return getHighestValueInChamber();
    }
}
