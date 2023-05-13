package org.tafta.taftaapi.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Joseph Kibe
 * Created on April 15, 2023.
 * Time 1:43 PM
 */

@Configuration
public class CustomHttpConfig {

    /**
     * Blocking Http RestTemplate configuration
     */
    @Bean
    public RestTemplate injectRestTemplate(){
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //Settings
        builder.setConnectTimeout(Duration.ofMillis(60000));
        builder.setReadTimeout(Duration.ofMillis(60000));

        return builder.build();
    }

    /**
     * None Blocking Web Client configuration
     */
    @Bean
    WebClient injectWebClient() {

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(60));

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(client))
                .codecs(codecs-> codecs
                        .defaultCodecs()
                        .maxInMemorySize(16*1024*1024)) // Buffer size limit increase
                .build();
    }
}
