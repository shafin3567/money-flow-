package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.BackupRestoreManager
import com.example.data.repository.FinanceRepository
import com.example.data.repository.UserPreferencesRepository

class FinanceViewModelFactory(
    private val financeRepository: FinanceRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val backupRestoreManager: BackupRestoreManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(financeRepository, preferencesRepository, backupRestoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
