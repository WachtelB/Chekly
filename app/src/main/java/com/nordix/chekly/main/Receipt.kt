package com.nordix.chekly.main

import androidx.compose.ui.graphics.Color
import com.nordix.chekly.ui.theme.CategoryColors

data class Receipt(
    val id: String,
    val shopName: String,
    val category: String,
    val amountRub: Long,
    val dateText: String
)

data class CategoryExpense(
    val name: String,
    val amountRub: Long,
    val color: Color
)

fun colorForCategory(category: String): Color = when (category) {
    "Продукты" -> CategoryColors.Groceries
    "Транспорт" -> CategoryColors.Transport
    "Кафе" -> CategoryColors.Cafes
    "Развлечения" -> CategoryColors.Entertainment
    else -> CategoryColors.Groceries
}

fun calculateCategoryExpenses(receipts: List<Receipt>): List<CategoryExpense> {
    return receipts
        .groupBy { it.category }
        .map { (categoryName, items) ->
            CategoryExpense(
                name = categoryName,
                amountRub = items.sumOf { it.amountRub },
                color = colorForCategory(categoryName)
            )
        }
        .sortedByDescending { it.amountRub }
}

val sampleReceipts = listOf(
    Receipt("1", "Пятёрочка", "Продукты", 21200, "25 окт."),
    Receipt("2", "Яндекс Заправки", "Транспорт", 10500, "24 окт."),
    Receipt("3", "Вкусно и точка", "Кафе", 8000, "24 окт."),
    Receipt("4", "Киномакс", "Развлечения", 5500, "22 окт.")
)