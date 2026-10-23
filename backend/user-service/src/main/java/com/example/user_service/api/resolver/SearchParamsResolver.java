package com.example.user_service.api.resolver;

import com.example.user_service.api.models.SearchParams;
import org.springframework.stereotype.Component;

@Component
public class SearchParamsResolver {
    public SearchParams resolve(SearchParams params) {
        validate(params);
        normalize(params);

        return params;
    }

    private void validate(SearchParams params) {
        if (params.getPage() == null) {
            throw new IllegalArgumentException("Параметр 'page' обязателен.");
        }

        if (params.getPage() - 1 < 0) {
            throw new IllegalArgumentException("Параметр страница должен быть больше нуля.");
        }
        if (params.getMinAge() != null && params.getMinAge() < 0) {
            throw new IllegalArgumentException("Минимальный возраст должен быть положительным числом.");
        }
        if (params.getMaxAge() != null && params.getMaxAge() < 0) {
            throw new IllegalArgumentException("Максимальный возраст должен быть положительным числом.");
        }
        if (params.getMinAge() != null && params.getMaxAge() != null && params.getMinAge() > params.getMaxAge())  {
            throw new IllegalArgumentException("Минимальная граница значения парметра \"возраст\" должена быть меньше максимальной границы.");
        }

        if(params.getCity().trim().length() > 255) {
            throw  new IllegalArgumentException("Допустимая длина строки параметра \"город\" - 255 символов.");
        }
        if(params.getSearchName().trim().length() > 255) {
            throw  new IllegalArgumentException("Допустимая длина строки параметра \"ФИО\" - 255 символов.");
        }
    }

    private void normalize(SearchParams params) {
        if (params.getPage() > 1000) params.setPage(1000);
        params.setPage(params.getPage() - 1);
        params.setSearchName(trimToNull(params.getSearchName()));
        params.setCity(trimToNull(params.getCity()));
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
