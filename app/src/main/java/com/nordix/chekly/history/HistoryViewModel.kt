package com.nordix.chekly.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordix.chekly.data.ReceiptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Locale

private const val KEY_QUERY = "history_search_query"
private const val KEY_CATEGORY = "history_selected_category"
private val russianLocale = Locale.forLanguageTag("ru-RU")

private fun String.normalizeForSearch(): String {
    return this.trim()
        .lowercase(russianLocale)
        .replace('ё', 'е')
}

class HistoryViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val queryFlow = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val categoryFlow = savedStateHandle.getStateFlow<String?>(KEY_CATEGORY, null)

    val uiState: StateFlow<HistoryUiState> = combine(
        ReceiptRepository.receipts,
        ReceiptRepository.selectedPeriod,
        queryFlow,
        categoryFlow
    ) { receipts, period, query, category ->
        val normalizedQuery = query.normalizeForSearch()

        val result = when {
            normalizedQuery == "error" || normalizedQuery == "ошибка" -> {
                HistoryListResult.Error("Ошибка фильтрации чеков. Проверьте запрос.")
            }
            period.monthIndex == 8 && period.year == 2026 -> {
                HistoryListResult.Loading
            }
            period.monthIndex == 7 && period.year == 2026 -> {
                HistoryListResult.Error("Не удалось загрузить историю за август 2026.")
            }
            else -> {
                val filtered = receipts.filter { receipt ->
                    val matchesPeriod =
                        receipt.monthIndex == period.monthIndex && receipt.year == period.year
                    val matchesCategory = category == null || receipt.category == category
                    val matchesQuery = normalizedQuery.isEmpty() ||
                            receipt.shopName.normalizeForSearch().contains(normalizedQuery) ||
                            receipt.category.normalizeForSearch().contains(normalizedQuery) ||
                            receipt.shopAddress.normalizeForSearch().contains(normalizedQuery) ||
                            receipt.items.any { item ->
                                item.name.normalizeForSearch().contains(normalizedQuery)
                            }
                    matchesPeriod && matchesCategory && matchesQuery
                }
                if (filtered.isEmpty()) HistoryListResult.Empty
                else HistoryListResult.Content(filtered)
            }
        }
        HistoryUiState(
            query = query,
            selectedCategory = category,
            selectedMonthIndex = period.monthIndex,
            selectedYear = period.year,
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