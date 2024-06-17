package websocketserver.game.services;

import websocketserver.game.model.CardCombination;
import websocketserver.game.model.CardStack;
import websocketserver.game.model.PlayingCard;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;

/***
 * Internal class to manage Cards, please use CardManager for game implementation
 */
public class CardController {
    private final CardStack cardStack;
    @Getter
    private int currentPosition;

    @Getter
    private CardCombination[] currentCombinations;
    @Getter
    private final LinkedList<CardCombination[]> pastCombinations;
    private final int stackSize;
    public CardController() {

        this.cardStack = new CardStack();
        this.currentPosition = 0;
        this.currentCombinations = getCombinationAtPosition(0);
        this.pastCombinations = new LinkedList<>();
        //Each Stack of cards is one third of the stack
        this.stackSize =cardStack.getCards().size()/3;
    }

    public CardCombination[] getCombinationAtPosition(int position) {
        if (position > stackSize) throw new IllegalArgumentException("Position cannot exceed "+ stackSize);
        CardCombination[] combinations = new CardCombination[3];
        combinations[0] = new CardCombination(cardStack.getCards().get(position), cardStack.getCards().get(position + 1));
        combinations[1] = new CardCombination(cardStack.getCards().get(position + stackSize), cardStack.getCards().get(position + stackSize +1));
        combinations[2] = new CardCombination(cardStack.getCards().get(position + (2* stackSize)), cardStack.getCards().get(position + (2* stackSize)+1));
        return combinations;
    }

    public void drawNextCard() {
        currentPosition++;
        pastCombinations.add(currentCombinations);
        if (currentPosition == stackSize -1) {
            PlayingCard card0Before = currentCombinations[0].getCard2();
            PlayingCard card1Before = currentCombinations[1].getCard2();
            PlayingCard card2Before = currentCombinations[2].getCard2();
            currentPosition = 0;
            cardStack.shuffleDeck();
            ArrayList<PlayingCard> cards = (ArrayList<PlayingCard>) cardStack.getCards();
            currentCombinations[0] = new CardCombination(card0Before, cards.get(0));
            currentCombinations[1] = new CardCombination(card1Before, cards.get(stackSize));
            currentCombinations[2] = new CardCombination(card2Before, cards.get(stackSize *2));

        } else {
            currentCombinations = getCombinationAtPosition(currentPosition);
        }
    }

    public CardCombination[] getLastCardCombination() {
        return pastCombinations.get(pastCombinations.size() - 1);
    }

    /***
     * Creates a message String with the current Combinations. The Combinations are split by ;
     * the data inside the combinations is split by - and ordered CombinationNumber-CurrentSymbol-CurrentNumber-NextSymbol
     * @param combinations The Combinations to be split
     * @return the created Message String
     */
    public static String getCurrentCardMessage(CardCombination[] combinations) {
        if (combinations == null || combinations.length != 3)
            throw new IllegalArgumentException("Combinations cannot be null or have anything but three entries");
        StringBuilder cardMessage = new StringBuilder();
        int count = 0;
        for (CardCombination combination : combinations) {
            cardMessage.append(String.format("%d-%s-%d-%s;", count, combination.getCurrentSymbol().toString(), combination.getCurrentNumber(), combination.getNextSymbol().toString()));
            count++;
        }
        return cardMessage.toString();
    }
}
