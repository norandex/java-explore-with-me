package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto categoryRequestDto);

    void delete(Long catId);

    CategoryDto update(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> get(Integer from, Integer size);

    CategoryDto getById(Long catId);
}
