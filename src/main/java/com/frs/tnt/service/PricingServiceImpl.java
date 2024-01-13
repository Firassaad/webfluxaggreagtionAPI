package com.frs.tnt.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import com.frs.tnt.utilities.ServiceUtils;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * PricingServiceImpl is a service implementation responsible for fetching
 * pricing data. It uses WebClient to make
 * HTTP requests to the specified BASE_URL. The service is annotated
 * with @RateLimiter, applying rate limiting for
 * resilience. It also includes a fallback method in case rate limits are
 * exceeded or errors occur.
 */
@Service
public class PricingServiceImpl implements PricingService {

  private static final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);

  private static final int CAP = 5;
  private static final String BASE_URL = "http://localhost:8080";

  // Queue to manage pricing requests for throttling and batching
  private final Queue<Set<String>> pricingQueue = new LinkedList<>();
  private final WebClient webClient;

  /**
   * Constructor for PricingServiceImpl.
   *
   * @param webClientBuilder An instance of WebClient.Builder for building the
   *                         WebClient.
   */
  public PricingServiceImpl(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
  }

  /**
   * Fetch pricing data using WebClient and handle rate limiting
   * using @RateLimiter annotation. The method also
   * includes a fallback mechanism in case rate limits are exceeded or errors
   * occur.
   *
   * @param pricing Set of pricing data to be fetched.
   * @return Mono representing the fetched pricing data as a Map.
   * @throws WebClientException If an error occurs during WebClient operation.
   */
  @Override
  @RateLimiter(name = "pricingServiceRateLimiter", fallbackMethod = "fallbackGetPricingData")
  public Mono<Map<String, Object>> getPricingData(Set<String> pricing) throws WebClientException {
    // Process the request and add to the queue
    ServiceUtils.addToQueue(new AtomicInteger(0), pricing, pricingQueue, CAP, "Pricing servie",
        BASE_URL + "/pricing?q={pricing}");

    // Retry configuration for 503 errors
    Retry retrySpec = ServiceUtils.createRetrySpec("pricing");
    // Other logic to retrieve data
    return WebClient.create(BASE_URL)
        .get().uri("/pricing?q={pricing}", String.join(",", pricing))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .retryWhen(retrySpec)
        .map(map -> {
          return (Map<String, Object>) map;
        });
  }

  /**
   * Fallback method for getPricingData. Executed when rate limits are exceeded or
   * errors occur during pricing data retrieval.
   *
   * @param pricing   Set of pricing data.
   * @param throwable The throwable representing the error.
   * @return Mono representing a fallback response.
   */
  public Mono<Map<String, Object>> fallbackGetPricingData(Set<String> pricing, Throwable throwable) {
    // Fallback logic when rate limit is exceeded or when an error occurs
    // You can return a default value, log the error, or implement custom fallback
    // behavior
    logger.error("########################--Fallback for Pricing--#################################");
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

}
