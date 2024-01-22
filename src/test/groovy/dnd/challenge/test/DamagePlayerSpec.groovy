package dnd.challenge.test

import dnd.challenge.model.players.DamageType
import dnd.challenge.model.players.PlayerDefenseRepository
import dnd.challenge.model.players.PlayerRepository
import dnd.challenge.model.players.ResistanceType
import dnd.challenge.model.players.entities.PlayerDefense
import dnd.challenge.test.config.ApiClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import spock.lang.Specification
import spock.lang.Unroll

@Import(ApiClientConfig.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class DamagePlayerSpec extends Specification {
    @Autowired
    ApiClientConfig apiClientConfig

    RestClient apiClient

    @Autowired
    PlayerRepository playerRepository

    @Autowired
    PlayerDefenseRepository playerDefenseRepository

    def playerId = "test-briv"

    def setup() {
        this.apiClient = apiClientConfig.apiClient()
    }

    def "applies damage to player"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(player.getMaxHitPoints())
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        def amount = 2
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getMaxHitPoints() - amount, 0)
    }

    def "player with resistance takes half damage"() {
        given:
        def playerId = "test-adam"

        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(player.getMaxHitPoints())
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        def defense = PlayerDefense.builder()
                .player(player)
                .damageType(DamageType.LIGHTNING)
                .resistanceType(ResistanceType.RESISTANCE)
                .build()

        playerDefenseRepository.save(defense)

        def amount = 2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(DamageType.LIGHTNING.getValue(), amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Adam", player.getMaxHitPoints() - amount / 2, 0)
    }

    def "player takes no damage if they are immune"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(player.getMaxHitPoints())
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        def defense = PlayerDefense.builder()
                .player(player)
                .damageType(DamageType.LIGHTNING)
                .resistanceType(ResistanceType.IMMUNITY)
                .build()

        playerDefenseRepository.save(defense)

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(DamageType.LIGHTNING.getValue(), 2))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getMaxHitPoints(), 0)
    }

    def "temporary hp is affected first"() {
        given:
        def initialHP = 10
        def initialTemporaryHP = 5
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(initialHP)
        player.setTemporaryHitPoints(initialTemporaryHP)
        playerRepository.save(player)

        def amount = 4
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", initialHP, initialTemporaryHP - amount)
    }

    def "damage overflows temporary hp if higher"() {
        given:
        def initialHP = 10
        def initialTemporaryHP = 5
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(initialHP)
        player.setTemporaryHitPoints(initialTemporaryHP)
        playerRepository.save(player)

        def amount = 14
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", initialHP + initialTemporaryHP - amount, 0)
    }


    def "hp cannot go negative after taking damage"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(player.getMaxHitPoints())
        playerRepository.save(player)

        def amount = player.getMaxHitPoints() * 2
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", 0, 0)
    }

    def "rejects request with invalid damage type"() {
        given:
        def damageType = "invalid damage type"

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(damageType, 2))
                .retrieve()
                .toBodilessEntity()

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    @Unroll
    def "rejects request with missing parameter \"#parameterName\""() {
        expect:
        def request = request("piercing", 2)
        request.remove(parameterName)

        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity()

        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        parameterName << ["damageType", "amount"]
    }

    def "rejects negative damage amount"() {
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", -2))
                .retrieve()
                .toBodilessEntity()

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects for not existing player"() {
        given:
        def playerId = "not_existing_player"

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request("piercing", 2))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    private def request(damageType, amount) {
        return ["damageType": damageType, "amount": amount]
    }

    private def expectedSuccessResponse(playerName, hitPoints, temporaryHitPoints) {
        return ["playerName": playerName, "hitPoints": hitPoints, "temporaryHitPoints": temporaryHitPoints]
    }

}
