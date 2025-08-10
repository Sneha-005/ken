package com.devrachit.ken.data.remote.services

import com.devrachit.ken.domain.models.Question
import com.devrachit.ken.domain.models.QuestionListResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LeetcodeApiService {

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchUser(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchUserQuestionCount(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchUserHeatMap(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchCurrentTime(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetUserProfileCalender(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchRecentSubmissionList(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchContestRankingHistogram(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchUserContestRanking(@Body requestBody: RequestBody): ResponseBody

    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    @POST("graphql")
    suspend fun fetchUserBadges(@Body requestBody: RequestBody): ResponseBody

    @POST("graphql")
    @Headers("Content-Type: application/json", "Referer: https://leetcode.com/")
    suspend fun fetchProblems(@Body requestBody: RequestBody): Response<ResponseBody>

}
