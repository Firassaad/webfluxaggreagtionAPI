package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
@Service
public class ShipmentsServiceImpl implements ShipmentsService {
  private static final String BASE_URL = "http://localhost:8080";

  @Override
  public Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments) {
    // Implement logic to call Pricing API and retrieve data based on countries
    // Example: return WebClient result
    return WebClient.builder()
        .baseUrl(BASE_URL)
        .build()
        .get()
        .uri("/shipments?q={shipments}", shipments.stream().map(Object::toString).collect(Collectors.joining(",")))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        });

  }

}
