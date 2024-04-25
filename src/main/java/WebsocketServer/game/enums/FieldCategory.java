package WebsocketServer.game.enums;

public enum FieldCategory {

    // TODO
    // english
    ROBOTER, WASSER, PFLANZE, ENERGIE, RAUMANZUG, PLANUNG, ANYTHING;


    // uncomment and replace when translated
    // public String getCategroy(){return this.name();}

    // PLACEHOLDER
    public String getCategory() {
        return switch (this) {
            case ROBOTER -> "ROBOT";
            case WASSER -> "WATER";
            case PFLANZE -> "PLANT";
            case ENERGIE -> "ENERGY";
            case RAUMANZUG -> "SPACESUIT";
            case PLANUNG -> "PLANNING";
            case ANYTHING -> "WILDCARD";
            default -> "";
        };
    }
}
