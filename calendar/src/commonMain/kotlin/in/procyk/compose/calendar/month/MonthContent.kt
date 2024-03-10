/**
 * Based on ComposeCalendar by Bogusz Pawłowski from [Github](https://github.com/boguszpawlowski/ComposeCalendar)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package `in`.procyk.compose.calendar.month

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import `in`.procyk.compose.calendar.day.DayState
import `in`.procyk.compose.calendar.header.MonthState
import `in`.procyk.compose.calendar.selection.SelectionState
import `in`.procyk.compose.calendar.util.SnapOffsets
import `in`.procyk.compose.calendar.util.SnapperFlingBehaviorDefaults
import `in`.procyk.compose.calendar.util.SnapperLayoutInfo
import `in`.procyk.compose.calendar.util.rememberSnapperFlingBehavior
import `in`.procyk.compose.calendar.week.WeekRow
import `in`.procyk.compose.calendar.week.getWeeks
import `in`.procyk.compose.calendar.year.YearMonth
import `in`.procyk.compose.calendar.year.YearMonth.Companion.monthsBetween
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
@Suppress("LongMethod")
internal fun <T : SelectionState> MonthPager(
    initialMonth: YearMonth,
    showAdjacentMonths: Boolean,
    selectionState: T,
    monthState: MonthState,
    daysOfWeek: List<DayOfWeek>,
    today: LocalDate,
    modifier: Modifier = Modifier,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit,
    weekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit,
    monthContainer: @Composable (content: @Composable (PaddingValues) -> Unit) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val initialFirstVisibleItemIndex = remember(initialMonth, monthState.minMonth) {
        monthsBetween(monthState.minMonth, initialMonth).toInt()
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
    )
    val flingBehavior = rememberSnapperFlingBehavior(
        lazyListState = listState,
        snapOffsetForItem = SnapOffsets.Start,
        springAnimationSpec = SnapperFlingBehaviorDefaults.SpringAnimationSpec,
        decayAnimationSpec = rememberSplineBasedDecay(),
        snapIndex = coerceSnapIndex,
    )

    val monthListState = remember {
        MonthListState(
            coroutineScope = coroutineScope,
            monthState = monthState,
            listState = listState,
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (weekDaysScrollEnabled.not()) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                content = { weekHeader(daysOfWeek) },
            )
        }
        val pagerCount = remember(monthState.minMonth, monthState.maxMonth) {
            monthsBetween(monthState.minMonth, monthState.maxMonth).toInt() + 1
        }
        LazyRow(
            modifier = modifier.testTag("MonthPager"),
            state = listState,
            flingBehavior = flingBehavior,
            verticalAlignment = Alignment.Top,
        ) {
            items(
                count = pagerCount,
                key = { index ->
                    monthListState.getMonthForPage(index).let { "${it.year}-${it.month}" }
                }
            ) { index ->
                MonthContent(
                    modifier = Modifier.fillParentMaxWidth(),
                    showAdjacentMonths = showAdjacentMonths,
                    selectionState = selectionState,
                    currentMonth = monthListState.getMonthForPage(index),
                    today = today,
                    weekDaysScrollEnabled = weekDaysScrollEnabled,
                    daysOfWeek = daysOfWeek,
                    dayContent = dayContent,
                    weekHeader = weekHeader,
                    monthContainer = monthContainer
                )
            }
        }
    }
}

@Composable
internal fun <T : SelectionState> MonthContent(
    showAdjacentMonths: Boolean,
    selectionState: T,
    currentMonth: YearMonth,
    daysOfWeek: List<DayOfWeek>,
    today: LocalDate,
    modifier: Modifier = Modifier,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit,
    weekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit,
    monthContainer: @Composable (content: @Composable (PaddingValues) -> Unit) -> Unit,
) {
    Column {
        if (weekDaysScrollEnabled) {
            Box(
                modifier = modifier
                    .wrapContentHeight(),
                content = { weekHeader(daysOfWeek) },
            )
        }
        monthContainer { paddingValues ->
            Column(
                modifier = modifier
                    .padding(paddingValues)
            ) {
                currentMonth.getWeeks(
                    includeAdjacentMonths = showAdjacentMonths,
                    firstDayOfTheWeek = daysOfWeek.first(),
                    today = today,
                ).forEach { week ->
                    WeekRow(
                        weekDays = week,
                        selectionState = selectionState,
                        dayContent = dayContent,
                    )
                }
            }
        }
    }
}

internal val coerceSnapIndex: (SnapperLayoutInfo, startIndex: Int, targetIndex: Int) -> Int =
    { _, startIndex, targetIndex ->
        targetIndex
            .coerceIn(startIndex - 1, startIndex + 1)
    }
