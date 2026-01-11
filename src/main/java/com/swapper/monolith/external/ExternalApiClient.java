package com.swapper.monolith.external;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiClient {

    private final RestTemplate restTemplate;

    public ExternalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Generic method to execute external API calls.
     *
     * @param url          The target URL.
     * @param method       The HTTP method (GET, POST, etc.).
     * @param requestBody  The request body object (can be null).
     * @param headers      The HTTP headers (can be null).
     * @param responseType The class type of the expected response.
     * @param <RES>          The type of the response.
     * @param <REQ>          The type of the request body.
     * @return The response body of type T.
     */
    public <REQ, RES> RES execute(String url, HttpMethod method, REQ requestBody, HttpHeaders headers, Class<RES> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            httpHeaders.putAll(headers);
        }

        HttpEntity<REQ> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<RES> response = restTemplate.exchange(
                url,
                method,
                requestEntity,
                responseType
        );

        return response.getBody();
    }
}
