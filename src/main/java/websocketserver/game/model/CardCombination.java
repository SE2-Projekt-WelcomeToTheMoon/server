package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import lombok.Getter;

@Getter
public class CardCombination {
    private PlayingCard card1;
    private PlayingCard card2;
    private FieldCategory currentSymbol;
    private FieldCategory nextSymbol;
    private int currentNumber;
    public CardCombination(PlayingCard card1, PlayingCard card2){
        this.card1=card1;
        this.card2=card2;
        this.currentSymbol= card1.getSymbol();
        this.nextSymbol= card2.getSymbol();
        this.currentNumber= card2.getNumber();

    }


}
