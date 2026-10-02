package com.example.ui.screens.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CurrencyData
import com.example.data.model.Frequency
import com.example.data.model.TransactionType
import com.example.ui.components.CategoryIconBadge
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.NegativeCoral
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PowderBlue
import com.example.ui.theme.PowderBlueAccent
import com.example.ui.theme.SoftPeach
import com.example.ui.theme.SoftPeachAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionComposerSheet(
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    defaultCurrencyCode: String,
    initialType: TransactionType = TransactionType.EXPENSE,
    lastUsedCategoryId: Long = -1L,
    lastUsedAccountId: Long = -1L,
    existingTransaction: TransactionEntity? = null,
    onDismissRequest: () -> Unit,
    onSaveTransaction: (
        type: TransactionType,
        amountMinorUnits: Long,
        currencyCode: String,
        categoryId: Long?,
        accountId: Long,
        toAccountId: Long?,
        dateMillis: Long,
        note: String,
        isRecurring: Boolean,
        recurringFrequency: Frequency?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var type by remember {
        mutableStateOf(
            existingTransaction?.let { TransactionType.valueOf(it.type) } ?: initialType
        )
    }

    var amountText by remember {
        mutableStateOf(
            if (existingTransaction != null) {
                CurrencyData.minorUnitsToDisplayNumber(existingTransaction.amountMinorUnits, existingTransaction.currencyCode)
            } else ""
        )
    }

    // Default account
    val defaultAccId = remember(accounts, lastUsedAccountId, existingTransaction) {
        existingTransaction?.accountId
            ?: if (accounts.any { it.id == lastUsedAccountId }) lastUsedAccountId
            else accounts.firstOrNull()?.id ?: 0L
    }
    var selectedAccountId by remember { mutableStateOf(defaultAccId) }

    // Destination account (for transfer)
    var selectedToAccountId by remember {
        mutableStateOf(
            existingTransaction?.toAccountId ?: accounts.firstOrNull { it.id != selectedAccountId }?.id
        )
    }

    // Default category
    val relevantCategories = remember(categories, type) {
        categories.filter { it.type == type.name }
    }

    val defaultCatId = remember(relevantCategories, lastUsedCategoryId, existingTransaction) {
        existingTransaction?.categoryId
            ?: if (relevantCategories.any { it.id == lastUsedCategoryId }) lastUsedCategoryId
            else relevantCategories.firstOrNull()?.id ?: -1L
    }
    var selectedCategoryId by remember { mutableStateOf(defaultCatId) }

    var note by remember { mutableStateOf(existingTransaction?.note ?: "") }
    var dateMillis by remember { mutableStateOf(existingTransaction?.dateMillis ?: System.currentTimeMillis()) }
    var isRecurring by remember { mutableStateOf(existingTransaction?.isRecurring ?: false) }
    var frequency by remember { mutableStateOf(Frequency.MONTHLY) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val curr = CurrencyData.getByCode(defaultCurrencyCode)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (existingTransaction != null) "Edit Transaction" else "New Transaction",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4EFE6))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Transaction Type Segmented Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF5EFE6))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    TransactionType.EXPENSE to "Expense",
                    TransactionType.INCOME to "Income",
                    TransactionType.TRANSFER to "Transfer"
                ).forEach { (t, label) ->
                    val isSelected = type == t
                    val activeBg = when (t) {
                        TransactionType.EXPENSE -> NegativeCoral
                        TransactionType.INCOME -> PositiveGreen
                        TransactionType.TRANSFER -> PowderBlueAccent
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) activeBg else Color.Transparent)
                            .clickable {
                                type = t
                                // Auto pick first category for new type
                                val cats = categories.filter { it.type == t.name }
                                selectedCategoryId = cats.firstOrNull()?.id ?: -1L
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Input Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        when (type) {
                            TransactionType.EXPENSE -> Color(0xFFFFECE9)
                            TransactionType.INCOME -> Color(0xFFEAF8F0)
                            TransactionType.TRANSFER -> PowderBlue.copy(alpha = 0.45f)
                        }
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = curr.symbol,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = when (type) {
                                TransactionType.EXPENSE -> NegativeCoral
                                TransactionType.INCOME -> PositiveGreen
                                TransactionType.TRANSFER -> PowderBlueAccent
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { input ->
                            // Allow valid decimal formatting
                            if (input.matches(Regex("""^\d*([.,]\d{0,2})?$"""))) {
                                amountText = input.replace(',', '.')
                                errorMessage = null
                            }
                        },
                        placeholder = {
                            Text(
                                "0.00",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = TextTertiary
                                )
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = TextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Selection (only for Expense and Income)
            if (type != TransactionType.TRANSFER) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredCats = categories.filter { it.type == type.name }
                    filteredCats.forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) PowderBlueAccent else Color(0xFFF5EFE6))
                                .clickable { selectedCategoryId = cat.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIconBadge(
                                iconName = cat.iconName,
                                colorHex = if (isSelected) "#FFFFFF" else cat.colorHex,
                                size = 26.dp,
                                iconSize = 14.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Account Selection
            Text(
                text = if (type == TransactionType.TRANSFER) "From Account" else "Account",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = selectedAccountId == acc.id
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PowderBlueAccent else Color(0xFFF5EFE6))
                            .clickable {
                                selectedAccountId = acc.id
                                if (selectedToAccountId == acc.id) {
                                    selectedToAccountId = accounts.firstOrNull { it.id != acc.id }?.id
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = acc.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        )
                    }
                }
            }

            // Destination account for Transfer
            if (type == TransactionType.TRANSFER) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To Account",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.filter { it.id != selectedAccountId }.forEach { acc ->
                        val isSelected = selectedToAccountId == acc.id
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) PowderBlueAccent else Color(0xFFF5EFE6))
                                .clickable { selectedToAccountId = acc.id }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = acc.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date & Time quick chips
            Text(
                text = "Date",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val cal = Calendar.getInstance()
                val todayMillis = cal.timeInMillis

                val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                val yesterdayMillis = yesterdayCal.timeInMillis

                val isToday = isSameDay(dateMillis, todayMillis)
                val isYesterday = isSameDay(dateMillis, yesterdayMillis)
                val isCustom = !isToday && !isYesterday

                DateChip(
                    text = "Today",
                    isSelected = isToday,
                    onClick = { dateMillis = todayMillis }
                )

                DateChip(
                    text = "Yesterday",
                    isSelected = isYesterday,
                    onClick = { dateMillis = yesterdayMillis }
                )

                val customDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                DateChip(
                    text = if (isCustom) customDateFormat.format(Date(dateMillis)) else "Custom",
                    isSelected = isCustom,
                    icon = Icons.Default.Event,
                    onClick = { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Note Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                placeholder = { Text("e.g. Lunch, Groceries, Rent") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PowderBlueAccent,
                    unfocusedBorderColor = Color(0x2223272F)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recurring transaction toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Make recurring",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Auto-track future repeated instances",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = isRecurring,
                    onCheckedChange = { isRecurring = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PowderBlueAccent
                    )
                )
            }

            AnimatedVisibility(visible = isRecurring) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Frequency",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(Frequency.DAILY, Frequency.WEEKLY, Frequency.MONTHLY, Frequency.YEARLY).forEach { freq ->
                            val selected = frequency == freq
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) PowderBlueAccent else Color(0xFFF5EFE6))
                                    .clickable { frequency = freq }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = freq.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) Color.White else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Error display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            PrimaryButton(
                text = if (existingTransaction != null) "Update Transaction" else "Save Transaction",
                onClick = {
                    val minorUnits = CurrencyData.parseToMinorUnits(amountText, defaultCurrencyCode)
                    if (minorUnits <= 0) {
                        errorMessage = "Please enter an amount greater than 0"
                        return@PrimaryButton
                    }
                    if (accounts.isEmpty()) {
                        errorMessage = "Please create an account first"
                        return@PrimaryButton
                    }
                    if (type == TransactionType.TRANSFER && (selectedToAccountId == null || selectedToAccountId == selectedAccountId)) {
                        errorMessage = "Please select a different destination account"
                        return@PrimaryButton
                    }

                    onSaveTransaction(
                        type,
                        minorUnits,
                        defaultCurrencyCode,
                        if (type == TransactionType.TRANSFER) null else selectedCategoryId.takeIf { it > 0 },
                        selectedAccountId,
                        if (type == TransactionType.TRANSFER) selectedToAccountId else null,
                        dateMillis,
                        note.trim(),
                        isRecurring,
                        if (isRecurring) frequency else null
                    )
                    onDismissRequest()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) {
                    Text("Select", color = PowderBlueAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun DateChip(
    text: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PowderBlueAccent else Color(0xFFF5EFE6))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimary
            )
        )
    }
}
