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
  int currentRequests =0;
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
    logger.info("Entering getShipmentsData");


    ServiceUtils.addToQueue(new AtomicInteger(0), shipments, shipmentsQueue,CAP, "Shipement servie", BASE_URL+"/shipments?q={shipments}");
    // addToQueue(shipmentsQueue, shipments);
     currentRequests = requestCounter.incrementAndGet();
    logger.info("Number of requests handled: {}", currentRequests);

    Retry retrySpec = ServiceUtils.createRetrySpec();
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
    logger.error("Fallback for shipments", throwable);
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

  // private void addToQueue(Queue<Set<Integer>> queue, Set<Integer> data) {
  //   queue.add(data);
  //   if (queue.size() >= 5) {
  //     logger.error("cap reached  | queue size is =" + queue.size());
  //     forwardBulkRequestIfCapReached(queue);
  //   }
  // }

  // private void forwardBulkRequestIfCapReached(Queue<Set<Integer>> queue) {
  //   Set<Integer> bulkRequest = new HashSet<>();
  //   for (int i = 0; i < 5; i++) {
  //     Set<Integer> request = queue.poll();
  //     if (request != null) {
  //       bulkRequest.addAll(request);
  //     }
  //   }

  //   forwardBulkRequestToAPI(bulkRequest);
  // }

  // // Utility method to forward a bulk request to the API

  // private void forwardBulkRequestToAPI(Set<Integer> bulkRequest) {

  //   logger.info("----------------------------------bulkRequestToAPI-------------");
  //   webClient.get()
  //       .uri("/shipments?q={shipments}", bulkRequest.stream().map(Object::toString).collect(Collectors.joining(",")))
  //       .retrieve()
  //       .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
  //       })
  //       .collectList() // Collect the responses into a list
  //       .flatMap(apiResponses -> {
  //         // Handle the API responses asynchronously
  //         logger.info("Handling the API responses asynchronously");
  //         // Iterate through each response and log the data
  //         for (Map<String, Object> response : apiResponses) {
  //           logger.info("Received API response: {}", response);
  //           // You can process or display the data as needed
  //         }
  //         // TODO: Implement logic to wait for responses from all queried API endpoints
  //         // Example: waitUntilAllResponsesReceived(apiResponses);
  //         // TODO: Respond to the original service request once all responses are received
  //         // Example: respondToOriginalRequest(apiResponses);
  //         return Mono.empty();
  //       })
  //       .subscribe(); // Subscribe to initiate the request
  // }

  @Override
  public void processBulkRequest(Set<String> bulkRequest) {
    throw new UnsupportedOperationException("Unimplemented method 'processBulkRequest'");
  }
}
