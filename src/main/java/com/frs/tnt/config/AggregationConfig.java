package com.frs.tnt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.frs.tnt.service.AggregationService;
import com.frs.tnt.service.AggregationServiceImpl;
import com.frs.tnt.service.PricingService;
import com.frs.tnt.service.ShipmentsService;
import com.frs.tnt.service.TrackService;


// This configuration class ensures that Spring manages the lifecycle of the AggregationServiceImpl bean and injects the required services into it.



@Configuration
public class AggregationConfig {
  @Bean
  public AggregationService aggregationService(
      PricingService pricingService,
      TrackService trackService,
      ShipmentsService shipmentsService) {
    return new AggregationServiceImpl(pricingService, trackService, shipmentsService);
  }
}
