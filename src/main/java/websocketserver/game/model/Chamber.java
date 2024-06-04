package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


public class Chamber {
    private final List<Field> fields;
    @Getter
    @JsonIgnore
    private final FieldCategory fieldCategory;
    @Getter
    @JsonIgnore
    private boolean isFinalized = false;
    @Getter
    @JsonIgnore
    private List<Reward> rewards;




    public Chamber(FieldCategory fieldCategory, List<Reward> rewards, int fieldAmount) {
        this.fieldCategory = fieldCategory;
        fields = new ArrayList<>();
        this.rewards=rewards;
        for (int i=0;i<fieldAmount;i++) {
            addField(new Field(fieldCategory));
        }
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

    @JsonIgnore
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

    @JsonIgnore
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
     */
    public void setFieldAtIndex(int index, FieldValue value, int previousChamberMax){

        int currentMax=previousChamberMax;
        int count=0;
        for (Field field: fields) {
            if(field.getFieldValue().getValue()>=value.getValue())throw new FloorSequenceException("Values within Floor must be in ascending order");
            currentMax=Math.max(field.getFieldValue().getValue(), currentMax);
            if(index==count&&value.getValue()>currentMax){
                if(field.getFieldValue()!=FieldValue.NONE)throw new FloorSequenceException("Values within Floor must be in ascending order");
                field.setFieldValue(value);
            }
            count++;

        }
    }
    public List<Field> getFields(){
        return new ArrayList<>(fields);
    }
}