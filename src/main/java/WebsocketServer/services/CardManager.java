package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.services.CardController;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public class CardManager {

    private final CardController cardController;
    private final Lobby gameLobby;
    private static final Logger logger= LoggerFactory.getLogger(CardManager.class);

    public CardManager(Lobby gameLobby) {
        this.gameLobby = gameLobby;
        this.cardController=new CardController();
    }
    public void drawNextCard(WebSocketSession session){
        cardController.drawNextCard();
        handleSendCardDraw(session);
    }
    private void handleSendCardDraw(WebSocketSession session){
        List<String> userListFromLobby=this.gameLobby.getUserListFromLobby();
        String cardData=this.cardController.getCurrentCardMessage(this.cardController.currentCombinations);
        try {
            for(String user: userListFromLobby){
                JSONObject object=GenerateJSONObjectService.generateJSONObject("getNextCard",user,true,cardData,"");
                session.sendMessage(new TextMessage(object.toString()));
            }
        } catch (IOException e) {
            logger.error("Error sending message to User: {}",e.getMessage());
        }
    }
}
