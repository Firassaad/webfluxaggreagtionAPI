package com.frs.tnt;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.frs.tnt.utilities.ServiceUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceUtilsTest {

  @Test
  void testIs503Error() {
    // Mock the logger
    Logger logger = mock(Logger.class);

    // Create a spy for ServiceUtils
    ServiceUtils serviceUtilsSpy = spy(ServiceUtils.class);
    // Set the logger spy to the spy instance
    ServiceUtils.logger = logger;

    // Create a WebClientResponseException with a 5xx status code
    WebClientResponseException webClientException = WebClientResponseException.create(
        503, "Service Unavailable", null, null, null);

    // Test with a WebClientResponseException
    assertTrue(serviceUtilsSpy.is503Error("testService", webClientException));

    // Test with a different exception
    assertFalse(serviceUtilsSpy.is503Error("testService", new RuntimeException("Test error")));

    // Verify that the logger is called
    // Mockito.verify(logger).info("503 unavailable service {}, but retry in 1 sec
    // will make it available", "testService");
  }

  // @Test
  // void testForwardBulkRequestToAPI() {
  //   // Mock WebClient
  //   WebClient webClientMock = Mockito.mock(WebClient.class);

  //   // Mock the behavior of the WebClient
  //   Mockito.when(webClientMock.get()).thenReturn(Mockito.mock(WebClient.RequestHeadersUriSpec.class));


  //   // Your bulk request data
  //   Set<Object> bulkRequest = Collections.singleton("example");

  //   // Call the method
  //   ServiceUtils.forwardBulkRequestToAPI(webClientMock, bulkRequest, "testEndpoint");

  //   // Verify that the WebClient interactions occurred
  //   Mockito.verify(webClientMock, times(1)).get();
  //   Mockito.verify(webClientMock, times(1))
  //       .get()
  //       .uri(anyString(), ArgumentMatchers.<Object>any());
  //   Mockito.verify(webClientMock, times(1))
  //       .get()
  //       .uri(anyString(), ArgumentMatchers.<Object>any())
  //       .retrieve();

  //   Mockito.verify(webClientMock, times(1))
  //       .get()
  //       .uri(anyString(), ArgumentMatchers.<Object>any())
  //       .retrieve()
  //       .bodyToMono(ArgumentMatchers.<Class<?>>any());
  // }



    @Test
    void testAddToQueue_CapReached() {
        // Mocks
        Queue<Set<Integer>> queueMock = mock(Queue.class);
        AtomicInteger requestCounterMock = mock(AtomicInteger.class);

        // Test data
        Set<Integer> data = new HashSet<>();
        int cap = 2; // Set a low cap for testing
        String serviceName = "TestService";
        String endpoint = "testEndpoint";

        // Mock behavior
        when(requestCounterMock.incrementAndGet()).thenReturn(1);
        when(queueMock.size()).thenReturn(cap + 1); // Cap is reached

        // Method call
        ServiceUtils.addToQueue(requestCounterMock, data, queueMock, cap, serviceName, endpoint);

        // Verifications
        verify(queueMock).add(data);
        verify(requestCounterMock).incrementAndGet();
        verify(requestCounterMock, never()).decrementAndGet(); // Cap reached, no further increment

        // Additional assertions based on your specific needs
    }
    @Test
    void testForwardBulkRequestIfCapReached() {
        // Mocks
        Queue<Set<Integer>> queueMock = mock(Queue.class);

        // Test data
        String serviceName = "TestService";
        String endpoint = "testEndpoint";

        // Prepare a queue with enough elements to reach the cap
        Set<Integer> request1 = new HashSet<>();
        Set<Integer> request2 = new HashSet<>();
        Set<Integer> request3 = new HashSet<>();
        Set<Integer> request4 = new HashSet<>();
        Set<Integer> request5 = new HashSet<>();

        Queue<Set<Integer>> queue = new LinkedList<>();
        queue.add(request1);
        queue.add(request2);
        queue.add(request3);
        queue.add(request4);
        queue.add(request5);

        // Mock behavior
        when(queueMock.poll()).thenReturn(request1, request2, request3, request4, request5, null);

        // Method call
        ServiceUtils.forwardBulkRequestIfCapReached(queueMock, serviceName, endpoint);

        // Verifications
        verify(queueMock, times(5)).poll(); // Verify that poll is called 5 times
        // Additional verifications based on your specific needs
    }

}
