package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import lombok.Getter;

@Getter
public class CardCombination {
    private final PlayingCard card1;
    private final PlayingCard card2;
    private final FieldCategory currentSymbol;
    private final FieldCategory nextSymbol;
    private final int currentNumber;
    public CardCombination(PlayingCard card1, PlayingCard card2){
        this.card1=card1;
        this.card2=card2;
        this.currentSymbol= card1.getSymbol();
        this.nextSymbol= card2.getSymbol();
        this.currentNumber= card2.getNumber();

    }


}
