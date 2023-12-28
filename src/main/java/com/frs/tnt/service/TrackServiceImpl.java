package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Service
public class TrackServiceImpl implements TrackService{
  private static final String BASE_URL = "http://localhost:8080";


  @Override
  public Mono<Map<String, Object>> getTrackData(Set<Integer> track) {
      return WebClient.builder()
              .baseUrl(BASE_URL)
              .build()
              .get()
              .uri("/track?q={track}", track.stream().map(Object::toString).collect(Collectors.joining(",")))
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
              .map(map -> (Map<String, Object>) map);
  }

}
