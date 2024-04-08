package WebsocketServer.game.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import WebsocketServer.game.enums.FieldCategory;
public class CardStack{
    private ArrayList<PlayingCard> cards;

    public CardStack(){
        this.cards=createCardList(getCardNumberArray(),initializeCardSymbols());
    }
    /***
     * creates a list of PlayingCards with randomly assigned numbers and symbols from the input
     * @param numbers List of numbers to assign to this list
     * @param symbols List of symbols to assign to this list
     * @return an Arraylist with PlayingCards with randomized symbol and number combinations
     */
    public ArrayList<PlayingCard> createCardList(int[] numbers, FieldCategory[] symbols) {
        if (numbers == null || symbols == null || numbers.length == 0 || symbols.length == 0)throw new IllegalArgumentException("Numbers and symbols arrays must not be null or empty.");
        if(numbers.length!= symbols.length)throw new IllegalArgumentException("Number and Symbol array must be of same length");


        List<Integer> list = new ArrayList<>();
        for (int number : numbers) {
            Integer integer = number;
            list.add(integer);
        }
        List<Integer> numberList = new ArrayList<>(list);
        List<FieldCategory> symbolList = new ArrayList<>(Arrays.asList(symbols));

        Collections.shuffle(numberList);
        Collections.shuffle(symbolList);

        ArrayList<PlayingCard> cardList = new ArrayList<>();

        for (int i = 0; i < numbers.length; i++) {
            cardList.add(new PlayingCard(symbolList.get(i), numberList.get(i)));
        }

        return cardList;
    }

    public List<PlayingCard> getCards() {
        return cards;
    }
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public int[] getCardNumberArray(){
        return new int[]{
                1, 1, 2, 2, 14, 14, 15, 15, // 2 cards with 1/2/14/15
                3, 3, 3, 13, 13, 13, // 3 cards with 3/13
                4, 4, 4, 4, 12, 12, 12, 12, // 4 cards with 4/12
                5, 5, 5, 5, 5, 11, 11, 11, 11, 11, // 5 cards with 5/11
                6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, // 6 cards with 6/7/9/10
                8, 8, 8, 8, 8, 8, 8 // 7 cards with 8
        };
    }

     static FieldCategory[] initializeCardSymbols(){
        FieldCategory[] cardSymbols = new FieldCategory[63]; // Total number of cards

        addCards(cardSymbols, FieldCategory.ROBOTER, 0, 14); //14 Robot
        addCards(cardSymbols, FieldCategory.ENERGIE, 14, 28); //14 Energy
        addCards(cardSymbols, FieldCategory.PFLANZE, 28, 42); //14 Plant
        addCards(cardSymbols, FieldCategory.WASSER, 42, 49); //7 Water
        addCards(cardSymbols, FieldCategory.RAUMANZUG, 49, 56); //7 Astronaut
        addCards(cardSymbols, FieldCategory.PLANUNG, 56, 63); //7 Calender

        return cardSymbols;
    }
    static void addCards(FieldCategory[] cardSymbols, FieldCategory symbol, int start, int end) {
        for (int i = start; i < end; i++) {
            cardSymbols[i] = symbol;
        }
    }
}

