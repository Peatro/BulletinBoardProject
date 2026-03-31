package com.peatroxd.bulletinboardproject.category.service;

import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import com.peatroxd.bulletinboardproject.category.enitty.Category;

import java.util.List;

public interface CategoryService {

    Category findCategoryByIdOrThrow(Long categoryId);

    List<CategoryResponse> getAllCategories();
}
