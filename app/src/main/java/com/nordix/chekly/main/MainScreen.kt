package com.nordix.chekly.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    selectedMonth: String,
    selectedYear: Int,
    currentTab: Int,
    isInitialLoading: Boolean,
    onTabSelected: (Int) -> Unit,
    onMonthClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onAddReceiptClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val fontScale = LocalConfiguration.current.fontScale
    val isLargeFont = fontScale >= 1.5f
    val categories = remember(receipts) { calculateCategoryExpenses(receipts) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBar(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (currentTab == 0 && !isInitialLoading) {
                ExtendedFloatingActionButton(
                    onClick = onAddReceiptClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(CheklyDimens.SpacingSmall))
                    Text(text = stringResource(R.string.button_add_receipt))
                }
            }
        }
    ) { innerPadding ->
        when {
            isInitialLoading || currentTab == 3 -> {
                LoadingStateView(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            currentTab == 1 || currentTab == 2 -> {
                ErrorStateView(
                    onRetryClick = { onTabSelected(0) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + CheklyDimens.SpacingSmall,
                        bottom = innerPadding.calculateBottomPadding() + CheklyDimens.FabBottomClearance,
                        start = CheklyDimens.ScreenPadding,
                        end = CheklyDimens.ScreenPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)
                ) {
                    item {
                        TopHeaderBar(
                            month = selectedMonth,
                            year = selectedYear,
                            onMonthClick = onMonthClick,
                            onProfileClick = onProfileClick
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = stringResource(R.string.main_title_expenses),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (receipts.isEmpty()) {
                        item {
                            EmptyStateView(
                                monthName = selectedMonth,
                                year = selectedYear,
                                onAddReceiptClick = onAddReceiptClick
                            )
                        }
                    } else {
                        item {
                            MonthlySummaryCard(
                                categories = categories,
                                receiptCount = receipts.size,
                                monthName = selectedMonth.lowercase(),
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
}