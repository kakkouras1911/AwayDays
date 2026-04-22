package com.awaydays.api.controller;

import com.awaydays.api.dto.request.CreateCommentRequest;
import com.awaydays.api.dto.response.CommentResponse;
import com.awaydays.api.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    /**
     * POST /api/comments/review/{reviewId} - Add a comment to a review
     */
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<?> addComment(
            @PathVariable UUID reviewId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        try {
            UUID userId = getCurrentUserId();
            CommentResponse response = commentService.addComment(reviewId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/comments/review/{reviewId} - Get all comments for a review
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> getCommentsByReview(@PathVariable UUID reviewId) {
        try {
            List<CommentResponse> comments = commentService.getCommentsByReview(reviewId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/comments/{id} - Delete a comment
     */
    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
    try {
        UUID userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();
        commentService.deleteComment(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", e.getMessage()));
    }
}

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return (UUID) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    private boolean isCurrentUserAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;
    return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}
}