package com.awaydays.api.repository;

import com.awaydays.api.model.Stadiums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<Stadiums, UUID> {
    Optional<Stadiums> findByName(String name);
    Optional<Stadiums> findByCity(String city);
    Boolean existsByName(String name);
    Boolean existsByCity(String city);
}