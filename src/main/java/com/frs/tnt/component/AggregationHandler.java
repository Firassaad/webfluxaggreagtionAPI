package com.frs.tnt.component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.frs.tnt.service.AggregationService;

import reactor.core.publisher.Mono;

@Component
public class AggregationHandler {

  private final AggregationService aggregationService;

  public AggregationHandler(AggregationService aggregationService) {
    this.aggregationService = aggregationService;
  }

  public Mono<ServerResponse> handleAggregation(ServerRequest request) {
    Set<String> pricing = request.queryParam("pricing").map(Set::of).orElse(Set.of());
    Set<Integer> track = request.queryParam("track")
        .map(values -> Arrays.stream(values.split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toSet()))
        .orElse(Set.of());

    Set<Integer> shipments = request.queryParam("shipments").map(values -> Arrays.stream(values.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toSet()))
        .orElse(Set.of());

    return ServerResponse.ok()
        .body(aggregationService.aggregateData(pricing, track, shipments), Map.class);
  }


}
