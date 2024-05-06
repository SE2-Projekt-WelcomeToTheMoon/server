package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.RewardCategory;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    @Getter
    private final List<Floor> floors;
    private List<MissionCard> missionCards;
    private final SystemErrors systemErrors;

    private final RocketBarometer rocketBarometer;
    @Getter(onMethod_ = {@JsonIgnore})
    private boolean isFinalized = false;

    public GameBoard() {
        floors = new ArrayList<>();
        missionCards = initializeMissionCards();
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

    public void setValueWithinFloorAtIndex(int floor, int index, FieldValue value) {
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
        return rocketBarometer.addRockets(rockets);
    }

    public boolean hasWon() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        //TODO: RocketCount must be actually greater than ROCKET_TO_COMPLETE + SYSTEM ERRORS

        return rocketBarometer.hasWon();
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

    private List<MissionCard> initializeMissionCards() {
        List<MissionCard> cards = new ArrayList<>();
        cards.add(new MissionCard("Test 1", new Reward(RewardCategory.ROCKET, 5)));
        cards.add(new MissionCard("Test 2", new Reward(RewardCategory.ROCKET, 5)));
        // Other Cards
        return cards;
    }

    public void checkAndFlipMissionCards(String missionDescription) {
        for (MissionCard card : missionCards) {
            if (!card.isFlipped() && card.getMissionDescription().equals(missionDescription)) {
                card.flipCard();
                informAllPlayersAboutFlip(card);
            }
        }
    }

    private void informAllPlayersAboutFlip(MissionCard card) {
        // TODO: Inform players that mission card was flipped
    }
}
