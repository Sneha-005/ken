package com.devrachit.ken.presentation.screens.dashboard.questions

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.devrachit.ken.domain.models.Question
import com.devrachit.ken.domain.repository.remote.LeetcodeRemoteRepository
import com.devrachit.ken.utility.NetworkUtility.Resource

class LeetCodeQuestionsPagingSource(
    private val repository: LeetcodeRemoteRepository,
    private val difficulty: String? = null,
    private val searchQuery: String? = null
) : PagingSource<Int, Question>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Question> {
        return try {
            val page = params.key ?: 1

            val (searchKeyword, categorySlug) = when {
                !searchQuery.isNullOrBlank() -> {
                    Log.d("PagingSource", "Searching with query: '$searchQuery'")
                    Pair(searchQuery, "")
                }
                !difficulty.isNullOrBlank() -> {
                    Log.d("PagingSource", "Filtering by difficulty: '$difficulty'")
                    Pair("", difficulty)
                }
                else -> {
                    Log.d("PagingSource", "No search or filter - loading all")
                    Pair("", "")
                }
            }

            val response = repository.fetchProblems(
                page = page,
                limit = params.loadSize,
                searchKeyword = searchKeyword,
                categorySlug = categorySlug
            )

            when (response) {
                is Resource.Success -> {
                    val allQuestions = response.data?.data?.problemsetQuestionListV2?.questions ?: emptyList()

                    // If we have both search and filter, do client-side filtering
                    val filteredQuestions = if (!searchQuery.isNullOrBlank() && !difficulty.isNullOrBlank()) {
                        Log.d("PagingSource", "Applying client-side difficulty filter: $difficulty")
                        allQuestions.filter { question ->
                            question.difficulty?.equals(difficulty, ignoreCase = true) == true
                        }
                    } else {
                        allQuestions
                    }

                    Log.d("PagingSource", "Returning ${filteredQuestions.size} questions (from ${allQuestions.size} total)")

                    LoadResult.Page(
                        data = filteredQuestions,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (filteredQuestions.isEmpty()) null else page + 1
                    )
                }
                is Resource.Error -> {
                    Log.e("PagingSource", "API Error: ${response.message}")
                    LoadResult.Error(Exception(response.message ?: "Unknown error"))
                }
                is Resource.Loading -> {
                    LoadResult.Error(Exception("Loading state in paging source"))
                }
            }
        } catch (exception: Exception) {
            Log.e("PagingSource", "Exception: ${exception.message}", exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Question>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}