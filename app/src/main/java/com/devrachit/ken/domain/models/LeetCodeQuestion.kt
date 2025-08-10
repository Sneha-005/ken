package com.devrachit.ken.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionListResponse(
    val data: Data?
)

@Serializable
data class Data(
    val problemsetQuestionListV2: ProblemsetQuestionListV2?
)

@Serializable
data class ProblemsetQuestionListV2(
    val questions: List<Question>,
    val totalLength: Int,
    val hasMore: Boolean
)

@Serializable
data class Question(
    val id: Int,
    val titleSlug: String,
    val title: String,
    val difficulty: String,
    val paidOnly: Boolean,
    val acRate: Double
)
data class SearchFilterParams(
    val query: String,
    val difficulty: String?
)