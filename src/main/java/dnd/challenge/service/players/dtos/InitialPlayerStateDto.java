package dnd.challenge.service.players.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InitialPlayerStateDto(String name, Integer hitPoints, List<PlayerDefenseDto> defenses) {
}
