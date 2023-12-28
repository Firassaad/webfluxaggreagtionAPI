package com.frs.tnt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

	public static void main(String[] args) {
    System.out.println("main 1");

    SpringApplication.run(Application.class, args);


  }

  //           // Set of countries or orders
        // Set<String> countries = Set.of("USA","NL","CN","FR","DE","TN","XX");
        // Set<String> countries = Set.of("USA");
  //       Set<String> orders = Set.of("109347263" , "123456891");

  //       // Call different methods from AggregationWebClient
  //       Mono<Map<String, Object>> pricingDataMono = AggregationWebClient.getPricingData(countries);
  //       Mono<Map<String, Object>> shipmentsDataMono = AggregationWebClient.getShipmentsData(orders);
  //       Mono<Map<String, Object>> trackDataMono = AggregationWebClient.getTrackData(orders);

  //       // Subscribe to the Monos and print or process the data
  //       pricingDataMono.subscribe(pricingData -> {
  //           System.out.println("Pricing Data: " + pricingData);
  //           // Add further processing logic if needed
  //       });

  //       shipmentsDataMono.subscribe(shipmentsData -> {
  //           System.out.println("Shipments Data: " + shipmentsData);
  //           // Add further processing logic if needed
  //       });

  //       trackDataMono.subscribe(trackData -> {
  //           System.out.println("Track Data: " + trackData);
  //           // Add further processing logic if needed
  //       });



// ----------------------------------------------------------------------------------------------------------------------------

            // // Set of countries and orders
            // Set<String> countries = Set.of("USA", "Canada", "UK");
            // Set<String> orders = Set.of("109347263", "123456891");

            // // Call the method from AggregationWebClient
            // Mono<Tuple3<Map<String, Object>, Map<String, Object>, Map<String, Object>>> allDataMono = AggregationWebClient.getAllData(countries, orders);

            // // Subscribe to the Mono and process the combined data
            // allDataMono.subscribe(allData -> {
            //     Map<String, Object> pricingData = allData.getT1();
            //     Map<String, Object> shipmentsData = allData.getT2();
            //     Map<String, Object> trackData = allData.getT3();

            //     System.out.println("Pricing Data: " + pricingData);
            //     System.out.println("Shipments Data: " + shipmentsData);
            //     System.out.println("Track Data: " + trackData);

            //     // Add further processing logic if needed
            // });
	}
