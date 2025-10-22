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
package `in`.procyk.compose.calendar.week

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
import `in`.procyk.compose.calendar.day.WeekDay
import `in`.procyk.compose.calendar.header.WeekState
import `in`.procyk.compose.calendar.month.coerceSnapIndex
import `in`.procyk.compose.calendar.selection.SelectionState
import `in`.procyk.compose.calendar.util.SnapOffsets
import `in`.procyk.compose.calendar.util.SnapperFlingBehaviorDefaults
import `in`.procyk.compose.calendar.util.rememberSnapperFlingBehavior
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
@Suppress("LongMethod")
internal fun <T : SelectionState> WeekPager(
    initialWeek: Week,
    selectionState: T,
    weekState: WeekState,
    daysOfWeek: List<DayOfWeek>,
    today: LocalDate,
    modifier: Modifier = Modifier,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit,
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val initialFirstVisibleItemIndex = remember(initialWeek, weekState.minWeek) {
        weeksBetween(weekState.minWeek, initialWeek)
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

    val weekListState = remember {
        WeekListState(
            coroutineScope = coroutineScope,
            weekState = weekState,
            listState = listState,
        )
    }
    Column(
        modifier = modifier,
    ) {
        if (weekDaysScrollEnabled.not()) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                content = { daysOfWeekHeader(daysOfWeek) },
            )
        }
        val pagerCount = remember(weekState.minWeek, weekState.maxWeek) {
            weeksBetween(weekState.minWeek, weekState.maxWeek) + 1
        }
        LazyRow(
            modifier = modifier.testTag("WeekPager"),
            state = listState,
            flingBehavior = flingBehavior,
            verticalAlignment = Alignment.Top,
        ) {
            items(
                count = pagerCount,
                key = { index -> weekListState.getWeekForPage(index).start.let { "${it.month}-${it.day}" } },
            ) { index ->
                WeekContent(
                    modifier = Modifier.fillParentMaxWidth(),
                    daysOfWeek = daysOfWeek,
                    weekDays = weekListState.getWeekForPage(index).getWeekDays(today = today),
                    selectionState = selectionState,
                    weekDaysScrollEnabled = weekDaysScrollEnabled,
                    dayContent = dayContent,
                    daysOfWeekHeader = daysOfWeekHeader,
                )
            }
        }
    }
}

@Composable
internal fun <T : SelectionState> WeekContent(
    selectionState: T,
    weekDays: WeekDays,
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit,
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        if (weekDaysScrollEnabled) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                content = { daysOfWeekHeader(daysOfWeek) },
            )
        }
        WeekRow(
            weekDays = weekDays,
            modifier = Modifier.fillMaxWidth(),
            selectionState = selectionState,
            dayContent = dayContent,
        )
    }
}

internal fun Week.getWeekDays(today: LocalDate): WeekDays {
    val weekDays = days.map {
        WeekDay(
            date = it,
            isCurrentDay = it == today,
            isFromCurrentMonth = true,
        )
    }

    return WeekDays(
        isFirstWeekOfTheMonth = false,
        days = weekDays,
    )
}
