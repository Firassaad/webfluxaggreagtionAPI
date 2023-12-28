package com.frs.tnt.controller;


import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frs.tnt.service.AggregationService;


import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;


@RestController
public class AggregationController {

  @Autowired
  private AggregationService aggregationService;

  @GetMapping("/aggregation11")
  public Mono<Map<String, Object>> aggregateData(
          @RequestParam Set<String> pricing,
          @RequestParam Set<Integer> track,
          @RequestParam Set<Integer> shipments
  ) {
      System.out.println("controller to be tested");
      return aggregationService.aggregateData(pricing, track, shipments);
  }



    @GetMapping("/test")
    public String testMethod() {
        System.out.println("hello world");
        return "hello world";
    }
}
