package WebsocketServer.game.exceptions;

public class FloorSequenceException extends RuntimeException {
    public FloorSequenceException() {
        super();
    }

    public FloorSequenceException(String message) {
        super(message);
    }

    public FloorSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FloorSequenceException(Throwable cause) {
        super(cause);
    }

    protected FloorSequenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
