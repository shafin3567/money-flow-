package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

data class CurrencyItem(
    val code: String,
    val name: String,
    val symbol: String,
    val decimals: Int = 2
)

object CurrencyData {
    val SUPPORTED_CURRENCIES = listOf(
        CurrencyItem("USD", "US Dollar", "$", 2),
        CurrencyItem("EUR", "Euro", "€", 2),
        CurrencyItem("GBP", "British Pound", "£", 2),
        CurrencyItem("JPY", "Japanese Yen", "¥", 0),
        CurrencyItem("CAD", "Canadian Dollar", "CA$", 2),
        CurrencyItem("AUD", "Australian Dollar", "A$", 2),
        CurrencyItem("CHF", "Swiss Franc", "CHF", 2),
        CurrencyItem("INR", "Indian Rupee", "₹", 2),
        CurrencyItem("BDT", "Bangladeshi Taka", "৳", 2),
        CurrencyItem("AED", "UAE Dirham", "AED", 2),
        CurrencyItem("SAR", "Saudi Riyal", "SAR", 2),
        CurrencyItem("SGD", "Singapore Dollar", "S$", 2),
        CurrencyItem("CNY", "Chinese Yuan", "¥", 2),
        CurrencyItem("BRL", "Brazilian Real", "R$", 2),
        CurrencyItem("MXN", "Mexican Peso", "MX$", 2),
        CurrencyItem("KRW", "South Korean Won", "₩", 0),
        CurrencyItem("SEK", "Swedish Krona", "kr", 2),
        CurrencyItem("NOK", "Norwegian Krone", "kr", 2),
        CurrencyItem("NZD", "New Zealand Dollar", "NZ$", 2),
        CurrencyItem("TRY", "Turkish Lira", "₺", 2),
        CurrencyItem("ZAR", "South African Rand", "R", 2)
    )

    fun getByCode(code: String): CurrencyItem {
        return SUPPORTED_CURRENCIES.find { it.code.equals(code, ignoreCase = true) }
            ?: CurrencyItem(code, code, code, 2)
    }

    /**
     * Formats minor units (e.g. cents) into a formatted string like "$1,234.50"
     */
    fun formatMoney(minorUnits: Long, currencyCode: String): String {
        val curr = getByCode(currencyCode)
        val isNegative = minorUnits < 0
        val absUnits = kotlin.math.abs(minorUnits)

        val mainPart: Long
        val fractionPart: Long
        val divisor = if (curr.decimals == 0) 1L else (if (curr.decimals == 2) 100L else 1000L)

        if (curr.decimals == 0) {
            mainPart = absUnits
            fractionPart = 0
        } else {
            mainPart = absUnits / divisor
            fractionPart = absUnits % divisor
        }

        val numberFormat = NumberFormat.getIntegerInstance(Locale.US)
        val formattedMain = numberFormat.format(mainPart)

        val formatted = if (curr.decimals == 0) {
            "${curr.symbol}$formattedMain"
        } else {
            val fracStr = fractionPart.toString().padStart(curr.decimals, '0')
            "${curr.symbol}$formattedMain.$fracStr"
        }

        return if (isNegative) "-$formatted" else formatted
    }

    /**
     * Parse user input text into minor units.
     * E.g. "25.50" -> 2550L for 2-decimal currency
     */
    fun parseToMinorUnits(input: String, currencyCode: String = "USD"): Long {
        val clean = input.trim().replace(",", ".").replace("[^0-9.]".toRegex(), "")
        if (clean.isBlank()) return 0L

        val curr = getByCode(currencyCode)
        if (curr.decimals == 0) {
            val integerOnly = clean.substringBefore('.')
            return integerOnly.toLongOrNull() ?: 0L
        }

        val parts = clean.split(".")
        val integerPart = parts[0].toLongOrNull() ?: 0L
        val fractionPart = if (parts.size > 1) {
            val dec = parts[1].take(curr.decimals).padEnd(curr.decimals, '0')
            dec.toLongOrNull() ?: 0L
        } else 0L

        val multiplier = if (curr.decimals == 2) 100L else 1000L
        return (integerPart * multiplier) + fractionPart
    }

    fun minorUnitsToDisplayNumber(minorUnits: Long, currencyCode: String = "USD"): String {
        val curr = getByCode(currencyCode)
        val isNegative = minorUnits < 0
        val absUnits = kotlin.math.abs(minorUnits)
        val divisor = if (curr.decimals == 0) 1L else 100L
        val main = absUnits / divisor
        val frac = absUnits % divisor
        val sign = if (isNegative) "-" else ""
        return if (curr.decimals == 0) {
            "$sign$main"
        } else {
            val fracStr = frac.toString().padStart(2, '0')
            "$sign$main.$fracStr"
        }
    }
}
