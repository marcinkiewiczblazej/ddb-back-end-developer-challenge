package dnd.challenge.model.players;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResistanceType {
    IMMUNITY("immunity"),
    RESISTANCE("resistance");

    private final String value;

    ResistanceType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
