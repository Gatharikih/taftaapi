package org.tafta.taftaapi.http;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.tafta.taftaapi.utility.ConvertTo;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author Joseph Kibe
 * Created on April 15, 2023.
 * Time 2:03 PM
 * <p>
 * Provide implementation for Blocking And None Blocking calls
 * </p>
 */

@Component
@Slf4j
public class HttpHandler {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebClient client;

    /**
     * Request with body
     *
     * @param method      GET POST, PUT ...
     * @param endpoint    full URL endpoint
     * @param requestBody the payload of the request
     * @param headers     request Headers
     * @return Mono
     */
    public Mono<JsonNode> sendAsyncCall(HttpMethod method, String endpoint, Object requestBody, MultiValueMap<String, String> headers) {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }

        MultiValueMap<String, String> finalHeaders = headers;

        log.info("Request URl : " + endpoint);
        return client
                .method(method)
                .uri(endpoint)
                .headers(httpHeaders -> httpHeaders.addAll(finalHeaders))
                .bodyValue(requestBody)
                .retrieve()
                .onStatus( //Handle common HTTP error for Better Processing On services consuming this functions
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new)
                )
                .bodyToMono(JsonNode.class);

    }

    /**
     * Async Call without request Body Preferably GET request
     *
     * @param method      GET, POST ...
     * @param endpoint    full URL
     * @param queryParams Request Query Parameters
     * @param headers     Request headers
     * @return Mono
     */
    public Mono<JsonNode> sendAsyncCallWithoutBody(HttpMethod method, String endpoint, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers) {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
        }

        if (queryParams == null) {
            queryParams = new LinkedMultiValueMap<>();
        }
        MultiValueMap<String, String> finalHeaders = headers;
        MultiValueMap<String, String> finalQueryParams = queryParams;

        return client
                .method(method)
                .uri(uriBuilder -> {
                            URI uri = UriComponentsBuilder
                                    .fromUriString(endpoint)
                                    .queryParams(finalQueryParams)
                                    .build().toUri();
                            log.info("Request Url :" + uri.getRawPath());
                            return uri;
                        }
                )
                .headers(httpHeaders -> httpHeaders.addAll(finalHeaders))
                .retrieve()
                .onStatus( //Handle common HTTP error for Better Processing On services consuming this functions
                        httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new)
                )
                .bodyToMono(JsonNode.class);

    }

    /**
     * Sync Call without request Body Preferably GET request
     *
     * @param method      GET, POST ...
     * @param endpoint    full URL
     * @param queryParams Request Query Parameters
     * @param headers     Request headers
     * @return Response Body
     */

    public JsonNode sendSyncCallWithoutBody(HttpMethod method, String endpoint, MultiValueMap<String, String> queryParams, MultiValueMap<String, String> headers) {

        try {
            if (headers == null) {
                headers = new LinkedMultiValueMap<>();
            }
            if (queryParams == null) {
                queryParams = new LinkedMultiValueMap<>();
            }

            MultiValueMap<String, String> finalHeaders = headers;
            MultiValueMap<String, String> finalQueryParams = queryParams;

            return client
                    .method(method)
                    .uri(uriBuilder -> {
                        URI uri = UriComponentsBuilder
                                .fromUriString(endpoint)
                                .queryParams(finalQueryParams)
                                .build().toUri();
                        log.info("Request URL  :: " + uri);
                        return uri;
                    })
                    .headers(httpHeaders -> {
                        httpHeaders.addAll(finalHeaders);
                    })
                    .retrieve()
                    .onStatus( //Handle common HTTP error for Better Processing On services consuming this functions
                            httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).flatMap(
                                    er -> Mono.error(new Exception(er))
                            )
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (Exception e) {

                String body = e.getMessage().substring(e.getMessage().indexOf("{"));
                JsonNode res = ConvertTo.jsonNodeFromStr(body);
                if (res != null) {
                    return res;
                }else {
                    throw new RuntimeException(e.getMessage());
                }
        }
    }

    /**
     * Sync Call without request Body Preferably GET request
     *
     * @param method      GET, POST ...
     * @param endpoint    full URL
     * @param requestBody Request payLoad
     * @param headers     Request headers
     * @return Response Body
     */
    public JsonNode sendSyncCall(HttpMethod method, String endpoint, Object requestBody, MultiValueMap<String, String> headers) {
        try {
            if (headers == null) {
                headers = new LinkedMultiValueMap<>();
            }

            log.info("Request URl : " + endpoint);
            MultiValueMap<String, String> finalHeaders = headers;

            return client
                    .method(method)
                    .uri(endpoint)
                    .headers(httpHeaders -> {
                        httpHeaders.addAll(finalHeaders);
                    })
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus( //Handle common HTTP error for Better Processing On services consuming this functions
                            httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new)
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (Exception e) {
            String body = e.getMessage().substring(e.getMessage().indexOf("{"));
            JsonNode res = ConvertTo.jsonNodeFromStr(body);
            if (res != null) {
                return res;
            }else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
