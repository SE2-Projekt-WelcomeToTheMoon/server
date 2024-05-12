package WebsocketServer.services;

import WebsocketServer.game.model.CardCombination;
import WebsocketServer.services.user.CreateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardManagerTest {
    private CardManager cardManager;
    @Mock
    private CreateUserService createUserService;

    @Mock
    private WebSocketSession session;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cardManager = new CardManager();

        /*
        CardCombination[] combinations={
                new CardCombination(new PlayingCard(FieldCategory.PLANUNG,1),new PlayingCard(FieldCategory.PFLANZE,2)),
                new CardCombination(new PlayingCard(FieldCategory.PFLANZE,3),new PlayingCard(FieldCategory.ROBOTER,4)),
                new CardCombination(new PlayingCard(FieldCategory.ENERGIE,5),new PlayingCard(FieldCategory.RAUMANZUG,6))};


        when(cardController.getCurrentCombinations()).thenReturn(combinations);
        */

        when(createUserService.getSession()).thenReturn(session);
        when(createUserService.getUsername()).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    void shouldReturnFalseWhenUsersAreIsNull(){
        assertFalse(cardManager.sendCurrentCardsToPlayers(null));
    }

    @Test
    void getCurrentCombinationDoesNotThrowError(){
        assertDoesNotThrow(()->cardManager.getCurrentCombination());
    }
    @Test
    void drawNextCardWorking(){
        CardCombination[] currentCombination=cardManager.getCurrentCombination().clone();
        cardManager.drawNextCard();
        assertNotEquals(currentCombination,cardManager.getCurrentCombination());
    }

}
