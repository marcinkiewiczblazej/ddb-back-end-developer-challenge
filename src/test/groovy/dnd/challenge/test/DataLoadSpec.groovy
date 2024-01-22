package dnd.challenge.test

import dnd.challenge.model.players.PlayerRepository
import dnd.challenge.test.config.ApiClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Import(ApiClientConfig.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class DataLoadSpec extends Specification {
    @Autowired
    PlayerRepository playerRepository

    def "loads all player data in the folder"() {
        when:
        def adam = playerRepository.findById("test-adam")
        def briv = playerRepository.findById("test-briv")

        then:
        adam.isPresent()
        adam.get().getMaxHitPoints() == 15
        adam.get().getCurrentHitPoints() == 15

        briv.isPresent()
        briv.get().getMaxHitPoints() == 25
        briv.get().getCurrentHitPoints() == 25
    }

}