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
package `in`.procyk.compose.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import `in`.procyk.compose.calendar.day.DayState
import `in`.procyk.compose.calendar.day.DefaultDay
import `in`.procyk.compose.calendar.header.DefaultMonthHeader
import `in`.procyk.compose.calendar.header.MonthState
import `in`.procyk.compose.calendar.month.MonthContent
import `in`.procyk.compose.calendar.month.MonthPager
import `in`.procyk.compose.calendar.selection.DynamicSelectionState
import `in`.procyk.compose.calendar.selection.EmptySelectionState
import `in`.procyk.compose.calendar.selection.SelectionMode
import `in`.procyk.compose.calendar.selection.SelectionState
import `in`.procyk.compose.calendar.util.now
import `in`.procyk.compose.calendar.week.DaysInAWeek
import `in`.procyk.compose.calendar.week.DefaultDaysOfWeekHeader
import `in`.procyk.compose.calendar.week.rotateRight
import `in`.procyk.compose.calendar.year.YearMonth
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * State of the calendar composable
 *
 * @property monthState currently showed month
 * @property selectionState handler for the calendar's selection
 */
@Stable
class CalendarState<T : SelectionState>(
    val monthState: MonthState,
    val selectionState: T,
)

/**
 * [Calendar] implementation using a [DynamicSelectionState] as a selection handler.
 *
 *  * Basic usage:
 * ```
 *  @Composable
 *  fun MainScreen() {
 *    SelectableCalendar()
 *  }
 * ```
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param showAdjacentMonths whenever to show or hide the days from adjacent months
 * @param horizontalSwipeEnabled whenever user is able to change the month by horizontal swipe
 * @param weekDaysScrollEnabled if the week days should be scrollable
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param monthHeader header for showing the current month and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 * @param monthContainer container composable for all the days in current month
 */
@Composable
fun SelectableCalendar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = LocalDate.now(),
    showAdjacentMonths: Boolean = true,
    horizontalSwipeEnabled: Boolean = true,
    weekDaysScrollEnabled: Boolean = true,
    calendarState: CalendarState<DynamicSelectionState> = rememberSelectableCalendarState(),
    dayContent: @Composable BoxScope.(DayState<DynamicSelectionState>) -> Unit = { DefaultDay(it) },
    monthHeader: @Composable ColumnScope.(MonthState) -> Unit = { DefaultMonthHeader(it) },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
    monthContainer: @Composable (content: @Composable (PaddingValues) -> Unit) -> Unit = { content ->
        Box { content(PaddingValues()) }
    },
) {
    Calendar(
        modifier = modifier,
        firstDayOfWeek = firstDayOfWeek,
        today = today,
        showAdjacentMonths = showAdjacentMonths,
        horizontalSwipeEnabled = horizontalSwipeEnabled,
        weekDaysScrollEnabled = weekDaysScrollEnabled,
        calendarState = calendarState,
        dayContent = dayContent,
        monthHeader = monthHeader,
        daysOfWeekHeader = daysOfWeekHeader,
        monthContainer = monthContainer
    )
}

/**
 * [Calendar] implementation without any mechanism for the selection.
 *
 * Basic usage:
 * ```
 *  @Composable
 *  fun MainScreen() {
 *    StaticCalendar()
 *  }
 * ```
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param showAdjacentMonths whenever to show or hide the days from adjacent months
 * @param horizontalSwipeEnabled whenever user is able to change the month by horizontal swipe
 * @param weekDaysScrollEnabled if the week days should be scrollable
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param monthHeader header for showing the current month and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 * @param monthContainer container composable for all the days in current month
 */
@Composable
fun StaticCalendar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = LocalDate.now(),
    showAdjacentMonths: Boolean = true,
    horizontalSwipeEnabled: Boolean = true,
    weekDaysScrollEnabled: Boolean = true,
    calendarState: CalendarState<EmptySelectionState> = rememberCalendarState(),
    dayContent: @Composable BoxScope.(DayState<EmptySelectionState>) -> Unit = { DefaultDay(it) },
    monthHeader: @Composable ColumnScope.(MonthState) -> Unit = { DefaultMonthHeader(it) },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
    monthContainer: @Composable (content: @Composable (PaddingValues) -> Unit) -> Unit = { content ->
        Box { content(PaddingValues()) }
    },
) {
    Calendar(
        modifier = modifier,
        firstDayOfWeek = firstDayOfWeek,
        today = today,
        showAdjacentMonths = showAdjacentMonths,
        horizontalSwipeEnabled = horizontalSwipeEnabled,
        weekDaysScrollEnabled = weekDaysScrollEnabled,
        calendarState = calendarState,
        dayContent = dayContent,
        monthHeader = monthHeader,
        daysOfWeekHeader = daysOfWeekHeader,
        monthContainer = monthContainer
    )
}

