package com.nordix.chekly.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.nordix.chekly.R
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun CheklyBottomBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontScale = LocalConfiguration.current.fontScale
    val showLabels = fontScale < 1.5f

    val homeLabel = stringResource(R.string.nav_home)
    val historyLabel = stringResource(R.string.nav_history)
    val addLabel = stringResource(R.string.nav_add)
    val budgetLabel = stringResource(R.string.nav_budget)
    val challengesLabel = stringResource(R.string.nav_challenges)

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = if (showLabels) null else homeLabel
                )
            },
            label = if (showLabels) {
                { Text(homeLabel) }
            } else null
        )
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = if (showLabels) null else historyLabel
                )
            },
            label = if (showLabels) {
                { Text(historyLabel) }
            } else null
        )
        NavigationBarItem(
            selected = currentTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(CheklyDimens.MinTouchTarget)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (showLabels) null else addLabel,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            label = if (showLabels) {
                { Text(addLabel) }
            } else null
        )
        NavigationBarItem(
            selected = currentTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = if (showLabels) null else budgetLabel
                )
            },
            label = if (showLabels) {
                { Text(budgetLabel) }
            } else null
        )
        NavigationBarItem(
            selected = currentTab == 4,
            onClick = { onTabSelected(4) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = if (showLabels) null else challengesLabel
                )
            },
            label = if (showLabels) {
                { Text(challengesLabel) }
            } else null
        )
    }
}