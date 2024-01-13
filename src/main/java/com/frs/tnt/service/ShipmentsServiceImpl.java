package com.frs.tnt.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.frs.tnt.utilities.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * ShipmentsServiceImpl is a service implementation responsible for fetching
 * shipments data. It uses WebClient to make
 * HTTP requests to the specified BASE_URL. The service is annotated
 * with @RateLimiter, applying rate limiting for
 * resilience. It also includes a fallback method in case rate limits are
 * exceeded or errors occur.
 */
@Service
public class ShipmentsServiceImpl implements ShipmentsService {
  int currentRequests = 0;
  // Logger for logging service-related information
  private static final Logger logger = LoggerFactory.getLogger(ShipmentsServiceImpl.class);

  private static final int CAP = 5;
  private final WebClient webClient;
  private static final String BASE_URL = "http://localhost:8080";
  // Queue to manage shipments requests for throttling and batching
  private final Queue<Set<Integer>> shipmentsQueue = new LinkedList<>();

  public ShipmentsServiceImpl(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
  }

    /**
   * Fetch shipments data using WebClient and handle rate limiting
   * using @RateLimiter annotation. The method also
   * includes a fallback mechanism in case rate limits are exceeded or errors
   * occur.
   *
   * @param shipments Set of shipments data to be fetched.
   * @return Mono representing the fetched shipments data as a Map.
   * @throws WebClientException If an error occurs during WebClient operation.
   */
  @Override
  @RateLimiter(name = "shipmentsServiceRateLimiter", fallbackMethod = "fallbackGetShipmentsData")
  public Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments) {
    ServiceUtils.addToQueue(new AtomicInteger(0), shipments, shipmentsQueue, CAP, "Shipement servie",
        BASE_URL + "/shipments?q={shipments}");

    Retry retrySpec = ServiceUtils.createRetrySpec("Shipments");
    return webClient.get()
        .uri("/shipments?q={shipments}", shipments.stream().map(Object::toString).collect(Collectors.joining(",")))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .retryWhen(retrySpec)
        .map(map -> {
          logger.info("Handling the response");
          return map;
        });
  }

  public Mono<Map<String, Object>> fallbackGetShipmentsData(Set<Integer> shipments, Throwable throwable) {
    logger.error("########################--Fallback for Shipements--#################################");
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

}
