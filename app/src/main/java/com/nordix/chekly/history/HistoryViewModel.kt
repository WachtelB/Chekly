package com.nordix.chekly.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordix.chekly.data.ReceiptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val KEY_QUERY = "history_search_query"
private const val KEY_CATEGORY = "history_selected_category"

class HistoryViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val queryFlow = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val categoryFlow = savedStateHandle.getStateFlow<String?>(KEY_CATEGORY, null)

    val uiState: StateFlow<HistoryUiState> = combine(
        ReceiptRepository.receipts,
        queryFlow,
        categoryFlow
    ) { receipts, query, category ->
        val cleanQuery = query.trim()
        val result = when {
            cleanQuery.equals("error", ignoreCase = true) -> {
                HistoryListResult.Error("Ошибка фильтрации чеков. Проверьте запрос.")
            }
            else -> {
                val filtered = receipts.filter { receipt ->
                    val matchesCategory = category == null || receipt.category == category
                    val matchesQuery = cleanQuery.isEmpty() ||
                            receipt.shopName.contains(cleanQuery, ignoreCase = true) ||
                            receipt.items.any { it.name.contains(cleanQuery, ignoreCase = true) }
                    matchesCategory && matchesQuery
                }
                if (filtered.isEmpty()) HistoryListResult.Empty
                else HistoryListResult.Content(filtered)
            }
        }
        HistoryUiState(
            query = query,
            selectedCategory = category,
            result = result
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun onQueryChange(newQuery: String) {
        savedStateHandle[KEY_QUERY] = newQuery
    }

    fun onCategoryToggle(category: String?) {
        val current = savedStateHandle.get<String?>(KEY_CATEGORY)
        savedStateHandle[KEY_CATEGORY] = if (current == category) null else category
    }

    fun onResetFilters() {
        savedStateHandle[KEY_QUERY] = ""
        savedStateHandle[KEY_CATEGORY] = null
    }
}