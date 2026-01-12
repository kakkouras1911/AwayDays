package com.awaydays.api.controller;

import com.awaydays.api.dto.response.StadiumResponse;
import com.awaydays.api.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StadiumController {

    private final StadiumService stadiumService;

    /**
     * GET /api/stadiums - Get all stadiums (simple list)
     */
    @GetMapping
    public ResponseEntity<List<StadiumResponse>> getAllStadiums() {
        List<StadiumResponse> stadiums = stadiumService.getAllStadiums();
        return ResponseEntity.ok(stadiums);
    }

    /**
     * GET /api/stadiums/paginated?page=0&size=20&sortBy=capacity
     * Get paginated stadiums with sorting
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<StadiumResponse>> getPaginatedStadiums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "capacity") String sortBy
    ) {
        Page<StadiumResponse> stadiums = stadiumService.getAllStadiums(page, size, sortBy);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * GET /api/stadiums/{id} - Get stadium by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StadiumResponse> getStadiumById(@PathVariable UUID id) {
        try {
            StadiumResponse stadium = stadiumService.getStadiumById(id);
            return ResponseEntity.ok(stadium);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/stadiums/name/{name} - Get stadium by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<StadiumResponse> getStadiumByName(@PathVariable String name) {
        try {
            StadiumResponse stadium = stadiumService.getStadiumByName(name);
            return ResponseEntity.ok(stadium);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/stadiums/country/{country} - Get stadiums by country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<StadiumResponse>> getStadiumsByCountry(@PathVariable String country) {
        List<StadiumResponse> stadiums = stadiumService.getStadiumsByCountry(country);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * GET /api/stadiums/city/{city} - Get stadiums by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<StadiumResponse>> getStadiumsByCity(@PathVariable String city) {
        List<StadiumResponse> stadiums = stadiumService.getStadiumsByCity(city);
        return ResponseEntity.ok(stadiums);
    }

    /**
     * GET /api/stadiums/count - Get total count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getStadiumCount() {
        long count = stadiumService.getStadiumCount();
        return ResponseEntity.ok(count);
    }
}