package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import com.example.ui.components.MoneyFlowTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CurrencyData
import com.example.data.model.CurrencyItem
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.ClayWaveBackground
import com.example.ui.components.MoneyFlowCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.MoneyFlowTheme

@Composable
fun OnboardingScreen(
    onComplete: (currency: String, enableLock: Boolean, pin: String, useBiometrics: Boolean, accountName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var step by remember { mutableIntStateOf(0) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var enableLock by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var useBiometrics by remember { mutableStateOf(false) }
    var initialAccountName by remember { mutableStateOf("Cash Wallet") }
    var pinError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top Bar with Back and Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 0) {
                    IconButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (colors.isDark) colors.surfaceElevated else Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(42.dp))
                }

                // Step dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (index == step) 28.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (index == step) Color(0xFF7FA7C4) else colors.divider)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(42.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content Animated
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                modifier = Modifier.weight(1f),
                label = "onboardingStep"
            ) { currentStep ->
                when (currentStep) {
                    0 -> WelcomeStep(
                        onNext = { step = 1 }
                    )
                    1 -> CurrencyStep(
                        selectedCurrency = selectedCurrency,
                        onCurrencySelected = { selectedCurrency = it },
                        onNext = { step = 2 }
                    )
                    2 -> SecurityStep(
                        enableLock = enableLock,
                        onEnableLockChange = { enableLock = it },
                        pin = pin,
                        onPinChange = {
                            pin = it
                            pinError = false
                        },
                        useBiometrics = useBiometrics,
                        onUseBiometricsChange = { useBiometrics = it },
                        pinError = pinError,
                        onNext = {
                            if (enableLock && pin.length < 4) {
                                pinError = true
                            } else {
                                step = 3
                            }
                        }
                    )
                    3 -> FinishStep(
                        currency = selectedCurrency,
                        enableLock = enableLock,
                        accountName = initialAccountName,
                        onAccountNameChange = { initialAccountName = it },
                        onFinish = {
                            onComplete(selectedCurrency, enableLock, pin, useBiometrics, initialAccountName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    val colors = MoneyFlowTheme.colors
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Soft 3D Claymorphic Hero Card with Wave & Spheres
        Box(
            modifier = Modifier
                .size(200.dp)
                .shadow(
                    elevation = if (colors.isDark) 4.dp else 8.dp,
                    shape = RoundedCornerShape(44.dp),
                    ambientColor = colors.shadow,
                    spotColor = colors.shadow
                )
                .clip(RoundedCornerShape(44.dp))
        ) {
            ClayWaveBackground(isDark = colors.isDark)

            Clay3dSphere(
                baseColor = Color(0xFFFBE4C6),
                size = 28.dp,
                modifier = Modifier.offset(x = 24.dp, y = 20.dp)
            )

            Clay3dSphere(
                baseColor = Color(0xFFFFFDFC),
                size = 56.dp,
                modifier = Modifier.align(Alignment.Center)
            )

            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color(0xFF7FA7C4),
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = colors.textSecondary,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        PrimaryButton(
            text = stringResource(R.string.get_started),
            onClick = onNext
        )
    }
}

@Composable
private fun CurrencyStep(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onNext: () -> Unit
) {
    val colors = MoneyFlowTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    val currencies = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            CurrencyData.SUPPORTED_CURRENCIES
        } else {
            CurrencyData.SUPPORTED_CURRENCIES.filter {
                it.code.contains(searchQuery, ignoreCase = true) ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.select_currency),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
        )
        Text(
            text = stringResource(R.string.currency_hint),
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoneyFlowTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search currency...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = colors.inputIcon
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (colors.isDark) 2.dp else 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = colors.shadow,
                    spotColor = colors.shadow
                ),
            shape = RoundedCornerShape(20.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currencies, key = { it.code }) { item ->
                val isSelected = item.code == selectedCurrency
                val itemShape = RoundedCornerShape(20.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isSelected) 3.dp else 1.dp,
                            shape = itemShape,
                            ambientColor = colors.shadow,
                            spotColor = colors.shadow
                        )
                        .clip(itemShape)
                        .background(if (isSelected) Color(0xFFD4E5F2) else if (colors.isDark) colors.surfaceElevated else Color.White)
                        .clickable { onCurrencySelected(item.code) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Clay3dSphere(
                            baseColor = if (isSelected) Color(0xFF7FA7C4) else Color(0xFFFBE4C6),
                            size = 38.dp
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.code} (${item.symbol})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF7FA7C4),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Continue",
            onClick = onNext
        )
    }
}

@Composable
private fun SecurityStep(
    enableLock: Boolean,
    onEnableLockChange: (Boolean) -> Unit,
    pin: String,
    onPinChange: (String) -> Unit,
    useBiometrics: Boolean,
    onUseBiometricsChange: (Boolean) -> Unit,
    pinError: Boolean,
    onNext: () -> Unit
) {
    val colors = MoneyFlowTheme.colors
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.security_setup),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
        )
        Text(
            text = stringResource(R.string.security_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
        )

        Spacer(modifier = Modifier.height(24.dp))

        MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Clay3dSphere(
                            baseColor = if (enableLock) Color(0xFF5BA678) else Color(0xFF9A8B8C),
                            size = 38.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.enable_lock),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            )
                            Text(
                                text = "Require authentication to view finances",
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                            )
                        }
                    }

                    Switch(
                        checked = enableLock,
                        onCheckedChange = onEnableLockChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF7FA7C4)
                        )
                    )
                }

                if (enableLock) {
                    Spacer(modifier = Modifier.height(20.dp))

                    MoneyFlowTextField(
                        value = pin,
                        onValueChange = {
                            if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                onPinChange(it)
                            }
                        },
                        label = { Text("Set 4 to 6 digit PIN") },
                        placeholder = { Text("••••") },
                        isError = pinError,
                        supportingText = if (pinError) {
                            { Text("Please enter at least 4 digits", color = colors.inputError) }
                        } else null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enable Biometrics (Fingerprint / Face)",
                                style = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary)
                            )
                        }

                        Switch(
                            checked = useBiometrics,
                            onCheckedChange = onUseBiometricsChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF7FA7C4)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "Continue",
            onClick = onNext
        )
    }
}

@Composable
private fun FinishStep(
    currency: String,
    enableLock: Boolean,
    accountName: String,
    onAccountNameChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    val colors = MoneyFlowTheme.colors
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Ready to start",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
            )
            Text(
                text = "Set up your initial primary account.",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column {
                    Text(
                        text = "PRIMARY ACCOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = colors.textTertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MoneyFlowTextField(
                        value = accountName,
                        onValueChange = onAccountNameChange,
                        label = { Text("Account Name") },
                        placeholder = { Text("Cash Wallet, Main Bank...") },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Default Currency", style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary))
                        Text(currency, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Security Lock", style = MaterialTheme.typography.bodyMedium.copy(color = colors.textSecondary))
                        Text(if (enableLock) "Active" else "Disabled", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        PrimaryButton(
            text = stringResource(R.string.finish_setup),
            onClick = onFinish
        )
    }
}
