package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * AggregationServiceImpl is a service implementation responsible for
 * aggregating data from PricingService,
 * TrackService, and ShipmentsService. It injects instances of these services
 * during construction.
 * The aggregateData method uses Mono.zip to execute the three services
 * concurrently and combine their
 * results into a Tuple3. The asynchronous result of the aggregation is
 * represented by the Mono returned
 * by aggregateData.
 */

@Service
public class AggregationServiceImpl implements AggregationService {

  private final PricingService pricingService;
  private final TrackService trackService;
  private final ShipmentsService shipmentsService;

  /**
   * Constructor for AggregationServiceImpl.
   *
   * @param pricingService   An instance of PricingService.
   * @param trackService     An instance of TrackService.
   * @param shipmentsService An instance of ShipmentsService.
   */
  public AggregationServiceImpl(
      PricingService pricingService,
      TrackService trackService,
      ShipmentsService shipmentsService) {
    this.pricingService = pricingService;
    this.trackService = trackService;
    this.shipmentsService = shipmentsService;
  }

  /**
   * Aggregates data from PricingService, TrackService, and ShipmentsService
   * concurrently using Mono.zip.
   * Combines the individual service responses into a single Map and returns a
   * Mono representing the result.
   *
   * @param pricing   Set of pricing data.
   * @param track     Set of track data.
   * @param shipments Set of shipments data.
   * @return Mono representing the aggregated result as a Map.
   */
  @Override
  public Mono<Map<String, Object>> aggregateData(Set<String> pricing, Set<Integer> track, Set<Integer> shipments) {
    // fetch from individual services and combine them as needed
    // return a map with individual service responses
    return Mono.zip(
        pricingService.getPricingData(pricing),
        trackService.getTrackData(track),
        shipmentsService.getShipmentsData(shipments)).map(tuple -> {
          Map<String, Object> pricingData = tuple.getT1();
          Map<String, Object> trackData = tuple.getT2();
          Map<String, Object> shipmentsData = tuple.getT3();

          // Combine the responses as needed
          // Combine and return responses as-is in a new map
          Map<String, Object> result = Map.of(
              "pricing", pricingData,
              "track", trackData,
              "shipments", shipmentsData);

          return result;
        });
  }

}
