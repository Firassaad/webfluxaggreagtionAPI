package com.frs.tnt;
import org.junit.jupiter.api.Test;

import com.frs.tnt.service.AggregationService;
import com.frs.tnt.service.AggregationServiceImpl;
import com.frs.tnt.service.PricingService;
import com.frs.tnt.service.ShipmentsService;
import com.frs.tnt.service.TrackService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;

import java.util.Map;
import java.util.Set;

public class AggregationServiceImplTest {

    // @Test
    // public void testAggregateData() {
    //     // Create mock instances for PricingService, TrackService, and ShipmentsService
    //     PricingService pricingService = new MockPricingService();
    //     TrackService trackService = new MockTrackService();
    //     ShipmentsService shipmentsService = new MockShipmentsService();

    //     // Create an instance of AggregationServiceImpl with mock services
    //     AggregationService aggregationService = new AggregationServiceImpl(
    //             pricingService,
    //             trackService,
    //             shipmentsService
    //     );

    //     // Set of countries and orders for testing
    //     Set<String> countries = Set.of("NL", "CN");
    //     Set<Integer> orders = Set.of(109347263, 123456891);

    //     // Call the aggregateData method and use StepVerifier for assertions
    //     Mono<Map<String, Object>> resultMono =
    //             aggregationService.aggregateData(countries, orders, orders);

    //     StepVerifier.create(resultMono)
    //             .expectNextMatches(tuple3 -> {
    //                 // Perform assertions on the aggregated data
    //                 // Example: assert that pricing, track, and shipments maps are not null
    //                 Map<String, Object> pricing = ((Object) tuple3).getT1();
    //                 Map<String, Object> track = ((Object) tuple3).getT2();
    //                 Map<String, Object> shipments = ((Object) tuple3).getT3();

    //                 return pricing != null && track != null && shipments != null;
    //             })
    //             .verifyComplete();
    // }
}
