package com.example.ui.screens.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.repository.BudgetProgress
import com.example.ui.components.BudgetProgressCard
import com.example.ui.components.Clay3dSphere
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MoneyFlowCard
import com.example.ui.theme.MoneyFlowTheme

@Composable
fun BudgetsScreen(
    budgets: List<BudgetProgress>,
    categories: List<CategoryEntity>,
    onAddBudgetClick: () -> Unit,
    onDeleteBudget: (BudgetEntity) -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = MoneyFlowTheme.colors
    var budgetToDelete by remember { mutableStateOf<BudgetEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        if (onBackClick != null) {
                            IconButton(
                                onClick = onBackClick,
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
                            Spacer(modifier = Modifier.width(14.dp))
                        }

                        Column {
                            Text(
                                text = "Budgets",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = colors.textPrimary,
                                    letterSpacing = (-0.5).sp,
                                    fontSize = 26.sp
                                ),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Spend mindfully & avoid overspending",
                                style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 3D Sphere Add Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Clay3dSphere(
                            baseColor = Color(0xFF7FA7C4),
                            size = 46.dp
                        )
                        IconButton(onClick = onAddBudgetClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Budget",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Budget list or Empty State
        if (budgets.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(24.dp)) {
                    MoneyFlowCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                        EmptyStateView(
                            icon = Icons.Default.PieChart,
                            title = "No budgets created",
                            description = "Set category-based or overall monthly spending limits to stay on track.",
                            actionButtonText = "Create Budget",
                            onActionClick = onAddBudgetClick
                        )
                    }
                }
            }
        } else {
            items(budgets, key = { it.budget.id }) { bp ->
                val catName = categories.find { it.id == bp.budget.categoryId }?.name ?: "Overall Budget"
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    BudgetProgressCard(
                        budgetProgress = bp,
                        categoryName = catName,
                        onClick = { budgetToDelete = bp.budget }
                    )
                }
            }
        }
    }

    if (budgetToDelete != null) {
        val b = budgetToDelete!!
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = {
                Text("Delete Budget?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text("Do you want to remove the budget '${b.name}'? Your transactions will remain safe.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBudget(b)
                        budgetToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFE26B58), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
