package com.awaydays.api.controller;

import com.awaydays.api.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LikeController {

    private final LikeService likeService;

    /**
     * POST /api/likes/review/{reviewId} - Like a review
     */
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<?> likeReview(@PathVariable UUID reviewId) {
        try {
            UUID userId = getCurrentUserId();
            long likeCount = likeService.likeReview(reviewId, userId);
            return ResponseEntity.ok(Map.of(
                    "liked", true,
                    "likeCount", likeCount
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/likes/review/{reviewId} - Unlike a review
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> unlikeReview(@PathVariable UUID reviewId) {
        try {
            UUID userId = getCurrentUserId();
            long likeCount = likeService.unlikeReview(reviewId, userId);
            return ResponseEntity.ok(Map.of(
                    "liked", false,
                    "likeCount", likeCount
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/likes/review/{reviewId} - Get like count for a review
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getLikeCount(@PathVariable UUID reviewId) {
        try {
            UUID userId = getCurrentUserId();
            long likeCount = likeService.getLikeCount(reviewId);
            boolean isLiked = likeService.isLikedByUser(reviewId, userId);
            return ResponseEntity.ok(Map.of(
                    "likeCount", likeCount,
                    "isLiked", isLiked
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
}