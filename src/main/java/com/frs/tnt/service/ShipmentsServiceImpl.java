package com.frs.tnt.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.frs.tnt.utilities.ServiceUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ShipmentsServiceImpl implements ShipmentsService {
  int currentRequests = 0;
  private static final Logger logger = LoggerFactory.getLogger(ShipmentsServiceImpl.class);

  private static final int CAP = 5;
  private final WebClient webClient;
  private static final String BASE_URL = "http://localhost:8080";
  // = AppConfig.getBaseUrl();
  private final Queue<Set<Integer>> shipmentsQueue = new LinkedList<>();
  private final AtomicInteger requestCounter = new AtomicInteger(0);

  public ShipmentsServiceImpl(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
  }

  @Override
  @RateLimiter(name = "shipmentsServiceRateLimiter", fallbackMethod = "fallbackGetShipmentsData")
  public Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments) {
    // logger.info("Entering getShipmentsData");
    ServiceUtils.addToQueue(new AtomicInteger(0), shipments, shipmentsQueue, CAP, "Shipement servie",
        BASE_URL + "/shipments?q={shipments}");
    currentRequests = requestCounter.incrementAndGet();
    // logger.info("Number of requests handled: {}", currentRequests);

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
    logger.error("------------------------------------------------------------------------------Fallback for shipments");
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

  @Override
  public void processBulkRequest(Set<String> bulkRequest) {
    throw new UnsupportedOperationException("Unimplemented method 'processBulkRequest'");
  }
}
