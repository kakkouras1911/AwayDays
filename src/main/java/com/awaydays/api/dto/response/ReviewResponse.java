package com.awaydays.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    private UUID id;
    private UUID userId;
    private String username; // We'll add this later when we fetch user info
    private UUID stadiumId;
    private String stadiumName; // We'll add this too
    private String title;
    private String content;
    private LocalDate visitDate;
    private BigDecimal overallRating;
    private Map<String, BigDecimal> categoryRatings; // category -> rating
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}