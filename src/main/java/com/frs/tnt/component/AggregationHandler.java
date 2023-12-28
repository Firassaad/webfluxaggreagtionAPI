package com.frs.tnt.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.frs.tnt.webClient.AggregationWebClient;

import reactor.core.publisher.Mono;

public class AggregationHandler {

    // public Mono<Map<String, Object>> getAggregationData(Set<String> pricing, Set<String> track, Set<String> shipments) {
    //     // Implement logic to fetch data from each external API using AggregationWebClient
    //     Mono<Map<String, Object>> pricingResult = AggregationWebClient.getPricingData(pricing);
    //     Mono<Map<String, Object>> trackResult = AggregationWebClient.getTrackData(track);
    //     Mono<Map<String, Object>> shipmentsResult = AggregationWebClient.getShipmentsData(shipments);

    //     // Combine and format responses
    //     return Mono.zip(pricingResult, trackResult, shipmentsResult)
    //             .map(tuple -> formatResponse(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    // }

    private Map<String, Object> formatResponse(Map<String, Object> pricing, Map<String, Object> track, Map<String, Object> shipments) {
        // Implement the logic to format the responses into the desired GraphQL format
        // ...

        // Example format (adjust based on your needs):
        Map<String, Object> result = new HashMap<>();
        result.put("pricing", pricing);
        result.put("track", track);
        result.put("shipments", shipments);
        return result;
    }
}
