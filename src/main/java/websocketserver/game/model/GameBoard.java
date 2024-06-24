package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.MissionType;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.exceptions.FinalizedException;
import websocketserver.game.exceptions.FloorSequenceException;
import websocketserver.services.GameService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocketserver.services.user.CreateUserService;

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

    public boolean setValueWithinFloorAtIndex(int floor, int index, CardCombination value) throws FloorSequenceException {
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }

        try {
            Floor currentFloor = getFloorAtIndex(floor);
            if(currentFloor.getFieldCategory()!=value.getCurrentSymbol()&& currentFloor.getFieldCategory() != FieldCategory.ANYTHING)return false;
            if(currentFloor.setFieldAtIndex(index, FieldValue.fromWeight(value.getCurrentNumber())))return true;

        } catch (FloorSequenceException e) {
            throw new FloorSequenceException(e.getMessage());
        }
        return false;
    }
    public void setFieldWithinFloor(int floor, int index, CardCombination value){
        if (!isFinalized) {
            throw new FinalizedException(ERRORMESSAGE);
        }
        getFloorAtIndex(floor).setFieldAtIndex(index,value);

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
    
        try {
            cards.add(new MissionCard(random.nextBoolean() ? MissionType.A1 : MissionType.A2, new Reward(RewardCategory.ROCKET, 3)));
            cards.add(new MissionCard(random.nextBoolean() ? MissionType.B1 : MissionType.B2, new Reward(RewardCategory.ROCKET, 3)));
            cards.add(new MissionCard(random.nextBoolean() ? MissionType.C1 : MissionType.C2, new Reward(RewardCategory.ROCKET, 3)));
        } catch (Exception e) {
            // If random selection fails, default to A1, B1, and C1
            cards.clear();
            cards.add(new MissionCard(MissionType.A1, new Reward(RewardCategory.ROCKET, 3)));
            cards.add(new MissionCard(MissionType.B1, new Reward(RewardCategory.ROCKET, 3)));
            cards.add(new MissionCard(MissionType.C1, new Reward(RewardCategory.ROCKET, 3)));
        }
    
        return cards;
    }

    public void notifyPlayersInitialMissionCards() {
        gameService.notifyPlayersInitialMissionCards(missionCards);
    }

    public void checkAndFlipMissionCards(MissionType missionType) {
        for (MissionCard card : missionCards) {
            if (!card.isFlipped() && card.getMissionType() == missionType) {
                card.flipCard();
                gameService.notifyPlayersMissionFlipped(card);
            }
        }
    }

    public void checkMissions(GameService gameService, List<CreateUserService> players) {
        for (MissionCard missionCard : missionCards) {
            if (missionCard.isFlipped()) {
                missionCard.applyRewardDecrease();
            }
            MissionType missionType = missionCard.getMissionType();
            if (checkAndFlipMission(missionType)) {
                handleMissionReward(missionCard, gameService, players);
            }
        }
    }

    private boolean checkAndFlipMission(MissionType missionType) {
        switch (missionType) {
            case A1:
                if (areAllFieldsNumbered(FieldCategory.RAUMANZUG, FieldCategory.WASSER)) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            case A2:
                if (areAllFieldsNumbered(FieldCategory.ROBOTER, FieldCategory.PLANUNG)) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            case B1:
                if (areAllFieldsNumbered(FieldCategory.ENERGIE)) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            case B2:
                if (areAllFieldsNumbered(FieldCategory.PFLANZE, FieldCategory.ANYTHING)) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            case C1:
                if (systemErrors.getCurrentErrors() >= 5) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            case C2:
                if (systemErrors.getCurrentErrors() >= 6) {
                    flipMissionCard(missionType);
                    return true;
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + missionType);
        }
        return false;
    }

    private void flipMissionCard(MissionType missionType) {
        for (MissionCard card : missionCards) {
            if (!card.isFlipped() && card.getMissionType() == missionType) {
                card.flipCard();
                gameService.notifyPlayersMissionFlipped(card);
            }
        }
    }

    private void handleMissionReward(MissionCard missionCard, GameService gameService, List<CreateUserService> players) {
        for (CreateUserService player : players) {
            player.getGameBoard().addRockets(missionCard.getReward().getNumberRockets());
            gameService.addRocketToPlayer(player, missionCard.getReward().getNumberRockets());
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
