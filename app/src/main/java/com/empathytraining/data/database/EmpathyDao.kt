package com.empathytraining.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.empathytraining.data.models.EmpathyScenario
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Empathy Training database Provides
 * methods for all database operations including CRUD operations
 * and complex queries for analytics and user progress tracking
 */
@Dao
interface EmpathyDao {

    // ==========================================
    // SCENARIO OPERATIONS
    // ==========================================

    /**
     * Get all active empathy scenarios Used for selecting random scenarios for
     * daily challenges
     *
     * @return Flow of all active scenarios
     */
    @Query("SELECT * FROM empathy_scenarios WHERE is_active = 1 ORDER BY scenario_text")
    fun getAllActiveScenarios(): Flow<List<EmpathyScenario>>

    /**
     * Get a random active scenario that user hasn't responded to today
     * Prioritizes scenarios with lower usage count for variety
     *
     * @param todayDate Today's date in ISO format (YYYY-MM-DD)
     * @return Single scenario or null if all scenarios were used today
     */
    @Query(
        """
        SELECT s.* FROM empathy_scenarios s 
        WHERE s.is_active = 1 
        AND s.scenario_id NOT IN (
            SELECT ur.scenario_id FROM user_responses ur 
            WHERE DATE(ur.date_created) = DATE(:todayDate)
        )
        ORDER BY s.usage_count ASC, RANDOM() 
        LIMIT 1
    """
    )
    suspend fun getRandomUnusedScenarioForToday(todayDate: String): EmpathyScenario?

    /**
     * Get a specific scenario by ID
     *
     * @param scenarioId The ID of the scenario to retrieve
     * @return The scenario or null if not found
     */
    @Query("SELECT * FROM empathy_scenarios WHERE scenario_id = :scenarioId")
    suspend fun getScenarioById(scenarioId: Long): EmpathyScenario?

    /**
     * Get scenarios by category
     *
     * @param category The category to filter by
     * @return Flow of scenarios in the specified category
     */
    @Query("SELECT * FROM empathy_scenarios WHERE category = :category AND is_active = 1 ORDER BY scenario_text")
    fun getScenariosByCategory(category: String): Flow<List<EmpathyScenario>>

    /**
     * Get scenarios by difficulty level
     *
     * @param difficultyLevel The difficulty level to filter by (1-5)
     * @return Flow of scenarios at the specified difficulty
     */
    @Query("SELECT * FROM empathy_scenarios WHERE difficulty_level = :difficultyLevel AND is_active = 1 ORDER BY usage_count ASC")
    fun getScenariosByDifficulty(difficultyLevel: Int): Flow<List<EmpathyScenario>>

    /**
     * Insert multiple scenarios (used for prepopulating database)
     *
     * @param scenarios List of scenarios to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<EmpathyScenario>)

    /**
     * Update scenario usage count Called when a scenario is used for a
     * challenge
     *
     * @param scenarioId ID of the scenario to update
     */
    @Query("UPDATE empathy_scenarios SET usage_count = usage_count + 1 WHERE scenario_id = :scenarioId")
    suspend fun incrementScenarioUsage(scenarioId: Long)

    /**
     * Get total number of active scenarios
     *
     * @return Count of active scenarios
     */
    @Query("SELECT COUNT(*) FROM empathy_scenarios WHERE is_active = 1")
    suspend fun getActiveScenarioCount(): Int

    // ==========================================
    // USER RESPONSE OPERATIONS
    // ==========================================

    /**
     * Insert a new user response
     *
     * @param response The response to insert
     * @return The ID of the inserted response
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserResponse(response: UserResponse): Long

    /**
     * Get all user responses ordered by date (newest first)
     *
     * @return Flow of all user responses
     */
    @Query("SELECT * FROM user_responses ORDER BY date_created DESC")
    fun getAllUserResponses(): Flow<List<UserResponse>>

    /**
     * Get user responses for a specific date
     *
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return Flow of responses from that date
     */
    @Query("SELECT * FROM user_responses WHERE DATE(date_created) = DATE(:date) ORDER BY date_created DESC")
    fun getResponsesForDate(date: String): Flow<List<UserResponse>>

    /**
     * Get user responses for today
     *
     * @param todayDate Today's date in ISO format
     * @return Flow of today's responses
     */
    @Query("SELECT * FROM user_responses WHERE DATE(date_created) = DATE(:todayDate) ORDER BY date_created DESC")
    fun getTodaysResponses(todayDate: String): Flow<List<UserResponse>>

    /**
     * Check if user has responded to any scenario today
     *
     * @param todayDate Today's date in ISO format
     * @return True if user has responded today
     */
    @Query("SELECT COUNT(*) > 0 FROM user_responses WHERE DATE(date_created) = DATE(:todayDate)")
    suspend fun hasRespondedToday(todayDate: String): Boolean

    /**
     * Get response count for today
     *
     * @param todayDate Today's date in ISO format
     * @return Number of responses today
     */
    @Query("SELECT COUNT(*) FROM user_responses WHERE DATE(date_created) = DATE(:todayDate)")
    suspend fun getTodayResponseCount(todayDate: String): Int

    /**
     * Get total response count across all time
     *
     * @return Total number of responses ever made
     */
    @Query("SELECT COUNT(*) FROM user_responses")
    suspend fun getTotalResponseCount(): Int

    /**
     * Get responses for a specific scenario
     *
     * @param scenarioId The scenario ID to get responses for
     * @return Flow of responses for that scenario
     */
    @Query("SELECT * FROM user_responses WHERE scenario_id = :scenarioId ORDER BY date_created DESC")
    fun getResponsesForScenario(scenarioId: Long): Flow<List<UserResponse>>

