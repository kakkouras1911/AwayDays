package com.awaydays.api.controller;

import com.awaydays.api.dto.request.CreateReviewRequest;
import com.awaydays.api.dto.response.ReviewResponse;
import com.awaydays.api.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews - Create a new review
     * UserId is extracted from JWT token automatically
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        try {
            // Get userId from JWT token (set by JwtAuthenticationFilter)
            UUID userId = getCurrentUserId();
            
            ReviewResponse response = reviewService.createReview(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // Return error message with details
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
   /**
 * POST /api/reviews/with-photos - Create a new review with photos
 */
@PostMapping(value = "/with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createReviewWithPhotos(
        @RequestPart("review") String reviewJson,
        @RequestPart(value = "photos", required = false) List<MultipartFile> photos
) {
    try {
        UUID userId = getCurrentUserId();

        // Parse JSON manually
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        CreateReviewRequest request = objectMapper.readValue(reviewJson, CreateReviewRequest.class);

        ReviewResponse response = reviewService.createReviewWithPhotos(userId, request, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request format: " + e.getMessage()));
    }
}
    /**
     * GET /api/reviews/{id} - Get review by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable UUID id) {
        try {
            ReviewResponse review = reviewService.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/reviews/stadium/{stadiumId} - Get all reviews for a stadium
     */
    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByStadium(@PathVariable UUID stadiumId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByStadiumId(stadiumId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * GET /api/reviews/user/{userId} - Get all reviews by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable UUID userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * DELETE /api/reviews/{id} - Delete a review
     * UserId is extracted from JWT token automatically
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        try {
            // Get userId from JWT token
            UUID userId = getCurrentUserId();
            
            reviewService.deleteReview(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Helper method to extract userId from JWT token
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
}