package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;



public interface AggregationService {
    Mono<Map<String, Object>> aggregateData(Set<String> pricing, Set<Integer> track ,Set<Integer> shipments );

}
