package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardStackTest {
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
    void shuffleDeck() {
        CardStack originalStack = new CardStack();
        CardStack shuffledStack = new CardStack();
        shuffledStack.shuffleDeck();
        assertNotEquals(originalStack.getCards(), shuffledStack.getCards());
    }
}
