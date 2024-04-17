package WebsocketServer.game.services;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.enums.RewardCategory;
import WebsocketServer.game.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameBoardService {
    public GameBoard createGameBoard() {
        GameBoard gameBoard = new GameBoard();
        //TOP FLOOR RAUMANZUG, ONE CHAMBER SIZE 3, rewards 3 Rockets, one filling and a planning
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,3), new Reward(RewardCategory.ROCKETFILLING),new Reward(RewardCategory.PLANING));
        gameBoard.addFloor(createFloor(FieldCategory.RAUMANZUG, List.of(new Chamber(FieldCategory.RAUMANZUG,rewards,3))));

        //SECOND FLOOR RAUMANZUG, ONE CHAMBER SIZE 3, rewards 2 rockets filled, 2 unfilled and a systemerror
        rewards= List.of(new Reward(RewardCategory.ROCKET,2), new Reward(RewardCategory.UNFILLEDROCKET,2),new Reward(RewardCategory.SYSTEMERROR));
        gameBoard.addFloor(createFloor(FieldCategory.RAUMANZUG, List.of(new Chamber(FieldCategory.RAUMANZUG,rewards,3))));

        //THIRD FLOOR WASSER, TWO CHAMBERS SIZE 2-3, first rewards 2 rockets and systemerror, second 3 rockets filled 3 unfilled
        List<Chamber> chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.WASSER,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3),new Reward(RewardCategory.UNFILLEDROCKET,3));
        chambers.add(new Chamber(FieldCategory.WASSER,rewards,3));
        gameBoard.addFloor(createFloor(FieldCategory.WASSER, chambers));


        //FOURTH FLOOR ROBOTER, 2 CHAMBERS SIZE 2-3, first rewards filling and planing, second 3 rockets and planing
        chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKETFILLING),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,3));
        gameBoard.addFloor(createFloor(FieldCategory.ROBOTER, chambers));

        //FIFTH FLOOR ROBOTER, ONE CHAMBER SIZE 5, rewards 3 rockets filled, 3 unfilled, two systemerrors
        rewards= List.of(new Reward(RewardCategory.ROCKET,3),new Reward(RewardCategory.UNFILLEDROCKET,3),new Reward(RewardCategory.SYSTEMERROR),new Reward(RewardCategory.SYSTEMERROR));
        chambers=new ArrayList<>();
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,5));
        gameBoard.addFloor(createFloor(FieldCategory.ROBOTER, chambers));

        //SIXTH FLOOR PLANNING, ONE CHAMBER SIZE 5, rewards 4 rockets filled, 4 unfilled, planning and systemerror
        chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKET,4),new Reward(RewardCategory.UNFILLEDROCKET,4),new Reward(RewardCategory.PLANING),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.PLANUNG,rewards,5));
        gameBoard.addFloor(createFloor(FieldCategory.PLANUNG, chambers));

        //SEVENTH FLOOR ENERGY, 3 CHAMBERS SIZE 5-2-3, first rewards 4 rockets filled, 4 unfilled and 2 systemerrors, second 3 rockets and third 2 rockets, filling and planing
        chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKET,4),new Reward(RewardCategory.UNFILLEDROCKET,4),new Reward(RewardCategory.SYSTEMERROR),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,5));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.ROCKETFILLING), new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,3));
        gameBoard.addFloor(createFloor(FieldCategory.ENERGIE, chambers));

        //EIGHT FLOOR PLANT, 4 CHAMBERS SIZE 2-2-2-2, first rewards 2 rockets filled 2 unfilled, second 2 rockets filled and filling, third 2 rockets filled 2 unfilled and fourth planing and systemerror
        chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.UNFILLEDROCKET,2));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2),new Reward((RewardCategory.ROCKETFILLING)));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.UNFILLEDROCKET,2));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.PLANING),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        gameBoard.addFloor(createFloor(FieldCategory.PFLANZE, chambers));

        //NINTH FLOOR ANYTHING, 4 CHAMBERS 2-2-2-2, first through third reward 2 rockets and planing, fourth filling and planing
        chambers=new ArrayList<>();
        rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKETFILLING),new Reward(RewardCategory.PLANING));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        gameBoard.addFloor(createFloor(FieldCategory.PFLANZE, chambers));

        gameBoard.finalizeGameBoard();

        return gameBoard;
    }

    //TODO ADD Rewards
    private Floor createFloor(FieldCategory fieldCategory, List<Chamber> chambers) {
        Floor floor = new Floor(fieldCategory);

        for (Chamber chamber : chambers) {
            floor.addChamber(chamber);
        }

        return floor;
    }
}
