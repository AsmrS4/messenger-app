package com.example.user_service.api.resolver;


import com.example.user_service.api.models.SearchParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SearchParamsResolverTest {
    private final SearchParamsResolver resolver = new SearchParamsResolver();
    private static final SearchParams NORMALIZED_SEARCH_PARAMS = new SearchParams();

    @BeforeAll
    public static void setUp() {
        NORMALIZED_SEARCH_PARAMS.setPage(0);
        NORMALIZED_SEARCH_PARAMS.setMinAge(10);
        NORMALIZED_SEARCH_PARAMS.setMaxAge(60);
        NORMALIZED_SEARCH_PARAMS.setSearchName("Ivanov Ivan");
        NORMALIZED_SEARCH_PARAMS.setCity("Tomsk");
    }

    @Test
    @DisplayName("Проверка успешного сценария")
    public void shouldResolveSuccessfully() {
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(1);
        rawParams.setMinAge(10);
        rawParams.setMaxAge(60);
        rawParams.setSearchName("Ivanov Ivan");
        rawParams.setCity("Tomsk");

        SearchParams resolved = resolver.resolve(rawParams);

        assertEquals(NORMALIZED_SEARCH_PARAMS, resolved);
    }

    @Test
    @DisplayName("Проверка на корректную нормализацию параметров фильтрации")
    public void shouldNormalizeSearchParamsSuccessfully() {
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(1);
        rawParams.setMinAge(10);
        rawParams.setMaxAge(60);
        rawParams.setSearchName("    Ivanov Ivan");
        rawParams.setCity("   Tomsk    ");

        SearchParams resolved = resolver.resolve(rawParams);

        assertEquals(NORMALIZED_SEARCH_PARAMS, resolved);
    }

    @Test
    @DisplayName("Проверка огромного значения параметра \"page\"")
    public void shouldNormalizeMaxPageSuccessfully() {
        Integer maxAvailablePageSize = 999;
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(100000);
        rawParams.setMinAge(10);
        rawParams.setMaxAge(60);
        rawParams.setSearchName("    Ivanov Ivan");
        rawParams.setCity("   Tomsk    ");

        SearchParams resolved = resolver.resolve(rawParams);

        assertEquals(maxAvailablePageSize, resolved.getPage());
    }
    @Test
    @DisplayName("Проверка на выставление значения по умолчанию для пропущенного параметра \"page\"")
    public void shouldNormalizePageWithDefaultSuccessfully() {
        SearchParams rawParams = new SearchParams();
        rawParams.setMinAge(10);
        rawParams.setMaxAge(60);

        SearchParams resolved = resolver.resolve(rawParams);

        assertEquals(0, resolved.getPage());
    }

    @Test
    @DisplayName("Проверка некорректного значения параметра \"page\"")
    public void shouldThrowExceptionWithInvalidPageValue() {
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(0);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolve(rawParams);
        });

        assertEquals("Параметр страница должен быть больше нуля.", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка негативного значения параметра \"page\"")
    public void shouldThrowExceptionWithNegativePageValue() {
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(-1);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolve(rawParams);
        });

        assertEquals("Параметр страница должен быть больше нуля.", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка некорректный диапазон возраста")
    public void shouldThrowExceptionWithInvalidAgeRange(){
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(1);
        rawParams.setMinAge(100);
        rawParams.setMaxAge(60);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolve(rawParams);
        });

        assertEquals("Минимальная граница значения параметра \"возраст\" должена быть меньше максимальной границы.", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка на длинную строку параметра \"fullName\"")
    public void shouldThrowExceptionWithInvalidFullNameLength(){
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(1);
        rawParams.setSearchName("aasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskask");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolve(rawParams);
        });

        assertEquals("Допустимая длина строки параметра \"ФИО\" - 255 символов.", ex.getMessage());
    }

    @Test
    @DisplayName("Проверка на длинную строку параметра \"city\"")
    public void shouldThrowExceptionWithInvalidCityLength(){
        SearchParams rawParams = new SearchParams();
        rawParams.setPage(1);
        rawParams.setCity("aasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskaskaasdkalldalkdaldakadskask");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolve(rawParams);
        });

        assertEquals("Допустимая длина строки параметра \"город\" - 255 символов.", ex.getMessage());
    }
}
