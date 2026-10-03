package com.example

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringTransactionEntity
import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import com.example.notifications.MoneyFlowNotificationManager
import com.example.notifications.MoneyFlowNotificationReceiver
import com.example.notifications.NotificationPreferences
import com.example.notifications.NotificationPreferencesRepository
import com.example.notifications.NotificationProcessor
import com.example.notifications.NotificationScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationSystemTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var prefsRepo: NotificationPreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        prefsRepo = NotificationPreferencesRepository(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testNotificationChannelsCreation() {
        MoneyFlowNotificationManager.createNotificationChannels(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = notificationManager.notificationChannels

        val channelIds = channels.map { it.id }
        assertTrue("Bills channel must exist", channelIds.contains(MoneyFlowNotificationManager.CHANNEL_BILLS))
        assertTrue("Budget channel must exist", channelIds.contains(MoneyFlowNotificationManager.CHANNEL_BUDGET))
        assertTrue("Savings channel must exist", channelIds.contains(MoneyFlowNotificationManager.CHANNEL_SAVINGS))
        assertTrue("Daily summary channel must exist", channelIds.contains(MoneyFlowNotificationManager.CHANNEL_DAILY_SUMMARY))
        assertTrue("Monthly summary channel must exist", channelIds.contains(MoneyFlowNotificationManager.CHANNEL_MONTHLY_SUMMARY))

        val billsChannel = notificationManager.getNotificationChannel(MoneyFlowNotificationManager.CHANNEL_BILLS)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, billsChannel.importance)
    }

    @Test
    fun testNotificationPreferencesDataStore() = runBlocking {
        prefsRepo.setMasterEnabled(true)
        prefsRepo.setRecurringEnabled(false)
        prefsRepo.setBudgetAlertsEnabled(true)
        prefsRepo.setDailySummaryEnabled(true)
        prefsRepo.setDailySummaryTime(21, 30)
        prefsRepo.setPrivacyMode(true)

        val prefs = prefsRepo.getPreferences()
        assertTrue(prefs.masterEnabled)
        assertFalse(prefs.recurringEnabled)
        assertTrue(prefs.budgetAlertsEnabled)
        assertTrue(prefs.dailySummaryEnabled)
        assertEquals(21, prefs.dailySummaryHour)
        assertEquals(30, prefs.dailySummaryMinute)
        assertTrue(prefs.privacyModeEnabled)
    }

    @Test
    fun testNotificationSchedulerAlarmSetup() {
        val prefs = NotificationPreferences(
            masterEnabled = true,
            dailySummaryEnabled = true,
            dailySummaryHour = 20,
            dailySummaryMinute = 0,
            monthlySummaryEnabled = true,
            monthlySummaryDay = 1,
            monthlySummaryHour = 9,
            monthlySummaryMinute = 0,
            recurringEnabled = true
        )

        NotificationScheduler.scheduleAll(context, prefs)
        // Ensure no crashes and cancellation completes cleanly
        NotificationScheduler.cancelAll(context)
    }

    @Test
    fun testMoneyFlowNotificationReceiverBootHandling() {
        val receiver = MoneyFlowNotificationReceiver()
        val bootIntent = Intent(Intent.ACTION_BOOT_COMPLETED)
        receiver.onReceive(context, bootIntent)

        val timeChangeIntent = Intent(Intent.ACTION_TIMEZONE_CHANGED)
        receiver.onReceive(context, timeChangeIntent)
    }

    @Test
    fun testBudgetThresholdDuplicatePrevention() = runBlocking {
        val budgetKey = "budget_1_2026-10_75"
        prefsRepo.recordBudgetThresholdTriggered(budgetKey)

        val prefs = prefsRepo.getPreferences()
        assertTrue("Budget threshold key should be recorded", prefs.lastTriggeredBudgetThresholds.contains(budgetKey))

        // Triggering again shouldn't duplicate
        prefsRepo.recordBudgetThresholdTriggered(budgetKey)
        val prefs2 = prefsRepo.getPreferences()
        assertEquals(1, prefs2.lastTriggeredBudgetThresholds.filter { it == budgetKey }.size)
    }

    @Test
    fun testGoalCompletedDuplicatePrevention() = runBlocking {
        val goalId = 42L
        prefsRepo.recordGoalCompletedTriggered(goalId)

        val prefs = prefsRepo.getPreferences()
        assertTrue(prefs.lastTriggeredGoalCompletedIds.contains("42"))
    }
}
