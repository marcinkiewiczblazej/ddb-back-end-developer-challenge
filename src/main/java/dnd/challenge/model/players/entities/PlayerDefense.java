package dnd.challenge.model.players.entities;

import dnd.challenge.model.players.DamageType;
import dnd.challenge.model.players.ResistanceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "playerDefenses")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = "player")
@ToString(exclude = "player")
public class PlayerDefense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "playerName", nullable = false)
    private Player player;

    @NotNull
    DamageType damageType;

    @NotNull
    ResistanceType resistanceType;
}
