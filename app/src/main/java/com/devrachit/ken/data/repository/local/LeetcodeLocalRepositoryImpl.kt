package com.devrachit.ken.data.repository.local

import com.devrachit.ken.data.local.dao.LeetCodeUserContestRatingDao
import com.devrachit.ken.data.local.dao.LeetCodeUserDao
import com.devrachit.ken.data.local.dao.LeetCodeUserProfileCalenderDao
import com.devrachit.ken.data.local.dao.LeetCodeUserRecentSubmissionDao
import com.devrachit.ken.data.local.dao.LeetCodeUserBadgesDao
import com.devrachit.ken.data.local.entity.LeetCodeUserEntity
import com.devrachit.ken.data.local.entity.UserContestRankingEntity
import com.devrachit.ken.data.local.entity.UserProfileCalenderEntity
import com.devrachit.ken.data.local.entity.UserQuestionStatusEntity
import com.devrachit.ken.data.local.entity.UserRecentSubmissionEntity
import com.devrachit.ken.data.local.entity.UserBadgesEntity
import com.devrachit.ken.domain.models.LeetCodeUserInfo
import com.devrachit.ken.domain.models.UserQuestionStatusData
import com.devrachit.ken.domain.repository.local.LeetcodeLocalRepository
import com.devrachit.ken.utility.NetworkUtility.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeetcodeLocalRepositoryImpl @Inject constructor(
    private val userDao: LeetCodeUserDao,
    private val userProfileCalenderDao: LeetCodeUserProfileCalenderDao,
    private val userRecentSubmissionDao: LeetCodeUserRecentSubmissionDao,
    private val userContestRatingDao: LeetCodeUserContestRatingDao,
    private val userBadgesDao: LeetCodeUserBadgesDao,
) : LeetcodeLocalRepository {

    companion object {
        private const val USER_NOT_FOUND_ERROR = "User not found in cache"
        private const val DATA_NOT_FOUND_ERROR = "Data not found in cache"
    }

    // User Info Operations
    override fun getUserInfoFlow(username: String): Flow<Resource<LeetCodeUserInfo>> {
        return userDao.getUserByUsernameFlow(username)
            .map { cachedUser ->
                cachedUser?.let {
                    Resource.Success(it.toDomainModel())
                } ?: Resource.Error(USER_NOT_FOUND_ERROR)
            }
            .catch { exception ->
                emit(Resource.Error("Database error: ${exception.message}"))
            }
    }

    override suspend fun getUserInfo(username: String): Resource<LeetCodeUserInfo> {
        return try {
            val cachedUser = userDao.getUserByUsername(username)
            cachedUser?.let {
                Resource.Success(it.toDomainModel())
            } ?: Resource.Error(USER_NOT_FOUND_ERROR)
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun saveUserInfo(userInfo: LeetCodeUserInfo) {
        try {
            userInfo.username?.let { username ->
                userDao.insertUser(
                    LeetCodeUserEntity.fromDomainModel(userInfo, System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            // Log error or handle appropriately
            throw e
        }
    }

    override suspend fun getLastFetchTime(username: String): Long? {
        return try {
            userDao.getUserByUsername(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }

    // Cache Management
    override suspend fun clearCache() {
        try {
            userDao.deleteAllUsers()
            deleteAllUserQuestionStatus()
            deleteAllUserProfileCalender()
            deleteAllRecentSubmissions()
            deleteAllUserContestRankings()
            deleteAllUserBadges()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun clearUserCache(username: String) {
        try {
            deleteUser(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUser(username: String) {
        try {
            userDao.deleteUser(username)
            userDao.deleteUserQuestionStatus(username)
            userProfileCalenderDao.deleteUserCalendar(username)
            userRecentSubmissionDao.deleteUserRecentSubmission(username)
            userContestRatingDao.deleteUserContestRanking(username)
            userBadgesDao.deleteUserBadges(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun cleanExpiredCache(expiryTimeMillis: Long) {
        try {
            val expiredEntries = userDao.getExpiredCacheEntries(expiryTimeMillis)
            expiredEntries.forEach { entry ->
                deleteUser(entry.username)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllUsers(): List<LeetCodeUserEntity> {
        TODO("Not yet implemented")
    }

    // User Question Status Operations
    override suspend fun getLastUserQuestionStatusFetchTime(username: String): Long? {
        return try {
            userDao.getUserQuestionStatus(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserQuestionStatus(username: String): Resource<UserQuestionStatusData> {
        return try {
            val data = userDao.getUserQuestionStatus(username)?.toDomainModel()
            data?.let {
                Resource.Success(it)
            } ?: Resource.Error("User Question Status not found in cache")
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun saveUserQuestionStatus(userQuestionStatus: UserQuestionStatusEntity) {
        try {
            userDao.insertUserQuestionStatus(userQuestionStatus)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllUserQuestionStatus() {
        try {
            userDao.deleteAllUserQuestionStatus()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllUserQuestionStatuses(): List<UserQuestionStatusEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserQuestionStatus(username: String) {
        try {
            userDao.deleteUserQuestionStatus(username)
        } catch (e: Exception) {
            throw e
        }
    }

    // User Profile Calendar Operations
    override suspend fun getUserProfileCalender(username: String): Resource<UserProfileCalenderEntity> {
        return try {
            val data = userProfileCalenderDao.getUserProfileCalender(username)
            data?.let {
                Resource.Success(it)
            } ?: Resource.Error("User Profile Calendar not found in cache")
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun saveUserProfileCalender(username: String, userCalender: UserProfileCalenderEntity) {
        try {
            userProfileCalenderDao.insertUserProfileCalender(userCalender)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllUserProfileCalender() {
        try {
            userProfileCalenderDao.deleteAllUserCalendars()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUserProfileCalender(username: String) {
        try {
            userProfileCalenderDao.deleteUserCalendar(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getLastUserProfileCalenderFetchTime(username: String): Long? {
        return try {
            userProfileCalenderDao.getUserProfileCalender(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllUserCalendars(): List<UserProfileCalenderEntity> {
        TODO("Not yet implemented")
    }

    // Recent Submissions Operations
    override suspend fun saveRecentSubmissions(
        username: String,
        recentSubmissions: UserRecentSubmissionEntity
    ) {
        try {
            userRecentSubmissionDao.insertAll(recentSubmissions)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllRecentSubmissions() {
        try {
            userRecentSubmissionDao.deleteAllUserRecentSubmissions()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteRecentSubmissions(username: String) {
        try {
            userRecentSubmissionDao.deleteUserRecentSubmission(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getRecentSubmissions(username: String): Resource<UserRecentSubmissionEntity> {
        return try {
            val data = userRecentSubmissionDao.getRecentSubmissions(username)
            data?.let {
                Resource.Success(it)
            } ?: Resource.Error(DATA_NOT_FOUND_ERROR)
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun getLastRecentSubmissionsFetchTime(username: String): Long? {
        return try {
            userRecentSubmissionDao.getRecentSubmissions(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }

    // Contest Ranking Operations
    override suspend fun getUserContestRanking(username: String): Resource<UserContestRankingEntity> {
        return try {
            val data = userContestRatingDao.getUserContestRanking(username)
            data?.let {
                Resource.Success(it)
            } ?: Resource.Error("User Contest Ranking not found in cache")
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun saveUserContestRanking(username: String, contestRanking: UserContestRankingEntity) {
        try {
            userContestRatingDao.insertAll(contestRanking)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUserContestRanking(username: String) {
        try {
            userContestRatingDao.deleteUserContestRanking(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllUserContestRankings() {
        try {
            userContestRatingDao.deleteAllUserContestRankings()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getLastUserContestRankingFetchTime(username: String): Long? {
        return try {
            userContestRatingDao.getUserContestRanking(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }

    // User Badges Operations
    override suspend fun getUserBadges(username: String): Resource<UserBadgesEntity> {
        return try {
            val data = userBadgesDao.getUserBadges(username)
            data?.let {
                Resource.Success(it)
            } ?: Resource.Error("User Badges not found in cache")
        } catch (e: Exception) {
            Resource.Error("Database error: ${e.message}")
        }
    }

    override suspend fun saveUserBadges(username: String, userBadges: UserBadgesEntity) {
        try {
            userBadgesDao.insertUserBadges(userBadges)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteUserBadges(username: String) {
        try {
            userBadgesDao.deleteUserBadges(username)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllUserBadges() {
        try {
            userBadgesDao.deleteAllUserBadges()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getLastUserBadgesFetchTime(username: String): Long? {
        return try {
            userBadgesDao.getUserBadges(username)?.lastFetchTime
        } catch (e: Exception) {
            null
        }
    }
}