package dnd.challenge.test.config

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ApiClientConfig implements ApplicationListener<ServletWebServerInitializedEvent> {
    private int port

    @Override
    void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort()
    }

    RestClient apiClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:${this.port}")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    // noop to avoid try / catch in tests
                })
                .build()
    }
}
