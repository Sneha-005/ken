package com.devrachit.ken.presentation.screens.dashboard.questions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.devrachit.ken.domain.models.SearchFilterParams
import com.devrachit.ken.domain.repository.remote.LeetcodeRemoteRepository
import com.devrachit.ken.utility.NetworkUtility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeetCodeQuestionsViewModel @Inject constructor(
    private val repository: LeetcodeRemoteRepository
) : ViewModel() {

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combine search and difficulty filter with debugging
    private val searchAndFilterParams = combine(
        _searchQuery,
        _selectedDifficulty
    ) { query, difficulty ->
        val params = SearchFilterParams(
            query = query.trim(),
            difficulty = difficulty
        )
        Log.d("LeetCodeViewModel", "Filter params updated: query='${params.query}', difficulty='${params.difficulty}'")
        params
    }.distinctUntilChanged()

    // Updated questions flow that responds to both search and filter changes
    val questions = searchAndFilterParams
        .flatMapLatest { params ->
            Log.d("LeetCodeViewModel", "Creating new Pager with params: ${params}")
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    prefetchDistance = 5
                ),
                pagingSourceFactory = {
                    LeetCodeQuestionsPagingSource(
                        repository = repository,
                        difficulty = params.difficulty,
                        searchQuery = params.query.takeIf { it.isNotBlank() }
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

    // Search functions with debugging
    fun updateSearchQuery(query: String) {
        Log.d("LeetCodeViewModel", "Search query updated: '$query'")
        _searchQuery.value = query
    }

    fun searchQuestions(query: String) {
        Log.d("LeetCodeViewModel", "Search submitted: '$query'")
        _searchQuery.value = query.trim()
    }

    fun clearSearch() {
        Log.d("LeetCodeViewModel", "Search cleared")
        _searchQuery.value = ""
    }

    // Filter functions with debugging
    fun filterByDifficulty(difficulty: String) {
        Log.d("LeetCodeViewModel", "Difficulty filter applied: '$difficulty'")
        _selectedDifficulty.value = difficulty
    }

    fun clearFilters() {
        Log.d("LeetCodeViewModel", "All filters cleared")
        _selectedDifficulty.value = null
        _searchQuery.value = ""
    }

    // Test function to discover what categories are available
    fun discoverAvailableCategories() {
        viewModelScope.launch {
            try {
                Log.d("CategoryDiscovery", "🔍 Starting category discovery...")

                // Test common LeetCode category values
                val testCategories = listOf(
                    // Difficulty-related
                    "easy", "Easy", "EASY",
                    "medium", "Medium", "MEDIUM",
                    "hard", "Hard", "HARD",
                    "1", "2", "3", // Numeric difficulty

                    // Algorithm categories
                    "algorithms", "algorithm",

                    // Data structure categories
                    "array", "string", "tree", "graph", "linked-list",
                    "hash-table", "stack", "queue", "heap",

                    // Problem type categories
                    "dynamic-programming", "dp", "greedy", "backtracking",
                    "binary-search", "two-pointers", "sliding-window",

                    // Company categories
                    "amazon", "google", "microsoft", "apple", "facebook",

                    // Topic categories
                    "math", "bit-manipulation", "sorting", "searching",

                    // Other possible values
                    "all", "featured", "top-100", "top-interview-questions",
                    "leetcode-75", "blind-75",

                    // Empty value
                    ""
                )

                var workingCategories = mutableListOf<String>()

                for (category in testCategories) {
                    try {
                        Log.d("CategoryDiscovery", "Testing category: '$category'")
                        val response = repository.fetchProblems(
                            page = 1,
                            limit = 5,
                            searchKeyword = "",
                            categorySlug = category
                        )

                        when (response) {
                            is Resource.Success -> {
                                val count = response.data?.data?.problemsetQuestionListV2?.questions?.size ?: 0
                                if (count > 0) {
                                    Log.d("CategoryDiscovery", "✅ '$category' returned $count questions")
                                    workingCategories.add(category)

                                    // Show sample questions
                                    response.data?.data?.problemsetQuestionListV2?.questions?.take(2)?.forEach { question ->
                                        Log.d("CategoryDiscovery", "    - ${question.title} (${question.difficulty})")
                                    }
                                } else {
                                    Log.d("CategoryDiscovery", "⚪ '$category' returned 0 questions")
                                }
                            }
                            is Resource.Error -> {
                                Log.d("CategoryDiscovery", "❌ '$category' failed: ${response.message}")
                            }
                            else -> {
                                Log.d("CategoryDiscovery", "⚠️ '$category' unexpected response")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("CategoryDiscovery", "💥 '$category' exception: ${e.message}")
                    }

                    delay(200) // Small delay between requests to avoid overwhelming API
                }

                // Summary
                Log.d("CategoryDiscovery", "🎯 SUMMARY: Found ${workingCategories.size} working categories:")
                workingCategories.forEach { category ->
                    Log.d("CategoryDiscovery", "  ✓ '$category'")
                }

                if (workingCategories.isEmpty()) {
                    Log.d("CategoryDiscovery", "⚠️ No working categories found - API might not support filtering")
                }

            } catch (e: Exception) {
                Log.e("CategoryDiscovery", "💥 Error in category discovery: ${e.message}", e)
            }
        }
    }

    // Test search functionality
    fun testSearchFunctionality() {
        viewModelScope.launch {
            try {
                Log.d("SearchTest", "🔍 Testing search functionality...")

                val testSearches = listOf(
                    "array", "string", "tree", "sum", "palindrome",
                    "two", "valid", "merge", "binary", "maximum"
                )

                for (searchTerm in testSearches) {
                    try {
                        Log.d("SearchTest", "Testing search: '$searchTerm'")
                        val response = repository.fetchProblems(
                            page = 1,
                            limit = 3,
                            searchKeyword = searchTerm,
                            categorySlug = ""
                        )

                        when (response) {
                            is Resource.Success -> {
                                val count = response.data?.data?.problemsetQuestionListV2?.questions?.size ?: 0
                                Log.d("SearchTest", "✅ '$searchTerm' returned $count questions")

                                if (count > 0) {
                                    response.data?.data?.problemsetQuestionListV2?.questions?.forEach { question ->
                                        Log.d("SearchTest", "    - ${question.title}")
                                    }
                                }
                            }
                            is Resource.Error -> {
                                Log.e("SearchTest", "❌ '$searchTerm' failed: ${response.message}")
                            }
                            else -> {
                                Log.w("SearchTest", "⚠️ '$searchTerm' unexpected response")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SearchTest", "💥 '$searchTerm' exception: ${e.message}")
                    }

                    delay(200)
                }
            } catch (e: Exception) {
                Log.e("SearchTest", "💥 Error in search test: ${e.message}", e)
            }
        }
    }

    // Quick test to see if API works at all
    fun testBasicAPICall() {
        viewModelScope.launch {
            try {
                Log.d("BasicAPITest", "🔍 Testing basic API call...")

                val response = repository.fetchProblems(
                    page = 1,
                    limit = 10,
                    searchKeyword = "",
                    categorySlug = ""
                )

                when (response) {
                    is Resource.Success -> {
                        val count = response.data?.data?.problemsetQuestionListV2?.questions?.size ?: 0
                        Log.d("BasicAPITest", "✅ Basic call returned $count questions")

                        if (count > 0) {
                            response.data?.data?.problemsetQuestionListV2?.questions?.take(3)?.forEach { question ->
                                Log.d("BasicAPITest", "    - ${question.title} (${question.difficulty})")
                            }
                        }
                    }
                    is Resource.Error -> {
                        Log.e("BasicAPITest", "❌ Basic call failed: ${response.message}")
                    }
                    else -> {
                        Log.w("BasicAPITest", "⚠️ Basic call unexpected response")
                    }
                }
            } catch (e: Exception) {
                Log.e("BasicAPITest", "💥 Basic call exception: ${e.message}", e)
            }
        }
    }
}
