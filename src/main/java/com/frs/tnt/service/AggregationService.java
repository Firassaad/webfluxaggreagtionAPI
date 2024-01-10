package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;



public interface AggregationService {
    //   Mono<Map<String, Object>> aggregateData(
    //         Set<String> countries,
    //         Set<Integer> orders
    // );
    // Mono<Map<String, Object>> getPricingData(Set<String> countries);

    // Mono<Map<String, Object>> getTrackData(Set<Integer> track);

    // Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments);


    Mono<Map<String, Object>> aggregateData(Set<String> pricing, Set<Integer> track ,Set<Integer> shipments );

}
