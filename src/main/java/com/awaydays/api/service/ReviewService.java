package com.awaydays.api.service;

import com.awaydays.api.dto.request.CreateReviewRequest;
import com.awaydays.api.dto.response.ReviewResponse;
import com.awaydays.api.model.CategoryRating;
import com.awaydays.api.model.Photo;
import com.awaydays.api.model.Review;
import com.awaydays.api.repository.PhotoRepository;
import com.awaydays.api.repository.ReviewRepository;
import com.awaydays.api.repository.StadiumRepository;
import com.awaydays.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StadiumRepository stadiumRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    /**
     * Create a new review (no photos)
     */
    @Transactional
    public ReviewResponse createReview(UUID userId, CreateReviewRequest request) {
        Review review = buildReview(userId, request);
        Review savedReview = reviewRepository.save(review);
        savedReview = reviewRepository.findById(savedReview.getId()).orElse(savedReview);
        return convertToResponse(savedReview);
    }

    /**
     * Create a new review with optional photos
     */
    @Transactional
    public ReviewResponse createReviewWithPhotos(UUID userId, CreateReviewRequest request, List<MultipartFile> photos) {
        Review review = buildReview(userId, request);
        Review savedReview = reviewRepository.save(review);

        // Handle photos if provided
        if (photos != null && !photos.isEmpty()) {
            List<String> captions = request.getPhotoCaptions();
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile file = photos.get(i);
                if (file != null && !file.isEmpty()) {
                    String url = fileStorageService.storeFile(file);

                    Photo photo = new Photo();
                    photo.setReviewId(savedReview.getId());
                    photo.setUserId(userId);
                    photo.setStadiumId(request.getStadiumId());
                    photo.setUrl(url);
                    photo.setDisplayOrder(i);
                    if (captions != null && i < captions.size()) {
                        photo.setCaption(captions.get(i));
                    }
                    photoRepository.save(photo);
                }
            }
        }

        savedReview = reviewRepository.findById(savedReview.getId()).orElse(savedReview);
        return convertToResponse(savedReview);
    }

    /**
     * Shared logic to build a Review entity from a request
     */
    private Review buildReview(UUID userId, CreateReviewRequest request) {
        if (!stadiumRepository.existsById(request.getStadiumId())) {
            throw new RuntimeException("Stadium not found");
        }
        if (reviewRepository.existsByUserIdAndStadiumId(userId, request.getStadiumId())) {
            throw new RuntimeException("You have already reviewed this stadium");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setStadiumId(request.getStadiumId());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setVisitDate(request.getVisitDate());
        review.setIsFlagged(false);

        List<BigDecimal> ratings = new ArrayList<>();

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

        if (!ratings.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal rating : ratings) {
                sum = sum.add(rating);
            }
            review.setOverallRating(sum.divide(BigDecimal.valueOf(ratings.size()), 1, RoundingMode.HALF_UP));
        } else {
            review.setOverallRating(request.getOverallRating());
        }

        return review;
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
        return reviewRepository.findByStadiumId(stadiumId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all reviews by a user
     */
    public List<ReviewResponse> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
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
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }

    /**
     * Convert Review entity to ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
    Map<String, BigDecimal> categoryRatingsMap = new HashMap<>();
    for (CategoryRating cr : review.getCategoryRatings()) {
        categoryRatingsMap.put(cr.getCategory(), cr.getRating());
    }

    String username = userRepository.findById(review.getUserId())
            .map(user -> user.getUsername())
            .orElse("Unknown User");

    String stadiumName = stadiumRepository.findById(review.getStadiumId())
            .map(stadium -> stadium.getName())
            .orElse("Unknown Stadium");

    List<String> photoUrls = photoRepository.findByReviewIdOrderByDisplayOrderAsc(review.getId())
            .stream()
            .map(Photo::getUrl)
            .collect(Collectors.toList());

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
            review.getUpdatedAt(),
            photoUrls
    );

    }
}