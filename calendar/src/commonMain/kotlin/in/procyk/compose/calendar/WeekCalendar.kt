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

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import `in`.procyk.compose.calendar.day.DayState
import `in`.procyk.compose.calendar.day.DefaultDay
import `in`.procyk.compose.calendar.header.WeekState
import `in`.procyk.compose.calendar.selection.DynamicSelectionState
import `in`.procyk.compose.calendar.selection.EmptySelectionState
import `in`.procyk.compose.calendar.selection.SelectionMode
import `in`.procyk.compose.calendar.selection.SelectionState
import `in`.procyk.compose.calendar.util.now
import `in`.procyk.compose.calendar.week.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import `in`.procyk.compose.calendar.header.DefaultWeekHeader as DefaultProperWeekHeader

/**
 * State of the week calendar composable
 *
 * @property weekState currently showed week
 * @property selectionState handler for the calendar's selection
 */
@Stable
class WeekCalendarState<T : SelectionState>(
    val weekState: WeekState,
    val selectionState: T,
)

/**
 * [WeekCalendar] implementation using a [DynamicSelectionState] as a selection handler.
 *
 *  * Basic usage:
 * ```
 *  @Composable
 *  fun MainScreen() {
 *    SelectableWeekCalendar()
 *  }
 * ```
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param horizontalSwipeEnabled whenever user is able to change the week by horizontal swipe
 * @param weekDaysScrollEnabled if the week days should be scrollable
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param weekHeader header for showing the current week and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 */
@Composable
fun SelectableWeekCalendar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = LocalDate.now(),
    horizontalSwipeEnabled: Boolean = true,
    weekDaysScrollEnabled: Boolean = true,
    calendarState: WeekCalendarState<DynamicSelectionState> = rememberSelectableWeekCalendarState(),
    dayContent: @Composable BoxScope.(DayState<DynamicSelectionState>) -> Unit = { DefaultDay(it) },
    weekHeader: @Composable ColumnScope.(WeekState) -> Unit = {
        DefaultProperWeekHeader(it)
    },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
) {
    WeekCalendar(
        modifier = modifier,
        calendarState = calendarState,
        today = today,
        horizontalSwipeEnabled = horizontalSwipeEnabled,
        firstDayOfWeek = firstDayOfWeek,
        weekDaysScrollEnabled = weekDaysScrollEnabled,
        dayContent = dayContent,
        weekHeader = weekHeader,
        daysOfWeekHeader = daysOfWeekHeader,
    )
}

/**
 * [WeekCalendar] implementation without any mechanism for the selection.
 *
 *  * Basic usage:
 * ```
 *  @Composable
 *  fun MainScreen() {
 *    StaticWeekCalendar()
 *  }
 * ```
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param horizontalSwipeEnabled whenever user is able to change the week by horizontal swipe
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param weekHeader header for showing the current week and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 */
@Composable
fun StaticWeekCalendar(
    modifier: Modifier = Modifier,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    today: LocalDate = LocalDate.now(),
    horizontalSwipeEnabled: Boolean = true,
    calendarState: WeekCalendarState<EmptySelectionState> = rememberWeekCalendarState(),
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<EmptySelectionState>) -> Unit = { DefaultDay(it) },
    weekHeader: @Composable ColumnScope.(WeekState) -> Unit = {
        DefaultProperWeekHeader(it)
    },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
) {
    WeekCalendar(
        modifier = modifier,
        calendarState = calendarState,
        today = today,
        horizontalSwipeEnabled = horizontalSwipeEnabled,
        firstDayOfWeek = firstDayOfWeek,
        weekDaysScrollEnabled = weekDaysScrollEnabled,
        dayContent = dayContent,
        weekHeader = weekHeader,
        daysOfWeekHeader = daysOfWeekHeader,
    )
}

/**
 * Composable for showing a week calendar. The calendar state has to be provided by the call site. If you
 * want to use built-in implementation, check out:
 * [SelectableWeekCalendar] - calendar composable handling selection that can be changed at runtime
 * [StaticWeekCalendar] - calendar without any mechanism for selection
 *
 * @param modifier
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param today current day, defaults to [LocalDate.now]
 * @param horizontalSwipeEnabled whenever user is able to change the week by horizontal swipe
 * @param calendarState state of the composable
 * @param dayContent composable rendering the current day
 * @param weekHeader header for showing the current week and controls for changing it
 * @param daysOfWeekHeader header for showing captions for each day of week
 */
