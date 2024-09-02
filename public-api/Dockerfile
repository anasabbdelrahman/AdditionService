FROM alpine:latest

# Use Maven to build the application
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Use OpenJDK for the final stage
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/public-api-0.0.1-SNAPSHOT.jar ./public-api.jar
EXPOSE 8443
CMD ["java", "-jar", "public-api.jar"]