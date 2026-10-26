package com.nordix.chekly.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nordix.chekly.R
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun MainScreen(
    receipts: List<Receipt>,
    selectedMonth: String,
    selectedYear: Int,
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    onMonthClick: () -> Unit
) {
    val fontScale = LocalConfiguration.current.fontScale
    val isLargeFont = fontScale >= 1.5f
    val categories = remember(receipts) { calculateCategoryExpenses(receipts) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_challenges)) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + CheklyDimens.SpacingSmall,
                bottom = innerPadding.calculateBottomPadding() + CheklyDimens.ScreenPadding,
                start = CheklyDimens.ScreenPadding,
                end = CheklyDimens.ScreenPadding
            ),
            verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)
        ) {
            item {
                TopHeaderBar(
                    month = selectedMonth,
                    year = selectedYear,
                    onMonthClick = onMonthClick
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
                    EmptyStateView(monthName = selectedMonth, year = selectedYear)
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
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(
                            onClick = {},
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

@Composable
private fun EmptyStateView(monthName: String, year: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "За $monthName $year пока нет чеков",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Добавьте чек или импортируйте покупки",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TopHeaderBar(month: String, year: Int, onMonthClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .heightIn(min = CheklyDimens.MinTouchTarget)
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onMonthClick)
                .semantics { role = Role.Button },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = CheklyDimens.SpacingLarge,
                    vertical = CheklyDimens.SpacingSmall
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
            ) {
                Text(
                    text = "$month $year",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.a11y_change_month),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(CheklyDimens.AvatarBoxSize)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.a11y_profile),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(categories: List<CategoryExpense>, receiptCount: Int, monthName: String, isLargeFont: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CheklyDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.card_monthly_expenses),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.receipts_count_format, receiptCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isLargeFont) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { DonutChart(categories = categories, monthName = monthName) }
                CategoryLegend(categories = categories)
            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)) {
                    DonutChart(categories = categories, monthName = monthName)
                    CategoryLegend(categories = categories, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryLegend(categories: List<CategoryExpense>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)) {
        categories.forEach { category ->
            val formattedAmount = "%,d".format(category.amountRub).replace(',', ' ')
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)) {
                Box(modifier = Modifier.size(CheklyDimens.CategoryDotSize).clip(CircleShape).background(category.color))
                Text(text = category.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(text = "$formattedAmount ₽", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun ReceiptCard(receipt: Receipt) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(CheklyDimens.ScreenPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
        ) {
            Box(modifier = Modifier.size(CheklyDimens.IconBoxSize).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ShoppingCart, null, tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(receipt.shopName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(receipt.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(stringResource(R.string.receipt_amount_format, receipt.amountRub), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(receipt.dateText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}