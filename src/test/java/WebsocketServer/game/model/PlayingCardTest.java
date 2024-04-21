package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayingCardTest {
    @Test
    void testGetSymbol() {
        PlayingCard card = new PlayingCard(FieldCategory.ENERGIE, 3);
        assertEquals(FieldCategory.ENERGIE, card.getSymbol());
    }

    @Test
    void testGetNumber() {
        PlayingCard card = new PlayingCard(FieldCategory.PFLANZE, 8);
        assertEquals(8, card.getNumber());
    }
}
