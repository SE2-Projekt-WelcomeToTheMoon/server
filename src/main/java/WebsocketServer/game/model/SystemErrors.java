package WebsocketServer.game.model;

import lombok.Getter;

public class SystemErrors {
    public final int MAX_ERRORS = 8;
    @Getter
    private int currentErrors;

    public SystemErrors() {
        this.currentErrors = 0;
    }

    public boolean increaseCurrentErrors() {
        this.currentErrors++;
        return hasLost();
    }

    private boolean hasLost() {
        return currentErrors >= MAX_ERRORS;
    }

    private int getRemainingErrors() {
        return MAX_ERRORS - currentErrors;
    }
}
