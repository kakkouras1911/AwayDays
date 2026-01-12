package com.awaydays.api.service;

import com.awaydays.api.dto.response.StadiumResponse;
import com.awaydays.api.model.Stadiums;
import com.awaydays.api.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    /**
     * Get all stadiums with pagination and sorting
     */
    public Page<StadiumResponse> getAllStadiums(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Stadiums> stadiumsPage = stadiumRepository.findAll(pageable);
        
        return stadiumsPage.map(this::convertToResponse);
    }

    /**
     * Get all stadiums without pagination (for simple list)
     */
    public List<StadiumResponse> getAllStadiums() {
        List<Stadiums> stadiums = stadiumRepository.findAll();
        return stadiums.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get stadium by ID
     */
    public StadiumResponse getStadiumById(UUID id) {
        Stadiums stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stadium not found with id: " + id));
        
        return convertToResponse(stadium);
    }

    /**
     * Get stadium by name
     */
    public StadiumResponse getStadiumByName(String name) {
        Stadiums stadium = stadiumRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Stadium not found with name: " + name));
        
        return convertToResponse(stadium);
    }

    /**
     * Search stadiums by country
     */
    public List<StadiumResponse> getStadiumsByCountry(String country) {
        List<Stadiums> stadiums = stadiumRepository.findAll().stream()
                .filter(s -> s.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
        
        return stadiums.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search stadiums by city
     */
    public List<StadiumResponse> getStadiumsByCity(String city) {
        List<Stadiums> stadiums = stadiumRepository.findAll().stream()
                .filter(s -> s.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
        
        return stadiums.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get total count of stadiums
     */
    public long getStadiumCount() {
        return stadiumRepository.count();
    }

    /**
     * Convert Stadium entity to StadiumResponse DTO
     */
    private StadiumResponse convertToResponse(Stadiums stadium) {
        return new StadiumResponse(
                stadium.getId(),
                stadium.getName(),
                stadium.getCity(),
                stadium.getCountry(),
                stadium.getCapacity(),
                stadium.getBuiltYear(),
                stadium.getDescription(),
                stadium.getAddress(),
                stadium.getHomeTeam(),
                stadium.getCoverImageUrl()
        );
    }
}