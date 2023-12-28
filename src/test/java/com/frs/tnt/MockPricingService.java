package com.frs.tnt;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import com.frs.tnt.service.PricingService;
import com.frs.tnt.service.ShipmentsService;
import com.frs.tnt.service.TrackService;

import reactor.core.publisher.Mono;

public class MockPricingService implements PricingService {
     private static final String BASE_URL = "http://localhost:8080";

    @Override
    public Mono<Map<String, Object>> getPricingData(Set<String> countries) {
      System.out.println("test is going on ");
        return WebClient.create(BASE_URL)
                .get()
                .uri("/pricing?q={countries}", String.join(",", countries))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorResume(e -> Mono.just(defaultPricingData())); // Handle errors gracefully
    }

    private Map<String, Object> defaultPricingData() {
        // Return default pricing data in case of an error
        return Map.of("NL", 14.24, "CN", 20.50);
    }
}

class MockTrackService implements TrackService {
    @Override
    public Mono<Map<String, Object>> getTrackData(Set<Integer> orders) {
        // Implement a mock response for testing
        // Replace this with your actual implementation
        return Mono.just(Map.of("109347263", "IN TRANSIT", "123456891", "COLLECTING"));
    }
}

class MockShipmentsService implements ShipmentsService {
    @Override
    public Mono<Map<String, Object>> getShipmentsData(Set<Integer> orders) {
        // Implement a mock response for testing
        // Replace this with your actual implementation
        return Mono.just(Map.of("109347263", List.of("box", "box", "pallet"), "123456891", List.of("envelope")));
    }
}
