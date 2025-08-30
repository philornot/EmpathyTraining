package com.empathytraining.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse

@Composable
fun ComprehensiveStatsCard(stats: Map<String, Any>) {
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
fun EnhancedQualityCard(
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
fun RecentResponsesCard(recentResponses: List<UserResponse>) {
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
fun StreakStat(
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