@Composable
fun <T : SelectionState> WeekCalendar(
    calendarState: WeekCalendarState<T>,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    horizontalSwipeEnabled: Boolean = true,
    weekDaysScrollEnabled: Boolean = true,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit = { DefaultDay(it) },
    weekHeader: @Composable ColumnScope.(WeekState) -> Unit = {
        DefaultProperWeekHeader(it)
    },
    daysOfWeekHeader: @Composable BoxScope.(List<DayOfWeek>) -> Unit = { DefaultDaysOfWeekHeader(it) },
) {
    val initialWeek = remember { calendarState.weekState.currentWeek }
    val daysOfWeek = remember(firstDayOfWeek) {
        DayOfWeek.entries.rotateRight(DaysInAWeek - firstDayOfWeek.ordinal)
    }

    Column(
        modifier = modifier,
    ) {
        weekHeader(calendarState.weekState)
        if (horizontalSwipeEnabled) {
            WeekPager(
                initialWeek = initialWeek,
                daysOfWeek = daysOfWeek,
                weekState = calendarState.weekState,
                selectionState = calendarState.selectionState,
                today = today,
                weekDaysScrollEnabled = weekDaysScrollEnabled,
                dayContent = dayContent,
                daysOfWeekHeader = daysOfWeekHeader,
            )
        } else {
            WeekContent(
                daysOfWeek = daysOfWeek,
                weekDays = calendarState.weekState.currentWeek.getWeekDays(today),
                selectionState = calendarState.selectionState,
                weekDaysScrollEnabled = weekDaysScrollEnabled,
                dayContent = dayContent,
                daysOfWeekHeader = daysOfWeekHeader,
            )
        }
    }
}

/**
 * Helper function for providing a [WeekCalendarState] implementation with selection mechanism.
 *
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param initialWeek initially rendered month
 * @param initialSelection initial selection of the composable
 * @param initialSelectionMode initial mode of the selection
 * @param confirmSelectionChange callback for optional side-effects handling and vetoing the state change
 * @param minWeek first week that can be shown
 * @param maxWeek last week that can be shown
 */
@Composable
fun rememberSelectableWeekCalendarState(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    initialWeek: Week = Week.now(firstDayOfWeek),
    minWeek: Week = initialWeek.minusWeeks(DefaultCalendarPagerRange),
    maxWeek: Week = initialWeek.plusWeeks(DefaultCalendarPagerRange),
    initialSelection: List<LocalDate> = emptyList(),
    initialSelectionMode: SelectionMode = SelectionMode.Single,
    confirmSelectionChange: (newValue: List<LocalDate>) -> Boolean = { true },
    weekState: WeekState = rememberSaveable(saver = WeekState.Saver()) {
        WeekState(
            initialWeek = initialWeek,
            minWeek = minWeek,
            maxWeek = maxWeek,
        )
    },
    selectionState: DynamicSelectionState = rememberSaveable(
        saver = DynamicSelectionState.Saver(confirmSelectionChange),
    ) {
        DynamicSelectionState(confirmSelectionChange, initialSelection, initialSelectionMode)
    },
): WeekCalendarState<DynamicSelectionState> =
    remember { WeekCalendarState(weekState, selectionState) }

/**
 * Helper function for providing a [WeekCalendarState] implementation without a selection mechanism.
 *
 * @param firstDayOfWeek first day of a week, defaults to current locale's
 * @param initialWeek initially rendered week
 * @param minWeek first week that can be shown
 * @param maxWeek last week that can be shown
 */
@Composable
fun rememberWeekCalendarState(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    initialWeek: Week = Week.now(firstDayOfWeek),
    minWeek: Week = initialWeek.minusWeeks(DefaultCalendarPagerRange),
    maxWeek: Week = initialWeek.plusWeeks(DefaultCalendarPagerRange),
    weekState: WeekState = rememberSaveable(saver = WeekState.Saver()) {
        WeekState(
            initialWeek = initialWeek,
            minWeek = minWeek,
            maxWeek = maxWeek,
        )
    },
): WeekCalendarState<EmptySelectionState> =
    remember { WeekCalendarState(weekState, EmptySelectionState) }
