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
package `in`.procyk.compose.calendar.day

import kotlinx.datetime.LocalDate

/**
 * Container for basic info about the displayed day
 *
 * @param date local date of the day
 * @param isCurrentDay whenever the day is the today's date
 * @param isFromCurrentMonth whenever the day is from currently rendered month
 */
interface Day {
    val date: LocalDate
    val isCurrentDay: Boolean
    val isFromCurrentMonth: Boolean
}
