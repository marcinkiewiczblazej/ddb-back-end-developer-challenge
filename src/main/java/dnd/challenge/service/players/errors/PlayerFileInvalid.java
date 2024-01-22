package dnd.challenge.service.players.errors;

public class PlayerFileInvalid extends RuntimeException {
    public PlayerFileInvalid(String message, Throwable cause) {
        super(message, cause);
    }
}