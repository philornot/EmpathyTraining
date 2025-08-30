package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.utils.LocalizationUtils
import timber.log.Timber

/**
 * Progress Screen - Shows user statistics, achievements, and motivation
 * Now with full internationalization support
 */
@Composable
fun ProgressScreen(
    repository: EmpathyRepository,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.d("Composing Progress Screen")

    LocalContext.current
    // Collect user progress from repository
    val userProgress by repository.getUserProgress().collectAsState(initial = null)

    Timber.d("User progress loaded: ${userProgress?.totalResponses} total responses")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen header
        Text(
            text = stringResource(R.string.progress_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (userProgress) {
            null -> {
                // Loading state
                LoadingProgressContent()
            }

            else -> {
                // Main progress content
                ProgressContent(
                    userProgress = userProgress!!, onNavigateToChallenge = onNavigateToChallenge
                )
            }
        }
    }
}

/** Loading state while user progress is being fetched */
@Composable
private fun LoadingProgressContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.progress_loading),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/** Main progress content with all statistics and motivational elements */
@Composable
private fun ProgressContent(
    userProgress: UserProgress,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Motivational header with current level
        MotivationalHeader(userProgress = userProgress)

        // Streak information - most important for daily motivation
        StreakCard(userProgress = userProgress)

        // Statistics overview
        StatisticsCard(userProgress = userProgress)

        // Quality metrics
        QualityMetricsCard(userProgress = userProgress)

        // Next milestone and motivation
        MilestoneCard(userProgress = userProgress)

        // Call to action button
        ActionButton(
            userProgress = userProgress, onNavigateToChallenge = onNavigateToChallenge
        )
    }
}

/** Motivational header showing user level and encouraging message */
@Composable
private fun MotivationalHeader(
    userProgress: UserProgress,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = LocalizationUtils.getMotivationalMessage(
                    context,
                    userProgress.totalResponses
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Card showing current and longest streak with visual indicators */
@Composable
private fun StreakCard(
    userProgress: UserProgress,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
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
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = LocalizationUtils.getStreakStatusDescription(
                            context, userProgress.currentStreak
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Current streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${userProgress.currentStreak}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.days_current, userProgress.currentStreak
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Longest streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${userProgress.longestStreak}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.days_best, userProgress.longestStreak
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

/** Card with overall statistics and activity metrics */
@Composable
private fun StatisticsCard(
    userProgress: UserProgress,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.statistics_overview),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grid of statistics
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticItem(
                        value = "${userProgress.totalResponses}",
                        label = stringResource(R.string.total_responses),
                        modifier = Modifier.weight(1f)
                    )

                    StatisticItem(
                        value = "${userProgress.totalActiveDays}",
                        label = stringResource(R.string.active_days),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticItem(
                        value = "${userProgress.uniqueScenariosCompleted}",
                        label = stringResource(R.string.scenarios_completed),
                        modifier = Modifier.weight(1f)
                    )

                    StatisticItem(
                        value = userProgress.getFormattedActivityPercentage(),
                        label = stringResource(R.string.activity_rate),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Activity rate progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.consistency),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = userProgress.getFormattedActivityPercentage(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { userProgress.getActivityPercentage().toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

/** Individual statistic item for the statistics grid */
@Composable
private fun StatisticItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Card showing response quality metrics and averages */
@Composable
private fun QualityMetricsCard(
    userProgress: UserProgress,
    modifier: Modifier = Modifier,
) {
    if (userProgress.totalResponses == 0) {
        // Don't show quality metrics if no responses yet
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
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
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.response_quality),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Average length
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${userProgress.averageResponseLength.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.avg_characters),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                // Average self-rating (if available)
                if (userProgress.averageSelfRating > 0.0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(
                                java.util.Locale.getDefault(),
                                "%.1f",
                                userProgress.averageSelfRating
                            ),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.avg_rating),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Examples viewed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${userProgress.examplesViewed}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.examples_viewed),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

/** Card showing next milestone and motivational target */
@Composable
private fun MilestoneCard(
    userProgress: UserProgress,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

/** Action button to navigate to daily challenge or show completion status */
@Composable
private fun ActionButton(
    userProgress: UserProgress,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = onNavigateToChallenge, modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.psychology),
            contentDescription = stringResource(R.string.cd_challenge)
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