package com.tcg.tracker.infrastructure.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class PokemonTcgClientConfig {

    @Bean
    public RestClient pokemonTcgRestClient(
            @Value("${tcg.api.pokemon.base-url}") String baseUrl,
            @Value("${tcg.api.pokemon.api-key:}") String apiKey) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(15));

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new BufferingClientHttpRequestFactory(factory))
                .requestInterceptor(new HttpRetryInterceptor(3, 2000L))
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("X-Api-Key", apiKey);
        }

        return builder.build();
    }
}
