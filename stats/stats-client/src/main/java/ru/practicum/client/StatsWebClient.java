package ru.practicum.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

@Component
public class StatsWebClient implements StatsClient {
    private final WebClient webClient;

    public StatsWebClient(String uri) {
        this.webClient = WebClient.create(uri);
    }

    @Override
    public void saveHit(String app, String uri, String ip, String timestamp) {

        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new HitDto(app, uri, ip, timestamp))
                .retrieve()
                .toBodilessEntity()
                .block();

    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder uri = new StringBuilder("/stats?start=" + start + "&end=" + end);
        if (unique) {
            uri.append("&unique=true");
        }

        for (String u : uris) {
            uri.append("&uri=").append(u);
        }

        return webClient.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StatsDto>>() {
                })
                .block();
    }
}