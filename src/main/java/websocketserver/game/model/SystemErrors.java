package websocketserver.game.model;

import lombok.Getter;

@Getter
public class SystemErrors {
    public static final int MAX_ERRORS = 8;
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
