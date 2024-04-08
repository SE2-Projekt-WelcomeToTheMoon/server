package WebsocketServer.game.services;
import WebsocketServer.game.model.CardCombination;
import WebsocketServer.game.model.CardStack;

import java.util.LinkedList;

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
        combinations[0]=new CardCombination(cardStack.getCards().get(currentPosition),cardStack.getCards().get(currentPosition+1));
        combinations[1]=new CardCombination(cardStack.getCards().get(currentPosition+21),cardStack.getCards().get(currentPosition+22));
        combinations[2]=new CardCombination(cardStack.getCards().get(currentPosition+42),cardStack.getCards().get(currentPosition+43));
        return combinations;
    }

    public void drawNextCard() {
        currentPosition++;
        pastCombinations.add(currentCombinations);
        if(currentPosition==20){
         cardStack.shuffleDeck();
         currentPosition=0;
        }else {
            currentCombinations=getCombinationAtPosition(currentPosition);
        }
    }
    public CardCombination[] getLastCardCombination(){
        return pastCombinations.getLast();
    }

    public LinkedList<CardCombination[]> getPastCombinations(){
        return this.pastCombinations;
    }
}
