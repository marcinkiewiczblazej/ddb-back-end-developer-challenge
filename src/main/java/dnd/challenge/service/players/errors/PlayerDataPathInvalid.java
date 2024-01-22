package dnd.challenge.service.players.errors;

public class PlayerDataPathInvalid extends RuntimeException{
    public PlayerDataPathInvalid(String message) {
        super(message);
    }
}
