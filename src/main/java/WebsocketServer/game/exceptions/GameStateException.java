package WebsocketServer.game.exceptions;

public class GameStateException extends RuntimeException{
    public GameStateException() {
        super();
    }

    public GameStateException(String message) {
        super(message);
    }

    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameStateException(Throwable cause) {
        super(cause);
    }

    protected GameStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
