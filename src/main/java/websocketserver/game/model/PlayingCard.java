package websocketserver.game.model;

import websocketserver.game.enums.FieldCategory;
import lombok.Getter;

public class PlayingCard{

    @Getter
    private FieldCategory symbol;
    @Getter
    private int number;

    public PlayingCard(FieldCategory symbol, int number) {
        this.symbol = symbol;
        this.number = number;
    }
}
