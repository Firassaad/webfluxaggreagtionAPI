package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;


public interface AggregationService {
    //   Mono<Map<String, Object>> aggregateData(
    //         Set<String> countries,
    //         Set<Integer> orders
    // );
    Mono<Map<String, Object>> aggregateData(Set<String> pricing, Set<Integer> track ,Set<Integer> shipments );

}
