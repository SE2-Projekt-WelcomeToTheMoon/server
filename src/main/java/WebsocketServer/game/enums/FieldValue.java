package WebsocketServer.game.enums;

public enum FieldValue {
    NONE(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15);

    private final int value;

    FieldValue(int weight){
        this.value = weight;
    }

    public int getValue(){
        return value;
    }
}
