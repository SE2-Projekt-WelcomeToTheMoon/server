package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.Chamber;
import WebsocketServer.game.model.Field;
import WebsocketServer.game.model.Floor;
import WebsocketServer.game.model.GameBoard;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import WebsocketServer.game.services.GameBoardService;

import java.io.IOException;
import java.util.List;

public class GameBoardManager {

    private GameBoardService gameBoardService;
    private final GameBoard gameBoard;
    private final Lobby gamelobby;
    private static final Logger logger = LoggerFactory.getLogger(GameBoardManager.class);

    public GameBoardManager(Lobby gameLobby){
        this.gamelobby = gameLobby;
        this.gameBoard = gameBoardService.createGameBoard();
    }

    /**
     * temp javadoc
     *
     * makes JSON arrays and objects
     * basically nested json for easy parsing
     *
     * @return
     */
    private String getGameBoardAsString() {
        JSONObject gameBoardJson = new JSONObject();
        JSONArray floorsJson = new JSONArray();

        List<Floor> floors = gameBoard.getFloors();
        for (int i = 0; i < gameBoard.getSize(); i++) {
            Floor floor = floors.get(i);
            JSONObject floorJson = new JSONObject();
            floorJson.put("floorIndex", i);
            floorJson.put("floorCategory", floor.getFieldCategory());
            floorJson.put("totalChambers", floor.getSize());

            JSONArray chambersJson = new JSONArray();
            List<Chamber> chambers = floor.getChambers();
            for (int j = 0; j < floor.getSize(); j++){
                Chamber chamber = chambers.get(j);
                JSONObject chamberJson = new JSONObject();
                chamberJson.put("chamberIndex", j);
                chamberJson.put("chamberSize", chamber.getSize());

                JSONArray fieldsJson = new JSONArray();
                List<Field> fields = chamber.getFields();
                for (int k = 0; k < chamber.getSize(); k++){
                    Field field = fields.get(k);
                    JSONObject fieldJson = new JSONObject();

                    fieldJson.put("fieldIndex", k);
                    fieldJson.put("fieldValue", field.getFieldValue());
                    fieldsJson.put(fieldJson);
                }
                chambersJson.put(chamberJson);
            }
            floorsJson.put(floorJson);
        }

        gameBoardJson.put("floors", floorsJson);

        return gameBoardJson.toString();
    }

    public void handleProvideGameBoardInfo(WebSocketSession session) {
        List<String> userListFromLobby = this.gamelobby.getUserListFromLobby();
        String gameboardData = getGameBoardAsString();

        try {
            for (String user : userListFromLobby) {
                JSONObject object = GenerateJSONObjectService.generateJSONObject("initGameBoard", user, true, gameboardData , "");

                session.sendMessage(new TextMessage(object.toString()));
            }
        } catch (IOException e) {
            logger.error("Error sending message to user: {}", e.getMessage());
        }
    }
}