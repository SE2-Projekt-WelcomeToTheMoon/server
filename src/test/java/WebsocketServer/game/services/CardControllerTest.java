package WebsocketServer.game.services;

import WebsocketServer.game.model.CardCombination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardControllerTest {

    private CardController cardController;

    @BeforeEach
    public void setUp() {
        cardController = new CardController();
    }

    @Test
    public void testGetCombinationAtPosition() {
        CardCombination[] combinations = cardController.getCombinationAtPosition(0);
        assertNotNull(combinations);
        assertEquals(3, combinations.length);
        assertNotNull(combinations[0]);
        assertNotNull(combinations[1]);
        assertNotNull(combinations[2]);
    }

    @Test
    public void testGetCombinationAtInvalidPosition() {
        assertThrows(IllegalArgumentException.class, () -> cardController.getCombinationAtPosition(22));
    }

    @Test
    public void testDrawNextCard() {
        int initialPosition = cardController.currentPosition;
        cardController.drawNextCard();
        assertEquals(initialPosition + 1, cardController.currentPosition);
    }

    @Test
    public void testDrawNextCardShuffle() {
        // Set current position to 20 to trigger shuffle
        cardController.currentPosition = 19;
        cardController.drawNextCard();
        assertEquals(0, cardController.currentPosition);
    }

    @Test
    public void testGetLastCardCombination() {
        // Assuming there are past combinations
        cardController.drawNextCard();
        CardCombination[] lastCombination = cardController.getLastCardCombination();
        assertNotNull(lastCombination);
    }

    @Test
    public void testGetPastCombinations() {
        // Assuming there are past combinations
        cardController.drawNextCard();
        assertNotNull(cardController.getPastCombinations());
    }
}