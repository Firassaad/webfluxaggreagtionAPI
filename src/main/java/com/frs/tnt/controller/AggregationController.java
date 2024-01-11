package com.frs.tnt.controller;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.frs.tnt.service.AggregationService;
import com.frs.tnt.service.ShipmentsService;
import reactor.core.publisher.Mono;

@RestController
public class AggregationController {
  private static final Logger logger = LoggerFactory.getLogger(AggregationController.class);
// RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
  @Autowired
  private AggregationService aggregationService;

  @Autowired
  private ShipmentsService shipmentsService;

  @GetMapping("/aggregation1")
  public Mono<Map<String, Object>> aggregateData(
      @RequestParam Set<String> pricing,
      @RequestParam Set<Integer> track,
      @RequestParam Set<Integer> shipments) {
    System.out.println("controller to be tested");
    return aggregationService.aggregateData(pricing, track, shipments);
  }

  @GetMapping("/Shipments")
  // @RateLimiter(name = "backendB", fallbackMethod = "fallbackGetShipmentsData")
  public Mono<Map<String, Object>> getShipments(@RequestParam Set<Integer> shipments) {
      logger.info("the controller ---------------------");
      return shipmentsService.getShipmentsData(shipments);
  }

  // public Mono<Map<String, Object>> fallbackGetShipmentsData(Set<Integer> shipments, Throwable throwable) {
  //   System.out.println("-----------------fallback------------------");
  //   logger.error("Fallback for shipments", throwable);
  //     return Mono.just(Collections.singletonMap("fallbackKey", "fallback fallbackGetShipmentsData"));
  // }
  // @GetMapping("/test")
  // @RateLimiter(name = "backendB", fallbackMethod = "fallbackGetShipmentsDataB")
  // public String testMethod() {
  //     logger.info("the controller ---------------------");
  //     return "test the new rateLimiter";
  // }

  // public String fallbackGetShipmentsDataB(Throwable throwable) {
  //     System.out.println("-----------------fallback------------------");
  //     logger.error("Fallback for shipments", throwable);
  //     return "fallback B";
  // }
}
