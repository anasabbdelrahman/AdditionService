package se.omegapoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class AdditionController {

    private final RestTemplate restTemplate;
    private final Map<String, Map<String, Object>> resultsStore = new ConcurrentHashMap<>();

    @Autowired
    public AdditionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // POST /add - Forwards request to Addition Service (internal API)
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addNumbers(@RequestBody Map<String, Integer> numbers) {
        String internalApiUrl = "http://addition-service:3000/add"; // URL of the internal Addition Service

        try {
            // Forward the request to the internal API
            ResponseEntity<Map> response = restTemplate.postForEntity(internalApiUrl, numbers, Map.class);

            // Return the asyncId received from the internal API
            Map<String, String> result = new HashMap<>();
            result.put("asyncId", response.getBody().get("asyncId").toString());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // GET /list results - Retrieves all results stored
    @GetMapping("/list-results")
    public ResponseEntity<Collection<Map<String, Object>>> getAllResults() {
        return ResponseEntity.ok(resultsStore.values());
    }

    // Kafka listener for the list result coming from addition-service
    @KafkaListener(topics = "addition-service.results", groupId = "public-api-group")
    public void listenForResults(String message) {
        try {
            HashMap result = new ObjectMapper().readValue(message, HashMap.class);
            String asyncId = (String) result.get("asyncId");
            resultsStore.put(asyncId, result);
            System.out.println("Stored result: " + result);
        } catch (Exception e) {
            System.err.println("Error parsing result message: " + e.getMessage());
        }
    }
}
