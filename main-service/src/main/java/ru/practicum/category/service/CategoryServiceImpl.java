package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.exception.NotFoundElementException;
import ru.practicum.exception.RequestException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Optional<Category> optCategory = categoryRepository.findByName(categoryDto.getName());
        Category category = categoryRepository.save(CategoryMapper.dtoToCategory(categoryDto));
        log.info("Category with id = {} saved", category.getId());
        return CategoryMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<CategoryDto> result = categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::categoryToDto)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new NotFoundElementException("List of categories empty");
        }
        return result;
    }

    @Override
    @Transactional
    public CategoryDto getCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundElementException("Category with id " + categoryId + " not found"));
        log.info("Category by id = {} find", categoryId);
        return CategoryMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundElementException("Category with id " + categoryId + " not found"));
        Optional<Category> checkName = categoryRepository.findByName(categoryDto.getName());
        if (checkName.isPresent() && checkName.get().getId() != categoryId) {
            throw new RequestException("Category name already present");
        }
        category.setName(category.getName());
        categoryRepository.save(category);
        log.info("Category with id = {} updated", category.getId());
        return CategoryMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundElementException("Category with id " + categoryId + " not found"));
        categoryRepository.deleteById(categoryId);
        log.info("Category with id {} deleted", categoryId);
    }
}