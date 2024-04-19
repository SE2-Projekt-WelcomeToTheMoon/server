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

    protected boolean hasLost() {
        return currentErrors >= MAX_ERRORS;
    }

    protected int getRemainingErrors() {
        return MAX_ERRORS - currentErrors;
    }
}
