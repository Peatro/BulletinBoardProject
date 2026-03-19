package com.peatroxd.bulletinboardproject.category.repository;

import com.peatroxd.bulletinboardproject.category.enitty.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
