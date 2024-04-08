package WebsocketServer.game.model;

public class PlayingCard{

    public CardSymbolEnum symbol;
    public int number;

    public PlayingCard(CardSymbolEnum symbol, int number) {
        this.symbol = symbol;
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("Card has Symbol %s and Number %d",this.symbol,this.number);
    }
}
