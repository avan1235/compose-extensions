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
package `in`.procyk.compose.calendar.month

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import `in`.procyk.compose.calendar.header.MonthState
import `in`.procyk.compose.calendar.util.throttleOnOffset
import `in`.procyk.compose.calendar.year.YearMonth
import `in`.procyk.compose.calendar.year.YearMonth.Companion.monthsBetween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
internal class MonthListState(
    private val coroutineScope: CoroutineScope,
    private val monthState: MonthState,
    private val listState: LazyListState,
) {

    private val currentFirstVisibleMonth by derivedStateOf {
        getMonthForPage(listState.firstVisibleItemIndex)
    }

    init {
        snapshotFlow { monthState.currentMonth }.onEach { month ->
            moveToMonth(month)
        }.launchIn(coroutineScope)

        snapshotFlow { currentFirstVisibleMonth }
            .throttleOnOffset(listState)
            .onEach { newMonth ->
                monthState.currentMonth = newMonth
            }.launchIn(coroutineScope)
    }

    fun getMonthForPage(index: Int): YearMonth =
        monthState.minMonth.plusMonths(index.toLong())

    private fun moveToMonth(month: YearMonth) {
        if (month == currentFirstVisibleMonth) return
        coroutineScope.launch {
            listState.animateScrollToItem(monthsBetween(monthState.minMonth, month).toInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MonthListState

        if (monthState != other.monthState) return false
        if (listState != other.listState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = monthState.hashCode()
        result = 31 * result + listState.hashCode()
        return result
    }
}
