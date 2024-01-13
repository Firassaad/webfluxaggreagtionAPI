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

/**
 * AggregationHandler is a component responsible for handling HTTP requests
 * related to data aggregation.
 * It retrieves query parameters from the incoming ServerRequest, transforms
 * them into appropriate data types,
 * and delegates the aggregation logic to AggregationService. The response is
 * then sent using ServerResponse.
 */
@Component
public class AggregationHandler {

  private final AggregationService aggregationService;

  /**
   * Constructor for AggregationHandler.
   * Dependency injection
   *
   * @param aggregationService An instance of AggregationService for data
   *                           aggregation.
   */
  public AggregationHandler(AggregationService aggregationService) {
    this.aggregationService = aggregationService;
  }

  /**
   * Handle the HTTP request for data aggregation. Retrieve query parameters,
   * convert them into appropriate types,
   * and delegate the aggregation logic to AggregationService. Return the result
   * as part of the HTTP response.
   *
   * @param request The incoming ServerRequest containing query parameters.
   * @return Mono representing the HTTP response containing the aggregated data.
   */
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
