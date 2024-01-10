package com.frs.tnt.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AggregationRouter {

    @Bean
    // @RateLimiter(name = "AggregationService" , fallbackMethod = "aggregationMethod")
    public RouterFunction<ServerResponse> aggregationRoutes(AggregationHandler handler) {
        return route(GET("/aggregation")
                .and(accept(MediaType.APPLICATION_JSON)),
                handler::handleAggregation);
    }

    // public String aggregationMethod(){
    //   return "fallback aggregation is turned";
    // }
}
