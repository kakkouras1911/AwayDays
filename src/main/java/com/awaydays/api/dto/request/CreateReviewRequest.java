package com.awaydays.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Stadium ID is required")
    private UUID stadiumId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 20, message = "Review content must be at least 20 characters")
    private String content;

    private LocalDate visitDate;

    // Overall rating - optional, will be calculated from category ratings if not provided
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
    private BigDecimal overallRating;

    // Category ratings (optional but recommended)
    // Key = category name (food, atmosphere, etc.)
    // Value = rating (1.0 - 5.0)
    private Map<String, BigDecimal> categoryRatings;
}