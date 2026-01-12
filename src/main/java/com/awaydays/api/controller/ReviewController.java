package com.awaydays.api.controller;

import com.awaydays.api.dto.request.CreateReviewRequest;
import com.awaydays.api.dto.response.ReviewResponse;
import com.awaydays.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * For now, we'll pass userId in the request body
     * Later, we'll get it from JWT token
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestParam UUID userId, // Temporary - will come from JWT later
            @Valid @RequestBody CreateReviewRequest request
    ) {
        try {
            ReviewResponse response = reviewService.createReview(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // Return error message
            return ResponseEntity.badRequest().build();
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
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            @RequestParam UUID userId // Temporary - will come from JWT later
    ) {
        try {
            reviewService.deleteReview(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}