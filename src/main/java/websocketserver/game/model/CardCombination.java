package websocketserver.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import websocketserver.game.enums.FieldCategory;
import lombok.Getter;
import websocketserver.game.enums.FieldValue;

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

    @JsonCreator
    public CardCombination(
            @JsonProperty("currentSymbol") FieldCategory currentSymbol,
            @JsonProperty("nextSymbol") FieldCategory nextSymbol,
            @JsonProperty("currentNumber") FieldValue currentNumber) {
        this.card1 = new PlayingCard(currentSymbol, currentNumber.getValue());
        this.card2 = new PlayingCard(nextSymbol, 0);
        this.currentSymbol = currentSymbol;
        this.nextSymbol = nextSymbol;
        this.currentNumber = currentNumber.getValue();
    }
}
