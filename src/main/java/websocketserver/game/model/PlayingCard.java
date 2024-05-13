package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import lombok.Getter;

@Getter
public class PlayingCard{

    private FieldCategory symbol;
    private int number;

    public PlayingCard(FieldCategory symbol, int number) {
        this.symbol = symbol;
        this.number = number;
    }
}
