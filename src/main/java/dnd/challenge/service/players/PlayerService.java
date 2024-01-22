package dnd.challenge.service.players;

import dnd.challenge.model.players.DamageType;
import dnd.challenge.model.players.PlayerRepository;
import dnd.challenge.model.players.ResistanceType;
import dnd.challenge.model.players.entities.Player;
import dnd.challenge.model.players.entities.PlayerDefense;
import dnd.challenge.service.players.dtos.PlayerStatusDto;
import dnd.challenge.service.players.errors.PlayerDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public PlayerStatusDto damagePlayer(String playerId, DamageType damageType, Integer amount) {
        Player player = this.playerRepository
                .findById(playerId)
                .orElseThrow(PlayerDoesNotExist::new);

        int damageTaken = this.calculateDamage(player, damageType, amount);

        if (damageTaken > player.getTemporaryHitPoints()) {
            int leftoverDamage = damageTaken - player.getTemporaryHitPoints();
            int updatedHealth = Math.max(0, player.getCurrentHitPoints() - leftoverDamage);

            player.setTemporaryHitPoints(0);
            player.setCurrentHitPoints(updatedHealth);
        } else {
            int updatedTemporaryHP = player.getTemporaryHitPoints() - damageTaken;
            player.setTemporaryHitPoints(updatedTemporaryHP);
        }

        this.playerRepository.save(player);

        return new PlayerStatusDto(player.getPlayerName(), player.getCurrentHitPoints(), player.getTemporaryHitPoints());
    }

    public PlayerStatusDto healPlayer(String playerId, Integer amount) {
        Player player = this.playerRepository
                .findById(playerId)
                .orElseThrow(PlayerDoesNotExist::new);

        if (player.getCurrentHitPoints() != 0) {
            int updatedHealth = Math.min(player.getCurrentHitPoints() + amount, player.getMaxHitPoints());
            player.setCurrentHitPoints(updatedHealth);
            this.playerRepository.save(player);
        }

        return new PlayerStatusDto(player.getPlayerName(), player.getCurrentHitPoints(), player.getTemporaryHitPoints());
    }

    public PlayerStatusDto addTemporaryHP(String playerId, Integer amount) {
        Player player = this.playerRepository
                .findById(playerId)
                .orElseThrow(PlayerDoesNotExist::new);

        int updatedHP = Math.max(player.getTemporaryHitPoints(), amount);
        player.setTemporaryHitPoints(updatedHP);
        this.playerRepository.save(player);

        return new PlayerStatusDto(player.getPlayerName(), player.getCurrentHitPoints(), player.getTemporaryHitPoints());
    }

    private Integer calculateDamage(Player player, DamageType damageType, Integer amount) {
        Optional<PlayerDefense> damageResistance = player.getDefenses()
                .stream()
                .filter(playerDefense -> playerDefense.getDamageType() == damageType)
                .findFirst();

        if (damageResistance.isEmpty()) {
            return amount;
        } else if (damageResistance.get().getResistanceType() == ResistanceType.RESISTANCE) {
            return Math.floorDiv(amount, 2);
        } else {
            return 0;
        }
    }
}
