package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class BackupMetadata(
    val exportDate: Long,
    val accountsCount: Int,
    val transactionsCount: Int,
    val budgetsCount: Int,
    val goalsCount: Int,
    val recurringCount: Int = 0
)

class BackupRestoreManager(
    private val context: Context,
    private val database: AppDatabase
) {

    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val accounts = database.accountDao().getAllAccounts().first()
        val categories = database.categoryDao().getAllCategories().first()
        val transactions = database.transactionDao().getAllTransactions().first()
        val budgets = database.budgetDao().getAllBudgets().first()
        val goals = database.savingsGoalDao().getAllGoals().first()
        val recurring = database.recurringTransactionDao().getAllRecurring().first()

        val root = JSONObject().apply {
            put("app", "MoneyFlow")
            put("version", 1)
            put("timestamp", System.currentTimeMillis())

            val accountsArray = JSONArray()
            accounts.forEach {
                accountsArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("type", it.type)
                    put("currencyCode", it.currencyCode)
                    put("initialBalanceMinorUnits", it.initialBalanceMinorUnits)
                    put("colorHex", it.colorHex)
                    put("iconName", it.iconName)
                    put("isArchived", it.isArchived)
                    put("createdAt", it.createdAt)
                })
            }
            put("accounts", accountsArray)

            val categoriesArray = JSONArray()
            categories.forEach {
                categoriesArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("type", it.type)
                    put("iconName", it.iconName)
                    put("colorHex", it.colorHex)
                    put("isDefault", it.isDefault)
                })
            }
            put("categories", categoriesArray)

            val transactionsArray = JSONArray()
            transactions.forEach {
                transactionsArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("type", it.type)
                    put("amountMinorUnits", it.amountMinorUnits)
                    put("currencyCode", it.currencyCode)
                    if (it.categoryId != null) put("categoryId", it.categoryId)
                    put("accountId", it.accountId)
                    if (it.toAccountId != null) put("toAccountId", it.toAccountId)
                    put("dateMillis", it.dateMillis)
                    put("note", it.note)
                    put("isRecurring", it.isRecurring)
                    put("createdAt", it.createdAt)
                })
            }
            put("transactions", transactionsArray)

            val budgetsArray = JSONArray()
            budgets.forEach {
                budgetsArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("limitMinorUnits", it.limitMinorUnits)
                    put("currencyCode", it.currencyCode)
                    if (it.categoryId != null) put("categoryId", it.categoryId)
                    put("monthYear", it.monthYear)
                    put("notify75", it.notify75)
                    put("notify90", it.notify90)
                    put("notify100", it.notify100)
                })
            }
            put("budgets", budgetsArray)

            val goalsArray = JSONArray()
            goals.forEach {
                goalsArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("targetMinorUnits", it.targetMinorUnits)
                    put("currentMinorUnits", it.currentMinorUnits)
                    put("currencyCode", it.currencyCode)
                    put("targetDateMillis", it.targetDateMillis)
                    put("iconName", it.iconName)
                    put("colorHex", it.colorHex)
                    put("isArchived", it.isArchived)
                    put("createdAt", it.createdAt)
                })
            }
            put("goals", goalsArray)

            val recurringArray = JSONArray()
            recurring.forEach {
                recurringArray.put(JSONObject().apply {
                    put("id", it.id)
                    put("title", it.title)
                    put("type", it.type)
                    put("amountMinorUnits", it.amountMinorUnits)
                    put("currencyCode", it.currencyCode)
                    if (it.categoryId != null) put("categoryId", it.categoryId)
                    put("accountId", it.accountId)
                    if (it.toAccountId != null) put("toAccountId", it.toAccountId)
                    put("frequency", it.frequency)
                    put("nextDueDateMillis", it.nextDueDateMillis)
                    put("startDateMillis", it.startDateMillis)
                    put("isActive", it.isActive)
                    put("note", it.note)
                })
            }
            put("recurring", recurringArray)
        }

        root.toString(2)
    }

    suspend fun saveBackupToFile(filename: String = "moneyflow_backup.json"): File = withContext(Dispatchers.IO) {
        val json = createBackupJson()
        val file = File(context.filesDir, filename)
        file.writeText(json, Charsets.UTF_8)
        file
    }

    suspend fun restoreFromJson(jsonString: String, replaceExisting: Boolean): Result<BackupMetadata> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            val app = root.optString("app", "")
            if (app != "MoneyFlow") {
                return@withContext Result.failure(IllegalArgumentException("Invalid MoneyFlow backup format."))
            }

            val timestamp = root.optLong("timestamp", System.currentTimeMillis())

            val accountsArray = root.optJSONArray("accounts") ?: JSONArray()
            val categoriesArray = root.optJSONArray("categories") ?: JSONArray()
            val transactionsArray = root.optJSONArray("transactions") ?: JSONArray()
            val budgetsArray = root.optJSONArray("budgets") ?: JSONArray()
            val goalsArray = root.optJSONArray("goals") ?: JSONArray()
            val recurringArray = root.optJSONArray("recurring") ?: JSONArray()

            val accounts = mutableListOf<AccountEntity>()
            for (i in 0 until accountsArray.length()) {
                val o = accountsArray.getJSONObject(i)
                accounts.add(
                    AccountEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        name = o.getString("name"),
                        type = o.getString("type"),
                        currencyCode = o.getString("currencyCode"),
                        initialBalanceMinorUnits = o.optLong("initialBalanceMinorUnits", 0L),
                        colorHex = o.optString("colorHex", "#7CB9E8"),
                        iconName = o.optString("iconName", "account_balance"),
                        isArchived = o.optBoolean("isArchived", false),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val categories = mutableListOf<CategoryEntity>()
            for (i in 0 until categoriesArray.length()) {
                val o = categoriesArray.getJSONObject(i)
                categories.add(
                    CategoryEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        name = o.getString("name"),
                        type = o.getString("type"),
                        iconName = o.getString("iconName"),
                        colorHex = o.getString("colorHex"),
                        isDefault = o.optBoolean("isDefault", false)
                    )
                )
            }

            val transactions = mutableListOf<TransactionEntity>()
            for (i in 0 until transactionsArray.length()) {
                val o = transactionsArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        type = o.getString("type"),
                        amountMinorUnits = o.getLong("amountMinorUnits"),
                        currencyCode = o.getString("currencyCode"),
                        categoryId = if (o.has("categoryId")) o.getLong("categoryId") else null,
                        accountId = o.getLong("accountId"),
                        toAccountId = if (o.has("toAccountId")) o.getLong("toAccountId") else null,
                        dateMillis = o.getLong("dateMillis"),
                        note = o.optString("note", ""),
                        isRecurring = o.optBoolean("isRecurring", false),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val budgets = mutableListOf<BudgetEntity>()
            for (i in 0 until budgetsArray.length()) {
                val o = budgetsArray.getJSONObject(i)
                budgets.add(
                    BudgetEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        name = o.getString("name"),
                        limitMinorUnits = o.getLong("limitMinorUnits"),
                        currencyCode = o.getString("currencyCode"),
                        categoryId = if (o.has("categoryId")) o.getLong("categoryId") else null,
                        monthYear = o.optString("monthYear", "ALL"),
                        notify75 = o.optBoolean("notify75", true),
                        notify90 = o.optBoolean("notify90", true),
                        notify100 = o.optBoolean("notify100", true)
                    )
                )
            }

            val goals = mutableListOf<SavingsGoalEntity>()
            for (i in 0 until goalsArray.length()) {
                val o = goalsArray.getJSONObject(i)
                goals.add(
                    SavingsGoalEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        name = o.getString("name"),
                        targetMinorUnits = o.getLong("targetMinorUnits"),
                        currentMinorUnits = o.optLong("currentMinorUnits", 0L),
                        currencyCode = o.getString("currencyCode"),
                        targetDateMillis = o.getLong("targetDateMillis"),
                        iconName = o.optString("iconName", "flag"),
                        colorHex = o.optString("colorHex", "#F7C59F"),
                        isArchived = o.optBoolean("isArchived", false),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val recurring = mutableListOf<RecurringTransactionEntity>()
            for (i in 0 until recurringArray.length()) {
                val o = recurringArray.getJSONObject(i)
                recurring.add(
                    RecurringTransactionEntity(
                        id = if (replaceExisting) o.getLong("id") else 0L,
                        title = o.getString("title"),
                        type = o.getString("type"),
                        amountMinorUnits = o.getLong("amountMinorUnits"),
                        currencyCode = o.getString("currencyCode"),
                        categoryId = if (o.has("categoryId")) o.getLong("categoryId") else null,
                        accountId = o.getLong("accountId"),
                        toAccountId = if (o.has("toAccountId")) o.getLong("toAccountId") else null,
                        frequency = o.getString("frequency"),
                        nextDueDateMillis = o.getLong("nextDueDateMillis"),
                        startDateMillis = o.optLong("startDateMillis", System.currentTimeMillis()),
                        isActive = o.optBoolean("isActive", true),
                        note = o.optString("note", "")
                    )
                )
            }

            // Insert into DB
            if (accounts.isNotEmpty()) database.accountDao().insertAccounts(accounts)
            if (categories.isNotEmpty()) database.categoryDao().insertCategories(categories)
            if (transactions.isNotEmpty()) database.transactionDao().insertTransactions(transactions)
            if (budgets.isNotEmpty()) database.budgetDao().insertBudgets(budgets)
            if (goals.isNotEmpty()) database.savingsGoalDao().insertGoals(goals)
            if (recurring.isNotEmpty()) database.recurringTransactionDao().insertAllRecurring(recurring)

            Result.success(
                BackupMetadata(
                    exportDate = timestamp,
                    accountsCount = accounts.size,
                    transactionsCount = transactions.size,
                    budgetsCount = budgets.size,
                    goalsCount = goals.size,
                    recurringCount = recurring.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
