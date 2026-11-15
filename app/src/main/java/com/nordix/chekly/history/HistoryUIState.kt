package com.nordix.chekly.history

import androidx.compose.runtime.Immutable
import com.nordix.chekly.data.Receipt

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
    val availableCategories: List<String> = listOf("Продукты", "Транспорт", "Кафе", "Развлечения"),
    val result: HistoryListResult = HistoryListResult.Loading
)