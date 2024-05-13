package websocketserver.game.exceptions;

public class FinalizedException extends RuntimeException {
    public FinalizedException() {
        super();
    }

    public FinalizedException(String message) {
        super(message);
    }

    public FinalizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinalizedException(Throwable cause) {
        super(cause);
    }

    protected FinalizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
