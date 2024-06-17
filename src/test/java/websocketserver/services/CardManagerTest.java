package websocketserver.services;

import websocketserver.game.enums.FieldCategory;
import websocketserver.game.model.CardCombination;
import websocketserver.game.model.PlayingCard;
import websocketserver.game.services.CardController;
import websocketserver.services.user.CreateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardManagerTest {
    private CardManager cardManager;
    @Mock
    private CreateUserService createUserService;
    @Mock
    private WebSocketSession session;
    @Mock
    private CardController cardController;
    @Mock
    private Logger mockLogger;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);
        mockLogger = mock(Logger.class);
        cardManager = new CardManager();
        cardManager.setLogger(mockLogger);


        CardCombination[] combinations = new CardCombination[]{
                new CardCombination(new PlayingCard(FieldCategory.PLANUNG, 1), new PlayingCard(FieldCategory.PFLANZE, 2)),
                new CardCombination(new PlayingCard(FieldCategory.PFLANZE, 3), new PlayingCard(FieldCategory.ROBOTER, 4)),
                new CardCombination(new PlayingCard(FieldCategory.ENERGIE, 5), new PlayingCard(FieldCategory.RAUMANZUG, 6))};


        Field field = cardManager.getClass().getDeclaredField("cardController");
        field.setAccessible(true);
        field.set(cardManager, cardController);
        
        when(cardController.getCurrentCombinations()).thenReturn(combinations);
        when(createUserService.getSession()).thenReturn(session);
        when(createUserService.getUsername()).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    void shouldReturnFalseWhenUsersAreIsNull() {
        assertFalse(cardManager.sendCurrentCardsToPlayers(null));
    }

    @Test
    void getCurrentCombinationDoesNotThrowError() {
        assertDoesNotThrow(() -> cardManager.getCurrentCombination());
    }

    @Test
    void drawNextCardWorking() {
        CardCombination[] currentCombination = cardManager.getCurrentCombination().clone();
        cardManager.drawNextCard();
        assertNotEquals(currentCombination, cardManager.getCurrentCombination());
    }

    @Test
    void testSendCurrentCardsToPlayers() {

        List<CreateUserService> players = new ArrayList<>();
        players.add(createUserService);
        boolean result = cardManager.sendCurrentCardsToPlayers(players);
        assertTrue(result);
        verify(cardController, times(1)).getCurrentCombinations();
    }

    @Test
    void testSendCurrentCardsToPlayersWhenPlayersAreNull() {
        List<CreateUserService> players = new ArrayList<>();
        players.add(null);

        boolean result = cardManager.sendCurrentCardsToPlayers(players);
        assertFalse(result);
    }

    @Test
    void testUpdateUserAboutCurrentCardsNULL() {
        cardManager.updateUserAboutCurrentCards(null);
        verify(mockLogger).error("Player is null");
    }

    @Test
    void testUpdateUserAboutCurrentCards() {
        cardManager.updateUserAboutCurrentCards(createUserService);
        verify(mockLogger).info("Sending current card draw to player: {}", createUserService.getUsername());
    }

}
