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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        ServiceUtils.addToQueue(new AtomicInteger(0), track, trackQueue,CAP, "Track servie", BASE_URL+"/track?q={track}");

    // addToQueue(trackQueue, track);

    Retry retrySpec = ServiceUtils.createRetrySpec();
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
//   private Retry createRetrySpec() {
//     return Retry.backoff(3, Duration.ofSeconds(1))
//         .filter(this::is503Error)
//         .onRetryExhaustedThrow((retryBackoff, retrySignal) -> new Exception("Retry exhausted"));
//   }
//     private boolean is503Error(Throwable error) {
//       logger.info("503 unavailble service  Track, but retry in 1 sec will make it available");
//       return error instanceof WebClientResponseException &&
//             ((WebClientResponseException) error).getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE;
// }



  public Mono<Map<String, Object>> fallbackGetTrackData(Set<Integer> track, Throwable throwable) {
    logger.error("Fallback for Track", throwable);
    return Mono.just(Collections.singletonMap("message", "Fallback method executed"));
  }

//   // Utility method to add a request to the track queue
//   private void addToQueue(Queue<Set<Integer>> queue, Set<Integer> data) {
//     queue.add(data);
//     if (queue.size() >= 5) {
//       logger.error("cap reached | queue size is=" + queue.size());
//       forwardBulkRequestIfCapReached(queue);
//     }
//   }
//   private void forwardBulkRequestIfCapReached(Queue<Set<Integer>> queue) {
//     // Create a bulk request by combining the first 5 requests in the queue
//     Set<Integer> bulkRequest = new HashSet<>();
//     for (int i = 0; i < 5; i++) {
//         Set<Integer> request = queue.poll();
//         if (request != null) {
//             bulkRequest.addAll(request);
//         }
//     }

//     // Forward the bulk request to the API
//     forwardBulkRequestToAPI(bulkRequest);
// }
//   // Utility method to forward a bulk request to the API

//   private void forwardBulkRequestToAPI(Set<Integer> bulkRequest) {

//     logger.info("----------------------------------bulkRequestToAPI-------------");
//     webClient.get()
//         .uri("/track?q={track}", bulkRequest.stream().map(Object::toString).collect(Collectors.joining(",")))
//         .retrieve()
//         .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
//         })
//         .collectList() // Collect the responses into a list
//         .flatMap(apiResponses -> {
//           // Handle the API responses asynchronously
//           logger.info("Handling the API responses asynchronously");
//           // Iterate through each response and log the data
//           for (Map<String, Object> response : apiResponses) {
//             logger.info("Received API response: {}", response);
//             // You can process or display the data as needed
//           }
//           // TODO: Implement logic to wait for responses from all queried API endpoints
//           // Example: waitUntilAllResponsesReceived(apiResponses);
//           // TODO: Respond to the original service request once all responses are received
//           // Example: respondToOriginalRequest(apiResponses);
//           return Mono.empty();
//         })
//         .subscribe(); // Subscribe to initiate the request
//   }


  @Override
  public void processBulkRequest(Set<String> bulkRequest) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'processBulkRequest'");
  }

}
