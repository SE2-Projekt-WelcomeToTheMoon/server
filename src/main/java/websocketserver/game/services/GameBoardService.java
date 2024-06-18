package websocketserver.game.services;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.enums.RewardCategory;
import websocketserver.game.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameBoardService {
    public GameBoard createGameBoard() {
        GameBoard gameBoard = new GameBoard();

        gameBoard.addFloor(createFirstFloor());
        gameBoard.addFloor(createSecondFloor());
        gameBoard.addFloor(createThirdFloor());
        gameBoard.addFloor(createFourthFloor());
        gameBoard.addFloor(createFifthFloor());
        gameBoard.addFloor(createSixthFloor());
        gameBoard.addFloor(createSeventhFloor());
        gameBoard.addFloor(createEigthFloor());
        gameBoard.addFloor(createNinthFloor());
        gameBoard.finalizeGameBoard();

        return gameBoard;
    }




    private Floor createFloor(FieldCategory fieldCategory, List<Chamber> chambers) {
        Floor floor = new Floor(fieldCategory);

        for (Chamber chamber : chambers) {
            floor.addChamber(chamber);
        }

        return floor;
    }

    /***
     * TOP FLOOR RAUMANZUG, ONE CHAMBER SIZE 3, rewards 3 Rockets
     * @return the created floor
     */
    private Floor createFirstFloor(){
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,3));
        return createFloor(FieldCategory.RAUMANZUG, List.of(new Chamber(FieldCategory.RAUMANZUG,rewards,3)));
    }

    /***
     *  SECOND FLOOR RAUMANZUG, ONE CHAMBER SIZE 3, rewards 4 rockets and a systemerror
     * @return the second floor
     */
    private Floor createSecondFloor(){
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,4),new Reward(RewardCategory.SYSTEMERROR));
        return createFloor(FieldCategory.RAUMANZUG, List.of(new Chamber(FieldCategory.RAUMANZUG,rewards,3)));
    }

    /***
     * THIRD FLOOR WASSER, TWO CHAMBERS SIZE 2-3, first rewards 2 rockets and systemerror, second 6 rockets
     * @return the created floor
     */
    private Floor createThirdFloor(){
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,2),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.WASSER,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3));
        chambers.add(new Chamber(FieldCategory.WASSER,rewards,3));
        return createFloor(FieldCategory.WASSER, chambers);
    }

    /***
     * FOURTH FLOOR ROBOTER, 2 CHAMBERS SIZE 2-3, first rewards 2 Rockets, second 3 rockets
     * @return the created floor
     */
    private Floor createFourthFloor() {
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3));
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,3));
        return createFloor(FieldCategory.ROBOTER, chambers);
    }
    /***
     * FIFTH FLOOR ROBOTER, ONE CHAMBER SIZE 5, rewards 3 rockets and two systemerrors
     * @return the created floor
     */
    private Floor createFifthFloor() {
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,3),new Reward(RewardCategory.SYSTEMERROR),new Reward(RewardCategory.SYSTEMERROR));
        List<Chamber> chambers=new ArrayList<>();
        chambers.add(new Chamber(FieldCategory.ROBOTER,rewards,5));
        return createFloor(FieldCategory.ROBOTER, chambers);
    }

    /***
     * SIXTH FLOOR PLANNING, ONE CHAMBER SIZE 5, rewards 8 rockets and systemerror
     * @return the created floor
     */
    private Floor createSixthFloor(){
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,8),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.PLANUNG,rewards,5));
        return createFloor(FieldCategory.PLANUNG, chambers);


    }

    /***
     *  SEVENTH FLOOR ENERGY, 3 CHAMBERS SIZE 5-2-3, first rewards 4 rockets filled and 2 systemerrors, second 3 rockets and third 2 rockets
     *  @return the created floor
     */
    private Floor createSeventhFloor(){
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,4),new Reward(RewardCategory.SYSTEMERROR),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,5));
        rewards=List.of(new Reward(RewardCategory.ROCKET,3));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ENERGIE,rewards,3));
        return createFloor(FieldCategory.ENERGIE, chambers);

    }

    /***
     * EIGHT FLOOR PLANT, 4 CHAMBERS SIZE 2-2-2-2, first rewards 4 Rockets, second 2 rockets, third 4 rockets and fourth 1 Rocket and 1 SystemError
     * @return the created floor
     */
    private Floor createEigthFloor(){
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,4));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,4));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,1),new Reward(RewardCategory.SYSTEMERROR));
        chambers.add(new Chamber(FieldCategory.PFLANZE,rewards,2));
        return createFloor(FieldCategory.PFLANZE, chambers);
    }

    /***
     *  NINTH FLOOR ANYTHING, 4 CHAMBERS 2-2-2-2, first through third reward 2 rockets and planing, fourth filling and planing
     * @return the created floor
     */
    private Floor createNinthFloor(){
        List<Chamber> chambers=new ArrayList<>();
        List<Reward> rewards= List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards= List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards= List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        rewards=List.of(new Reward(RewardCategory.ROCKET,2));
        chambers.add(new Chamber(FieldCategory.ANYTHING,rewards,2));
        return createFloor(FieldCategory.ANYTHING, chambers);
    }
}
