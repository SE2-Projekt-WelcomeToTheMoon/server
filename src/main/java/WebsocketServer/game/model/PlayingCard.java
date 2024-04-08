package WebsocketServer.game.model;

import WebsocketServer.game.enums.FieldCategory;

public class PlayingCard{

    public FieldCategory symbol;
    public int number;

    public PlayingCard(FieldCategory symbol, int number) {
        this.symbol = symbol;
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("Card has Symbol %s and Number %d",this.symbol,this.number);
    }
}
