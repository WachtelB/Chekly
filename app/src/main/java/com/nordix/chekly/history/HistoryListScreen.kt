package com.nordix.chekly.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nordix.chekly.main.components.CheklySearchField
import com.nordix.chekly.main.states.EmptyStateView
import com.nordix.chekly.main.states.ErrorStateView
import com.nordix.chekly.main.states.LoadingStateView
import com.nordix.chekly.main.components.ReceiptCard
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun HistoryListScreenContainer(
    onReceiptClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryListScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelect = viewModel::onCategoryToggle,
        onResetFilters = viewModel::onResetFilters,
        onReceiptClick = onReceiptClick,
        modifier = modifier
    )
}

@Composable
fun HistoryListScreen(
    state: HistoryUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onReceiptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = CheklyDimens.ScreenPadding)
    ) {
        Text(
            text = "История покупок",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = CheklyDimens.ScreenPadding)
        )

        CheklySearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            placeholderText = "Поиск по магазину или товару…",
            modifier = Modifier.padding(
                horizontal = CheklyDimens.ScreenPadding,
                vertical = CheklyDimens.SpacingSmall
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = CheklyDimens.ScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onCategorySelect(null) },
                    label = { Text("Все") }
                )
            }
            items(state.availableCategories) { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category) }
                )
            }
        }

        when (val result = state.result) {
            HistoryListResult.Loading -> LoadingStateView()
            is HistoryListResult.Error -> ErrorStateView(
                message = result.message,
                buttonText = "Сбросить фильтр",
                onRetryClick = onResetFilters
            )
            HistoryListResult.Empty -> EmptyStateView(
                monthName = "выбранным фильтрам",
                year = 2026,
                onAddReceiptClick = onResetFilters
            )
            is HistoryListResult.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(CheklyDimens.ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
                ) {
                    items(items = result.receipts, key = { it.id }) { receipt ->
                        ReceiptCard(
                            receipt = receipt,
                            modifier = Modifier.clickable { onReceiptClick(receipt.id) }
                        )
                    }
                }
            }
        }
    }
}