package com.frs.tnt.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Configuration class defining the routing for the Aggregation Service.
 * It uses Spring WebFlux's functional programming model to define routes.
 */
@Configuration
public class AggregationRouter {

  @Bean
  public RouterFunction<ServerResponse> aggregationRoutes(AggregationHandler handler) {
    return route(GET("/aggregation") // Define a route for HTTP GET requests to "/aggregation"
        .and(accept(MediaType.APPLICATION_JSON)), // Specify that the client expects JSON response
        handler::handleAggregation); // Delegate the handling of the request to the AggregationHandler
  }

}
