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

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import `in`.procyk.compose.calendar.header.WeekState
import `in`.procyk.compose.calendar.util.throttleOnOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
internal class WeekListState(
    private val coroutineScope: CoroutineScope,
    private val weekState: WeekState,
    private val listState: LazyListState,
) {

    private val currentlyFirstVisibleMonth by derivedStateOf {
        getWeekForPage(listState.firstVisibleItemIndex)
    }

    init {
        snapshotFlow { weekState.currentWeek }.onEach { week ->
            moveToWeek(week)
        }.launchIn(coroutineScope)

        snapshotFlow { currentlyFirstVisibleMonth }
            .throttleOnOffset(listState)
            .onEach { newMonth ->
                weekState.currentWeek = newMonth
            }.launchIn(coroutineScope)
    }

    fun getWeekForPage(index: Int): Week =
        weekState.minWeek.plusWeeks(index.toLong())

    private fun moveToWeek(week: Week) {
        if (week == currentlyFirstVisibleMonth) return
        coroutineScope.launch {
            listState.animateScrollToItem(weeksBetween(weekState.minWeek, week))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WeekListState

        if (weekState != other.weekState) return false
        if (listState != other.listState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = weekState.hashCode()
        result = 31 * result + listState.hashCode()
        return result
    }
}
