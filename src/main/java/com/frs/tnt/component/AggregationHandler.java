package com.frs.tnt.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;


import reactor.core.publisher.Mono;

@Component
public class AggregationHandler {

    public Mono<ServerResponse> handleAggregation(ServerRequest request) {
        System.out.println("Handling aggregation request");
        return ServerResponse.ok()
                .bodyValue(Map.of("message", "hello world"));
    }

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
