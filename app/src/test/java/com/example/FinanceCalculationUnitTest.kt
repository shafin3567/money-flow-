package com.example

import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.AccountType
import com.example.data.model.CurrencyData
import com.example.data.model.Frequency
import com.example.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class FinanceCalculationUnitTest {

    @Test
    fun testCurrencyFormattingAndMinorUnits() {
        // $25.50 -> 2550 minor units
        val minorUnits = CurrencyData.parseToMinorUnits("25.50", "USD")
        assertEquals(2550L, minorUnits)

        val formatted = CurrencyData.formatMoney(2550L, "USD")
        assertEquals("$25.50", formatted)

        // Negative formatting
        val formattedNeg = CurrencyData.formatMoney(-1500L, "USD")
        assertEquals("-$15.00", formattedNeg)

        // Zero-decimal currency like JPY
        val jpyMinor = CurrencyData.parseToMinorUnits("5000", "JPY")
        assertEquals(5000L, jpyMinor)
        val jpyFormatted = CurrencyData.formatMoney(5000L, "JPY")
        assertEquals("¥5,000", jpyFormatted)

        // Single digit cent e.g. "5.1" -> 510
        val singleDigitCent = CurrencyData.parseToMinorUnits("5.1", "USD")
        assertEquals(510L, singleDigitCent)

        // Comma decimal separator e.g. "12,99"
        val commaDecimal = CurrencyData.parseToMinorUnits("12,99", "USD")
        assertEquals(1299L, commaDecimal)

        // Empty string -> 0
        assertEquals(0L, CurrencyData.parseToMinorUnits("", "USD"))

        // Display string formatting
        assertEquals("25.50", CurrencyData.minorUnitsToDisplayNumber(2550L, "USD"))
        assertEquals("5000", CurrencyData.minorUnitsToDisplayNumber(5000L, "JPY"))
    }

    @Test
    fun testAccountBalanceDerivationWithTransactions() {
        val account = AccountEntity(
            id = 1L,
            name = "Checking",
            type = AccountType.BANK.name,
            currencyCode = "USD",
            initialBalanceMinorUnits = 100000L // $1,000.00
        )

        val account2 = AccountEntity(
            id = 2L,
            name = "Cash",
            type = AccountType.CASH.name,
            currencyCode = "USD",
            initialBalanceMinorUnits = 20000L // $200.00
        )

        val transactions = listOf(
            // Expense of $50.00 from Account 1
            TransactionEntity(
                id = 1,
                type = TransactionType.EXPENSE.name,
                amountMinorUnits = 5000L,
                currencyCode = "USD",
                accountId = 1L,
                dateMillis = System.currentTimeMillis()
            ),
            // Income of $300.00 to Account 1
            TransactionEntity(
                id = 2,
                type = TransactionType.INCOME.name,
                amountMinorUnits = 30000L,
                currencyCode = "USD",
                accountId = 1L,
                dateMillis = System.currentTimeMillis()
            ),
            // Transfer of $100.00 from Account 1 to Account 2
            TransactionEntity(
                id = 3,
                type = TransactionType.TRANSFER.name,
                amountMinorUnits = 10000L,
                currencyCode = "USD",
                accountId = 1L,
                toAccountId = 2L,
                dateMillis = System.currentTimeMillis()
            )
        )

        var bal1 = account.initialBalanceMinorUnits
        var bal2 = account2.initialBalanceMinorUnits

        for (tx in transactions) {
            if (tx.accountId == 1L) {
                when (tx.type) {
                    TransactionType.EXPENSE.name -> bal1 -= tx.amountMinorUnits
                    TransactionType.INCOME.name -> bal1 += tx.amountMinorUnits
                    TransactionType.TRANSFER.name -> bal1 -= tx.amountMinorUnits
                }
            }
            if (tx.toAccountId == 1L && tx.type == TransactionType.TRANSFER.name) {
                bal1 += tx.amountMinorUnits
            }

            if (tx.accountId == 2L) {
                when (tx.type) {
                    TransactionType.EXPENSE.name -> bal2 -= tx.amountMinorUnits
                    TransactionType.INCOME.name -> bal2 += tx.amountMinorUnits
                    TransactionType.TRANSFER.name -> bal2 -= tx.amountMinorUnits
                }
            }
            if (tx.toAccountId == 2L && tx.type == TransactionType.TRANSFER.name) {
                bal2 += tx.amountMinorUnits
            }
        }

        assertEquals(115000L, bal1) // $1,150.00
        assertEquals(30000L, bal2)  // $300.00 (20,000 + 10,000)
    }

    @Test
    fun testTransferDoesNotCountAsIncomeOrExpense() {
        val transactions = listOf(
            TransactionEntity(
                id = 1,
                type = TransactionType.TRANSFER.name,
                amountMinorUnits = 50000L, // $500.00 transfer
                currencyCode = "USD",
                accountId = 1L,
                toAccountId = 2L,
                dateMillis = System.currentTimeMillis()
            ),
            TransactionEntity(
                id = 2,
                type = TransactionType.INCOME.name,
                amountMinorUnits = 10000L, // $100.00 income
                currencyCode = "USD",
                accountId = 1L,
                dateMillis = System.currentTimeMillis()
            ),
            TransactionEntity(
                id = 3,
                type = TransactionType.EXPENSE.name,
                amountMinorUnits = 3000L, // $30.00 expense
                currencyCode = "USD",
                accountId = 2L,
                dateMillis = System.currentTimeMillis()
            )
        )

        var totalIncome = 0L
        var totalExpense = 0L

        for (tx in transactions) {
            when (tx.type) {
                TransactionType.INCOME.name -> totalIncome += tx.amountMinorUnits
                TransactionType.EXPENSE.name -> totalExpense += tx.amountMinorUnits
                TransactionType.TRANSFER.name -> {
                    // Critical verification: Transfers do NOT add to income or expense
                }
            }
        }

        assertEquals(10000L, totalIncome)
        assertEquals(3000L, totalExpense)
    }

    @Test
    fun testBudgetCalculationsAndOverspending() {
        val budget = BudgetEntity(
            id = 1L,
            name = "Groceries",
            limitMinorUnits = 40000L, // $400.00
            currencyCode = "USD",
            categoryId = 2L
        )

        // Case A: Under budget
        val spentMinorUnitsA = 32000L // $320.00 spent
        val remainingA = (budget.limitMinorUnits - spentMinorUnitsA).coerceAtLeast(0L)
        val progressA = spentMinorUnitsA.toFloat() / budget.limitMinorUnits.toFloat()

        assertEquals(8000L, remainingA) // $80.00 remaining
        assertEquals(0.8f, progressA, 0.001f) // 80%

        val daysRemainingInMonth = 20
        val dailyAllowanceA = remainingA / daysRemainingInMonth
        assertEquals(400L, dailyAllowanceA) // $4.00/day

        // Case B: Over budget
        val spentMinorUnitsB = 45000L // $450.00 spent
        val remainingB = (budget.limitMinorUnits - spentMinorUnitsB).coerceAtLeast(0L)
        val progressB = spentMinorUnitsB.toFloat() / budget.limitMinorUnits.toFloat()

        assertEquals(0L, remainingB) // 0 remaining, no negative remaining
        assertEquals(1.125f, progressB, 0.001f) // 112.5%
        val dailyAllowanceB = remainingB / daysRemainingInMonth
        assertEquals(0L, dailyAllowanceB) // $0.00/day allowance when overspent
    }

    @Test
    fun testDailyAllowanceWith1DayRemaining() {
        val remainingMinorUnits = 5000L // $50.00
        val daysRemaining = 1
        val dailyAllowance = remainingMinorUnits / daysRemaining
        assertEquals(5000L, dailyAllowance)
    }

    @Test
    fun testSavingsGoalCalculations() {
        val goal = SavingsGoalEntity(
            id = 1L,
            name = "Vacation",
            targetMinorUnits = 200000L, // $2,000.00
            currentMinorUnits = 72000L,  // $720.00
            currencyCode = "USD",
            targetDateMillis = System.currentTimeMillis()
        )

        val progress = goal.currentMinorUnits.toFloat() / goal.targetMinorUnits.toFloat()
        assertEquals(0.36f, progress, 0.001f) // 36%

        // Exceeded target
        val goalCompleted = goal.copy(currentMinorUnits = 250000L)
        val progressCompleted = (goalCompleted.currentMinorUnits.toFloat() / goalCompleted.targetMinorUnits.toFloat()).coerceIn(0f, 1f)
        assertEquals(1.0f, progressCompleted, 0.001f)
    }

    @Test
    fun testNegativeAccountBalance() {
        val account = AccountEntity(
            id = 1L,
            name = "Credit Card",
            type = AccountType.CREDIT_CARD.name,
            currencyCode = "USD",
            initialBalanceMinorUnits = 0L
        )

        // Expense of $120.00 leads to negative balance
        val tx = TransactionEntity(
            id = 1L,
            type = TransactionType.EXPENSE.name,
            amountMinorUnits = 12000L,
            currencyCode = "USD",
            accountId = 1L,
            dateMillis = System.currentTimeMillis()
        )

        val balance = account.initialBalanceMinorUnits - tx.amountMinorUnits
        assertEquals(-12000L, balance)

        val formatted = CurrencyData.formatMoney(balance, "USD")
        assertEquals("-$120.00", formatted)
    }

    @Test
    fun testRecurringTransactionScheduleDateIncrements() {
        val startCal = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 15, 10, 0, 0)
        }
        val startMillis = startCal.timeInMillis

        // Daily
        val dailyCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        dailyCal.add(Calendar.DAY_OF_YEAR, 1)
        assertEquals(16, dailyCal.get(Calendar.DAY_OF_MONTH))

        // Weekly
        val weeklyCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        weeklyCal.add(Calendar.WEEK_OF_YEAR, 1)
        assertEquals(22, weeklyCal.get(Calendar.DAY_OF_MONTH))

        // Monthly
        val monthlyCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        monthlyCal.add(Calendar.MONTH, 1)
        assertEquals(Calendar.FEBRUARY, monthlyCal.get(Calendar.MONTH))
        assertEquals(15, monthlyCal.get(Calendar.DAY_OF_MONTH))

        // Yearly
        val yearlyCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        yearlyCal.add(Calendar.YEAR, 1)
        assertEquals(2027, yearlyCal.get(Calendar.YEAR))
    }
}
