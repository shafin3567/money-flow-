package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.BackupRestoreManager
import com.example.data.repository.FinanceRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.notifications.NotificationPreferencesRepository

class FinanceViewModelFactory(
    private val financeRepository: FinanceRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val backupRestoreManager: BackupRestoreManager,
    private val notificationPreferencesRepository: NotificationPreferencesRepository,
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(
                financeRepository,
                preferencesRepository,
                backupRestoreManager,
                notificationPreferencesRepository,
                appContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

