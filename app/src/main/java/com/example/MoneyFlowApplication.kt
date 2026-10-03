package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.BackupRestoreManager
import com.example.data.repository.FinanceRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.notifications.MoneyFlowNotificationManager
import com.example.notifications.NotificationPreferencesRepository
import com.example.notifications.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoneyFlowApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val financeRepository by lazy { FinanceRepository(database) }
    val backupRestoreManager by lazy { BackupRestoreManager(this, database) }
    val notificationPreferencesRepository by lazy { NotificationPreferencesRepository(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels for Android 8.0+
        MoneyFlowNotificationManager.createNotificationChannels(this)

        // Schedule notification alarms based on current settings
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = notificationPreferencesRepository.getPreferences()
            NotificationScheduler.scheduleAll(this@MoneyFlowApplication, prefs)
        }
    }
}
