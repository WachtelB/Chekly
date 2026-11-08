package com.nordix.chekly.main

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
    receipts: List<Receipt>,
    categories: List<CategoryExpense>,
    periodText: String,
    isLoading: Boolean,
    isError: Boolean,
    onPrevMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddReceiptClick: () -> Unit,
    onPeriodClick: () -> Unit = {}
) {
    val fontScale = LocalConfiguration.current.fontScale
    val isLargeFont = fontScale >= 1.5f

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeaderBar(
            periodText = periodText,
            onPrevClick = onPrevMonthClick,
            onNextClick = onNextMonthClick,
            onProfileClick = onProfileClick,
            onPeriodClick = onPeriodClick
        )

        if (isLoading) {
            LoadingStateView()
        } else if (isError) {
            ErrorStateView(onRetryClick = {})
        } else {
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
                if (receipts.isEmpty()) {
                    item {
                        EmptyStateView(
                            monthName = periodText,
                            year = 2026,
                            onAddReceiptClick = onAddReceiptClick
                        )
                    }
                } else {
                    item {
                        MonthlySummaryCard(
                            categories = categories,
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
                                onClick = onSeeAllClick,
                                modifier = Modifier.heightIn(min = CheklyDimens.MinTouchTarget)
                            ) {
                                Text(stringResource(R.string.button_see_all))
                            }
                        }
                    }

                    items(
                        items = receipts,
                        key = { it.id }
                    ) { receipt ->
                        ReceiptCard(receipt = receipt)
                    }
                }
            }
        }
    }
}