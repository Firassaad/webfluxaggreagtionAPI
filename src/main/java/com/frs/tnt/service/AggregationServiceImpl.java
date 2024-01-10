package com.frs.tnt.service;


import java.util.Map;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

// **************** AggregationServiceImpl injects instances of PricingService, TrackService, and ShipmentsService.
// **************** The aggregateData method uses Mono.zip to execute the three services concurrently and combine their results into a Tuple3.
// **************** The Mono returned by aggregateData represents the asynchronous result of the aggregation.

@Service
public class AggregationServiceImpl implements AggregationService {


    private final PricingService pricingService;
    private final TrackService trackService;
    private final ShipmentsService shipmentsService;

    public AggregationServiceImpl(
            PricingService pricingService,
            TrackService trackService,
            ShipmentsService shipmentsService
    ) {
        this.pricingService = pricingService;
        this.trackService = trackService;
        this.shipmentsService = shipmentsService;
    }

    // @Override
    // // @RateLimiter(name = "pricing") // Apply rate limiter
    // public Mono<Map<String, Object>> getPricingData(Set<String> countries) {
    //     return pricingService.getPricingData(countries);
    // }

    // @Override
    // // @RateLimiter(name = "track") // Apply rate limiter
    // public Mono<Map<String, Object>> getTrackData(Set<Integer> track) {
    //     return trackService.getTrackData(track);
    // }

    // @Override
    // // @RateLimiter(name = "shipments") // Apply rate limiter
    // public Mono<Map<String, Object>> getShipmentsData(Set<Integer> shipments) {
    //     return shipmentsService.getShipmentsData(shipments);
    // }




    @Override
    public Mono<Map<String, Object>> aggregateData(Set<String> pricing, Set<Integer> track ,Set<Integer> shipments ) {
        // Implement your aggregation logic here
        // You can use the data fetched from individual services and combine them as needed
        // For simplicity, let's assume you just return a map with individual service responses
        return Mono.zip(
                pricingService.getPricingData(pricing),
                trackService.getTrackData(track),
                shipmentsService.getShipmentsData(shipments)
        ).map(tuple -> {
            Map<String, Object> pricingData = tuple.getT1();
            Map<String, Object> trackData = tuple.getT2();
            Map<String, Object> shipmentsData = tuple.getT3();

            // Combine the responses as needed
            // For simplicity, just return them as-is in a new map
            Map<String, Object> result = Map.of(
                    "pricing", pricingData,
                    "track", trackData,
                    "shipments", shipmentsData
            );

            return result;
        });
    }



        // Utility method to forward a bulk request to the respective API
        private void forwardBulkRequestToAPI(String apiName, Set<String> bulkRequest) {
          switch (apiName) {
              case "pricing":
                  pricingService.processBulkRequest(bulkRequest);
                  break;
              case "shipments":
                  shipmentsService.processBulkRequest(bulkRequest);
                  break;
              case "track":
                  trackService.processBulkRequest(bulkRequest);
                  break;
              // Add cases for other APIs if needed
              default:
                  throw new IllegalArgumentException("Unsupported API: " + apiName);
          }
        }

}
