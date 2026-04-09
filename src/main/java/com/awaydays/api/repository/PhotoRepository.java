package com.awaydays.api.repository;

import com.awaydays.api.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    
    List<Photo> findByReviewIdOrderByDisplayOrderAsc(UUID reviewId);
    
    List<Photo> findByStadiumIdOrderByCreatedAtDesc(UUID stadiumId);
    
    List<Photo> findByUserId(UUID userId);
    
    Long countByReviewId(UUID reviewId);
}