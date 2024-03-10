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
import `in`.procyk.compose.calendar.year.YearMonth

fun MonthState(
    initialMonth: YearMonth,
    minMonth: YearMonth,
    maxMonth: YearMonth,
): MonthState = MonthStateImpl(initialMonth, minMonth, maxMonth)

@Stable
interface MonthState {
    var currentMonth: YearMonth
    var minMonth: YearMonth
    var maxMonth: YearMonth

    companion object {
        fun Saver(): Saver<MonthState, Any> = mapSaver(
            save = { monthState ->
                mapOf(
                    "currentMonth" to monthState.currentMonth.toString(),
                    "minMonth" to monthState.minMonth.toString(),
                    "maxMonth" to monthState.maxMonth.toString(),
                )
            },
            restore = { restoreMap ->
                MonthState(
                    YearMonth.parse(restoreMap["currentMonth"] as String)!!,
                    YearMonth.parse(restoreMap["minMonth"] as String)!!,
                    YearMonth.parse(restoreMap["maxMonth"] as String)!!,
                )
            }
        )
    }
}

@Stable
private class MonthStateImpl(
    initialMonth: YearMonth,
    minMonth: YearMonth,
    maxMonth: YearMonth,
) : MonthState {

    private var _currentMonth by mutableStateOf(initialMonth)
    private var _minMonth by mutableStateOf(minMonth)
    private var _maxMonth by mutableStateOf(maxMonth)

    override var currentMonth: YearMonth
        get() = _currentMonth
        set(value) {
            _currentMonth = value
        }

    override var minMonth: YearMonth
        get() = _minMonth
        set(value) {
            if (value > _maxMonth) return
            _minMonth = value
        }

    override var maxMonth: YearMonth
        get() = _maxMonth
        set(value) {
            if (value < _minMonth) return
            _maxMonth = value
        }
}
