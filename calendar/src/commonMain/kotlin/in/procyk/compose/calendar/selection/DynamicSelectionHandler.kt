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

import `in`.procyk.compose.calendar.util.addOrRemoveIfExists
import kotlinx.datetime.LocalDate


object DynamicSelectionHandler {
    /**
     * Helper class for calculating new selection, when using a [DynamicSelectionState] approach.
     * @param date clicked on date
     * @param selection current selection
     * @param selectionMode current selection mode
     * @returns new selection in a form of a list of local dates.
     */
    fun calculateNewSelection(
        date: LocalDate,
        selection: List<LocalDate>,
        selectionMode: SelectionMode,
    ): List<LocalDate> = when (selectionMode) {
        SelectionMode.None -> emptyList()
        SelectionMode.Single -> if (date == selection.firstOrNull()) {
            emptyList()
        } else {
            listOf(date)
        }

        SelectionMode.Multiple -> selection.addOrRemoveIfExists(date)
        SelectionMode.Period -> when {
            date < selection.startOrMax() -> listOf(date)
            date > selection.startOrMax() -> selection.fillUpTo(date)
            date == selection.startOrMax() -> emptyList()
            else -> selection
        }
    }
}
