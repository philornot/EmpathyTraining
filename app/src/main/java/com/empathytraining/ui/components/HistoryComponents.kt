package com.empathytraining.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserResponse
import com.empathytraining.data.repository.EmpathyRepository

@Composable
fun HistoryLoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.loading))
        }
    }
}

@Composable
fun HistoryEmptyState() {
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
                    imageVector = ImageVector.vectorResource(R.drawable.chat_bubble),
                    contentDescription = stringResource(R.string.cd_no_responses),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = stringResource(R.string.no_responses_yet),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.complete_first_challenge),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HistoryContent(
    responsesWithScenarios: List<EmpathyRepository.UserResponseWithScenario>,
    recentResponses: List<UserResponse>,
    comprehensiveStats: Map<String, Any>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    selectedDateResponses: List<UserResponse>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Statistics overview card
        if (comprehensiveStats.isNotEmpty()) {
            HistoryStatsCard(comprehensiveStats)
        }

        // Recent responses section
        if (recentResponses.isNotEmpty()) {
            Text(
                text = stringResource(R.string.recent_responses),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentResponses.take(5)) { response ->
                    RecentResponseItem(response)
                }
            }
        }

        // All responses with scenarios
        if (responsesWithScenarios.isNotEmpty()) {
            Text(
                text = stringResource(R.string.all_responses),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            ResponsesWithScenariosSection(responsesWithScenarios)
        }

        // Date filter section
        selectedDate?.let { date ->
            Text(
                text = stringResource(R.string.responses_for_date, date),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            if (selectedDateResponses.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedDateResponses) { response ->
                        DateFilteredResponseItem(response)
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_responses_for_date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryStatsCard(stats: Map<String, Any>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.comprehensive_statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
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
                    value = String.format("%.1f", stats["averageResponseLength"] as? Double ?: 0.0),
                    label = stringResource(R.string.avg_characters)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = String.format("%.1f", stats["averageSelfRating"] as? Double ?: 0.0),
                    label = stringResource(R.string.avg_rating)
                )
                StatItem(
                    value = (stats["examplesViewed"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.examples_viewed)
                )
            }
        }
    }
}


@Composable
private fun RecentResponseItem(response: UserResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = response.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                response.selfRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.cd_rating),
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.rating_format, rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = response.userResponseText.take(100) + if (response.userResponseText.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun ResponsesWithScenariosSection(
    responsesWithScenarios: List<EmpathyRepository.UserResponseWithScenario>,
) {
    LazyColumn(
        modifier = Modifier.height(400.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(responsesWithScenarios) { responseWithScenario ->
            ResponseWithScenarioItem(responseWithScenario)
        }
    }
}

@Composable
private fun ResponseWithScenarioItem(
    responseWithScenario: EmpathyRepository.UserResponseWithScenario,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with time and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = responseWithScenario.response.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                responseWithScenario.response.selfRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.cd_rating),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.rating_format, rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Localized scenario
            ResponseSection(
                title = stringResource(R.string.scenario),
                content = "\"${responseWithScenario.localizedScenarioText}\"",
                color = MaterialTheme.colorScheme.primary
            )

            // User response
            ResponseSection(
                title = stringResource(R.string.your_response),
                content = responseWithScenario.response.userResponseText,
                color = MaterialTheme.colorScheme.secondary
            )

            // Metadata
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = responseWithScenario.response.getQualityAssessment(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = stringResource(
                        R.string.character_count, responseWithScenario.response.responseLength
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (responseWithScenario.response.viewedExample) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.viewed_example),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun DateFilteredResponseItem(response: UserResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = response.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                            text = stringResource(R.string.rating_format, rating),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = response.userResponseText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ResponseSection(
    title: String,
    content: String,
    color: Color,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}