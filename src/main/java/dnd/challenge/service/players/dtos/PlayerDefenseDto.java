package dnd.challenge.service.players.dtos;

import dnd.challenge.model.players.DamageType;
import dnd.challenge.model.players.ResistanceType;

public record PlayerDefenseDto(DamageType type, ResistanceType defense) {
}
