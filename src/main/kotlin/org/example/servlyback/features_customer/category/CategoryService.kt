package org.example.servlyback.features_customer.category

import org.example.servlyback.dto.CategoryInfo
import org.example.servlyback.dto.QuestionInfo
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun getCategories(languageCode: String): List<CategoryInfo> {
        return categoryRepository.findAll().map { category ->
            val translation = category.translations.find { it.languageCode == languageCode }
                ?: throw IllegalArgumentException("Translation not found for language: $languageCode")

            CategoryInfo(
                id = category.id,
                name = translation.name
            )
        }
    }

    fun getQuestionsForCategory(categoryId: Long, languageCode: String): List<QuestionInfo> {
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("Category not found with id: $categoryId") }

        return category.questions.map { question ->
            val translation = question.translations.find { it.languageCode == languageCode }
                ?: throw IllegalArgumentException("Question translation not found for language: $languageCode")

            QuestionInfo(
                id = question.id,
                text = translation.text
            )
        }
    }
}