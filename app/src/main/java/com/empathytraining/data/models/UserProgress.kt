package com.empathytraining.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Data model for tracking user's overall progress in empathy training
 * Maintains statistics about streaks, total responses, and achievements
 *
 * This is a singleton entity - only one record should exist in the
 * database representing the current user's progress
 */
@Entity(tableName = "user_progress")
data class UserProgress(
    /**
     * Primary key - should always be 1 since we only track one user Using
     * fixed ID ensures only one progress record exists
     */
    @PrimaryKey @ColumnInfo(name = "progress_id") val id: Long = 1L,

    /** Current consecutive days streak Resets to 0 when user misses a day */
    @ColumnInfo(name = "current_streak") val currentStreak: Int = 0,

    /** Longest streak ever achieved Personal best record for motivation */
    @ColumnInfo(name = "longest_streak") val longestStreak: Int = 0,

    /**
     * Total number of responses given across all scenarios Lifetime count for
     * overall progress tracking
     */
    @ColumnInfo(name = "total_responses") val totalResponses: Int = 0,

    /**
     * Date of the last activity (response submission) Stored as ISO date
     * string for Room compatibility Used to calculate streaks and detect
     * missed days
     */
    @ColumnInfo(name = "last_activity_date") val lastActivityDate: String? = null,

    /**
     * Date when the user started using the app Stored as ISO date string for
     * calculating total days active
     */
    @ColumnInfo(name = "start_date") val startDate: String = LocalDate.now().toString(),

    /**
     * Total number of unique days when user provided responses Different from
     * streak - counts all active days, not consecutive
     */
    @ColumnInfo(name = "total_active_days") val totalActiveDays: Int = 0,

    /**
     * Average response length across all responses Useful for tracking how
     * detailed user responses are becoming
     */
    @ColumnInfo(name = "average_response_length") val averageResponseLength: Double = 0.0,

    /**
     * Number of times user viewed example responses Indicates learning
     * engagement
     */
    @ColumnInfo(name = "examples_viewed") val examplesViewed: Int = 0,

    /**
     * Average self-rating across all responses that included ratings Shows
     * user's perception of their improvement over time
     */
    @ColumnInfo(name = "average_self_rating") val averageSelfRating: Double = 0.0,

    /**
     * Total number of different scenarios the user has responded to Tracks
     * variety in training
     */
    @ColumnInfo(name = "unique_scenarios_completed") val uniqueScenariosCompleted: Int = 0,

    /**
     * JSON string storing achievement IDs that user has unlocked Format:
     * ["first_response", "week_streak", "month_streak"]
     */
    @ColumnInfo(name = "achievements_unlocked") val achievementsUnlocked: String = "[]",

    /**
     * User's preferred difficulty level (1-5) Can be adjusted based on
     * performance or user preference
     */
    @ColumnInfo(name = "preferred_difficulty") val preferredDifficulty: Int = 1,

    /** Whether user has completed the initial tutorial/onboarding */
    @ColumnInfo(name = "tutorial_completed") val tutorialCompleted: Boolean = false,
) {

    /**
     * Convert last activity date string to LocalDate
     *
     * @return LocalDate of last activity, or null if never active
     */
    fun getLastActivityLocalDate(): LocalDate? {
        return lastActivityDate?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Convert start date string to LocalDate
     *
     * @return LocalDate when user started using the app
     */
    fun getStartLocalDate(): LocalDate {
        return try {
            LocalDate.parse(startDate)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    /**
     * Calculate total days since user started using the app
     *
     * @return Number of days since start date
     */
    fun getTotalDaysSinceStart(): Long {
        return ChronoUnit.DAYS.between(getStartLocalDate(), LocalDate.now())
    }

    /**
     * Calculate activity percentage (active days / total days)
     *
     * @return Percentage of days user was active (0.0 to 1.0)
     */
    fun getActivityPercentage(): Double {
        val totalDays = getTotalDaysSinceStart()
        return if (totalDays > 0) {
            totalActiveDays.toDouble() / totalDays.toDouble()
        } else {
            0.0
        }
    }

    /**
     * Get formatted activity percentage for UI display
     *
     * @return Formatted percentage string like "75%"
     */
    fun getFormattedActivityPercentage(): String {
        return "${(getActivityPercentage() * 100).toInt()}%"
    }

    /**
     * Check if user responded today
     *
     * @return True if last activity was today
     */
    fun hasRespondedToday(): Boolean {
        val lastActivity = getLastActivityLocalDate()
        return lastActivity == LocalDate.now()
    }

    /**
     * Check if user missed yesterday (streak should be broken)
     *
     * @return True if streak should be reset
     */
    fun shouldResetStreak(): Boolean {
        val lastActivity = getLastActivityLocalDate() ?: return true
        val daysBetween = ChronoUnit.DAYS.between(lastActivity, LocalDate.now())
        return daysBetween > 1
    }

    /**
     * Get current streak status description
     *
     * @return Description of current streak for UI display
     */
    fun getStreakStatusDescription(): String {
        return when {
            currentStreak == 0 -> "Start your streak today!"
            currentStreak == 1 -> "Great start! Keep it going."
            currentStreak < 7 -> "Building momentum! $currentStreak days strong."
            currentStreak < 30 -> "Excellent streak! $currentStreak days in a row."
            currentStreak < 100 -> "Amazing dedication! $currentStreak days straight."
            else -> "Incredible! You're a empathy master with $currentStreak days!"
        }
    }

    /**
     * Get motivational message based on progress
     *
     * @return Encouraging message for the user
     */
    fun getMotivationalMessage(): String {
        return when {
            totalResponses == 0 -> "Welcome! Ready to start your empathy journey?"
            totalResponses < 5 -> "Great start! Every response makes you more empathetic."
            totalResponses < 20 -> "You're building great habits! Keep practicing."
            totalResponses < 50 -> "Wonderful progress! Your empathy skills are growing."
            totalResponses < 100 -> "Impressive dedication! You're becoming truly empathetic."
            else -> "You're an empathy expert! Your kindness makes a difference."
        }
    }

    /**
     * Get next milestone information
     *
     * @return Description of next achievement to work towards
     */
    fun getNextMilestone(): String {
        return when {
            currentStreak < 3 -> "Respond for 3 days in a row"
            currentStreak < 7 -> "Complete your first week"
            currentStreak < 14 -> "Achieve a 2-week streak"
            currentStreak < 30 -> "Reach a 1-month streak"
            currentStreak < 100 -> "Aim for 100 days in a row"
            else -> "You've achieved all major milestones!"
        }
    }

    /**
     * Calculate responses per day average
     *
     * @return Average responses per active day
     */
    fun getResponsesPerDay(): Double {
        return if (totalActiveDays > 0) {
            totalResponses.toDouble() / totalActiveDays.toDouble()
        } else {
            0.0
        }
    }

    /**
     * Get user level based on total responses Simple progression system for
     * gamification
     *
     * @return User level (1-10+)
     */
    fun getUserLevel(): Int {
        return when {
            totalResponses < 5 -> 1
            totalResponses < 15 -> 2
            totalResponses < 30 -> 3
            totalResponses < 50 -> 4
            totalResponses < 75 -> 5
            totalResponses < 100 -> 6
            totalResponses < 150 -> 7
            totalResponses < 200 -> 8
            totalResponses < 300 -> 9
            else -> 10
        }
    }

    /**
     * Get level description for UI display
     *
     * @return String describing current user level
     */
    fun getLevelDescription(): String {
        return when (getUserLevel()) {
            1 -> "Beginner - Just starting your journey"
            2 -> "Novice - Learning the basics"
            3 -> "Student - Understanding empathy"
            4 -> "Practitioner - Developing skills"
            5 -> "Skilled - Good empathy awareness"
            6 -> "Advanced - Strong empathy skills"
            7 -> "Expert - Excellent empathy response"
            8 -> "Master - Outstanding empathy abilities"
            9 -> "Guru - Exceptional empathy mastery"
            else -> "Sage - Empathy wisdom achieved"
        }
    }

    companion object {
        /**
         * Achievement IDs that can be unlocked Used for parsing
         * achievementsUnlocked JSON
         */
        val AVAILABLE_ACHIEVEMENTS = listOf(
            "first_response",
            "three_day_streak",
            "week_streak",
            "two_week_streak",
            "month_streak",
            "fifty_responses",
            "hundred_responses",
            "scenario_variety", // Completed 20+ different scenarios
            "example_learner", // Viewed 25+ examples
            "self_reflector", // Provided 50+ self ratings
            "consistent_user", // 80%+ activity rate over 30 days
            "empathy_master" // Achieved all other achievements
        )

        /** Achievement descriptions for UI display */
        val ACHIEVEMENT_DESCRIPTIONS = mapOf(
            "first_response" to "First Steps - Completed your first empathy response",
            "three_day_streak" to "Building Habits - 3 days in a row",
            "week_streak" to "Weekly Warrior - 7 days straight",
            "two_week_streak" to "Consistent Learner - 14 days in a row",
            "month_streak" to "Monthly Master - 30 days straight",
            "fifty_responses" to "Half Century - 50 total responses",
            "hundred_responses" to "Century Club - 100 total responses",
            "scenario_variety" to "Explorer - Completed 20+ different scenarios",
            "example_learner" to "Student - Viewed 25+ example responses",
            "self_reflector" to "Thoughtful - Provided 50+ self ratings",
            "consistent_user" to "Dedicated - 80%+ activity rate",
            "empathy_master" to "Master - All achievements unlocked"
        )
    }
}