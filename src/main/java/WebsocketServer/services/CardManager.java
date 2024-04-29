package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.services.CardController;


import WebsocketServer.services.json.GenerateJSONObjectService;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;
import java.util.List;

public class CardManager {

    private final CardController cardController;
    private final Lobby gameLobby;

    public CardManager(Lobby gameLobby) {
        this.gameLobby = gameLobby;
        this.cardController=new CardController();
    }
    public void drawAndSendNextCard(WebSocketSession session){
        cardController.drawNextCard();
        handleSendCardDraw(session);
    }

    private void handleSendCardDraw(WebSocketSession session){
        List<String> userListFromLobby=this.gameLobby.getUserListFromLobby();
        String cardData= CardController.getCurrentCardMessage(this.cardController.currentCombinations);

            for(String user: userListFromLobby){
                JSONObject object= GenerateJSONObjectService.generateJSONObject("getNextCard",user,true,cardData,"");
                SendMessageService.sendSingleMessage(session,object);
            }
    }
}
