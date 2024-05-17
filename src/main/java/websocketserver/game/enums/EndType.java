package websocketserver.game.enums;

public enum EndType {
    ROCKETS_COMPLETED(0),
    SYSTEM_ERROR_EXCEEDED(1);

    // fixme remove unused, misleading i
    private final int value;

    EndType(int index) {
        this.value = index;
    }
}
