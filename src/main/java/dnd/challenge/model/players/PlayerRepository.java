package dnd.challenge.model.players;

import dnd.challenge.model.players.entities.Player;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PlayerRepository extends Repository<Player, String> {
    Player save(Player player);

    Optional<Player> findById(String playerId);
}
