package dnd.challenge.api.players.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HealPlayerRequest(@NotNull @Positive Integer amount){}
