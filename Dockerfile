# Use an official OpenJDK runtime as a base image
FROM amazoncorretto:17.0.7-alpine

# Set the working directory in the container
WORKDIR /tnt/src/main/java/com/frs/tnt


# Copy the application JAR file into the container
COPY ../target/tnt-0.0.1-SNAPSHOT.jar .

# Expose the port your application will run on
EXPOSE 8081

# Specify the command to run your application
CMD ["java", "-jar", "tnt-0.0.1-SNAPSHOT.jar"]
