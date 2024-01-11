package com.frs.tnt.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.frs.tnt.utilities.ServiceUtils;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class PricingServiceImpl implements PricingService {

  private static final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);
  private final WebClient webClient;
  private static final int CAP = 5;
  private static final String BASE_URL = "http://localhost:8080";
  // = AppConfig.getBaseUrl();
  private final Queue<Set<String>> pricingQueue = new LinkedList<>();

  public PricingServiceImpl(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
  }

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
          // Handle the response as needed
          // You can add additional logic here if needed
          return (Map<String, Object>) map;
        });
  }

  public Mono<Map<String, Object>> fallbackGetPricingData(Set<String> pricing, Throwable throwable) {
    // Fallback logic when rate limit is exceeded or when an error occurs
    // You can return a default value, log the error, or implement custom fallback
    // behavior
    logger.error("Fallback for pricing");
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

  @Override
  public void processBulkRequest(Set<String> bulkRequest) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'processBulkRequest'");
  }

}
