package dnd.challenge.service.players;

import com.fasterxml.jackson.databind.ObjectMapper;
import dnd.challenge.model.players.PlayerDefenseRepository;
import dnd.challenge.model.players.PlayerRepository;
import dnd.challenge.model.players.entities.Player;
import dnd.challenge.model.players.entities.PlayerDefense;
import dnd.challenge.service.players.dtos.InitialPlayerStateDto;
import dnd.challenge.service.players.errors.PlayerDataPathInvalid;
import dnd.challenge.service.players.errors.PlayerFileInvalid;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.stream.Stream;

@Service
public class PlayerDataLoaderService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerDefenseRepository playerDefenseRepository;

    @Value("${application.player.data.folder}")
    String playerDataFolder;

    @PostConstruct
    public void init() {
        ObjectMapper jsonMapper = new ObjectMapper();

        Stream.of(getFilesInFolder(this.playerDataFolder))
                .forEach((playerFile) -> {
                    try {
                        InitialPlayerStateDto initialState = jsonMapper.readValue(playerFile, InitialPlayerStateDto.class);

                        Player createdPlayer = this.readPlayer(playerFile, initialState);

                        readDefensesForPlayer(initialState, createdPlayer);
                    } catch (Exception exception) {
                        throw new PlayerFileInvalid("Cannot process file: " + playerFile.getName(), exception);
                    }
                });
    }

    private void readDefensesForPlayer(InitialPlayerStateDto initialState, Player createdPlayer) {
        initialState.defenses()
                .stream()
                .map(playerDefenseDto -> PlayerDefense.builder()
                        .player(createdPlayer)
                        .damageType(playerDefenseDto.type())
                        .resistanceType(playerDefenseDto.defense())
                        .build())
                .forEach((playerDefense -> this.playerDefenseRepository.save(playerDefense)));
    }

    private Player readPlayer(File playerFile, InitialPlayerStateDto initialState) {
        Player player = Player.builder()
                .id(playerFile.getName().split("\\.")[0])
                .playerName(initialState.name())
                .currentHitPoints(initialState.hitPoints())
                .maxHitPoints(initialState.hitPoints())
                .temporaryHitPoints(0)
                .build();

        return this.playerRepository.save(player);
    }


    private File[] getFilesInFolder(String path) {
        File storageDirectory = new File(path);
        File[] fileList = storageDirectory.listFiles();

        if (!storageDirectory.isDirectory() || fileList == null) {
            throw new PlayerDataPathInvalid("Provided path is not a directory");
        }

        return fileList;
    }
}
