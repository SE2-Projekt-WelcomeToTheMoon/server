package WebsocketServer.game.services;
import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.model.CardStack;
import WebsocketServer.game.model.PlayingCard;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.LinkedList;

@Component
@Scope("prototype")
public class CardController {
    public CardStack cardStack;
    public int currentPosition;

    public CardCombination[] currentCombinations;
    private LinkedList<CardCombination[]> pastCombinations;

    public CardController() {

        this.cardStack = new CardStack();
        this.currentPosition = 0;
        this.currentCombinations=getCombinationAtPosition(0);
        this.pastCombinations=new LinkedList<>();
    }
        public CardCombination[] getCombinationAtPosition(int position) {
        if (position > 21) throw new IllegalArgumentException("Position cannot exceed 21");
        CardCombination[] combinations= new CardCombination[3];
        combinations[0]=new CardCombination(cardStack.getCards().get(position),cardStack.getCards().get(position+1));
        combinations[1]=new CardCombination(cardStack.getCards().get(position+21),cardStack.getCards().get(position+22));
        combinations[2]=new CardCombination(cardStack.getCards().get(position+42),cardStack.getCards().get(position+43));
        return combinations;
    }

    public void drawNextCard() {
        currentPosition++;
        pastCombinations.add(currentCombinations);
        if(currentPosition==20){
            PlayingCard card0Before= currentCombinations[0].getCard2();
            PlayingCard card1Before= currentCombinations[1].getCard2();
            PlayingCard card2Before= currentCombinations[2].getCard2();
            currentPosition=0;
            cardStack.shuffleDeck();
            ArrayList<PlayingCard> cards= (ArrayList<PlayingCard>) cardStack.getCards();
            currentCombinations[0]=new CardCombination(card0Before,cards.get(0));
            currentCombinations[1]=new CardCombination(card1Before,cards.get(21));
            currentCombinations[2]=new CardCombination(card2Before,cards.get(42));

        }else {
            currentCombinations=getCombinationAtPosition(currentPosition);
        }
    }
    public CardCombination[] getLastCardCombination(){
        return pastCombinations.get(pastCombinations.size() - 1);
    }

    public LinkedList<CardCombination[]> getPastCombinations(){
        return this.pastCombinations;
    }

    /***
     * Creates a message String with the current Combinations. The Combinations are split by ;
     * the data inside the combinations is split by - and ordered CombinationNumber-CurrentSymbol-CurrentNumber-NextSymbol
     * @param combinations The Combinations to be split
     * @return the created Message String
     */
    public static String getCurrentCardMessage(CardCombination[] combinations){
        if(combinations==null||combinations.length!=3)throw new IllegalArgumentException("Combinations cannot be null or have anything but three entries");
        StringBuilder cardMessage= new StringBuilder();
        int count=0;
        for (CardCombination combination:combinations) {
            cardMessage.append(String.format("%d-%s-%d-%s;", count, combination.getCurrentSymbol().toString(), combination.getCurrentNumber(), combination.getNextSymbol().toString()));
            count++;
        }
        return cardMessage.toString();
    }
}
