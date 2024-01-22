package dnd.challenge.test

import dnd.challenge.model.players.PlayerRepository
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

@Import(ApiClientConfig.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class HealPlayerSpec extends Specification {
    @Autowired
    ApiClientConfig apiClientConfig

    RestClient apiClient

    @Autowired
    PlayerRepository playerRepository

    def playerId = "test-briv"

    def setup() {
        this.apiClient = apiClientConfig.apiClient()
    }

    def "applies heal to player"() {
        given:
        def initial = 10
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(initial)
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        def amount = 2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/heal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", initial + amount, 0)
    }

    def "does not exceed max hit points"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(player.getMaxHitPoints())
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/heal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(2))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getMaxHitPoints(), 0)
    }

    def "should not heal if player hp is 0"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setCurrentHitPoints(0)
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/heal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(2))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", 0, 0)
    }

    def "does not allow negative values"() {
        given:
        def amount = -2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/heal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "rejects for not existing player"() {
        given:
        def playerId = "not_existing_id"
        def amount = 2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/heal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    private def request(amount) {
        return ["amount": amount]
    }

    private def expectedSuccessResponse(playerName, hitPoints, temporaryHitPoints) {
        return ["playerName": playerName, "hitPoints": hitPoints, "temporaryHitPoints": temporaryHitPoints]
    }
}