/**
 * Composable for showing a calendar. The calendar state has to be provided by the call site. If you
 * want to use built-in implementation, check out:
 * [SelectableCalendar] - calendar composable handling selection that can be changed at runtime
 * [StaticCalendar] - calendar without any mechanism for selection
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param showAdjacentMonths whenever to show or hide the days from adjacent months
 * @param horizontalSwipeEnabled whenever user is able to change the month by horizontal swipe
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param monthHeader header for showing the current month and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 * @param monthContainer container composable for all the days in current month
 */
@Composable
fun <T : SelectionState> Calendar(
    calendarState: CalendarState<T>,
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = LocalDate.now(),
    showAdjacentMonths: Boolean = true,
    horizontalSwipeEnabled: Boolean = true,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit = { DefaultDay(it) },
    monthHeader: @Composable ColumnScope.(MonthState) -> Unit = { DefaultMonthHeader(it) },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
    monthContainer: @Composable (content: @Composable (PaddingValues) -> Unit) -> Unit = { content ->
        Box { content(PaddingValues()) }
    },
) {
    val initialMonth = remember { calendarState.monthState.currentMonth }
    val daysOfWeek = remember(firstDayOfWeek) {
        DayOfWeek.entries.rotateRight(DaysInAWeek - firstDayOfWeek.ordinal)
    }

    Column(
        modifier = modifier,
    ) {
        monthHeader(calendarState.monthState)
        if (horizontalSwipeEnabled) {
            MonthPager(
                initialMonth = initialMonth,
                showAdjacentMonths = showAdjacentMonths,
                monthState = calendarState.monthState,
                selectionState = calendarState.selectionState,
                today = today,
                weekDaysScrollEnabled = weekDaysScrollEnabled,
                daysOfWeek = daysOfWeek,
                dayContent = dayContent,
                weekHeader = daysOfWeekHeader,
                monthContainer = monthContainer,
            )
        } else {
            MonthContent(
                modifier = Modifier.fillMaxWidth(),
                currentMonth = calendarState.monthState.currentMonth,
                showAdjacentMonths = showAdjacentMonths,
                selectionState = calendarState.selectionState,
                today = today,
                weekDaysScrollEnabled = weekDaysScrollEnabled,
                daysOfWeek = daysOfWeek,
                dayContent = dayContent,
                weekHeader = daysOfWeekHeader,
                monthContainer = monthContainer,
            )
        }
    }
}

/**
 * Helper function for providing a [CalendarState] implementation with selection mechanism.
 *
 * @param initialMonth initially rendered month
 * @param initialSelection initial selection of the composable
 * @param initialSelectionMode initial mode of the selection
 * @param confirmSelectionChange callback for optional side-effects handling and vetoing the state change
 * @param minMonth first month that can be shown
 * @param maxMonth last month that can be shown
 */
@Composable
fun rememberSelectableCalendarState(
    initialMonth: YearMonth = YearMonth.now(),
    minMonth: YearMonth = initialMonth.minusMonths(DefaultCalendarPagerRange),
    maxMonth: YearMonth = initialMonth.plusMonths(DefaultCalendarPagerRange),
    initialSelection: List<LocalDate> = emptyList(),
    initialSelectionMode: SelectionMode = SelectionMode.Single,
    confirmSelectionChange: (newValue: List<LocalDate>) -> Boolean = { true },
    monthState: MonthState = rememberSaveable(saver = MonthState.Saver()) {
        MonthState(
            initialMonth = initialMonth,
            minMonth = minMonth,
            maxMonth = maxMonth
        )
    },
    selectionState: DynamicSelectionState = rememberSaveable(
        saver = DynamicSelectionState.Saver(confirmSelectionChange),
    ) {
        DynamicSelectionState(confirmSelectionChange, initialSelection, initialSelectionMode)
    },
): CalendarState<DynamicSelectionState> = remember { CalendarState(monthState, selectionState) }

/**
 * Helper function for providing a [CalendarState] implementation without a selection mechanism.
 *
 * @param initialMonth initially rendered month
 * @param minMonth first month that can be shown
 * @param maxMonth last month that can be shown
 */
@Composable
fun rememberCalendarState(
    initialMonth: YearMonth = YearMonth.now(),
    minMonth: YearMonth = initialMonth.minusMonths(DefaultCalendarPagerRange),
    maxMonth: YearMonth = initialMonth.plusMonths(DefaultCalendarPagerRange),
    monthState: MonthState = rememberSaveable(saver = MonthState.Saver()) {
        MonthState(
            initialMonth = initialMonth,
            minMonth = minMonth,
            maxMonth = maxMonth
        )
    },
): CalendarState<EmptySelectionState> = remember { CalendarState(monthState, EmptySelectionState) }

internal const val DefaultCalendarPagerRange = 10_000L
