package com.empathytraining.utils

import android.content.Context
import com.empathytraining.R

/**
 * Utility class for handling localized strings and dynamic content that
 * depends on user progress values.
 *
 * This class provides localized versions of messages that are dynamically
 * generated based on user statistics, ensuring proper internationalization
 * support throughout the app.
 */
object LocalizationUtils {

    /**
     * Get localized level description based on user level
     *
     * @param context Android context for accessing string resources
     * @param userLevel Current user level (1-10+)
     * @return Localized description of the user's current level
     */
    fun getLevelDescription(context: Context, userLevel: Int): String {
        return when (userLevel) {
            1 -> context.getString(R.string.level_1_desc)
            2 -> context.getString(R.string.level_2_desc)
            3 -> context.getString(R.string.level_3_desc)
            4 -> context.getString(R.string.level_4_desc)
            5 -> context.getString(R.string.level_5_desc)
            6 -> context.getString(R.string.level_6_desc)
            7 -> context.getString(R.string.level_7_desc)
            8 -> context.getString(R.string.level_8_desc)
            9 -> context.getString(R.string.level_9_desc)
            else -> context.getString(R.string.level_10_desc)
        }
    }

    /**
     * Get localized motivational message based on total responses
     *
     * @param context Android context for accessing string resources
     * @param totalResponses User's total number of responses
     * @return Localized motivational message appropriate for user's progress
     */
    fun getMotivationalMessage(context: Context, totalResponses: Int): String {
        return when {
            totalResponses == 0 -> context.getString(R.string.motivation_welcome)
            totalResponses < 5 -> context.getString(R.string.motivation_getting_started)
            totalResponses < 20 -> context.getString(R.string.motivation_building_habits)
            totalResponses < 50 -> context.getString(R.string.motivation_good_progress)
            totalResponses < 100 -> context.getString(R.string.motivation_impressive)
            else -> context.getString(R.string.motivation_expert)
        }
    }

    /**
     * Get localized streak status description based on current streak
     *
     * @param context Android context for accessing string resources
     * @param currentStreak User's current consecutive days streak
     * @return Localized description of the current streak status
     */
    fun getStreakStatusDescription(context: Context, currentStreak: Int): String {
        return when {
            currentStreak == 0 -> context.getString(R.string.streak_start_today)
            currentStreak == 1 -> context.getString(R.string.streak_great_start)
            currentStreak < 7 -> context.getString(R.string.streak_building_momentum, currentStreak)
            currentStreak < 30 -> context.getString(R.string.streak_excellent, currentStreak)
            currentStreak < 100 -> context.getString(R.string.streak_amazing, currentStreak)
            else -> context.getString(R.string.streak_incredible, currentStreak)
        }
    }

    /**
     * Get localized next milestone description based on current streak
     *
     * @param context Android context for accessing string resources
     * @param currentStreak User's current consecutive days streak
     * @return Localized description of the next milestone to achieve
     */
    fun getNextMilestone(context: Context, currentStreak: Int): String {
        return when {
            currentStreak < 3 -> context.getString(R.string.milestone_3_days)
            currentStreak < 7 -> context.getString(R.string.milestone_week)
            currentStreak < 14 -> context.getString(R.string.milestone_2_weeks)
            currentStreak < 30 -> context.getString(R.string.milestone_month)
            currentStreak < 100 -> context.getString(R.string.milestone_100_days)
            else -> context.getString(R.string.milestone_all_achieved)
        }
    }

    /**
     * Get localized quality assessment based on response characteristics
     *
     * @param context Android context for accessing string resources
     * @param responseLength Length of the user's response
     * @param selfRating User's self-rating (nullable)
     * @return Localized quality assessment string
     */
    fun getQualityAssessment(context: Context, responseLength: Int, selfRating: Int?): String {
        return when {
            selfRating != null && selfRating >= 4 -> context.getString(R.string.quality_great)
            selfRating != null && selfRating >= 3 -> context.getString(R.string.quality_good)
            selfRating != null && selfRating >= 2 -> context.getString(R.string.quality_decent)
            responseLength >= 100 -> context.getString(R.string.quality_thoughtful)
            responseLength >= 50 -> context.getString(R.string.quality_good_length)
            responseLength >= 20 -> context.getString(R.string.quality_brief)
            else -> context.getString(R.string.quality_very_brief)
        }
    }

    /**
     * Get localized response time description
     *
     * @param context Android context for accessing string resources
     * @param responseTimeSeconds Time taken to respond in seconds
     * @return Localized description of response time
     */
    fun getResponseTimeDescription(context: Context, responseTimeSeconds: Int): String {
        return when {
            responseTimeSeconds < 0 -> context.getString(R.string.time_not_tracked)
            responseTimeSeconds < 30 -> context.getString(R.string.time_quick, responseTimeSeconds)
            responseTimeSeconds < 120 -> context.getString(
                R.string.time_moderate, responseTimeSeconds
            )

            responseTimeSeconds < 300 -> context.getString(
                R.string.time_thoughtful, responseTimeSeconds / 60
            )

            else -> context.getString(R.string.time_deep, responseTimeSeconds / 60)
        }
    }

