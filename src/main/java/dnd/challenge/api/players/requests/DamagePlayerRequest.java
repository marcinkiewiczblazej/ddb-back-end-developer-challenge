package dnd.challenge.api.players.requests;

import dnd.challenge.model.players.DamageType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DamagePlayerRequest(@NotNull @Enumerated(EnumType.STRING) DamageType damageType, @NotNull @Positive Integer amount){}
