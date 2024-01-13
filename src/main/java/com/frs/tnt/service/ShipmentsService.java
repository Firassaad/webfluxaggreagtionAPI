package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;

public interface ShipmentsService {
    Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments);

}
