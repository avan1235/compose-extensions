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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import `in`.procyk.compose.calendar.selection.SelectionState
import kotlinx.datetime.LocalDate

/**
 * Default implementation for day content. It supports different appearance for days from
 * current and adjacent month, as well as current day and selected day
 *
 * @param selectionColor color of the border, when day is selected
 * @param currentDayColor color of content for the current date
 * @param onClick callback for interacting with day clicks
 */
@Composable
fun <T : SelectionState> DefaultDay(
    state: DayState<T>,
    modifier: Modifier = Modifier,
    selectionColor: Color = MaterialTheme.colorScheme.secondary,
    currentDayColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (LocalDate) -> Unit = {},
) {
    val date = state.date
    val selectionState = state.selectionState

    val isSelected = selectionState.isDateSelected(date)

    Card(
        modifier = modifier
            .alpha(if (state.isFromCurrentMonth) 1f else 0.5f)
            .aspectRatio(1f)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (state.isFromCurrentMonth) 2.dp else 0.dp),
        border = if (state.isCurrentDay) BorderStroke(1.dp, currentDayColor) else null,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) selectionColor else Color.Unspecified),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick(date)
                    selectionState.onDateSelected(date)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = date.day.toString())
        }
    }
}
