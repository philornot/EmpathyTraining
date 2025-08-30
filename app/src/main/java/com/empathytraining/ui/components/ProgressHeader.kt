package com.empathytraining.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse
import com.empathytraining.utils.LocalizationUtils

@Composable
fun ProgressHeader(
    userProgress: UserProgress?,
    todayResponseCount: Int = 0,
    todaysResponses: List<UserResponse> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.daily_empathy_challenge),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            userProgress?.let { progress ->
                Spacer(modifier = Modifier.height(12.dp))

                // Main stats row
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(
                        value = "${progress.currentStreak}",
                        label = stringResource(R.string.day_streak),
                        icon = R.drawable.local_fire_department
                    )
                    StatItem(
                        value = "${progress.totalResponses}",
                        label = stringResource(R.string.responses),
                        icon = R.drawable.chat_bubble
                    )
                }

                // Today's progress indicator
                if (todayResponseCount > 0 || todaysResponses.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TodayProgressIndicator(
                        todayResponseCount = todayResponseCount,
                        maxResponses = 3,
                        todaysResponses = todaysResponses
                    )
                }

                // Level and motivational message
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.level_format, progress.getUserLevel()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = LocalizationUtils.getMotivationalMessage(
                        context,
                        progress.totalResponses
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: Int? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = ImageVector.vectorResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun TodayProgressIndicator(
    todayResponseCount: Int,
    maxResponses: Int,
    todaysResponses: List<UserResponse>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LinearProgressIndicator(
            progress = { todayResponseCount.toFloat() / maxResponses.toFloat() },
            modifier = Modifier.width(200.dp),
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "$todayResponseCount/$maxResponses " + stringResource(R.string.responses),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // Show average rating for today if available
        val avgRatingToday =
            todaysResponses.mapNotNull { it.selfRating }.average().takeIf { !it.isNaN() }

        avgRatingToday?.let { rating ->
            Text(
                text = "Today's avg: ${String.format("%.1f", rating)}/5",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}