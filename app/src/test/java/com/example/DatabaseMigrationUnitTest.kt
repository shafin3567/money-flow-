package com.example

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DatabaseMigrationUnitTest {

    @Test
    fun testMigration1To2_preservesAllDataAndCreatesIndices() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
            .name("test_migration.db")
            .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Create v1 tables without the new v2 indices
                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `accounts` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `type` TEXT NOT NULL,
                            `currencyCode` TEXT NOT NULL,
                            `initialBalanceMinorUnits` INTEGER NOT NULL,
                            `colorHex` TEXT NOT NULL,
                            `iconName` TEXT NOT NULL,
                            `isArchived` INTEGER NOT NULL,
                            `createdAt` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `categories` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `type` TEXT NOT NULL,
                            `iconName` TEXT NOT NULL,
                            `colorHex` TEXT NOT NULL,
                            `isDefault` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `transactions` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `type` TEXT NOT NULL,
                            `amountMinorUnits` INTEGER NOT NULL,
                            `currencyCode` TEXT NOT NULL,
                            `categoryId` INTEGER,
                            `accountId` INTEGER NOT NULL,
                            `toAccountId` INTEGER,
                            `dateMillis` INTEGER NOT NULL,
                            `note` TEXT NOT NULL,
                            `isRecurring` INTEGER NOT NULL,
                            `recurringRuleId` INTEGER,
                            `createdAt` INTEGER NOT NULL,
                            FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `budgets` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `limitMinorUnits` INTEGER NOT NULL,
                            `currencyCode` TEXT NOT NULL,
                            `categoryId` INTEGER,
                            `monthYear` TEXT NOT NULL,
                            `notify75` INTEGER NOT NULL,
                            `notify90` INTEGER NOT NULL,
                            `notify100` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `savings_goals` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `targetMinorUnits` INTEGER NOT NULL,
                            `currentMinorUnits` INTEGER NOT NULL,
                            `currencyCode` TEXT NOT NULL,
                            `targetDateMillis` INTEGER NOT NULL,
                            `iconName` TEXT NOT NULL,
                            `colorHex` TEXT NOT NULL,
                            `isArchived` INTEGER NOT NULL,
                            `createdAt` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `recurring_transactions` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `type` TEXT NOT NULL,
                            `amountMinorUnits` INTEGER NOT NULL,
                            `currencyCode` TEXT NOT NULL,
                            `categoryId` INTEGER,
                            `accountId` INTEGER NOT NULL,
                            `toAccountId` INTEGER,
                            `frequency` TEXT NOT NULL,
                            `nextDueDateMillis` INTEGER NOT NULL,
                            `startDateMillis` INTEGER NOT NULL,
                            `isActive` INTEGER NOT NULL,
                            `note` TEXT NOT NULL
                        )
                    """.trimIndent())
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val db = helper.writableDatabase

        // 1. Insert seed data in v1
        db.execSQL("INSERT INTO accounts (id, name, type, currencyCode, initialBalanceMinorUnits, colorHex, iconName, isArchived, createdAt) VALUES (1, 'Main Checking', 'BANK', 'USD', 50000, '#7CB9E8', 'account_balance', 0, 1000)")
        db.execSQL("INSERT INTO categories (id, name, type, iconName, colorHex, isDefault) VALUES (1, 'Groceries', 'EXPENSE', 'shopping_cart', '#72B095', 1)")
        db.execSQL("INSERT INTO transactions (id, type, amountMinorUnits, currencyCode, categoryId, accountId, toAccountId, dateMillis, note, isRecurring, createdAt) VALUES (1, 'EXPENSE', 4500, 'USD', 1, 1, NULL, 1500, 'Dinner', 0, 1500)")
        db.execSQL("INSERT INTO budgets (id, name, limitMinorUnits, currencyCode, categoryId, monthYear, notify75, notify90, notify100) VALUES (1, 'Food', 50000, 'USD', 1, 'ALL', 1, 1, 1)")
        db.execSQL("INSERT INTO savings_goals (id, name, targetMinorUnits, currentMinorUnits, currencyCode, targetDateMillis, iconName, colorHex, isArchived, createdAt) VALUES (1, 'Car', 200000, 50000, 'USD', 999999, 'directions_car', '#F7C59F', 0, 1000)")
        db.execSQL("INSERT INTO recurring_transactions (id, title, type, amountMinorUnits, currencyCode, categoryId, accountId, toAccountId, frequency, nextDueDateMillis, startDateMillis, isActive, note) VALUES (1, 'Rent', 'EXPENSE', 120000, 'USD', NULL, 1, NULL, 'MONTHLY', 200000, 100000, 1, 'Apartment')")

        // 2. Execute Migration from 1 to 2
        AppDatabase.MIGRATION_1_2.migrate(db)

        // 3. Verify all records survived
        val accountCursor = db.query("SELECT name, initialBalanceMinorUnits FROM accounts WHERE id = 1")
        assertTrue(accountCursor.moveToFirst())
        assertEquals("Main Checking", accountCursor.getString(0))
        assertEquals(50000L, accountCursor.getLong(1))
        accountCursor.close()

        val catCursor = db.query("SELECT name FROM categories WHERE id = 1")
        assertTrue(catCursor.moveToFirst())
        assertEquals("Groceries", catCursor.getString(0))
        catCursor.close()

        val txCursor = db.query("SELECT amountMinorUnits, note FROM transactions WHERE id = 1")
        assertTrue(txCursor.moveToFirst())
        assertEquals(4500L, txCursor.getLong(0))
        assertEquals("Dinner", txCursor.getString(1))
        txCursor.close()

        val budgetCursor = db.query("SELECT limitMinorUnits FROM budgets WHERE id = 1")
        assertTrue(budgetCursor.moveToFirst())
        assertEquals(50000L, budgetCursor.getLong(0))
        budgetCursor.close()

        val goalCursor = db.query("SELECT currentMinorUnits, targetMinorUnits FROM savings_goals WHERE id = 1")
        assertTrue(goalCursor.moveToFirst())
        assertEquals(50000L, goalCursor.getLong(0))
        assertEquals(200000L, goalCursor.getLong(1))
        goalCursor.close()

        val recCursor = db.query("SELECT title, amountMinorUnits FROM recurring_transactions WHERE id = 1")
        assertTrue(recCursor.moveToFirst())
        assertEquals("Rent", recCursor.getString(0))
        assertEquals(120000L, recCursor.getLong(1))
        recCursor.close()

        // 4. Verify new indexes exist and were created by migration
        val indexCursor = db.query("PRAGMA index_list('transactions')")
        var foundToAccountIndex = false
        while (indexCursor.moveToNext()) {
            val name = indexCursor.getString(1)
            if (name == "index_transactions_toAccountId") {
                foundToAccountIndex = true
            }
        }
        indexCursor.close()
        assertTrue("Index on toAccountId must exist after migration", foundToAccountIndex)

        db.close()
    }
}
