package com.awaydays.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stadiums")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stadiums {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = true)
    private Integer capacity;

    @Column(nullable = true)
    private Integer builtYear;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 100)
    private String homeTeam;

    @Column(nullable = true, length = 500)
    private String coverImageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
