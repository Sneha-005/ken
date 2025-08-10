package com.devrachit.ken.utility.composeUtility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.devrachit.ken.domain.models.Question
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.ImeAction

@Composable
fun DifficultyFilterSection(
    selectedDifficulty: String?,
    onFilterSelected: (String) -> Unit,
    onClear: () -> Unit
) {
    val difficulties = listOf("easy", "medium", "hard")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Filter buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            difficulties.forEach { difficulty ->
                Button(
                    onClick = { onFilterSelected(difficulty) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedDifficulty == difficulty)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface,
                        contentColor = if (selectedDifficulty == difficulty)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = difficulty.capitalize(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Clear button (centered)
        if (selectedDifficulty != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = onClear,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Clear Filter")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionCard(
    question: Question,
    isCompleted: Boolean = false,
    onToggleComplete: (Int) -> Unit = {},
    onClick: () -> Unit = {}
) {
    // Add debug logging
    android.util.Log.d("QuestionCard", "Rendering question: ${question.title}, ID: ${question.id}, Completed: $isCompleted")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        onClick = onClick,
        elevation = if (isCompleted) 2.dp else 4.dp,
        backgroundColor = if (isCompleted)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else
            MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Completion Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete(question.id ?: 0) },
                modifier = Modifier.padding(end = 12.dp, top = 4.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            // Question Content
            Column(modifier = Modifier.weight(1f)) {
                // Title with completion styling
                Text(
                    text = question.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (isCompleted)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                    ),
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Question details row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // Question ID
                        Text(
                            text = "#${question.id ?: "?"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        // AC Rate
                        question.acRate?.let { rate ->
                            Text(
                                text = "AC: ${String.format("%.1f%%", rate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    rate >= 70 -> Color(0xFF4CAF50) // Green
                                    rate >= 40 -> Color(0xFFFF9800) // Orange
                                    else -> Color(0xFFF44336) // Red
                                }
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        // Difficulty Badge
                        DifficultyBadge(
                            difficulty = question.difficulty ?: "Unknown"
                        )

                        // Premium Badge
                        if (question.paidOnly == true) {
                            Spacer(modifier = Modifier.height(4.dp))
                            PremiumBadge()
                        }
                    }
                }

                // Completion Status Indicator
                if (isCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Solved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DifficultyBadge(
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF4CAF50) to Color.White
        "medium" -> Color(0xFFFF9800) to Color.White
        "hard" -> Color(0xFFF44336) to Color.White
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f) to MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Text(
            text = difficulty.replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFFFD700), // Gold color
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Text(
            text = "Premium",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CompletionProgressHeader(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$completed / $total solved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Percentage text
            Text(
                text = "${String.format("%.1f", progress * 100)}% Complete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search questions...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchSubmit() }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    )
}