package com.frs.tnt.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.concurrent.Queues;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    public static Retry createRetrySpec() {
        return Retry.backoff(3, Duration.ofSeconds(1))
                .filter(ServiceUtils::is503Error)
                .onRetryExhaustedThrow((retryBackoff, retrySignal) -> new Exception("Retry exhausted"));
    }

    public static boolean is503Error(Throwable error) {
        logger.info("503 unavailable service, but retry in 1 sec will make it available");
        return error instanceof WebClientResponseException &&
                ((WebClientResponseException) error).getStatusCode().is5xxServerError();
    }

    public static Mono<Map<String, Object>> fallbackMethod(Set<Integer> data, Throwable throwable, String serviceName) {
        logger.error("Fallback for {} service", serviceName, throwable);
        return Mono.just(Collections.singletonMap("message", "Fallback method executed for " + serviceName));
    }

    public static <T>  void forwardBulkRequestToAPI(WebClient webClient, Set<T> bulkRequest, String endpoint) {
        webClient.get()
                .uri(endpoint, bulkRequest.stream().map(Object::toString).collect(Collectors.joining(",")))
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .doOnNext(response -> logger.info("Received API response: {}", response))
                .collectList()
                .flatMap(apiResponses -> {
                    logger.info("Handling the API responses asynchronously");
                    // TODO: Implement logic to wait for responses from all queried API endpoints
                    // Example: waitUntilAllResponsesReceived(apiResponses);
                    // TODO: Respond to the original service request once all responses are received
                    // Example: respondToOriginalRequest(apiResponses);
                    return Mono.empty();
                })
                .subscribe();
    }

    public static <T> void addToQueue(AtomicInteger requestCounter, Set<T> data, Queue<Set<T>> queue, int cap, String serviceName,String endpoint) {
      System.out.println("addtoqueues geneeric");
        queue.add(data);
        int currentRequests = requestCounter.incrementAndGet();
        logger.info("Number of requests handled for {}: {}", serviceName, currentRequests);

        if (queue.size() >= cap) {
            logger.error("Cap reached for {} | Queue size is {}", serviceName, queue.size());
            forwardBulkRequestIfCapReached(queue, serviceName, endpoint);
        }
    }

    // private static void forwardBulkRequestIfCapReached(Queue<Set<Integer>> queue, String serviceName) {
    //     // Create a bulk request by combining the first 5 requests in the queue
    //     Set<Integer> bulkRequest = new HashSet<>();
    //     for (int i = 0; i < 5; i++) {
    //         Set<Integer> request = queue.poll();
    //         if (request != null) {
    //             bulkRequest.addAll(request);
    //         }
    //     }

    //     // Forward the bulk request to the API
    //     forwardBulkRequestToAPI(WebClient.create(), bulkRequest, "/api/endpoint?q={shipments}");
    // }
    private static <T> void forwardBulkRequestIfCapReached(Queue<Set<T>> queue, String serviceName, String endpoint) {
      // Create a bulk request by combining the first 5 requests in the queue
System.err.println("forwardBulkRequestIfCapReached"+endpoint);
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
