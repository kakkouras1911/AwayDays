package com.awaydays.api.service;

import com.awaydays.api.dto.request.CreateCommentRequest;
import com.awaydays.api.dto.response.CommentResponse;
import com.awaydays.api.model.Comment;
import com.awaydays.api.repository.CommentRepository;
import com.awaydays.api.repository.ReviewRepository;
import com.awaydays.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(UUID reviewId, UUID userId, CreateCommentRequest request) {
        // Check review exists
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }

        Comment comment = new Comment();
        comment.setReviewId(reviewId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());

        Comment saved = commentRepository.save(comment);
        saved = commentRepository.findById(saved.getId())
                .orElse(saved);

        return convertToResponse(saved);
    }

    public List<CommentResponse> getCommentsByReview(UUID reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        return commentRepository.findByReviewIdOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        String username = userRepository.findById(comment.getUserId())
                .map(user -> user.getUsername())
                .orElse("Unknown User");

        return new CommentResponse(
                comment.getId(),
                comment.getReviewId(),
                comment.getUserId(),
                username,
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}