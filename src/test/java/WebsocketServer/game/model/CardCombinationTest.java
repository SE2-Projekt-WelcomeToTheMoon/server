package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardCombinationTest {

    @Test
     void testGetCurrentSymbol() {
        PlayingCard card1 = new PlayingCard(FieldCategory.ROBOTER, 5);
        PlayingCard card2 = new PlayingCard(FieldCategory.PFLANZE, 8);
        CardCombination combination = new CardCombination(card1, card2);
        assertEquals(FieldCategory.ROBOTER, combination.getCurrentSymbol());
    }

    @Test
    void testGetNextSymbol() {
        PlayingCard card1 = new PlayingCard(FieldCategory.ROBOTER, 5);
        PlayingCard card2 = new PlayingCard(FieldCategory.PFLANZE, 8);
        CardCombination combination = new CardCombination(card1, card2);
        assertEquals(FieldCategory.PFLANZE, combination.getNextSymbol());
    }

    @Test
    void testGetCurrentNumber() {
        PlayingCard card1 = new PlayingCard(FieldCategory.ROBOTER, 5);
        PlayingCard card2 = new PlayingCard(FieldCategory.PFLANZE, 8);
        CardCombination combination = new CardCombination(card1, card2);
        assertEquals(8, combination.getCurrentNumber());
    }
}