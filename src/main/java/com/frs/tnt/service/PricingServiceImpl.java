package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class PricingServiceImpl implements PricingService {

  private static final String BASE_URL = "http://localhost:8080";
  @Override
  public Mono<Map<String, Object>> getPricingData(Set<String> pricing) {
      return WebClient.create(BASE_URL)
              .get().uri("/pricing?q={pricing}", String.join(",", pricing))
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
              .map(map -> (Map<String, Object>) map);
  }
}
