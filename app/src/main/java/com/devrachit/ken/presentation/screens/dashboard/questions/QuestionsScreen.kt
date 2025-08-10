
package com.devrachit.ken.presentation.screens.dashboard.questions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import com.devrachit.ken.utility.composeUtility.SearchBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import com.devrachit.ken.domain.models.Question
import com.devrachit.ken.utility.composeUtility.CompletionProgressHeader
import com.devrachit.ken.utility.composeUtility.DifficultyFilterSection
import com.devrachit.ken.utility.composeUtility.QuestionCard

@Composable
fun LeetCodeQuestionsScreen(
    viewModel: LeetCodeQuestionsViewModel = hiltViewModel()
) {
    val questions = viewModel.questions.collectAsLazyPagingItems()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val completedQuestions = remember { mutableStateOf(setOf<Int>()) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Progress Header
        if (questions.itemCount > 0) {
            CompletionProgressHeader(
                completed = completedQuestions.value.size,
                total = questions.itemCount,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Search Bar
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery, // Add this to your ViewModel
            onSearchSubmit = {
                focusManager.clearFocus()
                viewModel.searchQuestions(searchQuery) // Add this to your ViewModel
            },
            onClearSearch = {
                viewModel.clearSearch() // Add this to your ViewModel
                focusManager.clearFocus()
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Difficulty Filter Section
        DifficultyFilterSection(
            selectedDifficulty = selectedDifficulty,
            onFilterSelected = viewModel::filterByDifficulty,
            onClear = viewModel::clearFilters
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Handle refresh loading state
            if (questions.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            // Handle refresh error state
            if (questions.loadState.refresh is LoadState.Error) {
                item {
                    val error = questions.loadState.refresh as LoadState.Error
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Failed to load questions",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.error
                            )
                            Text(
                                text = error.error.localizedMessage ?: "Unknown error",
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Button(
                                onClick = { questions.retry() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            // Show "No results found" message when search/filter returns empty results
            if (questions.itemCount == 0 && questions.loadState.refresh is LoadState.NotLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedDifficulty != null) {
                                    "No questions found matching your criteria"
                                } else {
                                    "No questions available"
                                },
                                style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Center
                            )
                            if (searchQuery.isNotBlank() || selectedDifficulty != null) {
                                Text(
                                    text = "Try adjusting your search or filters",
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Questions list
            items(
                count = questions.itemCount,
                key = { index -> questions.peek(index)?.id ?: index }
            ) { index ->
                val question = questions[index]
                question?.let {
                    QuestionCard(
                        question = it,
                        isCompleted = completedQuestions.value.contains(it.id),
                        onToggleComplete = { questionId ->
                            completedQuestions.value = if (completedQuestions.value.contains(questionId)) {
                                completedQuestions.value - questionId
                            } else {
                                completedQuestions.value + questionId
                            }
                            // TODO: Save to ViewModel/Database
                        },
                        onClick = { /* Navigate to question detail */ }
                    )
                }
            }

            // Handle append loading state (loading more items)
            if (questions.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            // Handle append error state
            if (questions.loadState.append is LoadState.Error) {
                item {
                    val error = questions.loadState.append as LoadState.Error
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Failed to load more questions",
                                color = MaterialTheme.colors.error
                            )
                            Button(
                                onClick = { questions.retry() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}