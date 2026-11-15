package com.nordix.chekly.main

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.nordix.chekly.data.Receipt
import com.nordix.chekly.ui.theme.CategoryColors

@Immutable
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