package com.nordix.chekly.history

import androidx.compose.runtime.Immutable
import com.nordix.chekly.data.Receipt
import com.nordix.chekly.main.components.allMonths

@Immutable
sealed interface HistoryListResult {
    data object Loading : HistoryListResult
    @Immutable
    data class Content(val receipts: List<Receipt>) : HistoryListResult
    data object Empty : HistoryListResult
    @Immutable
    data class Error(val message: String) : HistoryListResult
}

@Immutable
data class HistoryUiState(
    val query: String = "",
    val selectedCategory: String? = null,
    val selectedMonthIndex: Int = 9,
    val selectedYear: Int = 2026,
    val availableCategories: List<String> = listOf("Продукты", "Транспорт", "Кафе", "Развлечения"),
    val result: HistoryListResult = HistoryListResult.Loading
) {
    val selectedMonth: String
        get() = allMonths.getOrElse(selectedMonthIndex) { allMonths.first() }

    val periodText: String
        get() = "$selectedMonth $selectedYear"
}