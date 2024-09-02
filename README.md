# API Integration with Kafka and Spring Boot

## Overview

In this assignment, implement a public API that interacts with an internal addition service. The addition service performs the addition of two numbers and posts the results to a Kafka topic named `addition-service.results`. The task is to build a `public-api` service that provides two endpoints:

1. **`/api/add`**: This endpoint forwards requests to the internal addition service, which performs the addition and posts the result to Kafka. It responds synchronously with an `asyncId` that corresponds to the request.
2. **`/api/list-results`**: This endpoint fetches and returns a list of all calculation results from the Kafka topic `addition-service.results`.

### Project Structure

- **`./public-api/`**: Place all implementation files for the `public-api` service in this folder. This folder should contain the code for both the `/api/add` and `/api/list-results` endpoints.
- **`./addition-service/`**: Contains the source code for the internal addition service.
- **`./docker-compose.yml`**: Describes the system architecture including the Kafka and Zookeeper services.
- **`./public-api.yml`**: API specification for the `public-api` service.
- **`./start.sh`**: Script to rebuild and start the system.

### Implementation Details

1. **API Implementation (`public-api`)**:
    - Implement the `/api/add` endpoint to forward requests to the internal addition service and return an `asyncId`.
    - Implement the `/api/list-results` endpoint to consume messages from the Kafka topic `addition-service.results` and respond with a list of all results.

2. **Kafka Integration**:
    - Use Kafka to fetch results from the `addition-service.results` topic.
    - Implement a Kafka consumer in `public-api` service to listen to this topic.

3. **Security and Code Quality**:
    - Ensure the API is secure and handles errors gracefully.
    - Write clean and maintainable code, and adhere to best practices for performance and scalability.

### How to Run

1. **Build and Start the System**:
   Run the `start.sh` script to rebuild and start the entire system. This will build the Docker images and start the containers as described in `docker-compose.yml`.

   ```sh
   ./start.sh
