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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Month

/**
 * Default implementation of month header, shows current month and year, as well as
 * 2 arrows for changing currently showed month
 */
@Composable
fun DefaultMonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DecrementButton(monthState = monthState)
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = Month(monthState.currentMonth.month)
                .name
                .lowercase()
                .replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = monthState.currentMonth.year.toString(),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.weight(1f))
        IncrementButton(monthState = monthState)
    }
}

@Composable
private fun DecrementButton(
    monthState: MonthState,
) {
    IconButton(
        modifier = Modifier.testTag("Decrement"),
        enabled = monthState.currentMonth > monthState.minMonth,
        onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
    ) {
        Image(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentDescription = "Previous",
        )
    }
}

@Composable
private fun IncrementButton(
    monthState: MonthState,
) {
    IconButton(
        modifier = Modifier.testTag("Increment"),
        enabled = monthState.currentMonth < monthState.maxMonth,
        onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
    ) {
        Image(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentDescription = "Next",
        )
    }
}
