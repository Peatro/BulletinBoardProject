package com.peatroxd.bulletinboardproject.category.controller.impl;

import com.peatroxd.bulletinboardproject.category.controller.CategoryController;
import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import com.peatroxd.bulletinboardproject.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
