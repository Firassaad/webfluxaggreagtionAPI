package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;

public interface PricingService {
    Mono<Map<String, Object>> getPricingData(Set<String> pricing);

    void processBulkRequest(Set<String> bulkRequest);

}
