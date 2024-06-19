package websocketserver.game.services;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.FieldValue;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.model.Chamber;
import websocketserver.game.model.Field;
import websocketserver.game.model.Floor;
import websocketserver.game.model.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardServiceTest {
    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        GameBoardService gameBoardService = new GameBoardService();
        gameBoard = gameBoardService.createGameBoard();
    }

    @Test
    void testSizeOfGameBoard() {

        assertNotNull(gameBoard);
        assertEquals(9, gameBoard.getSize());
    }


    @Test
    void testAllChambersAndFieldsHaveSameCategoryWithinFloor() {
        for (int i = 0; i < gameBoard.getSize(); i++) {
            Floor currentFloor = gameBoard.getFloorAtIndex(i);
            for (int k = 0; k < currentFloor.getNumberOfChambers(); k++) {
                Chamber currentChamber = currentFloor.getChamber(k);
                assertEquals(currentFloor.getFieldCategory(), currentChamber.getFieldCategory());
                for (int j = 0; i < currentChamber.getSize(); i++) {
                    Field field = currentChamber.getField(j);
                    assertEquals(field.getFieldCategory(), currentFloor.getFieldCategory());
                    assertEquals(FieldValue.NONE, field.getFieldValue());
                }
            }
        }

    }

    @Test
    void testCreateFirstFloor() {
        Floor floor = gameBoard.getFloorAtIndex(0);
        assertEquals(FieldCategory.RAUMANZUG, floor.getFieldCategory());
        assertEquals(1, floor.getChambers().size());

        Chamber chamber = floor.getChambers().get(0);
        assertEquals(3, chamber.getSize());
        assertEquals(FieldCategory.RAUMANZUG, chamber.getFieldCategory());
        assertEquals(1, chamber.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber.getRewards().get(0).getCategory());
        assertEquals(3, chamber.getRewards().get(0).getNumberRockets());
    }

    // Repeat similar tests for other floors

    @Test
    void testCreateSecondFloor() {
        Floor floor = gameBoard.getFloorAtIndex(1);
        assertEquals(FieldCategory.RAUMANZUG, floor.getFieldCategory());
        assertEquals(1, floor.getChambers().size());

        Chamber chamber = floor.getChambers().get(0);
        assertEquals(3, chamber.getSize());
        assertEquals(FieldCategory.RAUMANZUG, chamber.getFieldCategory());
        assertEquals(2, chamber.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber.getRewards().get(0).getCategory());
        assertEquals(4, chamber.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber.getRewards().get(1).getCategory());
    }

    @Test
    void testCreateThirdFloor() {
        Floor floor = gameBoard.getFloorAtIndex(2);
        assertEquals(FieldCategory.WASSER, floor.getFieldCategory());
        assertEquals(2, floor.getChambers().size());

        Chamber chamber1 = floor.getChambers().get(0);
        assertEquals(2, chamber1.getSize());
        assertEquals(FieldCategory.WASSER, chamber1.getFieldCategory());
        assertEquals(2, chamber1.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber1.getRewards().get(0).getCategory());
        assertEquals(2, chamber1.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber1.getRewards().get(1).getCategory());


        Chamber chamber2 = floor.getChambers().get(1);
        assertEquals(3, chamber2.getSize());
        assertEquals(FieldCategory.WASSER, chamber2.getFieldCategory());
        assertEquals(1, chamber2.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber2.getRewards().get(0).getCategory());
        assertEquals(3, chamber2.getRewards().get(0).getNumberRockets());
    }

    @Test
    void testCreateFourthFloor() {
        Floor floor = gameBoard.getFloorAtIndex(3);
        assertEquals(FieldCategory.ROBOTER, floor.getFieldCategory());
        assertEquals(2, floor.getChambers().size());

        Chamber chamber1 = floor.getChambers().get(0);
        assertEquals(2, chamber1.getSize());
        assertEquals(FieldCategory.ROBOTER, chamber1.getFieldCategory());
        assertEquals(1, chamber1.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber1.getRewards().get(0).getCategory());
        assertEquals(2, chamber1.getRewards().get(0).getNumberRockets());

        Chamber chamber2 = floor.getChambers().get(1);
        assertEquals(3, chamber2.getSize());
        assertEquals(FieldCategory.ROBOTER, chamber2.getFieldCategory());
        assertEquals(1, chamber2.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber2.getRewards().get(0).getCategory());
        assertEquals(3, chamber2.getRewards().get(0).getNumberRockets());
    }

    @Test
    void testCreateFifthFloor() {
        Floor floor = gameBoard.getFloorAtIndex(4);
        assertEquals(FieldCategory.ROBOTER, floor.getFieldCategory());
        assertEquals(1, floor.getChambers().size());

        Chamber chamber = floor.getChambers().get(0);
        assertEquals(5, chamber.getSize());
        assertEquals(FieldCategory.ROBOTER, chamber.getFieldCategory());
        assertEquals(3, chamber.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber.getRewards().get(0).getCategory());
        assertEquals(3, chamber.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber.getRewards().get(1).getCategory());
        assertEquals(RewardCategory.SYSTEMERROR, chamber.getRewards().get(2).getCategory());
    }

    @Test
    void testCreateSixthFloor() {
        Floor floor = gameBoard.getFloorAtIndex(5);
        assertEquals(FieldCategory.PLANUNG, floor.getFieldCategory());
        assertEquals(1, floor.getChambers().size());

        Chamber chamber = floor.getChambers().get(0);
        assertEquals(5, chamber.getSize());
        assertEquals(FieldCategory.PLANUNG, chamber.getFieldCategory());
        assertEquals(2, chamber.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber.getRewards().get(0).getCategory());
        assertEquals(8, chamber.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber.getRewards().get(1).getCategory());
    }

    @Test
    void testCreateSeventhFloor() {
        Floor floor = gameBoard.getFloorAtIndex(6);
        assertEquals(FieldCategory.ENERGIE, floor.getFieldCategory());
        assertEquals(3, floor.getChambers().size());

        Chamber chamber1 = floor.getChambers().get(0);
        assertEquals(5, chamber1.getSize());
        assertEquals(FieldCategory.ENERGIE, chamber1.getFieldCategory());
        assertEquals(3, chamber1.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber1.getRewards().get(0).getCategory());
        assertEquals(4, chamber1.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber1.getRewards().get(1).getCategory());
        assertEquals(RewardCategory.SYSTEMERROR, chamber1.getRewards().get(2).getCategory());

        Chamber chamber2 = floor.getChambers().get(1);
        assertEquals(2, chamber2.getSize());
        assertEquals(FieldCategory.ENERGIE, chamber2.getFieldCategory());
        assertEquals(1, chamber2.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber2.getRewards().get(0).getCategory());
        assertEquals(3, chamber2.getRewards().get(0).getNumberRockets());

        Chamber chamber3 = floor.getChambers().get(2);
        assertEquals(3, chamber3.getSize());
        assertEquals(FieldCategory.ENERGIE, chamber3.getFieldCategory());
        assertEquals(1, chamber3.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber3.getRewards().get(0).getCategory());
        assertEquals(2, chamber3.getRewards().get(0).getNumberRockets());
    }

    @Test
    void testCreateEighthFloor() {
        Floor floor = gameBoard.getFloorAtIndex(7);
        assertEquals(FieldCategory.PFLANZE, floor.getFieldCategory());
        assertEquals(4, floor.getChambers().size());

        Chamber chamber1 = floor.getChambers().get(0);
        assertEquals(2, chamber1.getSize());
        assertEquals(FieldCategory.PFLANZE, chamber1.getFieldCategory());
        assertEquals(1, chamber1.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber1.getRewards().get(0).getCategory());
        assertEquals(4, chamber1.getRewards().get(0).getNumberRockets());

        Chamber chamber2 = floor.getChambers().get(1);
        assertEquals(2, chamber2.getSize());
        assertEquals(FieldCategory.PFLANZE, chamber2.getFieldCategory());
        assertEquals(1, chamber2.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber2.getRewards().get(0).getCategory());
        assertEquals(2, chamber2.getRewards().get(0).getNumberRockets());

        Chamber chamber3 = floor.getChambers().get(2);
        assertEquals(2, chamber3.getSize());
        assertEquals(FieldCategory.PFLANZE, chamber3.getFieldCategory());
        assertEquals(1, chamber3.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber3.getRewards().get(0).getCategory());
        assertEquals(4, chamber3.getRewards().get(0).getNumberRockets());

        Chamber chamber4 = floor.getChambers().get(3);
        assertEquals(2, chamber4.getSize());
        assertEquals(FieldCategory.PFLANZE, chamber4.getFieldCategory());
        assertEquals(2, chamber4.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber4.getRewards().get(0).getCategory());
        assertEquals(1, chamber4.getRewards().get(0).getNumberRockets());
        assertEquals(RewardCategory.SYSTEMERROR, chamber4.getRewards().get(1).getCategory());
    }

    @Test
    void testCreateNinthFloor() {
        Floor floor = gameBoard.getFloorAtIndex(8);
        assertEquals(FieldCategory.ANYTHING, floor.getFieldCategory());
        assertEquals(4, floor.getChambers().size());

        Chamber chamber1 = floor.getChambers().get(0);
        assertEquals(2, chamber1.getSize());
        assertEquals(FieldCategory.ANYTHING, chamber1.getFieldCategory());
        assertEquals(1, chamber1.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber1.getRewards().get(0).getCategory());
        assertEquals(2, chamber1.getRewards().get(0).getNumberRockets());

        Chamber chamber2 = floor.getChambers().get(1);
        assertEquals(2, chamber2.getSize());
        assertEquals(FieldCategory.ANYTHING, chamber2.getFieldCategory());
        assertEquals(1, chamber2.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber2.getRewards().get(0).getCategory());
        assertEquals(2, chamber2.getRewards().get(0).getNumberRockets());

        Chamber chamber3 = floor.getChambers().get(2);
        assertEquals(2, chamber3.getSize());
        assertEquals(FieldCategory.ANYTHING, chamber3.getFieldCategory());
        assertEquals(1, chamber3.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber3.getRewards().get(0).getCategory());
        assertEquals(2, chamber3.getRewards().get(0).getNumberRockets());

        Chamber chamber4 = floor.getChambers().get(3);
        assertEquals(2, chamber4.getSize());
        assertEquals(FieldCategory.ANYTHING, chamber4.getFieldCategory());
        assertEquals(1, chamber4.getRewards().size());
        assertEquals(RewardCategory.ROCKET, chamber4.getRewards().get(0).getCategory());
        assertEquals(2, chamber4.getRewards().get(0).getNumberRockets());
    }


    @Test
    void testCreateGameBoard() {
        assertEquals(9, gameBoard.getFloors().size());
        // Verify each floor
        assertEquals(FieldCategory.RAUMANZUG, gameBoard.getFloors().get(0).getFieldCategory());
        assertEquals(FieldCategory.RAUMANZUG, gameBoard.getFloors().get(1).getFieldCategory());
        assertEquals(FieldCategory.WASSER, gameBoard.getFloors().get(2).getFieldCategory());
        assertEquals(FieldCategory.ROBOTER, gameBoard.getFloors().get(3).getFieldCategory());
        assertEquals(FieldCategory.ROBOTER, gameBoard.getFloors().get(4).getFieldCategory());
        assertEquals(FieldCategory.PLANUNG, gameBoard.getFloors().get(5).getFieldCategory());
        assertEquals(FieldCategory.ENERGIE, gameBoard.getFloors().get(6).getFieldCategory());
        assertEquals(FieldCategory.PFLANZE, gameBoard.getFloors().get(7).getFieldCategory());
        assertEquals(FieldCategory.ANYTHING, gameBoard.getFloors().get(8).getFieldCategory());
    }
}

