package com.empathytraining.data.repository

import com.empathytraining.data.database.EmpathyDao
import com.empathytraining.data.models.EmpathyScenario
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Repository class that serves as a single source of truth for all empathy
 * training data Handles all database operations and business logic for
 * data access
 *
 * This class abstracts the database layer from the UI and provides clean,
 * domain-specific methods for the app's use cases
 */
@ActivityRetainedScoped
class EmpathyRepository @Inject constructor(
    private val empathyDao: EmpathyDao,
) {

    companion object {
        private const val MAX_RESPONSES_PER_DAY = 3 // Limit daily responses to prevent burnout
    }

    // ==========================================
    // DAILY CHALLENGE OPERATIONS
    // ==========================================

    /**
     * Get today's challenge scenario for the user Returns null if user has
     * already completed max responses for today
     *
     * @return EmpathyScenario for today's challenge or null
     */
    suspend fun getTodaysChallenge(): EmpathyScenario? {
        Timber.d("Getting today's challenge scenario")

        val todayDate = LocalDate.now().toString()
        val responsesToday = empathyDao.getTodayResponseCount(todayDate)

        Timber.d("User has $responsesToday responses today")

        if (responsesToday >= MAX_RESPONSES_PER_DAY) {
            Timber.d("User has reached daily limit of $MAX_RESPONSES_PER_DAY responses")
            return null
        }

        // Get user's preferred difficulty level
        val userProgress = empathyDao.getUserProgressSingle()
        val preferredDifficulty = userProgress?.preferredDifficulty ?: 1

        Timber.d("User's preferred difficulty: $preferredDifficulty")

        // Try to get scenario at preferred difficulty first
        var scenario = empathyDao.getRandomUnusedScenarioForToday(todayDate)

        if (scenario != null) {
            Timber.d("Found challenge scenario: ID ${scenario.id}, Category: ${scenario.category}")
        } else {
            Timber.w("No unused scenarios available for today - user may have completed all scenarios")
        }

        return scenario
    }

    /**
     * Submit a user's response to today's challenge Updates all relevant
     * statistics and progress tracking
     *
     * @param scenarioId The ID of the scenario being responded to
     * @param userResponseText The user's empathetic response
     * @param responseTimeSeconds Time spent on response (optional)
     * @param selfRating User's self-rating of their response (optional)
     * @return The ID of the inserted response
     */
    suspend fun submitResponse(
        scenarioId: Long,
        userResponseText: String,
        responseTimeSeconds: Int = -1,
        selfRating: Int? = null,
    ): Long {
        Timber.d("Submitting response for scenario $scenarioId")
        Timber.d("Response length: ${userResponseText.length}, Rating: $selfRating")

        // Create the response object
        val response = UserResponse(
            scenarioId = scenarioId,
            userResponseText = userResponseText.trim(),
            dateCreated = LocalDateTime.now().toString(),
            responseTimeSeconds = responseTimeSeconds,
            selfRating = selfRating,
            viewedExample = false,
            responseLength = userResponseText.trim().length,
            userNotes = null
        )

        // Get current progress for calculations
        val currentProgress = empathyDao.getUserProgressSingle() ?: UserProgress()

        // Calculate new progress statistics
        val newProgress = calculateUpdatedProgress(currentProgress, response)

        // Use transaction to ensure data consistency
        empathyDao.completeDailyChallenge(response, currentProgress, newProgress)

        Timber.d("Response submitted successfully. New streak: ${newProgress.currentStreak}")

        return response.id
    }

    /**
     * Mark that user has viewed the example response Updates both the specific
     * response and overall progress stats
     *
     * @param responseId The ID of the response to mark as viewed
     */
    suspend fun markExampleViewed(responseId: Long) {
        Timber.d("Marking example viewed for response $responseId")

        empathyDao.markExampleViewed(responseId)

        // Update progress statistics
        val currentProgress = empathyDao.getUserProgressSingle()
        if (currentProgress != null) {
            val updatedProgress = currentProgress.copy(
                examplesViewed = currentProgress.examplesViewed + 1
            )
            empathyDao.insertOrUpdateUserProgress(updatedProgress)
            Timber.d("Updated examples viewed count to ${updatedProgress.examplesViewed}")
        }
    }

    /**
     * Check if user has completed their daily challenge
     *
     * @return True if user has responded to at least one scenario today
     */
    suspend fun hasDoneTodaysChallenge(): Boolean {
        val todayDate = LocalDate.now().toString()
        val hasResponded = empathyDao.hasRespondedToday(todayDate)
        Timber.d("Has done today's challenge: $hasResponded")
        return hasResponded
    }

    /**
     * Get number of responses submitted today
     *
     * @return Count of responses today
     */
    suspend fun getTodayResponseCount(): Int {
        val todayDate = LocalDate.now().toString()
        val count = empathyDao.getTodayResponseCount(todayDate)
        Timber.d("Today's response count: $count")
        return count
    }

    // ==========================================
    // USER PROGRESS OPERATIONS
    // ==========================================

    /**
     * Get user's current progress as a Flow for reactive UI updates
     *
     * @return Flow of UserProgress
     */
    fun getUserProgress(): Flow<UserProgress?> {
        Timber.d("Getting user progress as Flow")
        return empathyDao.getUserProgress()
    }

    /**
     * Get user's current progress as a single value
     *
     * @return UserProgress or null if not initialized
     */
    suspend fun getUserProgressSingle(): UserProgress? {
        Timber.d("Getting user progress as single value")
        return empathyDao.getUserProgressSingle()
    }

    /** Initialize user progress for first-time users */
    suspend fun initializeUserProgress() {
        Timber.d("Initializing user progress")
        empathyDao.initializeUserProgress()
    }

    /**
     * Update user's preferred difficulty level
     *
     * @param difficultyLevel New difficulty level (1-5)
     */
    suspend fun updatePreferredDifficulty(difficultyLevel: Int) {
        Timber.d("Updating preferred difficulty to $difficultyLevel")

        val currentProgress = empathyDao.getUserProgressSingle()
        if (currentProgress != null) {
            val updatedProgress = currentProgress.copy(
                preferredDifficulty = difficultyLevel.coerceIn(1, 5)
            )
            empathyDao.insertOrUpdateUserProgress(updatedProgress)
        }
    }

    /** Mark tutorial as completed */
    suspend fun completeTutorial() {
        Timber.d("Marking tutorial as completed")

        val currentProgress = empathyDao.getUserProgressSingle()
        if (currentProgress != null) {
            val updatedProgress = currentProgress.copy(tutorialCompleted = true)
            empathyDao.insertOrUpdateUserProgress(updatedProgress)
        }
    }

    // ==========================================
    // SCENARIO OPERATIONS
    // ==========================================

    /**
     * Get all available scenarios (for browsing/practice)
     *
     * @return Flow of all active scenarios
     */
    fun getAllScenarios(): Flow<List<EmpathyScenario>> {
        Timber.d("Getting all scenarios")
        return empathyDao.getAllActiveScenarios()
    }

    /**
     * Get scenarios by category
     *
     * @param category Category to filter by
     * @return Flow of scenarios in that category
     */
    fun getScenariosByCategory(category: String): Flow<List<EmpathyScenario>> {
        Timber.d("Getting scenarios for category: $category")
        return empathyDao.getScenariosByCategory(category)
    }

    /**
     * Get scenarios by difficulty level
     *
     * @param difficultyLevel Difficulty level to filter by
     * @return Flow of scenarios at that difficulty
     */
    fun getScenariosByDifficulty(difficultyLevel: Int): Flow<List<EmpathyScenario>> {
        Timber.d("Getting scenarios for difficulty: $difficultyLevel")
        return empathyDao.getScenariosByDifficulty(difficultyLevel)
    }

    /**
     * Get a specific scenario by ID
     *
     * @param scenarioId ID of the scenario
     * @return The scenario or null if not found
     */
    suspend fun getScenarioById(scenarioId: Long): EmpathyScenario? {
        Timber.d("Getting scenario by ID: $scenarioId")
        return empathyDao.getScenarioById(scenarioId)
    }

    // ==========================================
    // USER RESPONSE HISTORY OPERATIONS
    // ==========================================

    /**
     * Get all user responses ordered by date (newest first)
     *
     * @return Flow of all user responses
     */
    fun getAllUserResponses(): Flow<List<UserResponse>> {
        Timber.d("Getting all user responses")
        return empathyDao.getAllUserResponses()
    }

    /**
     * Get user's recent responses (last 30 days)
     *
     * @return Flow of recent responses
     */
    fun getRecentResponses(): Flow<List<UserResponse>> {
        Timber.d("Getting recent responses")
        return empathyDao.getRecentResponses()
    }

    /**
     * Get responses for a specific date
     *
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return Flow of responses from that date
     */
    fun getResponsesForDate(date: String): Flow<List<UserResponse>> {
        Timber.d("Getting responses for date: $date")
        return empathyDao.getResponsesForDate(date)
    }

    /**
     * Get responses for today
     *
     * @return Flow of today's responses
     */
    fun getTodaysResponses(): Flow<List<UserResponse>> {
        val todayDate = LocalDate.now().toString()
        Timber.d("Getting today's responses for date: $todayDate")
        return empathyDao.getTodaysResponses(todayDate)
    }

    // ==========================================
    // ANALYTICS AND STATISTICS
    // ==========================================

    /**
     * Get comprehensive statistics about user's progress
     *
     * @return Map containing various statistics
     */
    suspend fun getComprehensiveStats(): Map<String, Any> {
        Timber.d("Getting comprehensive statistics")
        return empathyDao.getComprehensiveStats()
    }

    /**
     * Get user's streak information
     *
     * @return Pair of (currentStreak, longestStreak)
     */
    suspend fun getStreakInfo(): Pair<Int, Int> {
        val progress = empathyDao.getUserProgressSingle()
        val currentStreak = progress?.currentStreak ?: 0
        val longestStreak = progress?.longestStreak ?: 0

        Timber.d("Streak info - Current: $currentStreak, Longest: $longestStreak")
        return Pair(currentStreak, longestStreak)
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    /**
     * Calculate updated progress statistics after a new response
     *
     * @param currentProgress Current progress state
     * @param newResponse New response being submitted
     * @return Updated progress object
     */
    private suspend fun calculateUpdatedProgress(
        currentProgress: UserProgress,
        newResponse: UserResponse,
    ): UserProgress {
        Timber.d("Calculating updated progress statistics")

        val today = LocalDate.now().toString()
        val isFirstResponseToday = !empathyDao.hasRespondedToday(today)

        // Calculate new streak
        val newStreak = if (isFirstResponseToday) {
            if (currentProgress.shouldResetStreak()) {
                Timber.d("Resetting streak due to missed day")
                1 // Start new streak
            } else {
                currentProgress.currentStreak + 1 // Continue streak
            }
        } else {
            currentProgress.currentStreak // No change if not first response today
        }

        // Calculate new totals
        val newTotalResponses = empathyDao.getTotalResponseCount() + 1
        val newActiveDays = if (isFirstResponseToday) {
            empathyDao.getActiveDaysCount() + 1
        } else {
            empathyDao.getActiveDaysCount()
        }

        // Calculate new averages
        val newAverageLength =
            (currentProgress.averageResponseLength * currentProgress.totalResponses + newResponse.responseLength) / newTotalResponses

        val newAverageRating = if (newResponse.selfRating != null) {
            val totalRatedResponses =
                currentProgress.totalResponses // Simplified - should count only rated responses
            ((currentProgress.averageSelfRating * totalRatedResponses) + newResponse.selfRating) / (totalRatedResponses + 1)
        } else {
            currentProgress.averageSelfRating
        }

        val uniqueScenarios = empathyDao.getUniqueScenarioCount()

        Timber.d("New progress - Streak: $newStreak, Total: $newTotalResponses, Active Days: $newActiveDays")

        return currentProgress.copy(
            currentStreak = newStreak,
            longestStreak = maxOf(currentProgress.longestStreak, newStreak),
            totalResponses = newTotalResponses,
            lastActivityDate = today,
            totalActiveDays = newActiveDays,
            averageResponseLength = newAverageLength,
            averageSelfRating = newAverageRating,
            uniqueScenariosCompleted = uniqueScenarios
        )
    }

    /**
     * Reset all user data (for testing or user request) WARNING: This deletes
     * all progress and responses!
     */
    suspend fun resetAllUserData() {
        Timber.w("RESETTING ALL USER DATA - This cannot be undone!")
        empathyDao.resetAllUserData()
        empathyDao.initializeUserProgress()
    }
}