    /**
     * Get localized "time ago" text for displaying when response was created
     *
     * @param context Android context for accessing string resources
     * @param daysAgo Number of days since response was created
     * @return Localized "time ago" string
     */
    fun getTimeAgoText(context: Context, daysAgo: Long): String {
        val resources = context.resources // Get the Resources object from the context

        return when (daysAgo) {
            0L -> context.getString(R.string.time_today) // If 0 days, use "Today"
            1L -> context.getString(R.string.time_yesterday) // If 1 day, use "Yesterday"

            // For 2 to 6 days, use the "days_ago" plurals resource
            in 2L..6L -> resources.getQuantityString(
                R.plurals.days_ago, // The ID of the plurals resource
                daysAgo.toInt(),    // The quantity to determine "one" or "other"
                daysAgo.toInt()     // The argument to substitute for %d in the string
            )

            // For 7 to 13 days (which will always be 1 week)
            in 7L..13L -> {
                val weeks = 1 // In this range, it's always one week
                resources.getQuantityString(
                    R.plurals.weeks_ago, // The ID of the plurals resource
                    weeks,               // Quantity is 1, so it will pick "one" form ("1 week ago")
                    weeks                // Argument for %d if the "other" form were used (not relevant here but good practice)
                )
            }

            // For 14 to 29 days, calculate the number of weeks
            in 14L..29L -> {
                val weeks = (daysAgo / 7).toInt() // Calculate number of full weeks
                resources.getQuantityString(
                    R.plurals.weeks_ago, weeks, // Quantity to determine "one" or "other"
                    weeks  // Argument for %d
                )
            }

            // For 30 to 59 days (which will always be 1 month)
            in 30L..59L -> {
                val months = 1 // In this range, it's always one month
                resources.getQuantityString(
                    R.plurals.months_ago, // The ID of the plurals resource
                    months,               // Quantity is 1, so it will pick "one" form ("1 month ago")
                    months                // Argument for %d if the "other" form were used
                )
            }

            // For 60+ days, or any other case not caught above (though current logic covers all positive daysAgo)
            else -> {
                val months = (daysAgo / 30).toInt() // Calculate number of full months

                // This defensive check handles cases where 'months' might be 0
                // even if daysAgo > 0, which shouldn't happen with the current 'when' structure
                // for daysAgo >= 60, but is good for robustness.
                if (months == 0 && daysAgo > 0) {
                    // Fallback to weeks if months is 0 but daysAgo is still positive
                    // (e.g., if daysAgo was < 30 and somehow reached here)
                    val weeks = (daysAgo / 7).toInt()
                    if (weeks > 0) {
                        resources.getQuantityString(R.plurals.weeks_ago, weeks, weeks)
                    } else {
                        // Fallback to days if less than a week and not 0-1 days (highly unlikely path)
                        resources.getQuantityString(
                            R.plurals.days_ago,
                            daysAgo.toInt(),
                            daysAgo.toInt()
                        )
                    }
                } else run {
                    // Default case for months (e.g., 2 months ago, 3 months ago, etc.)
                    resources.getQuantityString(R.plurals.months_ago, months, months)
                }
            }
        }
    }

    /**
     * Get localized difficulty level description
     *
     * @param context Android context for accessing string resources
     * @param difficultyLevel Difficulty level (1-5)
     * @return Localized difficulty description
     */
    fun getDifficultyDescription(context: Context, difficultyLevel: Int): String {
        return when (difficultyLevel) {
            1 -> context.getString(R.string.difficulty_very_easy)
            2 -> context.getString(R.string.difficulty_easy)
            3 -> context.getString(R.string.difficulty_medium)
            4 -> context.getString(R.string.difficulty_hard)
            5 -> context.getString(R.string.difficulty_very_hard)
            else -> context.getString(R.string.difficulty_unknown)
        }
    }

    /**
     * Get localized category display name
     *
     * @param context Android context for accessing string resources
     * @param category Category identifier
     * @return Localized category name
     */
    fun getCategoryDisplayName(context: Context, category: String): String {
        return when (category.lowercase()) {
            "work" -> context.getString(R.string.category_work)
            "relationships" -> context.getString(R.string.category_relationships)
            "family" -> context.getString(R.string.category_family)
            "personal" -> context.getString(R.string.category_personal)
            "health" -> context.getString(R.string.category_health)
            "friendship" -> context.getString(R.string.category_friendship)
            "education" -> context.getString(R.string.category_education)
            "general" -> context.getString(R.string.category_general)
            else -> category.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        }
    }

    /**
     * Get localized achievement description
     *
     * @param context Android context for accessing string resources
     * @param achievementId ID of the achievement
     * @return Localized achievement description
     */
    fun getAchievementDescription(context: Context, achievementId: String): String {
        return when (achievementId) {
            "first_response" -> context.getString(R.string.achievement_first_response)
            "three_day_streak" -> context.getString(R.string.achievement_three_day_streak)
            "week_streak" -> context.getString(R.string.achievement_week_streak)
            "two_week_streak" -> context.getString(R.string.achievement_two_week_streak)
            "month_streak" -> context.getString(R.string.achievement_month_streak)
            "fifty_responses" -> context.getString(R.string.achievement_fifty_responses)
            "hundred_responses" -> context.getString(R.string.achievement_hundred_responses)
            "scenario_variety" -> context.getString(R.string.achievement_scenario_variety)
            "example_learner" -> context.getString(R.string.achievement_example_learner)
            "self_reflector" -> context.getString(R.string.achievement_self_reflector)
            "consistent_user" -> context.getString(R.string.achievement_consistent_user)
            "empathy_master" -> context.getString(R.string.achievement_empathy_master)
            else -> achievementId.replace("_", " ").split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
    }
}