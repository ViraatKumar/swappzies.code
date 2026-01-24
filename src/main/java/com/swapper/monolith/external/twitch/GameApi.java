package com.swapper.monolith.external.twitch;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.dto.GameSearchResponse;
import com.swapper.monolith.external.ExternalApiClient;
import com.swapper.monolith.external.dto.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
public class GameApi {
    private final ExternalApiClient client;
    private final TwitchConfiguration configuration;
    private final HttpHeaders headers;
    private TokenResponse cachedToken;

    public GameApi(ExternalApiClient client, TwitchConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
        this.headers = createHeaders();
    }

    /**
     * Ensures we always have a valid access token.
     * If the current one is missing or expired, it fetches a new one.
     */
    private synchronized String getValidAccessToken() {
        if (cachedToken == null || cachedToken.isExpired()) {
            cachedToken = fetchAccessToken();
        }
        return cachedToken.accessToken();
    }

    private TokenResponse fetchAccessToken() {
        String url = UriComponentsBuilder
                .fromUriString(configuration.getAccessTokenBaseUrl())
                .queryParam("client_id", configuration.getClientId())
                .queryParam("client_secret", configuration.getClientSecret())
                .queryParam("grant_type", "client_credentials")
                .toUriString();
        TokenResponse response = client.execute(url,HttpMethod.POST,null,null,TokenResponse.class);
        log.info(response.toString());
        return response;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = getValidAccessToken();
        
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Client-Id", configuration.getClientId());
        httpHeaders.set("Content-Type", "text/plain");
        return httpHeaders;
    }
    /**
     * Example method to search for games.
     */
    public GameSearchResponse searchByGameName(String gameName) {
        String  searchBody = "fields *; search \""+gameName+"\";";
        String gameUrl = "https://api.igdb.com/v4/games";
        HttpHeaders headers = createHeaders();
        String url = UriComponentsBuilder
                .fromUriString(gameUrl)
                .toUriString();

        List<GameDto> gameDTOList = List.of(client.execute(
                url,
                HttpMethod.POST,
                searchBody,
                headers,
                GameDto[].class
        ));
        return new GameSearchResponse(gameDTOList);
    }
}
