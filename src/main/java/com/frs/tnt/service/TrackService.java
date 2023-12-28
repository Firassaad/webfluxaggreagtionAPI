package com.frs.tnt.service;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;

public interface TrackService {
 Mono<Map<String, Object>> getTrackData(Set<Integer> track);


}
