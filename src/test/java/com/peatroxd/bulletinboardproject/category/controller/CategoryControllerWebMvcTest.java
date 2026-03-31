package com.peatroxd.bulletinboardproject.category.controller;

import com.peatroxd.bulletinboardproject.category.controller.impl.CategoryControllerImpl;
import com.peatroxd.bulletinboardproject.category.dto.response.CategoryResponse;
import com.peatroxd.bulletinboardproject.category.service.CategoryService;
import com.peatroxd.bulletinboardproject.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerWebMvcTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryControllerImpl categoryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllCategoriesShouldReturnCategoryList() throws Exception {
        List<CategoryResponse> response = List.of(
                new CategoryResponse(1L, "Transport", null),
                new CategoryResponse(2L, "Cars", 1L)
        );

        when(categoryService.getAllCategories()).thenReturn(response);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Transport"))
                .andExpect(jsonPath("$[1].parentId").value(1L));

        verify(categoryService).getAllCategories();
    }
}
