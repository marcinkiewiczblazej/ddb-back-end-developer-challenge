package dnd.challenge.model.players;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DamageType {
    BLUDGEONING("bludgeoning"),
    PIERCING("piercing"),
    SLASHING("slashing"),
    FIRE("fire"),
    COLD("cold"),
    ACID("acid"),
    THUNDER("thunder"),
    LIGHTNING("lightning"),
    POISON("poison"),
    RADIANT("radiant"),
    NECROTIC("necrotic"),
    PSYCHIC("psychic"),
    FORCE("force");


    private final String value;

    DamageType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
