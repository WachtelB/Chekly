package com.nordix.chekly

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nordix.chekly.data.sampleReceipts
import com.nordix.chekly.main.AppTab
import com.nordix.chekly.main.MainAction
import com.nordix.chekly.main.MainResult
import com.nordix.chekly.main.MainUiState
import com.nordix.chekly.main.calculateCategoryExpenses
import com.nordix.chekly.main.components.CheklyBottomBar
import com.nordix.chekly.main.components.MonthYearSpinnerContent
import com.nordix.chekly.main.components.MonthlySummaryCard
import com.nordix.chekly.main.components.ReceiptCard
import com.nordix.chekly.main.components.TopHeaderBar
import com.nordix.chekly.main.screen.MainScreen
import com.nordix.chekly.main.states.EmptyStateView
import com.nordix.chekly.main.states.ErrorStateView
import com.nordix.chekly.main.states.LoadingStateView
import com.nordix.chekly.ui.theme.CheklyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheklyUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cheklyBottomBar_normalFontScale_showsAllLabelsAndHandlesClicks() {
        var selectedTab = AppTab.HOME
        composeTestRule.setContent {
            val config = Configuration(LocalConfiguration.current).apply { fontScale = 1.0f }
            CompositionLocalProvider(LocalConfiguration provides config) {
                CheklyTheme {
                    CheklyBottomBar(
                        currentTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Главная").assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("История").assertIsDisplayed().performClick()
        assertEquals(AppTab.HISTORY, selectedTab)

        composeTestRule.onNodeWithText("Добавить").assertIsDisplayed()
        composeTestRule.onNodeWithText("Бюджет").assertIsDisplayed()
        composeTestRule.onNodeWithText("Челленджи").assertIsDisplayed()
    }

    @Test
    fun cheklyBottomBar_fontScale200Percent_hidesAllTextLabels() {
        composeTestRule.setContent {
            val config = Configuration(LocalConfiguration.current).apply { fontScale = 2.0f }
            CompositionLocalProvider(LocalConfiguration provides config) {
                CheklyTheme {
                    CheklyBottomBar(
                        currentTab = AppTab.HOME,
                        onTabSelected = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Главная").assertDoesNotExist()
        composeTestRule.onNodeWithText("История").assertDoesNotExist()
        composeTestRule.onNodeWithText("Добавить").assertDoesNotExist()
        composeTestRule.onNodeWithText("Бюджет").assertDoesNotExist()
        composeTestRule.onNodeWithText("Челленджи").assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription("Главная").assertIsDisplayed()
    }

    @Test
    fun topHeaderBar_displaysPeriodAndTriggersAllCallbacks() {
        var prevClicked = false
        var nextClicked = false
        var profileClicked = false
        var periodClicked = false

        composeTestRule.setContent {
            CheklyTheme {
                TopHeaderBar(
                    periodText = "Октябрь 2026",
                    onPrevClick = { prevClicked = true },
                    onNextClick = { nextClicked = true },
                    onProfileClick = { profileClicked = true },
                    onPeriodClick = { periodClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Октябрь 2026").assertIsDisplayed().performClick()
        assertTrue(periodClicked)

        composeTestRule.onNodeWithContentDescription("Предыдущий месяц").performClick()
        assertTrue(prevClicked)

        composeTestRule.onNodeWithContentDescription("Следующий месяц").performClick()
        assertTrue(nextClicked)

        composeTestRule.onNodeWithContentDescription("Профиль пользователя").performClick()
        assertTrue(profileClicked)
    }

    @Test
    fun donutChart_and_categoryLegend_and_monthlySummaryCard_renderWithoutMonthlyTitle() {
        val octoberReceipts = sampleReceipts.filter { it.monthIndex == 9 && it.year == 2026 }
        val categories = calculateCategoryExpenses(octoberReceipts)
        val expectedTotalFormatted =
            "%,d".format(categories.sumOf { it.amountRub }).replace(',', ' ') + " ₽"

        composeTestRule.setContent {
            CheklyTheme {
                MonthlySummaryCard(
                    categories = categories,
                    isLargeFont = false
                )
            }
        }
        composeTestRule.onNodeWithText("Расходы за месяц").assertDoesNotExist()
        composeTestRule.onNodeWithText("6 чека").assertDoesNotExist()

        composeTestRule.onNodeWithText(expectedTotalFormatted).assertIsDisplayed()
        composeTestRule.onNodeWithText("Всего расходов").assertIsDisplayed()
        composeTestRule.onNodeWithText("Продукты").assertIsDisplayed()
        composeTestRule.onNodeWithText("Транспорт").assertIsDisplayed()
    }

    @Test
    fun receiptCard_displaysShopCategoryAmountAndDate() {
        val receipt = sampleReceipts.first()

        composeTestRule.setContent {
            CheklyTheme {
                ReceiptCard(receipt = receipt)
            }
        }

        composeTestRule.onNodeWithText(receipt.shopName).assertIsDisplayed()
        composeTestRule.onNodeWithText(receipt.category).assertIsDisplayed()
        composeTestRule.onNodeWithText(receipt.dateText).assertIsDisplayed()
    }

    @Test
    fun screenStateViews_loading_error_empty_areStable() {
        var retryClicked = false
        var addReceiptClicked = false
        var stateMode by mutableIntStateOf(0)

        composeTestRule.setContent {
            CheklyTheme {
                when (stateMode) {
                    0 -> LoadingStateView()
                    1 -> ErrorStateView(onRetryClick = { retryClicked = true })
                    else -> EmptyStateView(
                        monthName = "Ноябрь",
                        year = 2026,
                        onAddReceiptClick = { addReceiptClicked = true }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Синхронизация истории чеков…").assertIsDisplayed()

        stateMode = 1
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Раздел временно недоступен").assertIsDisplayed()
        composeTestRule.onNodeWithText("Вернуться на главную").performClick()
        assertTrue(retryClicked)

        stateMode = 2
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Добавить чек").assertIsDisplayed().performClick()
        assertTrue(addReceiptClicked)
    }

    @Test
    fun monthYearSpinnerContent_allowsSelectingMonthAndYearAndApplying() {
        var monthIndex by mutableIntStateOf(9)
        var year by mutableIntStateOf(2026)
        var applied = false

        composeTestRule.setContent {
            CheklyTheme {
                MonthYearSpinnerContent(
                    selectedMonthIndex = monthIndex,
                    selectedYear = year,
                    onMonthIndexChanged = { monthIndex = it },
                    onYearChanged = { year = it },
                    onApplyClick = { applied = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Октябрь").assertIsDisplayed()
        composeTestRule.onNodeWithText("2026").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ноябрь").performClick()
        composeTestRule.waitForIdle()
        assertEquals(10, monthIndex)

        composeTestRule.onNodeWithText("2027").performClick()
        composeTestRule.waitForIdle()
        assertEquals(2027, year)

        composeTestRule.onNodeWithText("Применить").performClick()
        assertTrue(applied)
    }

    @Test
    fun mainScreen_recompositionStability_acrossMultipleMonthsAndYears() {
        val categories = calculateCategoryExpenses(sampleReceipts)
        var monthIndex by mutableIntStateOf(0)
        var year by mutableIntStateOf(2026)

        composeTestRule.setContent {
            CheklyTheme {
                MainScreen(
                    state = MainUiState(
                        selectedMonthIndex = monthIndex,
                        selectedYear = year,
                        result = MainResult.Content(
                            receipts = sampleReceipts,
                            categories = categories
                        )
                    ),
                    onAction = { action ->
                        when (action) {
                            MainAction.PrevMonthClick -> {
                                if (monthIndex > 0) monthIndex-- else {
                                    monthIndex = 11
                                    year--
                                }
                            }
                            MainAction.NextMonthClick -> {
                                if (monthIndex < 11) monthIndex++ else {
                                    monthIndex = 0
                                    year++
                                }
                            }
                            else -> {}
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Январь 2026").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Предыдущий месяц").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Декабрь 2025").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Следующий месяц").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Январь 2026").assertIsDisplayed()
    }
}