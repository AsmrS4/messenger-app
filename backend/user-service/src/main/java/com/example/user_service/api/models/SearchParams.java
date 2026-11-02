package com.example.user_service.api.models;

import lombok.Data;

@Data
public class SearchParams {
    private String searchName;
    private String city;
    private Integer minAge = 14;
    private Integer maxAge = 150;
    private Integer page = 1;
}
