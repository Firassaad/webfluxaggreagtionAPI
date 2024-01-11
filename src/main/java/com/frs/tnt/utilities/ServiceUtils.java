package com.frs.tnt.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Utility class for handling various service-related functionalities :
 * particularly : Pricing , track and shipments services (the provided external
 * APIs)
 *
 * Responsibilities include:
 * - Retry mechanism for handling 503 errors.
 * - Fallback method implementation.
 * - Forwarding bulk requests to external APIs.
 * - Queue management for throttling and batching requests.
 * - Scheduling periodic service calls.
 *
 * @author Firas SAADAOUI
 * @version 1.0
 */

public class ServiceUtils {

  public static Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

  /**
   * Creates a Retry specification for handling 503 errors.
   *
   * @param serviceName The name of the service for logging purposes.
   * @return Retry specification.
   */

  public static  Retry createRetrySpec(String serviceName) {
    return Retry.backoff(3, Duration.ofSeconds(1))
        .filter(error -> is503Error(serviceName, error))
        .onRetryExhaustedThrow((retryBackoff, retrySignal) -> new Exception("Retry exhausted"));
  }

  /**
   * Checks if an error is a 503 error.
   *
   * @param serviceName The name of the service for logging purposes.
   * @param error       The error to be checked.
   * @return True if the error is a 503 error, false otherwise.
   */
  public static boolean is503Error(String serviceName, Throwable error) {
    logger.info("503 unavailable service {}, but retry in 1 sec will make it available", serviceName);
    return error instanceof WebClientResponseException &&
        ((WebClientResponseException) error).getStatusCode().is5xxServerError();
  }

  /**
   * Fallback method to handle errors and provide a fallback response.
   *
   * @param data        The data for the fallback response.
   * @param throwable   The error that occurred.
   * @param serviceName The name of the service for logging purposes.
   * @return Fallback response as a Mono.
   */
  public static Mono<Map<String, Object>> fallbackMethod(Set<Integer> data, Throwable throwable, String serviceName) {
    logger.error("Fallback for {} service", serviceName, throwable);
    return Mono.just(Collections.singletonMap("message", "Fallback method executed for " + serviceName));
  }

  /**
   * Forwards a bulk request to an external API.
   *
   * @param webClient   The WebClient for making HTTP requests.
   * @param bulkRequest The set of requests to be forwarded.
   * @param endpoint    The endpoint of the external API.
   */
  public static <T> void forwardBulkRequestToAPI(WebClient webClient, Set<T> bulkRequest, String endpoint) {

    logger.info("Forwarding bulk request for endpoint: {}", endpoint);

    long startTime = System.currentTimeMillis();

    // Execute the API request
    Flux<Map<String, Object>> apiResponseFlux = webClient.get()
        .uri(endpoint, bulkRequest.stream().map(Object::toString).collect(Collectors.joining(",")))
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
        });

    // Schedule periodic calls using Flux.interval
    Flux.interval(Duration.ofSeconds(5)) // Schedule every 5 seconds
        .takeUntilOther(apiResponseFlux) // Stop if API response is received
        .flatMap(ignore -> {
          long elapsedTime = System.currentTimeMillis() - startTime;
          logger.info("Handling the API responses asynchronously | time elapsed {}", elapsedTime);

          // Forward the bulk request
          forwardBulkRequestToAPI(webClient, bulkRequest, endpoint);

          return Mono.empty();
        })
        .subscribe();
  }

  // private static <T> void schedulePeriodicServiceCalls(long elapsedTime, Set<T> bulkRequest, String endpoint,
  //     WebClient webClient) {
  //   // TODO: Implement logic to schedule periodic calls
  //   // Example: If elapsedTime is less than 5000 milliseconds, schedule another call
  //   // after the remaining time
  //   long remainingTime = Math.max(0, 5000 - elapsedTime);

  //   // Schedule a periodic call after the remaining time
  //   Flux.interval(Duration.ofMillis(remainingTime))
  //       .take(1) // Execute only once
  //       .subscribe(ignore -> forwardBulkRequestToAPI(webClient, bulkRequest, endpoint));
  // }

  public static <T> void addToQueue(AtomicInteger requestCounter, Set<T> data,
      Queue<Set<T>> queue, int cap,
      String serviceName, String endpoint) {
    queue.add(data);
    int currentRequests = requestCounter.incrementAndGet();
    logger.info("Number of requests handled for {}: {}", serviceName,
        currentRequests);

    if (queue.size() >= cap) {
      logger.error("Cap reached for {} | Queue size is {}", serviceName,
          queue.size());
      forwardBulkRequestIfCapReached(queue, serviceName, endpoint);
    }
  }

  public static <T> void forwardBulkRequestIfCapReached(Queue<Set<T>> queue, String serviceName, String endpoint) {
    // Create a bulk request by combining the first 5 requests in the queue
    System.err.println("forwardBulkRequestIfCapReached" + endpoint);
    Set<T> bulkRequest = new HashSet<>();
    for (int i = 0; i < 5; i++) {
      Set<T> request = queue.poll();
      if (request != null) {
        bulkRequest.addAll(request);
      }
    }

    // Forward the bulk request to the API
    forwardBulkRequestToAPI(WebClient.create(), bulkRequest, endpoint);
  }

}
