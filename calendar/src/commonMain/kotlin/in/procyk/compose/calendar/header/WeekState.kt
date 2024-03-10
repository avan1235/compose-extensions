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
package `in`.procyk.compose.calendar.header

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import `in`.procyk.compose.calendar.week.Week
import kotlinx.datetime.LocalDate

fun WeekState(
    initialWeek: Week,
    minWeek: Week,
    maxWeek: Week,
): WeekState = WeekStateImpl(
    initialWeek = initialWeek,
    minWeek = minWeek,
    maxWeek = maxWeek,
)

@Stable
interface WeekState {
    var currentWeek: Week
    var minWeek: Week
    var maxWeek: Week

    companion object {
        fun Saver(): Saver<WeekState, Any> = mapSaver(
            save = { weekState ->
                mapOf(
                    CurrentWeekKey to weekState.currentWeek.toString(),
                    MinWeekKey to weekState.minWeek.toString(),
                    MaxWeekKey to weekState.maxWeek.toString(),
                )
            },
            restore = { restoreMap ->
                WeekState(
                    initialWeek = Week(firstDay = LocalDate.parse(restoreMap[CurrentWeekKey] as String)),
                    minWeek = Week(firstDay = LocalDate.parse(restoreMap[MinWeekKey] as String)),
                    maxWeek = Week(firstDay = LocalDate.parse(restoreMap[MaxWeekKey] as String)),
                )
            }
        )

        private const val CurrentWeekKey = "CurrentWeek"
        private const val MinWeekKey = "MinWeek"
        private const val MaxWeekKey = "MaxWeek"
    }
}

@Stable
private class WeekStateImpl(
    initialWeek: Week,
    minWeek: Week,
    maxWeek: Week,
) : WeekState {

    private var _currentWeek by mutableStateOf(initialWeek)
    private var _minWeek by mutableStateOf(minWeek)
    private var _maxWeek by mutableStateOf(maxWeek)

    override var currentWeek: Week
        get() = _currentWeek
        set(value) {
            _currentWeek = value
        }

    override var minWeek: Week
        get() = _minWeek
        set(value) {
            if (value > _maxWeek) return
            _minWeek = value
        }

    override var maxWeek: Week
        get() = _maxWeek
        set(value) {
            if (value < _minWeek) return
            _maxWeek = value
        }
}
