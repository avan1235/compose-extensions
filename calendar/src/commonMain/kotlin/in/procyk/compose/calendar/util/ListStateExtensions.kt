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
package `in`.procyk.compose.calendar.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

internal fun <T> Flow<T>.throttleOnOffset(lazyListState: LazyListState) =
    combine(
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
    ) { newMonth, offset ->
        newMonth to (offset <= MinimalOffsetForEmit)
    }.filter { (_, shouldUpdate) ->
        shouldUpdate
    }.map { (newValue, _) -> newValue }

private const val MinimalOffsetForEmit = 10
