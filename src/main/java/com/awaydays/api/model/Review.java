package com.awaydays.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "stadium_id", nullable = false)
    private UUID stadiumId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "overall_rating", precision = 2, scale = 1)
    private BigDecimal overallRating;

    @Column(name = "is_flagged", nullable = false)
    private Boolean isFlagged = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    // Relationship with category ratings
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryRating> categoryRatings = new ArrayList<>();

    // Helper method to add category rating
    public void addCategoryRating(CategoryRating categoryRating) {
        categoryRatings.add(categoryRating);
        categoryRating.setReview(this);
    }
}