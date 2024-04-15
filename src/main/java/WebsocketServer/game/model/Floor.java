package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class Floor {

    private final List<Chamber> chambers;
    @Getter
    private FieldCategory fieldCategory;
    @Getter
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

    public void setFieldAtIndex(int index, FieldValue value) {
        if (!isFinalized) {
            throw new FinalizedException("Floor must be finalized.");
        }

        int currentMax = 0;
        int currentIndex = 0;
        Field fieldToChange = null;

        for (Chamber chamber : chambers) {
            for (int i = 0; i < chamber.getSize(); i++) {
                Field field = chamber.getField(i);
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
        }
        if (fieldToChange != null) {
            fieldToChange.setFieldValue(value);
        }
    }

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

    public int getNumberOfChambers() {
        return chambers.size();
    }

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

    public boolean checkFloorCompletion(Floor floor){
        int currentHighest=-1;
        for (Chamber chamber: floor.chambers) {
            if(!chamber.checkChamberCompletion(currentHighest))return false;
            currentHighest=chamber.getHighestValueInChamber();
        }
        return true;
    }

}
