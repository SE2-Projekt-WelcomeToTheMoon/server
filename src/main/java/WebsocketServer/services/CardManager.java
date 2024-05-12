package WebsocketServer.services;

import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.services.CardController;
import WebsocketServer.services.json.GenerateJSONObjectService;
import WebsocketServer.services.user.CreateUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class CardManager {

    private final CardController cardController;
    private final WebSocketSession session;
    private final Logger logger = LogManager.getLogger(CardManager.class);

    public CardManager(WebSocketSession session) {
        this.session = session;
        this.cardController=new CardController();
    }
    public void drawNextCard(){
        cardController.drawNextCard();

    }

    public CardCombination[] getCurrentCombination(){
        return cardController.currentCombinations;
    }

    public boolean sendCurrentCardsToPlayers(List<CreateUserService> players){
        if (players == null || players.isEmpty()){
            logger.error("User list is empty");
            return false;
        }

        String cardData= CardController.getCurrentCardMessage(this.cardController.currentCombinations);
        for (CreateUserService player : players) {
            if (player == null){
                logger.warn("Player is null");
                return false;
            }
            JSONObject jsonObject = GenerateJSONObjectService.generateJSONObject("nextCardDraw", player.getUsername(), true,cardData , "");
            SendMessageService.sendSingleMessage(this.session, jsonObject);
        }
        return true;
    }


}
