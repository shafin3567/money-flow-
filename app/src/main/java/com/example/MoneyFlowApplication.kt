package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.BackupRestoreManager
import com.example.data.repository.FinanceRepository
import com.example.data.repository.UserPreferencesRepository

class MoneyFlowApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val financeRepository by lazy { FinanceRepository(database) }
    val backupRestoreManager by lazy { BackupRestoreManager(this, database) }

    override fun onCreate() {
        super.onCreate()
    }
}
