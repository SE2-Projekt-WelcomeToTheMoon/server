package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;

public class CardCombination {
    public FieldCategory currentSymbol;
    public FieldCategory nextSymbol;
    public int currentNumber;
    public CardCombination(PlayingCard card1, PlayingCard card2){
        this.currentSymbol=card1.symbol;
        this.nextSymbol=card2.symbol;
        this.currentNumber=card2.number;
    }

    public FieldCategory getCurrentSymbol() {
        return currentSymbol;
    }

    public FieldCategory getNextSymbol() {
        return nextSymbol;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

}
