package org.example.servlyback.util

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.servlyback.entities.Category
import org.example.servlyback.entities.CategoryTranslation
import org.example.servlyback.entities.Question
import org.example.servlyback.entities.QuestionTranslation
import org.example.servlyback.features_customer.category.CategoryRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@JsonIgnoreProperties(ignoreUnknown = true)
data class CategoryJson(
    val icon: String,
    val nameTranslations: Map<String, String>,
    val questionsWithTranslations: List<Map<String, Map<String, String>>>
)

@Component
class DataInitializer(
    private val categoryRepository: CategoryRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        initCategories()
    }

    private fun initCategories() {
        if (categoryRepository.count() == 0L) {
            val categoryJsonList = loadCategoriesFromJson()
            val categories = mapCategoriesToCreateCategoryFormat(categoryJsonList)

            categoryRepository.saveAll(categories)
        }
    }

    private fun generateCategories(): List<Category> {
        val categories = listOf(
            createCategory(
                icon = "electrician",
                nameTranslations = mapOf("pl" to "Elektryk", "en" to "Electrician"),
                questionsWithTranslations = listOf(
                    "Question 1" to mapOf(
                        "pl" to "Czy posiadasz certyfikat?",
                        "en" to "Do you have a certificate?"
                    ),
                    "Question 2" to mapOf(
                        "pl" to "Ile lat doświadczenia posiadasz?",
                        "en" to "How many years of experience do you have?"
                    )
                )
            )
        )
        return categories
    }

    private fun createCategory(
        icon: String,
        nameTranslations: Map<String, String>,
        questionsWithTranslations: List<Pair<String, Map<String, String>>>
    ): Category {
        val category = Category(icon = icon)

        category.translations.addAll(
            nameTranslations.map { (languageCode, name) ->
                CategoryTranslation(
                    category = category,
                    languageCode = languageCode,
                    name = name
                )
            }
        )
        category.questions.addAll(
            questionsWithTranslations.map { (defaultText, translations) ->
                val question = Question(category = category)
                question.translations.addAll(
                    translations.map { (languageCode, text) ->
                        QuestionTranslation(
                            question = question,
                            languageCode = languageCode,
                            text = text
                        )
                    }
                )
                question
            }
        )
        return category
    }

    private fun loadCategoriesFromJson(): List<CategoryJson> {
        val mapper = jacksonObjectMapper()
        println("LOADING JSON: categories.json")
        val jsonFile = ClassPathResource("categories.json").inputStream
        return mapper.readValue(jsonFile)
    }

    private fun mapCategoriesToCreateCategoryFormat(categoryJsonList: List<CategoryJson>): List<Category> {
        return categoryJsonList.map { categoryJson ->
            createCategory(
                icon = categoryJson.icon,
                nameTranslations = categoryJson.nameTranslations,
                questionsWithTranslations = categoryJson.questionsWithTranslations.map { questionMap ->
                    val (questionKey, translations) = questionMap.entries.first()
                    questionKey to translations
                }
            )
        }
    }
}

