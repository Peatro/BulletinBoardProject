package com.peatroxd.bulletinboardproject.category.service;

import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import com.peatroxd.bulletinboardproject.category.enitty.Category;
import com.peatroxd.bulletinboardproject.category.repository.CategoryRepository;
import com.peatroxd.bulletinboardproject.category.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getAllCategoriesShouldMapEntitiesToResponse() {
        Category parent = Category.builder()
                .id(1L)
                .name("Transport")
                .build();
        Category child = Category.builder()
                .id(2L)
                .name("Cars")
                .parent(parent)
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(parent, child));

        List<CategoryResponse> response = categoryService.getAllCategories();

        assertThat(response).containsExactly(
                new CategoryResponse(1L, "Transport", null),
                new CategoryResponse(2L, "Cars", 1L)
        );
    }
}
