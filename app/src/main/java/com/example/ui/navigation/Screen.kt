package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object Budgets : Screen("budgets")
    object More : Screen("more")
    object Accounts : Screen("accounts")
    object SavingsGoals : Screen("savings_goals")
    object Recurring : Screen("recurring")
    object Analytics : Screen("analytics")
    object SecurityLock : Screen("security_lock")
    object BackupRestore : Screen("backup_restore")
    object Categories : Screen("categories")
    object About : Screen("about")
}
