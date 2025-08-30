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

@Composable
fun ProgressScreen(
    repository: EmpathyRepository,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userProgress by repository.getUserProgress().collectAsState(initial = null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.progress_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (userProgress) {
            null -> LoadingProgress()
            else -> ProgressContent(userProgress!!, onNavigateToChallenge)
        }
    }
}

@Composable
private fun LoadingProgress() {
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
private fun ProgressContent(
    userProgress: UserProgress,
    onNavigateToChallenge: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        MotivationalCard(userProgress)
        StreakCard(userProgress)
        StatsCard(userProgress)
        if (userProgress.totalResponses > 0) {
            QualityCard(userProgress)
        }
        MilestoneCard(userProgress)
        ActionButton(userProgress, onNavigateToChallenge)
    }
}

@Composable
private fun MotivationalCard(userProgress: UserProgress) {
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
                    context,
                    userProgress.totalResponses
                ), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StreakCard(userProgress: UserProgress) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
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
                            context,
                            userProgress.currentStreak
                        ), style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StreakStat(
                    value = "${userProgress.currentStreak}",
                    label = pluralStringResource(
                        R.plurals.days_current,
                        userProgress.currentStreak
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
                StreakStat(
                    value = "${userProgress.longestStreak}",
                    label = pluralStringResource(R.plurals.days_best, userProgress.longestStreak),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun StreakStat(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
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
private fun StatsCard(userProgress: UserProgress) {
    Card {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
            Text(
                text = stringResource(R.string.statistics_overview),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = "${userProgress.totalResponses}",
                    label = stringResource(R.string.total_responses)
                )
                StatItem(
                    value = "${userProgress.totalActiveDays}",
                    label = stringResource(R.string.active_days)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = "${userProgress.uniqueScenariosCompleted}",
                    label = stringResource(R.string.scenarios_completed)
                )
                StatItem(
                    value = userProgress.getFormattedActivityPercentage(),
                    label = stringResource(R.string.activity_rate)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Consistency progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.consistency),
                    style = MaterialTheme.typography.bodyMedium
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
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
private fun QualityCard(userProgress: UserProgress) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
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
        }
    }
}

@Composable
private fun MilestoneCard(userProgress: UserProgress) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
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