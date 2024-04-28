package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.services.CardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardManager {

    private CardController cardController;
    private final Lobby gameLobby;
    private static final Logger logger= LoggerFactory.getLogger(CardManager.class);

    public CardManager(Lobby gameLobby) {
        this.gameLobby = gameLobby;
        this.cardController=new CardController();
    }

}
