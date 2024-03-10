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

import `in`.procyk.compose.calendar.selection.fillUpTo
import `in`.procyk.compose.calendar.util.daysUntil
import `in`.procyk.compose.calendar.util.now
import `in`.procyk.compose.calendar.year.YearMonth
import kotlinx.datetime.*

data class Week(
    val days: List<LocalDate>,
) {

    init {
        require(days.size == DaysInAWeek)
    }

    internal constructor(firstDay: LocalDate) : this(
        listOf(firstDay).fillUpTo(firstDay.plus(DaysInAWeek - 1, DateTimeUnit.DAY))
    )

    val start: LocalDate get() = days.first()

    val end: LocalDate get() = days.last()

    val yearMonth: YearMonth = YearMonth.of(start.year, start.month)

    operator fun inc(): Week = plusWeeks(1)

    operator fun dec(): Week = plusWeeks(-1)

    operator fun compareTo(other: Week): Int = start.compareTo(other.start)

    fun minusWeeks(value: Long): Week = plusWeeks(-value)

    fun plusWeeks(value: Long): Week = copy(days = days.map { it.plus(value, DateTimeUnit.WEEK) })

    companion object {
        fun now(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Week {
            val today = LocalDate.now()
            val offset = today.dayOfWeek.daysUntil(firstDayOfWeek)
            val firstDay = today.minus(offset, DateTimeUnit.DAY)

            return Week(firstDay)
        }
    }
}

internal fun weeksBetween(first: Week, other: Week): Int =
    (other.start - first.start).days / DaysInAWeek