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
package `in`.procyk.compose.calendar.selection

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

internal fun Collection<LocalDate>.startOrMax() = firstOrNull() ?: MAX_LOCAL_DATE

internal fun Collection<LocalDate>.endOrNull() = drop(1).lastOrNull()

internal fun Collection<LocalDate>.fillUpTo(date: LocalDate): List<LocalDate> =
    (0..date.toEpochDays() - first().toEpochDays()).map {
        first().plus(it, DateTimeUnit.DAY)
    }

private val MAX_LOCAL_DATE: LocalDate = LocalDate(Int.MAX_VALUE, Month.DECEMBER, 31)