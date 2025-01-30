package org.example.servlyback.features_customer.category

import org.example.servlyback.dto.CategoryInfo
import org.example.servlyback.dto.QuestionInfo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getCategories(@RequestParam languageCode: String): List<CategoryInfo> {
        return categoryService.getCategories(languageCode)
    }

    @GetMapping("/api/{categoryId}/questions")
    fun getQuestionsForCategory(
        @PathVariable categoryId: Long,
        @RequestParam languageCode: String
    ): List<QuestionInfo> {
        return categoryService.getQuestionsForCategory(categoryId, languageCode)
    }
}