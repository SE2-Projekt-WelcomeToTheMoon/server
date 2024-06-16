package websocketserver.game.exceptions;

public class GameStateException extends RuntimeException{
    public GameStateException(String message) {
        super(message);
    }
}
