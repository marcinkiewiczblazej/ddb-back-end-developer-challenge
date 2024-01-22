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
class AddTemporaryHPSpec extends Specification {
    @Autowired
    ApiClientConfig apiClientConfig

    RestClient apiClient

    @Autowired
    PlayerRepository playerRepository

    def playerId = "test-briv"

    def setup() {
        this.apiClient = apiClientConfig.apiClient()
    }

    def "adds temporary HP player"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setTemporaryHitPoints(0)
        playerRepository.save(player)

        def amount = 2
        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/add-temporary-hp")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getCurrentHitPoints(), amount)
    }

    def "replaces value with higher"() {
        given:
        def player = playerRepository.findById(playerId).get()
        player.setTemporaryHitPoints(2)
        playerRepository.save(player)

        def amount = 4

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/add-temporary-hp")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getCurrentHitPoints(), amount)
    }

    def "does not change anything if current temporary hp is higher"() {
        given:
        def initialValue = 10
        def player = playerRepository.findById(playerId).get()
        player.setTemporaryHitPoints(initialValue)
        playerRepository.save(player)

        def amount = 2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/add-temporary-hp")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request(amount))
                .retrieve()
                .toEntity(HashMap.class)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody() == this.expectedSuccessResponse("Briv", player.getCurrentHitPoints(), initialValue)
    }

    def "does not allow negative values"() {
        given:
        def amount = -2

        when:
        def response = apiClient
                .post()
                .uri("/players/${playerId}/add-temporary-hp")
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
                .uri("/players/${playerId}/add-temporary-hp")
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