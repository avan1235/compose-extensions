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

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import kotlinx.datetime.LocalDate

@Stable
interface SelectionState {
    fun isDateSelected(date: LocalDate): Boolean = false
    fun onDateSelected(date: LocalDate) {}
}

/**
 * Class that enables for dynamically changing selection modes in the runtime. Depending on the mode, selection changes differently.
 * Mode can be varied by setting desired [SelectionMode] in the [selectionMode] mutable property.
 * @param confirmSelectionChange return false from this callback to veto the selection change
 */
@Stable
class DynamicSelectionState(
    private val confirmSelectionChange: (newValue: List<LocalDate>) -> Boolean = { true },
    selection: List<LocalDate>,
    selectionMode: SelectionMode,
) : SelectionState {

    private var _selection by mutableStateOf(selection)
    private var _selectionMode by mutableStateOf(selectionMode)

    var selection: List<LocalDate>
        get() = _selection
        set(value) {
            if (value != selection && confirmSelectionChange(value)) {
                _selection = value
            }
        }

    var selectionMode: SelectionMode
        get() = _selectionMode
        set(value) {
            if (value != selectionMode) {
                _selection = emptyList()
                _selectionMode = value
            }
        }

    override fun isDateSelected(date: LocalDate): Boolean = selection.contains(date)

    override fun onDateSelected(date: LocalDate) {
        selection = DynamicSelectionHandler.calculateNewSelection(date, selection, selectionMode)
    }

    internal companion object {
        fun Saver(
            confirmSelectionChange: (newValue: List<LocalDate>) -> Boolean,
        ): Saver<DynamicSelectionState, Any> =
            listSaver(
                save = { raw ->
                    listOf(raw.selectionMode, raw.selection.map { it.toString() })
                },
                restore = { restored ->
                    DynamicSelectionState(
                        confirmSelectionChange = confirmSelectionChange,
                        selectionMode = restored[0] as SelectionMode,
                        selection = (restored[1] as? List<*>)?.map { LocalDate.parse(it as String) }.orEmpty(),
                    )
                }
            )
    }
}

@Immutable
object EmptySelectionState : SelectionState {
    override fun isDateSelected(date: LocalDate): Boolean = false

    override fun onDateSelected(date: LocalDate): Unit = Unit
}
