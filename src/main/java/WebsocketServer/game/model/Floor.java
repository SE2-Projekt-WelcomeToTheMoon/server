package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
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

    public Floor(FieldCategory fieldCategory) {
        this.fieldCategory = fieldCategory;
        chambers = new ArrayList<>();
    }

    public Field getFieldAtIndex(int index) {
        if (!isFinalized) {
            throw new FinalizedException("Floor must be finalized.");
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
            throw new FinalizedException("Floor already finalized.");
        }

        if (chamber.getFieldCategory().equals(fieldCategory)) {
            chambers.add(chamber);
        } else {
            throw new IllegalArgumentException("Chamber must have the same FieldCategory as Floor");
        }
    }

    //add check on fieldcategory via currentcombination
    public void setFieldAtIndex(int index, FieldValue value) {
        if (!isFinalized) {
            throw new FinalizedException("Floor must be finalized.");
        }
        int count=0;
        int currentMax=0;
        boolean fieldChanged=false;
        for (Chamber chamber : chambers) {
            if (index >= count && index <= count + chamber.getSize()) {
                chamber.setFieldAtIndex(index - count, value, currentMax);
                fieldChanged = true;
            }
            count += chamber.getSize();
            currentMax = Math.max(chamber.getHighestValueInChamber(), currentMax);
        }
        if(!fieldChanged)throw new FloorSequenceException("Values within Floor must be in ascending order");
    }

    public boolean canInsertValue(FieldValue value) {
        if (!isFinalized) {
            throw new FinalizedException("Floor must be finalized.");
        }

        int currentMax = 0;
        FieldValue nextValue = null;

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
            throw new FinalizedException("Floor must be finalized.");
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
            throw new FinalizedException("Floor already finalized.");
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
     * @return
     */
    public List<Chamber> getChambers() {
        return new ArrayList<>(chambers);
    }

}
