package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


public class Floor {

    private final List<Chamber> chambers;
    @Getter
    @JsonProperty("fieldCategory")
    private FieldCategory fieldCategory;
    @Getter
    @JsonIgnore
    private boolean isFinalized = false;
    private static final String TAG_FINALIZED = "Floor already finalized.";

    public Floor(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        chambers = new ArrayList<>();
    }

    public Field getFieldAtIndex(int index) {
        if (!isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }

        int currentIndex = 0;

        for (Chamber chamber : chambers) {
            for (int i = 0; i < chamber.getSize(); i++) {
                if (currentIndex == index) {
                    return chamber.getField(i);
                } else {
                    currentIndex++;
                }
            }
        }

        throw new IndexOutOfBoundsException("Field at index " + index + " is not present");
    }

    public void addChamber(Chamber chamber) {
        if (isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }

        if (chamber.getFieldCategory().equals(fieldCategory)) {
            chambers.add(chamber);
        } else {
            throw new IllegalArgumentException("Chamber must have the same FieldCategory as Floor");
        }
    }


    /***
     * Checks if setting the field at that index is legal and then sets it if it is
     * @param index index where value is to be set
     * @param value Value to set into the index
     * @return true is setting is legal, false otherwise
     */
    public boolean setFieldAtIndex(int index, FieldValue value) {
        if (!isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }
        int count=0;
        int currentMax=0;
        boolean fieldChanged=false;
        for (Chamber chamber : chambers) {
            if (index >= count && index <= count + chamber.getSize()&&chamber.getField(index-count).getFieldValue()==FieldValue.NONE) {
                chamber.setFieldAtIndex(index - count, value, currentMax);
                fieldChanged = true;
            }
            count += chamber.getSize();
            currentMax = Math.max(chamber.getHighestValueInChamber(), currentMax);
        }
        if(!fieldChanged){
            throw new FloorSequenceException("Values within Floor must be in ascending order");
        }
        return true;
    }

    /***
     * Sets the value of the Combination at the index in the floor. Does not check if it is a valid move
     * @param index The index to put the value at
     * @param value The value to be put
     */
    public void setFieldAtIndex(int index, CardCombination value) {
        if (!isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }
        if(index>=getFloorSize())throw new IllegalArgumentException("Index cannot be bigger than floor size");
        int count=0;
        for (Chamber chamber : chambers) {
            if (index >= count && index <= count + chamber.getSize()) {
                chamber.setFieldAtIndex(index - count, value);
                return;
            }
            count += chamber.getSize();
        }
    }

    public boolean canInsertValue(FieldValue value) {
        if (!isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }

        int currentMax = 0;
        FieldValue nextValue;

        for (int i = 0; i < chambers.size(); i++) {
            Chamber chamber = chambers.get(i);
            List<Field> fields = chamber.getFields();

            if (i < chambers.size() - 1) {
                nextValue = getNextValueInNextChamber(i + 1);
            } else {
                nextValue = null;
            }

            for (Field field : fields) {
                if (field.getFieldValue() == FieldValue.NONE) {
                    if (value.getValue() > currentMax && (nextValue == null || value.getValue() < nextValue.getValue())) {
                        return true;
                    }
                } else {
                    currentMax = Math.max(currentMax, field.getFieldValue().getValue());
                }
            }
        }

        return false;
    }

    @JsonIgnore
    private FieldValue getNextValueInNextChamber(int startChamberIndex) {
        for (int i = startChamberIndex; i < chambers.size(); i++) {
            for (Field field : chambers.get(i).getFields()) {
                if (field.getFieldValue() != FieldValue.NONE) {
                    return field.getFieldValue();
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public int getFloorSize() {
        return chambers.stream().mapToInt(Chamber::getSize).sum();
    }

    @JsonIgnore
    public Chamber getChamber(int index) {
        if (!isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        }

        if (index >= 0 && index < chambers.size()) {
            return chambers.get(index);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @JsonIgnore
    public int getNumberOfChambers() {
        return chambers.size();
    }

    @JsonProperty("floorSize")
    public int getSize() {
        int sum = 0;
        for (Chamber chamber : chambers) {
            sum += chamber.getSize();
        }

        return sum;
    }

    public void finalizeFloor() {
        if (isFinalized) {
            throw new FinalizedException(TAG_FINALIZED);
        } else {
            try {
                for (Chamber chamber : chambers) {
                    chamber.finalizeChamber();
                }
                isFinalized = true;
            } catch (FinalizedException e) {
                throw new FinalizedException("Some Chambers already finalized.");
            }
        }
    }

    public boolean checkFloorCompletion(){
        int currentHighest=-1;
        for (Chamber chamber: chambers) {
            if(!chamber.checkChamberCompletion(currentHighest))return false;
            currentHighest=chamber.getHighestValueInChamber();
        }
        return true;
    }

    /**
     * maybe change later?
     * return the original reference, which would allow to change the object itself
     */
    public List<Chamber> getChambers() {
        return new ArrayList<>(chambers);
    }

    /***
     * checks if entering a combination at a specific index is a legal move
     * @param combination The Combination to check for
     * @param index The Index where it wants to be inserted
     * @return true if legal false if not
     */
    public boolean isValidMove(CardCombination combination, int index){

        int biggestBefore=0;
        int smallestAfter=16;

       ArrayList<Field> allFields=getAllFieldsAsList();
        int pointerLeft=0;
        int pointerRight=allFields.size() - 1;
       if(allFields.get(index).getFieldValue()!=FieldValue.NONE||(fieldCategory!=FieldCategory.ANYTHING&&combination.getCurrentSymbol()!=fieldCategory))return false;
       for (int i = 0; i < allFields.size(); i++){
           if(pointerRight>index){
               if(allFields.get(pointerRight).getFieldValue()!=FieldValue.NONE){
                   smallestAfter=Math.min(allFields.get(allFields.size()-i-1).getFieldValue().getValue(),smallestAfter);
               }
               pointerRight--;
           }
           if(pointerLeft<index){
               biggestBefore= Math.max(allFields.get(i).getFieldValue().getValue(), biggestBefore);
               pointerLeft++;
           }
       }
        return combination.getCurrentNumber() > biggestBefore && combination.getCurrentNumber() < smallestAfter;
    }
    private ArrayList<Field> getAllFieldsAsList(){
        ArrayList<Field> allFields=new ArrayList<>();
        for (Chamber chamber:chambers ) {
            allFields.addAll(chamber.getFields());
        }
        return allFields;
    }
}
