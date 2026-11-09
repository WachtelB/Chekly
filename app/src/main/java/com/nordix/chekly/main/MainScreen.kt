package com.nordix.chekly.main

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.nordix.chekly.R
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun MainScreen(
    state: MainUiState,
    onAction: (MainAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontScale = LocalConfiguration.current.fontScale
    val isLargeFont = fontScale >= 1.5f

    Column(modifier = modifier.fillMaxSize()) {
        TopHeaderBar(
            periodText = state.periodText,
            onPrevClick = { onAction(MainAction.PrevMonthClick) },
            onNextClick = { onAction(MainAction.NextMonthClick) },
            onProfileClick = { onAction(MainAction.ProfileClick) },
            onPeriodClick = { onAction(MainAction.OpenMonthPicker) }
        )

        when (val result = state.result) {
            MainResult.Loading -> {
                LoadingStateView()
            }
            is MainResult.Error -> {
                ErrorStateView(
                    message = result.message,
                    onRetryClick = { onAction(MainAction.Retry) }
                )
            }
            MainResult.Empty -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(CheklyDimens.ScreenPadding)
                ) {
                    item {
                        EmptyStateView(
                            monthName = state.selectedMonth,
                            year = state.selectedYear,
                            onAddReceiptClick = { onAction(MainAction.AddReceiptClick) }
                        )
                    }
                }
            }
            is MainResult.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = CheklyDimens.SpacingLarge,
                        bottom = CheklyDimens.ScreenPadding,
                        start = CheklyDimens.ScreenPadding,
                        end = CheklyDimens.ScreenPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)
                ) {
                    item {
                        MonthlySummaryCard(
                            categories = result.categories,
                            isLargeFont = isLargeFont
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.section_recent_receipts),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.semantics { heading() }
                            )
                            TextButton(
                                onClick = { onAction(MainAction.SeeAllClick) },
                                modifier = Modifier.heightIn(min = CheklyDimens.MinTouchTarget)
                            ) {
                                Text(stringResource(R.string.button_see_all))
                            }
                        }
                    }

                    items(
                        items = result.receipts,
                        key = { it.id }
                    ) { receipt ->
                        ReceiptCard(
                            receipt = receipt,
                            modifier = Modifier.clickable {
                                onAction(MainAction.ReceiptClicked(receipt.shopName))
                            }
                        )
                    }
                }
            }
        }
    }
}