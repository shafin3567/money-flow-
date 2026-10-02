package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.AccountType
import com.example.data.model.Frequency
import com.example.data.model.TransactionType
import com.example.data.repository.BackupRestoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BackupRestoreUnitTest {

    private lateinit var db: AppDatabase
    private lateinit var manager: BackupRestoreManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        manager = BackupRestoreManager(context, db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testBackupAndRestoreFullCycle() = runBlocking {
        // 1. Seed database
        val accId = db.accountDao().insertAccount(
            AccountEntity(
                name = "Wallet",
                type = AccountType.CASH.name,
                currencyCode = "USD",
                initialBalanceMinorUnits = 50000L
            )
        )

        val catId = db.categoryDao().insertCategory(
            CategoryEntity(
                name = "Food",
                type = "EXPENSE",
                iconName = "restaurant",
                colorHex = "#F7C59F"
            )
        )

        db.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE.name,
                amountMinorUnits = 1550L,
                currencyCode = "USD",
                categoryId = catId,
                accountId = accId,
                dateMillis = System.currentTimeMillis(),
                note = "Lunch"
            )
        )

        db.budgetDao().insertBudget(
            BudgetEntity(
                name = "Monthly Food",
                limitMinorUnits = 30000L,
                currencyCode = "USD",
                categoryId = catId
            )
        )

        db.savingsGoalDao().insertGoal(
            SavingsGoalEntity(
                name = "Emergency Fund",
                targetMinorUnits = 500000L,
                currentMinorUnits = 100000L,
                currencyCode = "USD",
                targetDateMillis = System.currentTimeMillis() + 10000000L
            )
        )

        db.recurringTransactionDao().insertRecurring(
            RecurringTransactionEntity(
                title = "Gym",
                type = TransactionType.EXPENSE.name,
                amountMinorUnits = 4500L,
                currencyCode = "USD",
                accountId = accId,
                frequency = Frequency.MONTHLY.name,
                nextDueDateMillis = System.currentTimeMillis() + 86400000L
            )
        )

        // 2. Create Backup JSON
        val backupJson = manager.createBackupJson()
        assertTrue(backupJson.contains("MoneyFlow"))
        assertTrue(backupJson.contains("Emergency Fund"))
        assertTrue(backupJson.contains("Lunch"))
        assertTrue(backupJson.contains("Gym"))

        // 3. Clear database to simulate fresh restore
        db.clearAllTables()
        assertEquals(0, db.accountDao().getAllAccounts().first().size)
        assertEquals(0, db.transactionDao().getAllTransactions().first().size)

        // 4. Restore from Backup JSON
        val result = manager.restoreFromJson(backupJson, replaceExisting = true)
        assertTrue(result.isSuccess)
        val meta = result.getOrNull()!!
        assertEquals(1, meta.accountsCount)
        assertEquals(1, meta.transactionsCount)
        assertEquals(1, meta.budgetsCount)
        assertEquals(1, meta.goalsCount)
        assertEquals(1, meta.recurringCount)

        // 5. Verify restored data in DB
        val restoredAccounts = db.accountDao().getAllAccounts().first()
        assertEquals(1, restoredAccounts.size)
        assertEquals("Wallet", restoredAccounts[0].name)
        assertEquals(50000L, restoredAccounts[0].initialBalanceMinorUnits)

        val restoredTx = db.transactionDao().getAllTransactions().first()
        assertEquals(1, restoredTx.size)
        assertEquals(1550L, restoredTx[0].amountMinorUnits)
        assertEquals("Lunch", restoredTx[0].note)

        val restoredRecurring = db.recurringTransactionDao().getAllRecurring().first()
        assertEquals(1, restoredRecurring.size)
        assertEquals("Gym", restoredRecurring[0].title)
    }
}
