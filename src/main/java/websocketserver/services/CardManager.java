package websocketserver.services;

import lombok.Setter;
import websocketserver.game.model.CardCombination;
import websocketserver.game.services.CardController;
import websocketserver.services.json.GenerateJSONObjectService;
import websocketserver.services.user.CreateUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardManager {

    private final CardController cardController;

    @Setter
    private Logger logger = LogManager.getLogger(CardManager.class);

    public CardManager() {

        this.cardController = new CardController();
    }

    public void drawNextCard() {
        cardController.drawNextCard();

    }

    public CardCombination[] getCurrentCombination() {
        return cardController.getCurrentCombinations();
    }

    public boolean sendCurrentCardsToPlayers(List<CreateUserService> players) {
        if (players == null || players.isEmpty()) {
            logger.error("User list is empty");
            return false;
        }

        String cardData = CardController.getCurrentCardMessage(this.cardController.getCurrentCombinations());
        for (CreateUserService player : players) {
            if (player == null) {
                logger.warn("Player is null");
                return false;
            }
            JSONObject jsonObject = new GenerateJSONObjectService("nextCardDraw", player.getUsername(), true, cardData, "").generateJSONObject();
            SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
            logger.info("Sent next card draw to player: {}", player.getUsername());
        }
        return true;
    }

    public void updateUserAboutCurrentCards(CreateUserService player){
        if (player == null) {
            logger.error("Player is null");
            return;
        }
        String cardData = CardController.getCurrentCardMessage(this.cardController.getCurrentCombinations());
        JSONObject jsonObject = new GenerateJSONObjectService("nextCardDraw", player.getUsername(), true, cardData, "").generateJSONObject();
        SendMessageService.sendSingleMessage(player.getSession(), jsonObject);
        logger.info("Sending current card draw to player: {}", player.getUsername());

    }

}
