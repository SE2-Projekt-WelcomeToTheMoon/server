package WebsocketServer.game.model;

import WebsocketServer.game.enums.ChoosenCardCombination;
import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.FieldValue;
import WebsocketServer.game.enums.RewardCategory;
import WebsocketServer.game.exceptions.FinalizedException;
import WebsocketServer.game.exceptions.FloorSequenceException;
import WebsocketServer.services.GameService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

public class GameBoard {
    private final List<Floor> floors;
    @JsonIgnore
    private List<MissionCard> missionCards;
    private final SystemErrors systemErrors;
    @JsonIgnore
    private final int ROCKETS_TO_COMPLETE = 32;

    private final RocketBarometer rocketBarometer;
    @Getter(onMethod_ = {@JsonIgnore})
    private boolean isFinalized = false;

private GameService gameService;

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

    public void setValueWithinFloorAtIndex(int floor, int index, FieldValue value) throws FloorSequenceException {
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

    public boolean addRockets(int rockets) {
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

    @JsonIgnore
    public int getRocketBarometerPoints() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return rocketBarometer.getPointsOfRocketBarometer();
    }

    @JsonIgnore
    public int getRocketCount() {
        if (!isFinalized) {
            throw new FinalizedException("GameBoard must be finalized.");
        }
        return rocketBarometer.getRocketCount();
    }

    public boolean addSystemError() {
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

    public boolean checkCardCombination(CardCombination[] combinations) {
        //TODO: Check whether the new card combination allows player to find a spot or leads to a system error
        for (CardCombination currentCombination : combinations) {
            for (Floor floor : floors) {
                if (floor.getFieldCategory().equals(currentCombination.getCurrentSymbol()) &&
                        floor.canInsertValue(FieldValue.fromWeight(currentCombination.getCurrentNumber()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @JsonProperty("floors")
    public List<Floor> getFloors() {
        return new ArrayList<>(floors);
    }
    private List<MissionCard> initializeMissionCards() {
        List<MissionCard> cards = new ArrayList<>();
        Random random = new Random();

        // Initialize mission cards randomly as A1 or A2, B1 or B2, C1 or C2
        cards.add(new MissionCard("Mission A" + (random.nextBoolean() ? "1" : "2"), new Reward(RewardCategory.ROCKET, 3)));
        cards.add(new MissionCard("Mission B" + (random.nextBoolean() ? "1" : "2"), new Reward(RewardCategory.ROCKET, 3)));
        cards.add(new MissionCard("Mission C" + (random.nextBoolean() ? "1" : "2"), new Reward(RewardCategory.ROCKET, 3)));

        return cards;
    }

    public void checkAndFlipMissionCards(String missionDescription) {
        for (MissionCard card : missionCards) {
            if (!card.isFlipped() && card.getMissionDescription().equals(missionDescription)) {
                card.flipCard();
                gameService.notifyPlayersMissionFlipped(card);
            }
        }
    }

     public void checkMissions() {
        for (MissionCard missionCard : missionCards) {
                switch (missionCard.getMissionDescription()) {
                    case "Mission A1":
                if (areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER)) {
                    checkAndFlipMissionCards("Mission A1");
                }
                break;
            case "Mission A2":
                if (areAllFieldsNumbered(FieldCategory.ROBOTER, FieldCategory.PLANUNG)) {
                    checkAndFlipMissionCards("Mission A2");
                }
                break;
            case "Mission B1":
                if (areAllFieldsNumbered(FieldCategory.ENERGIE)) {
                    checkAndFlipMissionCards("Mission B1");
                }
                break;
            case "Mission B2":
                if (areAllFieldsNumbered(FieldCategory.PFLANZE)) {
                    checkAndFlipMissionCards("Mission B2");
                }
                break;
            case "Mission C1":
                if (systemErrors.getCurrentErrors() >= 5) {
                    checkAndFlipMissionCards("Mission C1");
                }
                break;
            case "Mission C2":
                //TODO: Implement Mission Card, if 10 X are entered
                break;
            }
        }
    }

    private boolean areAllFieldsNumbered(FieldCategory... categories) {
        return floors.stream()
            .filter(floor -> Arrays.asList(categories).contains(floor.getFieldCategory()))
            .allMatch(floor -> floor.getChambers().stream()
                .allMatch(chamber -> chamber.getFields().stream()
                    .allMatch(field -> field.getFieldValue() != FieldValue.NONE)));
    }

}
