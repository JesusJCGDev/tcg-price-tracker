package com.tcg.tracker.infrastructure.client.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
@NullMarked
public class HttpRetryInterceptor implements ClientHttpRequestInterceptor {

    private final int maxAttempts;
    private final long backoffInitialMs;

    public HttpRetryInterceptor(int maxAttempts, long backoffInitialMs) {
        this.maxAttempts = maxAttempts;
        this.backoffInitialMs = backoffInitialMs;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ClientHttpResponse response = execution.execute(request, body);
                HttpStatusCode status = response.getStatusCode();

                if ((status.value() == 429 || status.value() >= 502) && attempt < maxAttempts) {
                    log.warn("Fallo transitorio {} en URI: {}. Intento {}/{}", status.value(), request.getURI(), attempt, maxAttempts);
                    applyBackoff(attempt, response);
                    response.close();
                    continue;
                }
                return response;
            } catch (IOException ex) {
                if (attempt >= maxAttempts) {
                    log.error("Reintentos agotados ({}/{}) para URI: {}", attempt, maxAttempts, request.getURI(), ex);
                    throw ex;
                }
                log.warn("Fallo de red I/O en URI: {}. Intento {}/{}: {}", request.getURI(), attempt, maxAttempts, ex.getMessage());
                applyBackoff(attempt, null);
            }
        }
    }

    private void applyBackoff(int attempt, @Nullable ClientHttpResponse response) {
        long waitTime = backoffInitialMs * (long) Math.pow(2, attempt - 1);

        if (response != null) {
            String retryAfter = response.getHeaders().getFirst("Retry-After");
            if (retryAfter != null) {
                try {
                    waitTime = Math.max(waitTime, Long.parseLong(retryAfter) * 1000L);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Reintento interrumpido", ie);
        }
    }
}
