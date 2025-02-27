package org.example.servlyback.features.category

import org.example.servlyback.entities.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>