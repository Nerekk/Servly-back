package org.example.servlyback.features.category

import org.example.servlyback.dto.CategoryInfo
import org.example.servlyback.dto.QuestionInfo
import org.example.servlyback.util.ControllerMappings
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/" + ControllerMappings.CATEGORY)
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getCategories(@RequestParam languageCode: String): List<CategoryInfo> {
        return categoryService.getCategories(languageCode)
    }

    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long,
        @RequestParam languageCode: String
    ): CategoryInfo {
        return categoryService.getCategory(categoryId, languageCode)
    }

    @GetMapping("/questions/{categoryId}")
    fun getQuestionsForCategory(
        @PathVariable categoryId: Long,
        @RequestParam languageCode: String
    ): List<QuestionInfo> {
        return categoryService.getQuestionsForCategory(categoryId, languageCode)
    }
}