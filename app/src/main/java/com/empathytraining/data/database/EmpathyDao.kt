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
 * Data Access Object (DAO) for the Empathy Training database Simplified
 * version containing only methods that are actually used
 */
@Dao
interface EmpathyDao {

    // ==========================================
    // SCENARIO OPERATIONS
    // ==========================================

    /** Get all active empathy scenarios */
    @Query("SELECT * FROM empathy_scenarios WHERE is_active = 1 ORDER BY scenario_key")
    fun getAllActiveScenarios(): Flow<List<EmpathyScenario>>

    /** Get a random active scenario that user hasn't responded to today */
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

    /** Get a specific scenario by ID */
    @Query("SELECT * FROM empathy_scenarios WHERE scenario_id = :scenarioId")
    suspend fun getScenarioById(scenarioId: Long): EmpathyScenario?

    /** Get scenarios by category */
    @Query("SELECT * FROM empathy_scenarios WHERE category = :category AND is_active = 1 ORDER BY scenario_key")
    fun getScenariosByCategory(category: String): Flow<List<EmpathyScenario>>

    /** Get scenarios by difficulty level */
    @Query("SELECT * FROM empathy_scenarios WHERE difficulty_level = :difficultyLevel AND is_active = 1 ORDER BY usage_count ASC")
    fun getScenariosByDifficulty(difficultyLevel: Int): Flow<List<EmpathyScenario>>

    /** Insert multiple scenarios (used for prepopulating database) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<EmpathyScenario>)

    /** Update scenario usage count */
    @Query("UPDATE empathy_scenarios SET usage_count = usage_count + 1 WHERE scenario_id = :scenarioId")
    suspend fun incrementScenarioUsage(scenarioId: Long)

    // ==========================================
    // USER RESPONSE OPERATIONS
    // ==========================================

    /** Insert a new user response */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserResponse(response: UserResponse): Long

    /** Get all user responses ordered by date (newest first) */
    @Query("SELECT * FROM user_responses ORDER BY date_created DESC")
    fun getAllUserResponses(): Flow<List<UserResponse>>

    /** Get user responses for a specific date */
    @Query("SELECT * FROM user_responses WHERE DATE(date_created) = DATE(:date) ORDER BY date_created DESC")
    fun getResponsesForDate(date: String): Flow<List<UserResponse>>

    /** Get user responses for today */
    @Query("SELECT * FROM user_responses WHERE DATE(date_created) = DATE(:todayDate) ORDER BY date_created DESC")
    fun getTodaysResponses(todayDate: String): Flow<List<UserResponse>>

    /** Check if user has responded to any scenario today */
    @Query("SELECT COUNT(*) > 0 FROM user_responses WHERE DATE(date_created) = DATE(:todayDate)")
    suspend fun hasRespondedToday(todayDate: String): Boolean

    /** Get response count for today */
    @Query("SELECT COUNT(*) FROM user_responses WHERE DATE(date_created) = DATE(:todayDate)")
    suspend fun getTodayResponseCount(todayDate: String): Int

    /** Get user's recent responses (last 30 days) */
    @Query(
        """
        SELECT * FROM user_responses 
        WHERE DATE(date_created) >= DATE('now', '-30 days')
        ORDER BY date_created DESC
    """
    )
    fun getRecentResponses(): Flow<List<UserResponse>>

    /** Update response to mark example as viewed */
    @Query("UPDATE user_responses SET viewed_example = 1 WHERE response_id = :responseId")
    suspend fun markExampleViewed(responseId: Long)

    // ==========================================
    // USER PROGRESS OPERATIONS
    // ==========================================

    /** Get the user's progress record as Flow */
    @Query("SELECT * FROM user_progress WHERE progress_id = 1")
    fun getUserProgress(): Flow<UserProgress?>

    /** Get the user's progress record as a single value */
    @Query("SELECT * FROM user_progress WHERE progress_id = 1")
    suspend fun getUserProgressSingle(): UserProgress?

    /** Insert or update user progress */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProgress(progress: UserProgress)

    /** Initialize user progress with default values */
    @Query(
        """
        INSERT OR IGNORE INTO user_progress 
        (progress_id, current_streak, longest_streak, total_responses, start_date, total_active_days,
         preferred_difficulty, tutorial_completed, average_response_length, examples_viewed,
         average_self_rating, unique_scenarios_completed, achievements_unlocked) 
        VALUES (1, 0, 0, 0, DATE('now'), 0, 1, 0, 0.0, 0, 0.0, 0, '[]')
    """
    )
    suspend fun initializeUserProgress()

    // ==========================================
    // STATISTICS FOR getComprehensiveStats()
    // ==========================================

    @Query("SELECT COUNT(*) FROM user_responses")
    suspend fun getTotalResponseCount(): Int

    @Query("SELECT COUNT(DISTINCT DATE(date_created)) FROM user_responses")
    suspend fun getActiveDaysCount(): Int

    @Query("SELECT COUNT(DISTINCT scenario_id) FROM user_responses")
    suspend fun getUniqueScenarioCount(): Int

    @Query("SELECT AVG(response_length) FROM user_responses")
    suspend fun getAverageResponseLength(): Double

    @Query("SELECT AVG(CAST(self_rating AS REAL)) FROM user_responses WHERE self_rating IS NOT NULL")
    suspend fun getAverageSelfRating(): Double

    @Query("SELECT COUNT(*) FROM user_responses WHERE viewed_example = 1")
    suspend fun getExamplesViewedCount(): Int

    @Query("SELECT COUNT(*) FROM empathy_scenarios WHERE is_active = 1")
    suspend fun getActiveScenarioCount(): Int

    @Query("SELECT COUNT(*) FROM user_responses WHERE self_rating IS NOT NULL")
    suspend fun getRatedResponseCount(): Int

    // ==========================================
    // TRANSACTION FOR DAILY CHALLENGE
    // ==========================================

    /**
     * Complete a daily challenge - transaction that:
     * 1. Inserts the user response
     * 2. Updates scenario usage count
     * 3. Updates user progress statistics
     */
    @Transaction
    suspend fun completeDailyChallenge(
        response: UserResponse,
        currentProgress: UserProgress,
        newProgress: UserProgress,
    ) {
        insertUserResponse(response)
        incrementScenarioUsage(response.scenarioId)
        insertOrUpdateUserProgress(newProgress)
    }

    // ==========================================
    // UTILITY METHODS FOR REPOSITORY
    // ==========================================

    /** Reset all user data (for testing purposes) */
    @Transaction
    suspend fun resetAllUserData() {
        deleteAllUserResponses()
        resetUserProgress()
        resetScenarioUsageCounts()
    }

    @Query("DELETE FROM user_responses")
    suspend fun deleteAllUserResponses()

    @Query(
        """
        UPDATE user_progress SET 
        current_streak = 0, longest_streak = 0, total_responses = 0,
        last_activity_date = NULL, total_active_days = 0,
        average_response_length = 0.0, examples_viewed = 0,
        average_self_rating = 0.0, unique_scenarios_completed = 0,
        achievements_unlocked = '[]', preferred_difficulty = 1,
        tutorial_completed = 0
        WHERE progress_id = 1
    """
    )
    suspend fun resetUserProgress()

    @Query("UPDATE empathy_scenarios SET usage_count = 0")
    suspend fun resetScenarioUsageCounts()
}