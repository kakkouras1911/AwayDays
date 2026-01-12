package com.awaydays.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "category_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructor without review (will be set via addCategoryRating)
    public CategoryRating(String category, BigDecimal rating) {
        this.category = category;
        this.rating = rating;
    }
}