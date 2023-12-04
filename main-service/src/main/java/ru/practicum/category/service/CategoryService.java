package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, long categoryId);

    void deleteCategory(long categoryId);
}