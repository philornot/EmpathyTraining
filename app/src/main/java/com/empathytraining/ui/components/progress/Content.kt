package com.empathytraining.ui.components.progress

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse

@Composable
fun ProgressContent(
    userProgress: UserProgress,
    streakInfo: Pair<Int, Int>,
    comprehensiveStats: Map<String, Any>,
    hasCompletedTodaysChallenge: Boolean,
    todayResponseCount: Int,
    recentResponses: List<UserResponse>,
    missingResources: List<String>,
    onNavigateToChallenge: () -> Unit,
    onUpdateDifficulty: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Show resource validation warnings if any
        if (missingResources.isNotEmpty()) {
            ResourceValidationCard(missingResources)
        }

        // Enhanced motivational card with comprehensive stats
        EnhancedMotivationalCard(userProgress, comprehensiveStats)

        // Enhanced streak card with streak info
        EnhancedStreakCard(streakInfo)

        // Today's activity status
        TodayActivityCard(
            hasCompletedTodaysChallenge = hasCompletedTodaysChallenge,
            todayResponseCount = todayResponseCount
        )

        // Comprehensive statistics card
        if (comprehensiveStats.isNotEmpty()) {
            ComprehensiveStatsCard(comprehensiveStats)
        }

        // Recent responses preview
        if (recentResponses.isNotEmpty()) {
            RecentResponsesCard(recentResponses)
        }

        // Quality card with enhanced metrics
        if (userProgress.totalResponses > 0) {
            EnhancedQualityCard(userProgress, comprehensiveStats)
        }

        // Settings and difficulty card
        DifficultySettingsCard(
            currentDifficulty = userProgress.preferredDifficulty,
            onUpdateDifficulty = onUpdateDifficulty
        )

        // Milestone card
        MilestoneCard(userProgress)

        // Action button
        ActionButton(userProgress, onNavigateToChallenge)
    }
}