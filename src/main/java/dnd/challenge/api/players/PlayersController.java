package dnd.challenge.api.players;

import dnd.challenge.api.players.requests.AddTemporaryHitPointsRequest;
import dnd.challenge.api.players.requests.DamagePlayerRequest;
import dnd.challenge.api.players.requests.HealPlayerRequest;
import dnd.challenge.api.players.responses.DamagePlayerResponse;
import dnd.challenge.service.players.PlayerService;
import dnd.challenge.service.players.dtos.PlayerStatusDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayersController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/{playerId}/damage")
    @ResponseBody
    @Transactional
    public DamagePlayerResponse damagePlayer(@PathVariable String playerId, @RequestBody @Valid DamagePlayerRequest request) {
        PlayerStatusDto playerStatusDto = this.playerService.damagePlayer(playerId, request.damageType(), request.amount());

        return new DamagePlayerResponse(playerStatusDto.playerName(), playerStatusDto.currentHitPoints(), playerStatusDto.currentTemporaryHitPoints());
    }

    @PostMapping("/{playerId}/heal")
    @ResponseBody
    @Transactional
    public DamagePlayerResponse damagePlayer(@PathVariable String playerId, @RequestBody @Valid HealPlayerRequest request) {
        PlayerStatusDto playerStatusDto = this.playerService.healPlayer(playerId, request.amount());

        return new DamagePlayerResponse(playerStatusDto.playerName(), playerStatusDto.currentHitPoints(), playerStatusDto.currentTemporaryHitPoints());
    }

    @PostMapping("/{playerId}/add-temporary-hp")
    @ResponseBody
    @Transactional
    public DamagePlayerResponse damagePlayer(@PathVariable String playerId, @RequestBody @Valid AddTemporaryHitPointsRequest request) {
        PlayerStatusDto playerStatusDto = this.playerService.addTemporaryHP(playerId, request.amount());

        return new DamagePlayerResponse(playerStatusDto.playerName(), playerStatusDto.currentHitPoints(), playerStatusDto.currentTemporaryHitPoints());
    }
}
