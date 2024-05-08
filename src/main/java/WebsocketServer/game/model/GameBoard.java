package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    @Getter
    private final List<Floor> floors;
    private final SystemErrors systemErrors;
    private final int ROCKETS_TO_COMPLETE = 32;

    private final RocketBarometer rocketBarometer;
    @Getter(onMethod_ = {@JsonIgnore})
    private boolean isFinalized = false;

    public GameBoard() {
        floors = new ArrayList<>();
        systemErrors = new SystemErrors();
        rocketBarometer = new RocketBarometer();
    }

    public void finalizeGameBoard() {
        if (isFinalized) {
            throw new FinalizedException("GameBoard already finalized.");
        } else {
            try {
                for (Floor floor : floors) {
                    floor.finalizeFloor();
                }
                isFinalized = true;
            } catch (FinalizedException e) {
                throw new FinalizedException("Some Floors already finalized.");
            }
        }
    }

    public Floor getFloorAtIndex(int index) {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }

        if (index >= 0 && index < floors.size()) {
            return floors.get(index);
        } else {
            throw new IndexOutOfBoundsException("Floor at index " + index + " is not present");
        }
    }

    public void setValueWithinFloorAtIndex(int floor, int index, FieldValue value) throws FloorSequenceException{
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }

        try {
            Floor currentFloor = getFloorAtIndex(floor);
            currentFloor.setFieldAtIndex(index, value);
        } catch (FloorSequenceException e) {
            throw new FloorSequenceException(e.getMessage());
        }
    }

    public void addFloor(Floor floor) {
        if (isFinalized) {
            throw new FinalizedException("GameBoard already finalized.");
        }

        floors.add(floor);
    }

    public boolean addRockets(int rockets){
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        rocketBarometer.addRockets(rockets);
        return hasWon();
    }

    public boolean hasWon() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return rocketBarometer.getRocketCount() - systemErrors.getCurrentErrors() > ROCKETS_TO_COMPLETE;
    }

    public int getRocketBarometerPoints(){
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return rocketBarometer.getPointsOfRocketBarometer();
    }

    public int getRocketCount(){
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return rocketBarometer.getRocketCount();
    }

    public boolean addSystemError(){
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return systemErrors.increaseCurrentErrors();
    }

    public boolean hasLost() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return systemErrors.hasLost();
    }

    @JsonIgnore
    public int getRemainingErrors() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return systemErrors.getRemainingErrors();
    }

    @JsonIgnore
    public int getSize() {
        return floors.size();
    }

}
