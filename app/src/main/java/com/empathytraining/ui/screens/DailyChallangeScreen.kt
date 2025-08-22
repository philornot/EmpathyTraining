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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.EmpathyScenario
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.repository.EmpathyRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Daily Challenge Screen - The main screen of the app
 *
 * This screen handles:
 * - Displaying today's empathy scenario
 * - Collecting user's empathetic response
 * - Showing example response after submission
 * - Tracking user progress and streaks
 * - Motivating user with progress display
 */
@Composable
fun DailyChallengeScreen(
    repository: EmpathyRepository,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.d("Composing Daily Challenge Screen")

    // Screen state management
    var todaysScenario by remember { mutableStateOf<EmpathyScenario?>(null) }
    var userResponse by remember { mutableStateOf("") }
    var hasSubmitted by remember { mutableStateOf(false) }
    var showExample by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var hasDoneTodaysChallenge by remember { mutableStateOf(false) }

    // Coroutines for async operations
    val coroutineScope = rememberCoroutineScope()

    // Collect user progress for motivation display
    val userProgress by repository.getUserProgress().collectAsState(
        initial = null
    )

    // Load today's challenge when screen opens
    LaunchedEffect(Unit) {
        Timber.d("Loading today's challenge")

        try {
            // Check if user already completed today's challenge
            hasDoneTodaysChallenge = repository.hasDoneTodaysChallenge()
            Timber.d("Has done today's challenge: $hasDoneTodaysChallenge")

            if (!hasDoneTodaysChallenge) {
                // Get new scenario for today
                todaysScenario = repository.getTodaysChallenge()
                Timber.d("Loaded scenario: ${todaysScenario?.id}")
            }

            isLoading = false
        } catch (e: Exception) {
            Timber.e(e, "Error loading today's challenge")
            isLoading = false
        }
    }

    // Main screen content
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with progress motivation
        DailyProgressHeader(userProgress = userProgress)

        when {
            isLoading -> {
                // Loading state
                LoadingContent()
            }

            hasDoneTodaysChallenge -> {
                // Already completed today's challenge
                CompletedChallengeContent(
                    userProgress = userProgress, onNavigateToHistory = onNavigateToHistory
                )
            }

            todaysScenario == null -> {
                // No more scenarios available for today
                NoScenarioContent(onNavigateToHistory = onNavigateToHistory)
            }

            else -> {
                // Active challenge
                ActiveChallengeContent(
                    scenario = todaysScenario!!,
                    userResponse = userResponse,
                    onResponseChange = { userResponse = it },
                    hasSubmitted = hasSubmitted,
                    showExample = showExample,
                    onSubmit = {
                        coroutineScope.launch {
                            try {
                                Timber.d("Submitting response")
                                repository.submitResponse(
                                    scenarioId = todaysScenario!!.id,
                                    userResponseText = userResponse
                                )
                                hasSubmitted = true
                                Timber.d("Response submitted successfully")
                            } catch (e: Exception) {
                                Timber.e(e, "Error submitting response")
                            }
                        }
                    },
                    onShowExample = {
                        showExample = true
                        coroutineScope.launch {
                            try {
                                // This would need the response ID, simplified for now
                                Timber.d("Showing example response")
                            } catch (e: Exception) {
                                Timber.e(e, "Error marking example viewed")
                            }
                        }
                    })
            }
        }
    }
}

/** Header section showing user's current progress and motivation */
@Composable
private fun DailyProgressHeader(
    userProgress: UserProgress?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Empathy Challenge",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (userProgress != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Current streak
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${userProgress.currentStreak}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Total responses
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${userProgress.totalResponses}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "responses",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = userProgress.getStreakStatusDescription(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Loading state while fetching today's scenario */
@Composable
private fun LoadingContent(
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
                text = "Loading today's challenge...", style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/** Content shown when user has already completed today's challenge */
@Composable
private fun CompletedChallengeContent(
    userProgress: UserProgress?,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.check_circle),
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Great job! You've completed today's challenge.",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = userProgress?.getMotivationalMessage() ?: "Keep practicing empathy!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )

            FilledTonalButton(
                onClick = onNavigateToHistory
            ) {
                Text("View Your Responses")
            }
        }
    }
}

/** Content shown when no scenarios are available for today */
@Composable
private fun NoScenarioContent(
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No more challenges today",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "You've completed all available scenarios for today. Great work on your empathy practice!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            FilledTonalButton(
                onClick = onNavigateToHistory
            ) {
                Text("Review Your Progress")
            }
        }
    }
}

/** Main challenge content with scenario and response input */
@Composable
private fun ActiveChallengeContent(
    scenario: EmpathyScenario,
    userResponse: String,
    onResponseChange: (String) -> Unit,
    hasSubmitted: Boolean,
    showExample: Boolean,
    onSubmit: () -> Unit,
    onShowExample: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scenario card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Someone says:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"${scenario.scenarioText}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "How would you respond with empathy?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Response input
        if (!hasSubmitted) {
            OutlinedTextField(
                value = userResponse,
                onValueChange = onResponseChange,
                label = { Text("Your empathetic response") },
                placeholder = { Text("Type your response here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = onSubmit,
                enabled = userResponse.trim().isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.send),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Response")
            }
        }

        // Show submitted response
        if (hasSubmitted) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your Response:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Show example button or example response
            if (!showExample) {
                FilledTonalButton(
                    onClick = onShowExample, modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.lightbulb),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("See Example Response")
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Example Response:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = scenario.exampleResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}