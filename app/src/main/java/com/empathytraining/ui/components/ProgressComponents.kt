package com.empathytraining.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse
import com.empathytraining.utils.LocalizationUtils
import timber.log.Timber

@Composable
fun ProgressLoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.progress_loading))
        }
    }
}

@Composable
fun NoProgressState(onNavigateToChallenge: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.psychology),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.no_progress_yet),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                FilledTonalButton(onClick = onNavigateToChallenge) {
                    Text(stringResource(R.string.start_todays_challenge))
                }
            }
        }
    }
}

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
    onUpdateDifficulty: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Show resource validation warnings if any
        if (missingResources.isNotEmpty()) {
            ResourceValidationCard(missingResources)
        }

        // Enhanced motivational card with comprehensive stats
        EnhancedMotivationalCard(userProgress, comprehensiveStats)

        // Enhanced streak card with streak info
        EnhancedStreakCard(userProgress, streakInfo)

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

@Composable
private fun ResourceValidationCard(missingResources: List<String>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ), shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = stringResource(R.string.cd_warning),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.resource_validation_warning),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = stringResource(R.string.missing_resources_count, missingResources.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun EnhancedMotivationalCard(
    userProgress: UserProgress,
    comprehensiveStats: Map<String, Any>,
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.level_format, userProgress.getUserLevel()),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = LocalizationUtils.getLevelDescription(context, userProgress.getUserLevel()),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = LocalizationUtils.getMotivationalMessage(
                    context, userProgress.totalResponses
                ), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center
            )

            // Show additional stats from comprehensive data
            if (comprehensiveStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(
                        R.string.active_scenarios_info,
                        comprehensiveStats["activeScenarios"] as? Int ?: 0
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun EnhancedStreakCard(
    userProgress: UserProgress,
    streakInfo: Pair<Int, Int>,
) {
    val context = LocalContext.current
    val (currentStreak, longestStreak) = streakInfo

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.local_fire_department),
                    contentDescription = stringResource(R.string.cd_streak),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.current_streak),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = LocalizationUtils.getStreakStatusDescription(
                            context, currentStreak
                        ), style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StreakStat(
                    value = "$currentStreak", label = pluralStringResource(
                        R.plurals.days_current, currentStreak
                    ), color = MaterialTheme.colorScheme.secondary
                )
                StreakStat(
                    value = "$longestStreak",
                    label = pluralStringResource(R.plurals.days_best, longestStreak),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun TodayActivityCard(
    hasCompletedTodaysChallenge: Boolean,
    todayResponseCount: Int,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasCompletedTodaysChallenge) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.calendar_today),
                    contentDescription = stringResource(R.string.cd_today),
                    tint = if (hasCompletedTodaysChallenge) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.today_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = if (hasCompletedTodaysChallenge) {
                    stringResource(R.string.completed_today_count, todayResponseCount)
                } else {
                    stringResource(R.string.not_completed_today)
                }, style = MaterialTheme.typography.bodyLarge
            )

            if (todayResponseCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.todays_response_count, todayResponseCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ComprehensiveStatsCard(stats: Map<String, Any>) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.comprehensive_statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = (stats["totalResponses"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.total_responses)
                )
                StatItem(
                    value = (stats["activeDays"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.active_days)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = (stats["uniqueScenarios"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.unique_scenarios)
                )
                StatItem(
                    value = (stats["activeScenarios"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.active_scenarios_available)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = (stats["ratedResponses"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.rated_responses)
                )
                StatItem(
                    value = String.format("%.1f", stats["averageResponseLength"] as? Double ?: 0.0),
                    label = stringResource(R.string.avg_characters)
                )
            }
        }
    }
}

@Composable
private fun RecentResponsesCard(recentResponses: List<UserResponse>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.recent_activity),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentResponses.take(3)) { response ->
                    RecentResponsePreview(response)
                }
            }

            if (recentResponses.size > 3) {
                Text(
                    text = stringResource(R.string.and_more_responses, recentResponses.size - 3),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentResponsePreview(response: UserResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = response.userResponseText.take(50) + if (response.userResponseText.length > 50) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
            Text(
                text = response.getFormattedDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        response.selfRating?.let { rating ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.cd_rating),
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$rating", style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun EnhancedQualityCard(
    userProgress: UserProgress,
    comprehensiveStats: Map<String, Any>,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.cd_quality),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.response_quality),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = "${userProgress.averageResponseLength.toInt()}",
                    label = stringResource(R.string.avg_characters)
                )

                if (userProgress.averageSelfRating > 0.0) {
                    StatItem(
                        value = String.format("%.1f", userProgress.averageSelfRating),
                        label = stringResource(R.string.avg_rating)
                    )
                }

                StatItem(
                    value = "${userProgress.examplesViewed}",
                    label = stringResource(R.string.examples_viewed)
                )
            }

            // Additional quality metrics from comprehensive stats
            if (comprehensiveStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        value = (comprehensiveStats["ratedResponses"] as? Int ?: 0).toString(),
                        label = stringResource(R.string.rated_responses)
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultySettingsCard(
    currentDifficulty: Int,
    onUpdateDifficulty: (Int) -> Unit,
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.difficulty_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(onClick = { showDifficultyDialog = true }) {
                    Text(stringResource(R.string.change))
                }
            }

            Text(
                text = stringResource(
                    R.string.current_difficulty_level, getDifficultyString(currentDifficulty)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            currentDifficulty = currentDifficulty,
            onDifficultySelected = { difficulty ->
                onUpdateDifficulty(difficulty)
                showDifficultyDialog = false
                Timber.d("User selected difficulty level: $difficulty")
            },
            onDismiss = { showDifficultyDialog = false })
    }
}

@Composable
private fun DifficultySelectionDialog(
    currentDifficulty: Int,
    onDifficultySelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(stringResource(R.string.select_difficulty_level))
    }, text = {
        Column {
            (1..5).forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = level == currentDifficulty,
                        onClick = { onDifficultySelected(level) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getDifficultyString(level),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.close))
        }
    })
}

@Composable
private fun MilestoneCard(userProgress: UserProgress) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.flag),
                    contentDescription = stringResource(R.string.cd_milestone),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.next_milestone),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = LocalizationUtils.getNextMilestone(context, userProgress.currentStreak),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.milestone_encouragement),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionButton(
    userProgress: UserProgress,
    onNavigateToChallenge: () -> Unit,
) {
    FilledTonalButton(
        onClick = onNavigateToChallenge, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.psychology),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (userProgress.hasRespondedToday()) {
                stringResource(R.string.view_todays_challenge)
            } else {
                stringResource(R.string.start_todays_challenge)
            }
        )
    }
}

@Composable
private fun StreakStat(
    value: String,
    label: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun getDifficultyString(difficulty: Int): String {
    return when (difficulty) {
        1 -> stringResource(R.string.difficulty_very_easy)
        2 -> stringResource(R.string.difficulty_easy)
        3 -> stringResource(R.string.difficulty_medium)
        4 -> stringResource(R.string.difficulty_hard)
        5 -> stringResource(R.string.difficulty_very_hard)
        else -> stringResource(R.string.difficulty_unknown)
    }
}