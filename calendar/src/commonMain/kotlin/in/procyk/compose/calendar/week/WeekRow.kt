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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import `in`.procyk.compose.calendar.day.DayState
import `in`.procyk.compose.calendar.selection.SelectionState

@Composable
internal fun <T : SelectionState> WeekRow(
    weekDays: WeekDays,
    selectionState: T,
    modifier: Modifier = Modifier,
    dayContent: @Composable BoxScope.(DayState<T>) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = if (weekDays.isFirstWeekOfTheMonth) Arrangement.End else Arrangement.Start
    ) {
        weekDays.days.forEachIndexed { index, day ->
            Box(
                modifier = Modifier.fillMaxWidth(1f / (7 - index))
            ) {
                dayContent(DayState(day, selectionState))
            }
        }
    }
}