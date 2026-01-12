package com.awaydays.api.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumResponse {
    private UUID id;
    private String name;
    private String city;
    private String country;
    private Integer capacity;
    private Integer builtYear;
    private String description;
    private String address;
    private String homeTeam;
    private String coverImageUrl;
}