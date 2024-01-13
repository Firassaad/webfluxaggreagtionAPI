package com.frs.tnt.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.frs.tnt.utilities.ServiceUtils;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class TrackServiceImpl implements TrackService {

  private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);

  private final WebClient webClient;
  private static final int CAP = 5;
  private static final String BASE_URL = "http://localhost:8080";
  private final Queue<Set<Integer>> trackQueue = new LinkedList<>();
  public TrackServiceImpl(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
  }

  @Override
  @RateLimiter(name = "trackServiceRateLimiter", fallbackMethod = "fallbackGetTrackData")
  public Mono<Map<String, Object>> getTrackData(Set<Integer> track) {
    // Process the request and add to the queue
    ServiceUtils.addToQueue(new AtomicInteger(0), track, trackQueue, CAP, "Track serviCe",
        BASE_URL + "/track?q={track}");

    Retry retrySpec = ServiceUtils.createRetrySpec("Track");
    // Other logic to retrieve data
    return webClient
        .get()
        .uri("/track?q={track}", track.stream().map(Object::toString).collect(Collectors.joining(",")))
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

  public Mono<Map<String, Object>> fallbackGetTrackData(Set<Integer> track, Throwable throwable) {
    logger.info("########################--Fallback for Track--#################################");
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

}
