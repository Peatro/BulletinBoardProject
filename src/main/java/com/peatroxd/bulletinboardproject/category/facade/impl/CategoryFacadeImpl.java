package com.peatroxd.bulletinboardproject.category.facade.impl;

import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.facade.CategoryFacade;
import com.peatroxd.bulletinboardproject.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryFacadeImpl implements CategoryFacade {

    private final CategoryService categoryService;

    public Category getById(Long id) {
        return categoryService.findCategoryById(id);
    }
}
