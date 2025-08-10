package com.devrachit.ken.data.remote.queries

import org.json.JSONArray
import org.json.JSONObject

class GraphqlQuery {


    companion object {

        val userExistsQuery = """
     query userInfo(${"$"}username: String!) {
         matchedUser(username: ${"$"}username) {
             contestBadge {
                 name
                 expired
                 hoverText
                 icon
             }
             username
             githubUrl
             twitterUrl
             linkedinUrl
             profile {
                 ranking
                 userAvatar
                 realName
                 aboutMe
                 school
                 websites
                 countryName
                 company
                 jobTitle
                 skillTags
                 postViewCount
                 postViewCountDiff
                 reputation
                 reputationDiff
                 solutionCount
                 solutionCountDiff
                 categoryDiscussCount
                 categoryDiscussCountDiff
                 certificationLevel
             }
         }
     }
 """.trimIndent()


        fun getUserExistsJsonRequest(username: String): JSONObject {
            return JSONObject().apply {
                put("query", userExistsQuery)
                put("operationName", "userInfo")
                put("variables", JSONObject().put("username", username))
            }
        }


        val userQuestionCountQuery = """
    query userSessionProgress(${"$"}username: String!) {
      allQuestionsCount {
        difficulty
        count
      }
      matchedUser(username: ${"$"}username) {
        submitStats {
          acSubmissionNum {
            difficulty
            count
            submissions
          }
          totalSubmissionNum {
            difficulty
            count
            submissions
          }
        }
      }
    }
    """.trimIndent()

        fun getUserQuestionCountJsonRequest(username: String): JSONObject {
            return JSONObject().apply {
                put("query", userQuestionCountQuery)
                put("operationName", "userSessionProgress")
                put("variables", JSONObject().put("username", username))
            }
        }

        val currentDataQuery = """
        query currentTimestamp {
          currentTimestamp
        }
    """.trimIndent()

        fun getCurrentDataJsonRequest(): JSONObject {
            return JSONObject().apply {
                put("query", currentDataQuery)
                put("operationName", "currentTimestamp")
                put("variables", JSONObject())
            }
        }

        val getUserProfileCalenderQuery = """
    query userProfileCalendar(${"$"}username: String!, ${"$"}year: Int) {
      matchedUser(username: ${"$"}username) {
        userCalendar(year: ${"$"}year) {
          activeYears
          streak
          totalActiveDays
          dccBadges {
            timestamp
            badge {
              name
              icon
            }
          }
          submissionCalendar
        }
      }
    }
    """.trimIndent()

        fun getUserProfileCalendarJsonRequest(username: String, year: Int? = null): JSONObject {
            val variables = JSONObject().put("username", username)
            if (year != null) {
                variables.put("year", year)
            }
            return JSONObject().apply {
                put("query", getUserProfileCalenderQuery)
                put("operationName", "userProfileCalendar")
                put("variables", variables)
            }
        }

        //        val getUserContestRankingQuery = """
//    query userContestRankingInfo(${"$"}username: String!) {
//      userContestRanking(username: ${"$"}username) {
//        attendedContestsCount
//        rating
//        globalRanking
//        totalParticipants
//        topPercentage
//        badge {
//          name
//        }
//      }
//      userContestRankingHistory(username: ${"$"}username) {
//        attended
//        trendDirection
//        problemsSolved
//        totalProblems
//        finishTimeInSeconds
//        rating
//        ranking
//        contest {
//          title
//          startTime
//        }
//      }
//    }
//    """.trimIndent()
        val getUserContestRankingQuery = """
    query userContestRankingInfo(${"$"}username: String!) {
      userContestRanking(username: ${"$"}username) {
        attendedContestsCount
        rating
        globalRanking
        totalParticipants
        topPercentage
        badge {
          name
        }
      }
    }
    """.trimIndent()

        fun getUserContestRankingJsonRequest(username: String): JSONObject {
            return JSONObject().apply {
                put("query", getUserContestRankingQuery)
                put("operationName", "userContestRankingInfo")
                put("variables", JSONObject().put("username", username))
            }
        }

        val getRecentAcSubmissionsQuery = """
    query recentAcSubmissions(${"$"}username: String!, ${"$"}limit: Int!) {
      recentAcSubmissionList(username: ${"$"}username, limit: ${"$"}limit) {
        id
        title
        titleSlug
        timestamp
      }
    }
    """.trimIndent()

        fun getRecentAcSubmissionsJsonRequest(username: String, limit: Int? = 15): JSONObject {
            return JSONObject().apply {
                put("query", getRecentAcSubmissionsQuery)
                put("operationName", "recentAcSubmissions")
                put("variables", JSONObject().put("username", username).put("limit", limit))
            }
        }

        val getContestRatingHistogramQuery = """
    query contestRatingHistogram {
      contestRatingHistogram {
        userCount
        ratingStart
        ratingEnd
        topPercentage
      }
    }
    """.trimIndent()

        fun getContestRatingHistogramJsonRequest(): JSONObject {
            return JSONObject().apply {
                put("query", getContestRatingHistogramQuery)
                put("operationName", "contestRatingHistogram")
                put("variables", JSONObject())
            }
        }


        val getUserBadgesQuery = """
    query userBadges(${"$"}username: String!) {
      matchedUser(username: ${"$"}username) {
        badges {
          id
          name
          shortName
          displayName
          icon
          hoverText
          medal {
            slug
            config {
              iconGif
              iconGifBackground
            }
          }
          creationDate
          category
        }
        upcomingBadges {
          name
          icon
          progress
        }
      }
    }
    """.trimIndent()

        fun getUserBadgesJsonRequest(username: String): JSONObject {
            return JSONObject().apply {
                put("query", getUserBadgesQuery)
                put("operationName", "userBadges")
                put("variables", JSONObject().put("username", username))
            }
        }


        // Fixed version of the problems query
        private const val query = """
    query problemsetQuestionListV2(${"$"}limit: Int, ${"$"}skip: Int, ${"$"}searchKeyword: String, ${"$"}categorySlug: String) {
      problemsetQuestionListV2(
        limit: ${"$"}limit,
        skip: ${"$"}skip,
        searchKeyword: ${"$"}searchKeyword,
        sortBy: { sortField: CUSTOM, sortOrder: ASCENDING },
        categorySlug: ${"$"}categorySlug
      ) {
        questions {
          id
          titleSlug
          title
          difficulty
          paidOnly
          acRate
        }
        totalLength
        hasMore
      }
    }
"""

        fun getProblemsJsonRequest(
            page: Int, // Assuming 0-based paging for Paging3
            limit: Int,
            searchKeyword: String = "", // Default empty string
            categorySlug: String = ""   // Default empty string
        ): JSONObject {
            val skip = page * limit // This should be 0 for first page, not 60!
            val variables = JSONObject().apply {
                put("limit", limit)
                put("skip", skip)
                // Try with null values instead of empty strings
                put("searchKeyword", if (searchKeyword.isEmpty()) null else searchKeyword)
                // Try without categorySlug or use null
                if (categorySlug.isNotEmpty() && categorySlug != "all-code-questions") {
                    put("categorySlug", categorySlug)
                }
                // Don't include categorySlug at all for now to get all questions
            }

            return JSONObject().apply {
                put("query", query)
                put("operationName", "problemsetQuestionListV2")
                put("variables", variables)
            }
        }
    }
}