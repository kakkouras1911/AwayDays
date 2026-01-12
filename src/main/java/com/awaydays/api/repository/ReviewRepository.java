package com.awaydays.api.repository;

import com.awaydays.api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    // Find all reviews for a specific stadium
    List<Review> findByStadiumId(UUID stadiumId);
    
    // Find all reviews by a specific user
    List<Review> findByUserId(UUID userId);
    
    // Check if user already reviewed this stadium
    Boolean existsByUserIdAndStadiumId(UUID userId, UUID stadiumId);
    
    // Count reviews for a stadium
    Long countByStadiumId(UUID stadiumId);
}