package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.services.GameService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameBoard {
    private static final String ERRORMESSAGE = "GameBoard must be finalized.";
    private final List<Floor> floors;
    @JsonIgnore
    private List<MissionCard> missionCards;
    private final SystemErrors systemErrors;
    @JsonIgnore
    private static final int ROCKETS_TO_COMPLETE = 32;

    private final RocketBarometer rocketBarometer;
    @Getter(onMethod_ = {@JsonIgnore})
    private boolean isFinalized = false;
    @Getter
    private boolean hasCheated;

private GameService gameService;

    public GameBoard() {
        floors = new ArrayList<>();
        missionCards = initializeMissionCards();
        systemErrors = new SystemErrors();
        rocketBarometer = new RocketBarometer();
        hasCheated = false;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public void setMissionCards(List<MissionCard> missionCards) {
        this.missionCards = missionCards;
    }

    public List<MissionCard> getMissionCards() {
        return this.missionCards;
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
            throw new FinalizedException(ERRORMESSAGE);
        }

        if (index >= 0 && index < floors.size()) {
            return floors.get(index);
        } else {
            throw new IndexOutOfBoundsException("Floor at index " + index + " is not present");
        }
    }

    public void setValueWithinFloorAtIndex(int floor, int index, FieldValue value) throws FloorSequenceException {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
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
            throw new FinalizedException(ERRORMESSAGE);
        }
        rocketBarometer.addRockets(rockets);
        return hasWon();
    }

    public boolean hasWon() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return rocketBarometer.getRocketCount() - systemErrors.getCurrentErrors() > ROCKETS_TO_COMPLETE;
    }

    @JsonIgnore
    public int getRocketBarometerPoints() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return rocketBarometer.getPointsOfRocketBarometer();
    }

    @JsonIgnore
    public int getRocketCount() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return rocketBarometer.getRocketCount();
    }

    public int getSystemErrors() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return systemErrors.getCurrentErrors();
    }

    public boolean addSystemError() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return systemErrors.increaseCurrentErrors();
    }

    public boolean hasLost() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        return systemErrors.hasLost();
    }

    @JsonIgnore
    public int getRemainingErrors() {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
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
    public List<MissionCard> initializeMissionCards() {
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
            default:
            }
        }
    }

    public boolean areAllFieldsNumbered(FieldCategory... categories) {
        return floors.stream()
            .filter(floor -> Arrays.asList(categories).contains(floor.getFieldCategory()))
            .allMatch(floor -> floor.getChambers().stream()
                .allMatch(chamber -> chamber.getFields().stream()
                    .allMatch(field -> field.getFieldValue() != FieldValue.NONE)));
    }

    public void cheat() {
        addRockets(1);
        hasCheated = true;
    }
}
