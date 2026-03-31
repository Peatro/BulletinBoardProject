package com.peatroxd.bulletinboardproject.category.service.impl;

import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.repository.CategoryRepository;
import com.peatroxd.bulletinboardproject.category.service.CategoryService;
import com.peatroxd.bulletinboardproject.common.enums.NotFoundExceptionMessage;
import com.peatroxd.bulletinboardproject.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(NotFoundExceptionMessage.CATEGORY_NOT_FOUND.getMessage()));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
