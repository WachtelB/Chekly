package com.nordix.chekly.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nordix.chekly.R
import com.nordix.chekly.ui.theme.CheklyDimens
import kotlinx.coroutines.launch
import kotlin.math.abs

val allMonths = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
)

private val availableYears = (1970..2100).toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerBottomSheet(
    currentMonth: String,
    currentYear: Int,
    onDismiss: () -> Unit,
    onApply: (month: String, year: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val initialMonthIndex = allMonths.indexOf(currentMonth).coerceAtLeast(0)
    var selectedMonthIndex by rememberSaveable { mutableIntStateOf(initialMonthIndex) }
    var selectedYear by rememberSaveable { mutableIntStateOf(currentYear.coerceIn(1970, 2100)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MonthYearSpinnerContent(
            selectedMonthIndex = selectedMonthIndex,
            selectedYear = selectedYear,
            onMonthIndexChanged = { selectedMonthIndex = it },
            onYearChanged = { selectedYear = it },
            onApplyClick = {
                onApply(allMonths[selectedMonthIndex], selectedYear)
            }
        )
    }
}

@Composable
fun MonthYearSpinnerContent(
    selectedMonthIndex: Int,
    selectedYear: Int,
    onMonthIndexChanged: (Int) -> Unit,
    onYearChanged: (Int) -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val yearIndex = availableYears.indexOf(selectedYear).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CheklyDimens.SheetHorizontalPadding)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.sheet_select_month),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { heading() }
        )

        Spacer(modifier = Modifier.height(CheklyDimens.SpacingLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpinnerWheelPicker(
                items = allMonths,
                initialIndex = selectedMonthIndex,
                onItemSelected = { index, _ -> onMonthIndexChanged(index) },
                modifier = Modifier.weight(1.3f)
            )

            SpinnerWheelPicker(
                items = availableYears.map { it.toString() },
                initialIndex = yearIndex,
                onItemSelected = { index, _ -> onYearChanged(availableYears[index]) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(CheklyDimens.SpacingLarge))

        Button(
            onClick = onApplyClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = CheklyDimens.PrimaryButtonHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.button_apply),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(CheklyDimens.SpacingLarge))
    }
}

@Composable
fun SpinnerWheelPicker(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (index: Int, item: String) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 3
) {
    val halfVisible = visibleItemsCount / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                initialIndex
            } else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index
                    ?: initialIndex
            }
        }
    }

    LaunchedEffect(centerIndex) {
        if (centerIndex in items.indices) {
            onItemSelected(centerIndex, items[centerIndex])
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItemsCount),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * halfVisible),
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val isSelected = index == centerIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            onItemSelected(index, items[index])
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        style = if (isSelected) {
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(if (isSelected) 1f else 0.55f)
                    )
                }
            }
        }
    }
}