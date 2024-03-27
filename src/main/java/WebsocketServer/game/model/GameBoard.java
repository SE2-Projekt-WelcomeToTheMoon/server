package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.exceptions.FloorSequenceException;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private final List<Floor> floors;

    public GameBoard() {
        floors = new ArrayList<>();
    }

    public Floor getFloorAtIndex(int index){
        if(index >= 0 && index<floors.size()){
            return floors.get(index);
        }else{
            throw new IndexOutOfBoundsException("Floor at index " + index + " is not present");
        }
    }

    public void setValueWithinFloorAtIndex(int floor, int index, FieldValue value){
        try{
            Floor currentFloor = getFloorAtIndex(floor);
            currentFloor.setFieldAtIndex(index, value);
        }catch (FloorSequenceException e){
            throw new FloorSequenceException(e.getMessage());
        }
    }

    public void addFloor(Floor floor){
        floors.add(floor);
    }

    public int getSize() {
        return  floors.size();
    }
}
