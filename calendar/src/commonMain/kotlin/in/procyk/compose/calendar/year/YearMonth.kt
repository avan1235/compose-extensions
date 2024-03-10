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
package `in`.procyk.compose.calendar.year

import androidx.compose.runtime.Immutable
import `in`.procyk.compose.calendar.util.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number


@Immutable
class YearMonth private constructor(
    val year: Int,
    val month: Int,
) : Comparable<YearMonth> {

    operator fun dec(): YearMonth = minusMonths(1)

    operator fun inc(): YearMonth = plusMonths(1)

    override fun toString(): String = "$year-$month"

    override fun compareTo(other: YearMonth): Int =
        year.compareTo(other.year)
            .let { if (it == 0) month.compareTo(other.month) else it }

    fun plusMonths(count: Long): YearMonth {
        if (count == 0L) {
            return this
        }
        val monthCount = year * 12L + (month - 1)
        val calcMonths = monthCount + count
        val newYear = calcMonths.floorDiv(12)
        val newMonth = floorMod(calcMonths, 12) + 1
        return YearMonth(newYear.toInt(), newMonth.toInt())
    }

    fun minusMonths(count: Long): YearMonth =
        if (count == Long.MIN_VALUE) plusMonths(Long.MAX_VALUE).plusMonths(1) else plusMonths(-count)

    fun atDay(dayOfMonth: Int): LocalDate {
        return LocalDate(year, Month(month), dayOfMonth)
    }

    fun lengthOfMonth(): Int =
        month.monthLength(isLeapYear(year))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as YearMonth

        if (year != other.year) return false
        if (month != other.month) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        return result
    }


    companion object {
        fun now(): YearMonth =
            LocalDate.now().let { of(it.year, it.month) }

        fun of(year: Int, month: Month): YearMonth =
            YearMonth(year, month.number)

        fun parse(value: String): YearMonth? = runCatching {
            val (yearString, monthString) = value.split('-')
            val year = yearString.toInt()
            val month = monthString.toInt()
            YearMonth(year, month)
        }.getOrNull()

        fun monthsBetween(start: YearMonth, end: YearMonth): Long {
            return end.encoded - start.encoded
        }
    }
}

private inline val YearMonth.encoded: Long get() = year * 12L + (month - 1)

private fun floorMod(x: Long, y: Long): Long {
    var r = x / y
    if ((x xor y) < 0 && (r * y != x)) {
        r--
    }
    return x - r * y
}

private fun isLeapYear(year: Int): Boolean {
    val prolepticYear = year.toLong()
    return prolepticYear and 3 == 0L && (prolepticYear % 100 != 0L || prolepticYear % 400 == 0L)
}

private fun Int.monthLength(isLeapYear: Boolean): Int =
    when (this) {
        2 -> if (isLeapYear) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }