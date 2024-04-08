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
    @Test
    public void testDrawCorrectly() {
        cardController.drawNextCard();
        CardCombination[] lastCombination = cardController.currentCombinations;
        cardController.drawNextCard();
        assertEquals(lastCombination[0].nextSymbol,cardController.currentCombinations[0].currentSymbol);
        assertEquals(lastCombination[1].nextSymbol,cardController.currentCombinations[1].currentSymbol);
        assertEquals(lastCombination[2].nextSymbol,cardController.currentCombinations[2].currentSymbol);
    }
    @Test
    public void testShuffleCardsCorrectly() {
        cardController.currentPosition=18;
        cardController.drawNextCard();
        CardCombination[] lastCombination = new CardCombination[3];
        for (int i = 0; i < 3; i++) {
            lastCombination[i] = new CardCombination(cardController.currentCombinations[i].card1,cardController.currentCombinations[i].card2);
        }
        cardController.drawNextCard();
        assertEquals(0,cardController.currentPosition);
        assertEquals(lastCombination[0].nextSymbol,cardController.currentCombinations[0].currentSymbol);
        assertEquals(lastCombination[1].nextSymbol,cardController.currentCombinations[1].currentSymbol);
        assertEquals(lastCombination[2].nextSymbol,cardController.currentCombinations[2].currentSymbol);
    }
}