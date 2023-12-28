package com.frs.tnt.webClient;

import java.util.Map;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

public class AggregationWebClient {

  private static final String BASE_URL = "http://localhost:8080";

  public static Mono<Tuple3<Map<String, Object>, Map<String, Object>, Map<String, Object>>> getAllData(Set<String> countries, Set<String> orders) {
      WebClient webClient = WebClient.create(BASE_URL);

      Mono<Map<String, Object>> pricingDataMono = webClient
              .get().uri("/pricing?q={countries}", String.join(",", countries))
              .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

      Mono<Map<String, Object>> shipmentsDataMono = webClient
              .get().uri("/shipments?q={orders}", String.join(",", orders))
              .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

      Mono<Map<String, Object>> trackDataMono = webClient
              .get().uri("/track?q={orders}", String.join(",", orders))
              .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

      return Mono.zip(pricingDataMono, shipmentsDataMono, trackDataMono).cache();
  }
}
