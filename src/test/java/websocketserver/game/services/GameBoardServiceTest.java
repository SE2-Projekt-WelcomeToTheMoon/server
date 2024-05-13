package websocketserver.game.services;

import websocketserver.game.enums.FieldValue;
import websocketserver.game.model.Chamber;
import websocketserver.game.model.Field;
import websocketserver.game.model.Floor;
import websocketserver.game.model.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardServiceTest {
    private GameBoardService gameBoardService;

    @BeforeEach
    void setUp() {
        gameBoardService = new GameBoardService();
    }

    @Test
    void testSizeOfGameBoard() {
        GameBoard gameBoard = gameBoardService.createGameBoard();
        assertNotNull(gameBoard);
        assertEquals(9, gameBoard.getSize());
    }


    @Test
    void testAllChambersAndFieldsHaveSameCategoryWithinFloor() {
        GameBoard gameBoard = gameBoardService.createGameBoard();

        for(int i = 0; i < gameBoard.getSize(); i++){
            Floor currentFloor = gameBoard.getFloorAtIndex(i);
            for(int k = 0; k < currentFloor.getNumberOfChambers(); k++){
                Chamber currentChamber = currentFloor.getChamber(k);
                assertEquals(currentFloor.getFieldCategory(), currentChamber.getFieldCategory());
                for(int j = 0; i < currentChamber.getSize(); i++){
                    Field field = currentChamber.getField(j);
                    assertEquals(field.getFieldCategory(), currentFloor.getFieldCategory());
                    assertEquals(field.getFieldValue(), FieldValue.NONE);
                }
            }
        }

    }
}