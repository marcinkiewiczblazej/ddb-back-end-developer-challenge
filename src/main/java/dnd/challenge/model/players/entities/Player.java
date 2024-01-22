package dnd.challenge.model.players.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Player {
    @Id
    private String id;

    @NotNull
    private String playerName;

    @NotNull
    @Positive
    private Integer maxHitPoints;

    @NotNull
    private Integer currentHitPoints;

    @NotNull
    private Integer temporaryHitPoints;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<PlayerDefense> defenses;
}
