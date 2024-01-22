package dnd.challenge.model.players;

import dnd.challenge.model.players.entities.PlayerDefense;
import org.springframework.data.repository.Repository;

public interface PlayerDefenseRepository extends Repository<PlayerDefense, Long> {
    PlayerDefense save(PlayerDefense playerDefense);
}
