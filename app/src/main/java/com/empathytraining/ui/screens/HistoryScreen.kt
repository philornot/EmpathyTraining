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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.EmpathyScenario
import com.empathytraining.data.models.UserResponse
import com.empathytraining.data.repository.EmpathyRepository
import timber.log.Timber

/**
 * History Screen - Shows user's past empathy responses
 *
 * This screen displays:
 * - List of all user responses ordered by date (newest first)
 * - Response details including date, scenario, and user's response
 * - Self-ratings if provided by user
 * - Empty state when no responses exist yet
 * - Easy-to-read card layout for better UX
 */
@Composable
fun HistoryScreen(
    repository: EmpathyRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.d("Composing History Screen")

    // Collect user responses from repository
    val userResponses by repository.getAllUserResponses().collectAsState(emptyList())

    Timber.d("Loaded ${userResponses.size} user responses")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen header
        Text(
            text = "Your Response History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            userResponses.isEmpty() -> {
                // Empty state - no responses yet
                EmptyHistoryContent(
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                // Show responses in scrollable list
                HistoryList(
                    responses = userResponses,
                    repository = repository,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Empty state shown when user has no responses yet */
@Composable
private fun EmptyHistoryContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
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
                    contentDescription = "No responses",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "No responses yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Complete your first daily challenge to see your empathy responses here.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Scrollable list of user responses */
@Composable
private fun HistoryList(
    responses: List<UserResponse>,
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Group responses by date for better organization
        val groupedResponses = responses.groupBy { response ->
            response.getFormattedDate()
        }

        groupedResponses.forEach { (date, responsesForDate) ->
            // Date header
            item(key = "header_$date") {
                DateHeader(date = date)
            }

            // Responses for this date
            items(
                items = responsesForDate, key = { response -> response.id }) { response ->
                ResponseCard(
                    response = response, repository = repository
                )
            }
        }
    }
}

/** Date header to separate responses by day */
@Composable
private fun DateHeader(
    date: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.calendar_today),
            contentDescription = "Date",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** Individual response card showing scenario and user's response */
@Composable
private fun ResponseCard(
    response: UserResponse,
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    // State for holding the scenario
    var scenario by remember { mutableStateOf<EmpathyScenario?>(null) }

    // Fetch scenario details for this response
    LaunchedEffect(response.scenarioId) {
        scenario = repository.getScenarioById(response.scenarioId)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Response header with time and rating
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

                // Show self-rating if provided
                response.selfRating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$rating/5",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Original scenario (if available)
            scenario?.let { s ->
                Text(
                    text = "Scenario:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "\"${s.scenarioText}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // User's response
            Text(
                text = "Your Response:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = response.userResponseText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Response metadata
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = response.getQualityAssessment(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${response.responseLength} characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show if user viewed example
            if (response.viewedExample) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Viewed example response",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}