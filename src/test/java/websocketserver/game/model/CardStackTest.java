package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CardStackTest {
    private CardStack cardStack=new CardStack();

    @BeforeEach
    void setUp() {
        cardStack = new CardStack();
    }

    @Test
    void createCardList_InvalidInputs_NullNumbers() {
        FieldCategory[] symbols = {FieldCategory.ROBOTER, FieldCategory.ENERGIE, FieldCategory.PFLANZE};
        assertThrows(IllegalArgumentException.class, () -> cardStack.createCardList(null, symbols));
    }

    @Test
    void createCardList_InvalidInputs_NullSymbols() {
        int[] numbers = {1, 2, 3};
        assertThrows(IllegalArgumentException.class, () -> cardStack.createCardList(numbers, null));
    }

    @Test
    void createCardList_InvalidInputs_EmptyNumbers() {
        int[] numbers = {};
        FieldCategory[] symbols = {FieldCategory.ROBOTER, FieldCategory.ENERGIE, FieldCategory.PFLANZE};
        assertThrows(IllegalArgumentException.class, () -> cardStack.createCardList(numbers, symbols));
    }

    @Test
    void createCardList_InvalidInputs_EmptySymbols() {
        int[] numbers = {1, 2, 3};
        FieldCategory[] symbols = {};
        assertThrows(IllegalArgumentException.class, () -> cardStack.createCardList(numbers, symbols));
    }

    @Test
    void createCardList_InvalidInputs_DifferentLengths() {
        int[] numbers = {1, 2, 3};
        FieldCategory[] symbols = {FieldCategory.ROBOTER, FieldCategory.ENERGIE};
        assertThrows(IllegalArgumentException.class, () -> cardStack.createCardList(numbers, symbols));
    }

    @Test
    void createCardList_ValidInputs() {
        int[] numbers = {1, 2, 3};
        FieldCategory[] symbols = {FieldCategory.ROBOTER, FieldCategory.ENERGIE, FieldCategory.PFLANZE};
        assertEquals(3, cardStack.createCardList(numbers, symbols).size());
    }

    @Test
    void testShuffleDeck() {
        CardStack originalStack = new CardStack();
        ArrayList<PlayingCard> cards= new ArrayList<>(originalStack.getCards());
        originalStack.shuffleDeck();
        if(originalStack.getCards().get(0)!=cards.get(0)){
            assertTrue(true);
            return;
        }
        if(originalStack.getCards().get(1)!=cards.get(1)){
            assertTrue(true);
            return;
        }
        if(originalStack.getCards().get(2)!=cards.get(2)){
            assertTrue(true);
            return;
        }
        fail();
    }
}
