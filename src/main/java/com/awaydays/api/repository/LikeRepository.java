package com.awaydays.api.repository;

import com.awaydays.api.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByReviewIdAndUserId(UUID reviewId, UUID userId);
    Optional<Like> findByReviewIdAndUserId(UUID reviewId, UUID userId);
    Long countByReviewId(UUID reviewId);
}