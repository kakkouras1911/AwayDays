package com.awaydays.api.service;

import com.awaydays.api.dto.request.CreateReviewRequest;
import com.awaydays.api.dto.response.ReviewResponse;
import com.awaydays.api.model.CategoryRating;
import com.awaydays.api.model.Review;
import com.awaydays.api.repository.ReviewRepository;
import com.awaydays.api.repository.StadiumRepository;
import com.awaydays.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StadiumRepository stadiumRepository;
    private final UserRepository userRepository;

    /**
     * Create a new review
     */
    @Transactional
    public ReviewResponse createReview(UUID userId, CreateReviewRequest request) {
        // Check if stadium exists
        if (!stadiumRepository.existsById(request.getStadiumId())) {
            throw new RuntimeException("Stadium not found");
        }

        // Check if user already reviewed this stadium
        if (reviewRepository.existsByUserIdAndStadiumId(userId, request.getStadiumId())) {
            throw new RuntimeException("You have already reviewed this stadium");
        }

        // Create review
        Review review = new Review();
        review.setUserId(userId);
        review.setStadiumId(request.getStadiumId());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setVisitDate(request.getVisitDate());
        review.setOverallRating(request.getOverallRating());
        review.setIsFlagged(false);

        // Add fixed category ratings
        BigDecimal calculatedOverall = null;
        List<BigDecimal> ratings = new ArrayList<>();
        
        // Add all 5 category ratings (if provided)
        if (request.getFoodRating() != null) {
            review.addCategoryRating(new CategoryRating("food", request.getFoodRating()));
            ratings.add(request.getFoodRating());
        }
        if (request.getAtmosphereRating() != null) {
            review.addCategoryRating(new CategoryRating("atmosphere", request.getAtmosphereRating()));
            ratings.add(request.getAtmosphereRating());
        }
        if (request.getHospitalityRating() != null) {
            review.addCategoryRating(new CategoryRating("hospitality", request.getHospitalityRating()));
            ratings.add(request.getHospitalityRating());
        }
        if (request.getFacilitiesRating() != null) {
            review.addCategoryRating(new CategoryRating("facilities", request.getFacilitiesRating()));
            ratings.add(request.getFacilitiesRating());
        }
        if (request.getAccessibilityRating() != null) {
            review.addCategoryRating(new CategoryRating("accessibility", request.getAccessibilityRating()));
            ratings.add(request.getAccessibilityRating());
        }
        
        // Calculate average overall rating from provided category ratings
        if (!ratings.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal rating : ratings) {
                sum = sum.add(rating);
            }
            calculatedOverall = sum.divide(BigDecimal.valueOf(ratings.size()), 1, RoundingMode.HALF_UP);
        }
        
        // Use calculated overall rating if category ratings provided, otherwise use user's input
        if (calculatedOverall != null) {
            review.setOverallRating(calculatedOverall);
        } else {
            review.setOverallRating(request.getOverallRating());
        }

        // Save review (cascade will save category ratings)
        Review savedReview = reviewRepository.save(review);
        
        // Reload from database to get timestamps
        savedReview = reviewRepository.findById(savedReview.getId())
                .orElse(savedReview);

        return convertToResponse(savedReview);
    }

    /**
     * Get review by ID
     */
    public ReviewResponse getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return convertToResponse(review);
    }

    /**
     * Get all reviews for a stadium
     */
    public List<ReviewResponse> getReviewsByStadiumId(UUID stadiumId) {
        List<Review> reviews = reviewRepository.findByStadiumId(stadiumId);
        
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all reviews by a user
     */
    public List<ReviewResponse> getReviewsByUserId(UUID userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete review
     */
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if user owns this review
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Convert Review entity to ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
        // Convert category ratings to map
        Map<String, BigDecimal> categoryRatingsMap = new HashMap<>();
        for (CategoryRating cr : review.getCategoryRatings()) {
            categoryRatingsMap.put(cr.getCategory(), cr.getRating());
        }

        // Get username (you can optimize this later with a join query)
        String username = userRepository.findById(review.getUserId())
                .map(user -> user.getUsername())
                .orElse("Unknown User");

        // Get stadium name
        String stadiumName = stadiumRepository.findById(review.getStadiumId())
                .map(stadium -> stadium.getName())
                .orElse("Unknown Stadium");

        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                username,
                review.getStadiumId(),
                stadiumName,
                review.getTitle(),
                review.getContent(),
                review.getVisitDate(),
                review.getOverallRating(),
                categoryRatingsMap,
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}