    /**
     * Get user's recent responses (last 30 days)
     *
     * @return Flow of recent responses
     */
    @Query(
        """
        SELECT * FROM user_responses 
        WHERE DATE(date_created) >= DATE('now', '-30 days')
        ORDER BY date_created DESC
    """
    )
    fun getRecentResponses(): Flow<List<UserResponse>>

    /**
     * Get count of unique scenarios user has responded to
     *
     * @return Number of different scenarios completed
     */
    @Query("SELECT COUNT(DISTINCT scenario_id) FROM user_responses")
    suspend fun getUniqueScenarioCount(): Int

    /**
     * Get average response length
     *
     * @return Average length of user responses
     */
    @Query("SELECT AVG(response_length) FROM user_responses")
    suspend fun getAverageResponseLength(): Double

    /**
     * Get average self-rating (excluding null ratings)
     *
     * @return Average self-rating or 0.0 if no ratings
     */
    @Query("SELECT AVG(CAST(self_rating AS REAL)) FROM user_responses WHERE self_rating IS NOT NULL")
    suspend fun getAverageSelfRating(): Double

    /**
     * Get count of responses where user viewed the example
     *
     * @return Number of times user viewed examples
     */
    @Query("SELECT COUNT(*) FROM user_responses WHERE viewed_example = 1")
    suspend fun getExamplesViewedCount(): Int

    /**
     * Update response to mark example as viewed
     *
     * @param responseId ID of the response to update
     */
    @Query("UPDATE user_responses SET viewed_example = 1 WHERE response_id = :responseId")
    suspend fun markExampleViewed(responseId: Long)

    /**
     * Get count of active days (days with at least one response)
     *
     * @return Number of unique days user was active
     */
    @Query("SELECT COUNT(DISTINCT DATE(date_created)) FROM user_responses")
    suspend fun getActiveDaysCount(): Int

    /**
     * Delete old responses (older than specified days) Can be used for data
     * cleanup if needed
     *
     * @param daysToKeep Number of days of data to retain
     */
    @Query("DELETE FROM user_responses WHERE DATE(date_created) < DATE('now', '-' || :daysToKeep || ' days')")
    suspend fun deleteOldResponses(daysToKeep: Int)

    // ==========================================
    // USER PROGRESS OPERATIONS
    // ==========================================

    /**
     * Get the user's progress record There should only be one record with ID =
     * 1
     *
     * @return Flow of user progress (single record)
     */
    @Query("SELECT * FROM user_progress WHERE progress_id = 1")
    fun getUserProgress(): Flow<UserProgress?>

    /**
     * Get the user's progress record as a single value (not Flow)
     *
     * @return UserProgress or null if not found
     */
    @Query("SELECT * FROM user_progress WHERE progress_id = 1")
    suspend fun getUserProgressSingle(): UserProgress?

    /**
     * Insert or update user progress
     *
     * @param progress The progress record to save
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProgress(progress: UserProgress)

    /**
     * Initialize user progress with default values Called when user first uses
     * the app
     */
    @Query(
        """
        INSERT OR IGNORE INTO user_progress 
        (progress_id, current_streak, longest_streak, total_responses, start_date, total_active_days) 
        VALUES (1, 0, 0, 0, DATE('now'), 0)
    """
    )
    suspend fun initializeUserProgress()

    // ==========================================
    // COMBINED OPERATIONS & TRANSACTIONS
    // ==========================================

    /**
     * Complete a daily challenge - complex transaction that:
     * 1. Inserts the user response
     * 2. Updates scenario usage count
     * 3. Updates user progress statistics
     * 4. Handles streak calculations
     *
     * @param response The user's response
     * @param currentProgress Current progress state
     * @param newProgress Updated progress state
     */
    @Transaction
    suspend fun completeDailyChallenge(
        response: UserResponse,
        currentProgress: UserProgress,
        newProgress: UserProgress,
    ) {
        // Insert the response
        insertUserResponse(response)

        // Update scenario usage count
        incrementScenarioUsage(response.scenarioId)

        // Update user progress
        insertOrUpdateUserProgress(newProgress)
    }

    /**
     * Get comprehensive stats for analytics Returns a map with various
     * statistics
     */
    @Transaction
    suspend fun getComprehensiveStats(): Map<String, Any> {
        return mapOf(
            "totalResponses" to getTotalResponseCount(),
            "activeDays" to getActiveDaysCount(),
            "uniqueScenarios" to getUniqueScenarioCount(),
            "averageResponseLength" to getAverageResponseLength(),
            "averageSelfRating" to getAverageSelfRating(),
            "examplesViewed" to getExamplesViewedCount(),
            "activeScenarios" to getActiveScenarioCount()
        )
    }

    /**
     * Reset all user data (for testing or user request) WARNING: This deletes
     * all user progress and responses!
     */
    @Transaction
    suspend fun resetAllUserData() {
        // Delete all responses
        deleteAllUserResponses()

        // Reset progress
        resetUserProgress()

        // Reset scenario usage counts
        resetScenarioUsageCounts()
    }

    /** Delete all user responses */
    @Query("DELETE FROM user_responses")
    suspend fun deleteAllUserResponses()

    /** Reset user progress to initial state */
    @Query(
        """
        UPDATE user_progress SET 
        current_streak = 0,
        longest_streak = 0,
        total_responses = 0,
        last_activity_date = NULL,
        total_active_days = 0,
        average_response_length = 0.0,
        examples_viewed = 0,
        average_self_rating = 0.0,
        unique_scenarios_completed = 0,
        achievements_unlocked = '[]'
        WHERE progress_id = 1
    """
    )
    suspend fun resetUserProgress()

    /** Reset all scenario usage counts back to 0 */
    @Query("UPDATE empathy_scenarios SET usage_count = 0")
    suspend fun resetScenarioUsageCounts()
}