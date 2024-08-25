package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.CategoryNotEmptyException;
import ru.practicum.exception.DuplicateEntityException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new DuplicateEntityException("could not execute statement; SQL [n/a];" +
                    " constraint [uq_category_name];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryNotEmptyException("The category is not empty");
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID=" + catId + " was not found"));
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ForbiddenException("could not execute statement; SQL [n/a]; constraint [categories_name_key];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        category.setName(newCategoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(updatedCategory);

    }

    @Override
    public List<CategoryDto> get(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categories = categoryRepository.findAll(pageRequest).getContent();
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID=" + catId + " was not found"));
        return CategoryMapper.toCategoryDto(category);

    }
}
