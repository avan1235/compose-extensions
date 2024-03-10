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

import androidx.compose.runtime.Stable
import `in`.procyk.compose.calendar.selection.EmptySelectionState
import `in`.procyk.compose.calendar.selection.SelectionState

typealias NonSelectableDayState = DayState<EmptySelectionState>

/**
 * Contains information about current selection as well as date of rendered day
 */
@Stable
data class DayState<T : SelectionState>(
    private val day: Day,
    val selectionState: T,
) : Day by day
