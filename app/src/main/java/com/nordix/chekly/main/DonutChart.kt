package com.nordix.chekly.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nordix.chekly.R
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun DonutChart(
    categories: List<CategoryExpense>,
    monthName: String,
    modifier: Modifier = Modifier
) {
    val totalAmount = categories.sumOf { it.amountRub }
    val totalAmountFloat = totalAmount.coerceAtLeast(1L).toFloat()
    val formattedTotal = "%,d".format(totalAmount).replace(',', ' ')

    Box(
        modifier = modifier.size(CheklyDimens.DonutChartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(CheklyDimens.DonutChartSize)) {
            val strokeWidthPx = CheklyDimens.DonutStrokeWidth.toPx()
            var currentStartAngle = -90f
            val gapAngle = 4f

            categories.forEach { item ->
                val sweepAngle = (item.amountRub / totalAmountFloat) * 360f

                drawArc(
                    color = item.color,
                    startAngle = currentStartAngle + (gapAngle / 2f),
                    sweepAngle = (sweepAngle - gapAngle).coerceAtLeast(1f),
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
                currentStartAngle += sweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$formattedTotal ₽",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.chart_subtitle_format, monthName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}