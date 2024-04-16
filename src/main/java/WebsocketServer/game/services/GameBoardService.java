package WebsocketServer.game.services;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameBoardService {
    public GameBoard createGameBoard() {
        GameBoard gameBoard = new GameBoard();

        gameBoard.addFloor(createFloor(FieldCategory.RAUMANZUG, List.of(3)));
        gameBoard.addFloor(createFloor(FieldCategory.RAUMANZUG, List.of(3)));
        gameBoard.addFloor(createFloor(FieldCategory.WASSER, List.of(2, 3)));
        gameBoard.addFloor(createFloor(FieldCategory.ROBOTER, List.of(2, 3)));
        gameBoard.addFloor(createFloor(FieldCategory.ROBOTER, List.of(5)));
        gameBoard.addFloor(createFloor(FieldCategory.PLANUNG, List.of(6)));
        gameBoard.addFloor(createFloor(FieldCategory.ENERGIE, List.of(5, 2, 3)));
        gameBoard.addFloor(createFloor(FieldCategory.PFLANZE, List.of(2, 2, 2, 2)));
        gameBoard.addFloor(createFloor(FieldCategory.ANYTHING, List.of(2, 2, 2, 2)));

        gameBoard.finalizeGameBoard();

        return gameBoard;
    }

    //TODO ADD Rewards
    private Floor createFloor(FieldCategory fieldCategory, List<Integer> chambers) {
        Floor floor = new Floor(fieldCategory);

        for (Integer chamberSize : chambers) {
            floor.addChamber(createChamber(fieldCategory, chamberSize));
        }

        return floor;
    }

    //TODO Add rewards
    private Chamber createChamber(FieldCategory fieldCategory, Integer chamberSize) {
        Chamber chamber = new Chamber(fieldCategory);

        for (int i = 0; i < chamberSize; i++) {
            chamber.addField(new Field(fieldCategory));
        }

        return chamber;
    }
}
