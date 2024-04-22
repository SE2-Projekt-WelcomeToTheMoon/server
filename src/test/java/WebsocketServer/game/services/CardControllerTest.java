package WebsocketServer.game.services;

import WebsocketServer.game.enums.FieldCategory;
import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.model.PlayingCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardControllerTest {

    private CardController cardController;

    @BeforeEach
    void setUp() {
        cardController = new CardController();
    }

    @Test
    void testGetCombinationAtPosition() {
        CardCombination[] combinations = cardController.getCombinationAtPosition(0);
        assertNotNull(combinations);
        assertEquals(3, combinations.length);
        assertNotNull(combinations[0]);
        assertNotNull(combinations[1]);
        assertNotNull(combinations[2]);
    }

    @Test
    void testGetCombinationAtInvalidPosition() {
        assertThrows(IllegalArgumentException.class, () -> cardController.getCombinationAtPosition(22));
    }

    @Test
    void testDrawNextCard() {
        int initialPosition = cardController.currentPosition;
        cardController.drawNextCard();
        assertEquals(initialPosition + 1, cardController.currentPosition);
    }

    @Test
    void testDrawNextCardShuffle() {
        // Set current position to 20 to trigger shuffle
        cardController.currentPosition = 19;
        cardController.drawNextCard();
        assertEquals(0, cardController.currentPosition);
    }

    @Test
    void testGetLastCardCombination() {
        // Assuming there are past combinations
        cardController.drawNextCard();
        CardCombination[] lastCombination = cardController.getLastCardCombination();
        assertNotNull(lastCombination);
    }

    @Test
    void testGetPastCombinations() {
        // Assuming there are past combinations
        cardController.drawNextCard();
        assertNotNull(cardController.getPastCombinations());
    }
    @Test
    void testDrawCorrectly() {
        cardController.drawNextCard();
        CardCombination[] lastCombination = cardController.currentCombinations;
        cardController.drawNextCard();
        assertEquals(lastCombination[0].getNextSymbol(), cardController.currentCombinations[0].getCurrentSymbol());
        assertEquals(lastCombination[1].getNextSymbol(), cardController.currentCombinations[1].getCurrentSymbol());
        assertEquals(lastCombination[2].getNextSymbol(), cardController.currentCombinations[2].getCurrentSymbol());
    }
    @Test
    void testShuffleCardsCorrectly() {
        cardController.currentPosition=18;
        cardController.drawNextCard();
        CardCombination[] lastCombination = new CardCombination[3];
        for (int i = 0; i < 3; i++) {
            lastCombination[i] = new CardCombination(cardController.currentCombinations[i].getCard1(), cardController.currentCombinations[i].getCard2());
        }
        cardController.drawNextCard();
        assertEquals(0,cardController.currentPosition);
        assertEquals(lastCombination[0].getNextSymbol(), cardController.currentCombinations[0].getCurrentSymbol());
        assertEquals(lastCombination[1].getNextSymbol(), cardController.currentCombinations[1].getCurrentSymbol());
        assertEquals(lastCombination[2].getNextSymbol(), cardController.currentCombinations[2].getCurrentSymbol());
    }
    @Test
    void getCurrentCardMessagevalid(){
        cardController.currentCombinations[0]=new CardCombination(new PlayingCard(FieldCategory.PLANUNG,0),new PlayingCard(FieldCategory.PFLANZE,0));
        cardController.currentCombinations[1]=new CardCombination(new PlayingCard(FieldCategory.PFLANZE,3),new PlayingCard(FieldCategory.ROBOTER,3));
        cardController.currentCombinations[2]=new CardCombination(new PlayingCard(FieldCategory.ENERGIE,5),new PlayingCard(FieldCategory.RAUMANZUG,5));
        //CombinationNumber-CurrentSymbol-CurrentNumber-NextSymbol;
        String expectedResponse="0-PLANUNG-0-PFLANZE;1-PFLANZE-3-ROBOTER;2-ENERGIE-5-RAUMANZUG;";
        assertEquals(expectedResponse, CardController.getCurrentCardMessage(cardController.currentCombinations));
    }
    @Test
    void getCurrentCardMessageIncorrectCombinationCount(){

        CardCombination[] combinationsInvalidSize=new CardCombination[2];
        assertThrows(IllegalArgumentException.class,()->CardController.getCurrentCardMessage(null));
        assertThrows(IllegalArgumentException.class,()->CardController.getCurrentCardMessage(combinationsInvalidSize));
    }
}