package com.example.user_service.api.models;

import lombok.Data;

@Data
public class SearchParams {
    private String searchName;
    private String city;
    private Integer minAge = 150;
    private Integer maxAge = 0;
    private Integer page = 1;
}
