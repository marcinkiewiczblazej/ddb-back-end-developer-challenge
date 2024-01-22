package dnd.challenge.api.players;

import dnd.challenge.service.players.errors.PlayerDoesNotExist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(PlayerDoesNotExist.class)
    protected ResponseEntity<Object> handlePlayerNotFound(PlayerDoesNotExist exception) {
        return ResponseEntity.notFound().build();
    }
}
