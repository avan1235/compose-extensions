/**
 * Based on QRose by Alexander Zhirkevich from [Github](https://github.com/alexzhirkevich/qrose)
 *
 * MIT License
 *
 * Copyright (c) 2023 Alexander Zhirkevich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package `in`.procyk.compose.qrcode

object QrData {
    fun text(text: String): String = text

    fun phone(phoneNumber: String): String = "TEL:$phoneNumber"

    fun email(
        email: String,
        copyTo: String? = null,
        subject: String? = null,
        body: String? = null,
    ): String = buildString {
        append("mailto:$email")

        if (listOf(copyTo, subject, body).any { it.isNullOrEmpty().not() }) {
            append("?")
        }
        val querries = buildList<String> {
            if (copyTo.isNullOrEmpty().not()) {
                add("cc=$copyTo")
            }
            if (subject.isNullOrEmpty().not()) {
                add("subject=${escape(subject!!)}")
            }
            if (body.isNullOrEmpty().not()) {
                add("body=${escape(body!!)}")
            }
        }
        append(querries.joinToString(separator = "&"))
    }

    fun sms(
        phoneNumber: String,
        subject: String,
        isMMS: Boolean,
    ): String = "${if (isMMS) "MMS" else "SMS"}:" +
            "$phoneNumber${if (subject.isNotEmpty()) ":$subject" else ""}"


    fun meCard(
        name: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        email: String? = null,
    ): String = buildString {
        append("MECARD:")
        if (name != null)
            append("N:$name;")

        if (address != null)
            append("ADR:$address;")

        if (phoneNumber != null)
            append("TEL:$phoneNumber;")

        if (email != null)
            append("EMAIL:$email;")

        append(";")
    }

    fun vCard(
        name: String? = null,
        company: String? = null,
        title: String? = null,
        phoneNumber: String? = null,
        email: String? = null,
        address: String? = null,
        website: String? = null,
        note: String? = null,
    ): String = buildString {
        append("BEGIN:VCARD\n")
        append("VERSION:3.0\n")
        if (name != null)
            append("N:$name\n")

        if (company != null)
            append("ORG:$company\n")

        if (title != null)
            append("TITLE$title\n")

        if (phoneNumber != null)
            append("TEL:$phoneNumber\n")

        if (website != null)
            append("URL:$website\n")

        if (email != null)
            append("EMAIL:$email\n")

        if (address != null)
            append("ADR:$address\n")

        if (note != null) {
            append("NOTE:$note\n")
        }
        append("END:VCARD")
    }

    fun bizCard(
        firstName: String? = null,
        secondName: String? = null,
        job: String? = null,
        company: String? = null,
        address: String? = null,
        phone: String? = null,
        email: String? = null,
    ): String = buildString {
        append("BIZCARD:")
        if (firstName != null)
            append("N:$firstName;")

        if (secondName != null)
            append("X:$secondName;")

        if (job != null)
            append("T:$job;")

        if (company != null)
            append("C:$company;")

        if (address != null)
            append("A:$address;")

        if (phone != null)
            append("B:$phone;")

        if (email != null)
            append("E:$email;")

        append(";")
    }

    fun wifi(
        authentication: String? = null,
        ssid: String? = null,
        psk: String? = null,
        hidden: Boolean = false,
    ): String = buildString {
        append("WIFI:")
        if (ssid != null)
            append("S:${escape(ssid)};")

        if (authentication != null)
            append("T:${authentication};")

        if (psk != null)
            append("P:${escape(psk)};")

        append("H:$hidden;")
    }

    fun enterpriseWifi(
        ssid: String? = null,
        psk: String? = null,
        hidden: Boolean = false,
        user: String? = null,
        eap: String? = null,
        phase: String? = null,
    ): String = buildString {
        append("WIFI:")
        if (ssid != null)
            append("S:${escape(ssid)};")

        if (user != null)
            append("U:${escape(user)};")

        if (psk != null)
            append("P:${escape(psk)};")

        if (eap != null)
            append("E:${escape(eap)};")

        if (phase != null)
            append("PH:${escape(phase)};")

        append("H:$hidden;")
    }


    fun event(
        uid: String? = null,
        stamp: String? = null,
        organizer: String? = null,
        start: String? = null,
        end: String? = null,
        summary: String? = null,
    ): String = buildString {
        append("BEGIN:VEVENT\n")
        if (uid != null)
            append("UID:$uid\n")
        if (stamp != null)
            append("DTSTAMP:$stamp\n")
        if (organizer != null)
            append("ORGANIZER:$organizer\n")

        if (start != null)
            append("DTSTART:$start\n")

        if (end != null)
            append("DTEND:$end\n")
        if (summary != null)
            append("SUMMARY:$summary\n")

        append("END:VEVENT")
    }

    fun location(
        lat: Float,
        lon: Float,
    ): String = "GEO:$lat,$lon"
}

private fun escape(text: String): String = text.replace("\\", "\\\\")
    .replace(",", "\\,")
    .replace(";", "\\;")
    .replace(".", "\\.")
    .replace("\"", "\\\"")
    .replace("'", "\\'")