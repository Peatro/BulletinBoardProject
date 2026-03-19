package com.peatroxd.bulletinboardproject.category.service;

import com.peatroxd.bulletinboardproject.category.enitty.Category;

public interface CategoryService {

    Category findCategoryByIdOrThrow(Long categoryId);
}
