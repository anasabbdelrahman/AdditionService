package se.omegapoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import se.omegapoint.model.CalculationRequest;
import se.omegapoint.model.CalculationResponse;
import se.omegapoint.model.CalculationResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class AdditionController {

    private final RestTemplate restTemplate;
    private final Map<String, CalculationResponse> resultsStore = new ConcurrentHashMap<>();

    @Autowired
    public AdditionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // POST /add - Forwards request to Addition Service (internal API)
    @PostMapping("/add")
    public ResponseEntity<?> addNumbers(@RequestBody CalculationRequest request,
                                        @RequestParam(required = false, defaultValue = "false") boolean asyncId) {
        String internalApiUrl = "http://addition-service:3000/add"; // URL of the internal Addition Service

        try {
            // Forward the request to the internal API
            ResponseEntity<Map> response = restTemplate.postForEntity(internalApiUrl, request, Map.class);
            String asyncIdValue = response.getBody().get("asyncId").toString();

            if (asyncId) {
                // Wait for the result with a timeout
                long timeout = 20_000; // 20 seconds timeout
                long start = System.currentTimeMillis();

                while (System.currentTimeMillis() - start < timeout) {
                    if (resultsStore.containsKey(asyncIdValue)) {
                        CalculationResponse result = resultsStore.get(asyncIdValue);
                        return ResponseEntity.ok(result);
                    }
                    TimeUnit.MILLISECONDS.sleep(500); // Wait for 500 ms before retrying
                }

                // Return the asyncId with a placeholder for result if not ready
                return ResponseEntity.ok(new CalculationResponse(asyncIdValue, null));
            }

            // Return only the asyncId if asyncId is false
            return ResponseEntity.ok(Map.of("asyncId", asyncIdValue));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // GET /list-results - Retrieves all results stored
    @GetMapping("/list-results")
    public ResponseEntity<Collection<CalculationResponse>> getAllResults() {
        return ResponseEntity.ok(resultsStore.values());
    }

    // Kafka listener for the result coming from addition-service
    @KafkaListener(topics = "addition-service.results", groupId = "public-api-group")
    public void listenForResults(String message) {
        try {
            HashMap<String, Object> result = new ObjectMapper().readValue(message, HashMap.class);
            String asyncId = (String) result.get("asyncId");
            CalculationResult calculationResult = new CalculationResult(
                    ((Number) result.get("numberOne")).doubleValue(),
                    ((Number) result.get("numberTwo")).doubleValue(),
                    ((Number) result.get("result")).doubleValue()
            );
            CalculationResponse response = new CalculationResponse(asyncId, calculationResult);
            resultsStore.put(asyncId, response);
            System.out.println("Stored result: " + response);
        } catch (Exception e) {
            System.err.println("Error parsing result message: " + e.getMessage());
        }
    }
}
