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

import `in`.procyk.compose.calendar.day.WeekDay
import `in`.procyk.compose.calendar.util.daysUntil
import `in`.procyk.compose.calendar.util.now
import `in`.procyk.compose.calendar.year.YearMonth
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal const val DaysInAWeek = 7

internal fun YearMonth.getWeeks(
    includeAdjacentMonths: Boolean,
    firstDayOfTheWeek: DayOfWeek,
    today: LocalDate = LocalDate.now(),
): List<WeekDays> {
    val daysLength = lengthOfMonth()

    val starOffset = atDay(1).dayOfWeek daysUntil firstDayOfTheWeek
    val endOffset = DaysInAWeek - (atDay(daysLength).dayOfWeek daysUntil firstDayOfTheWeek) - 1

    return (1 - starOffset..daysLength + endOffset).chunked(DaysInAWeek).mapIndexed { index, days ->
        WeekDays(
            isFirstWeekOfTheMonth = index == 0,
            days = days.mapNotNull { dayOfMonth ->
                val (date, isFromCurrentMonth) = when (dayOfMonth) {
                    in Int.MIN_VALUE..0 -> if (includeAdjacentMonths) {
                        val previousMonth = this.minusMonths(1)
                        previousMonth.atDay(previousMonth.lengthOfMonth() + dayOfMonth) to false
                    } else {
                        return@mapNotNull null
                    }

                    in 1..daysLength -> atDay(dayOfMonth) to true
                    else -> if (includeAdjacentMonths) {
                        val previousMonth = this.plusMonths(1)
                        previousMonth.atDay(dayOfMonth - daysLength) to false
                    } else {
                        return@mapNotNull null
                    }
                }

                WeekDay(
                    date = date,
                    isFromCurrentMonth = isFromCurrentMonth,
                    isCurrentDay = date.equals(today),
                )
            }
        )
    }
}
