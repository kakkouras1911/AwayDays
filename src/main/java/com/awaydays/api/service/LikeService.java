package com.awaydays.api.service;

import com.awaydays.api.model.Like;
import com.awaydays.api.repository.LikeRepository;
import com.awaydays.api.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public long likeReview(UUID reviewId, UUID userId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        if (likeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RuntimeException("You have already liked this review");
        }

        Like like = new Like();
        like.setReviewId(reviewId);
        like.setUserId(userId);
        likeRepository.save(like);

        return likeRepository.countByReviewId(reviewId);
    }

    @Transactional
    public long unlikeReview(UUID reviewId, UUID userId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }

        Like like = likeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new RuntimeException("You have not liked this review"));

        likeRepository.delete(like);

        return likeRepository.countByReviewId(reviewId);
    }

    public long getLikeCount(UUID reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        return likeRepository.countByReviewId(reviewId);
    }

    public boolean isLikedByUser(UUID reviewId, UUID userId) {
        return likeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }
}