package com.frs.tnt.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;



// **************** AggregationServiceImpl injects instances of PricingService, TrackService, and ShipmentsService.
// **************** The aggregateData method uses Mono.zip to execute the three services concurrently and combine their results into a Tuple3.
// **************** The Mono returned by aggregateData represents the asynchronous result of the aggregation.

@Service
public class AggregationServiceImpl implements AggregationService {

  private final PricingService pricingService;
  private final TrackService trackService;
  private final ShipmentsService shipmentsService;

  public AggregationServiceImpl(PricingService pricingService, TrackService trackService, ShipmentsService shipmentsService) {
      this.pricingService = pricingService;
      this.trackService = trackService;
      this.shipmentsService = shipmentsService;
  }

// @Override
// public Mono<Tuple3<Map<String, Object>, Map<String, Object>, Map<String, Object>>> aggregateData(
//         Set<String> countries, Set<Integer> orders) {
//     return Mono.zip(
//             pricingService.getPricingData(countries).cast(Map.class),
//             trackService.getTrackData(orders).cast(Map.class),
//             shipmentsService.getShipmentsData(orders).cast(Map.class)
//     ).map(tuple3 -> {
//         Map<String, Object> result = new HashMap<>();
//         result.put("pricing", tuple3.getT1());
//         result.put("track", tuple3.getT2());
//         result.put("shipments", tuple3.getT3());
//         return Tuples.of(result, result, result);
//     });
// }
@Override
public Mono<Map<String, Object>> aggregateData(
    Set<String> pricing, Set<Integer> track, Set<Integer> shipments) {
    return Mono.zip(
            pricingService.getPricingData(pricing),
            trackService.getTrackData(track),
            shipmentsService.getShipmentsData(shipments)
    ).map(tuple3 -> {
        Map<String, Object> result = new HashMap<>();
        result.put("pricing", tuple3.getT1());
        result.put("track", tuple3.getT2());
        result.put("shipments", tuple3.getT3());
        return result;
    });
}





}
