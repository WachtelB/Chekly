package com.nordix.chekly.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.nordix.chekly.main.CategoryExpense
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun MonthlySummaryCard(
    categories: List<CategoryExpense>,
    isLargeFont: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CheklyDimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
        ) {
            if (isLargeFont) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(categories = categories)
                }
                CategoryLegend(categories = categories)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)
                ) {
                    DonutChart(categories = categories)
                    CategoryLegend(categories = categories, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryLegend(
    categories: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
    ) {
        categories.forEach { category ->
            val formattedAmount = "%,d".format(category.amountRub).replace(',', ' ')
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(CheklyDimens.CategoryDotSize)
                        .clip(CircleShape)
                        .background(category.color)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$formattedAmount ₽",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}