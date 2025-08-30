package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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

@Composable
fun DailyChallengeScreen(
    repository: EmpathyRepository,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var todaysScenario by remember { mutableStateOf<EmpathyScenario?>(null) }
    var userResponse by remember { mutableStateOf("") }
    var hasSubmitted by remember { mutableStateOf(false) }
    var showExample by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var hasDoneTodaysChallenge by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val userProgress by repository.getUserProgress().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        try {
            hasDoneTodaysChallenge = repository.hasDoneTodaysChallenge()
            if (!hasDoneTodaysChallenge) {
                todaysScenario = repository.getTodaysChallenge()
            }
            isLoading = false
        } catch (e: Exception) {
            Timber.e(e, "Error loading today's challenge")
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Header
        ProgressHeader(userProgress)

        when {
            isLoading -> LoadingState()
            hasDoneTodaysChallenge -> CompletedState(userProgress, onNavigateToHistory)
            todaysScenario == null -> NoScenarioState(onNavigateToHistory)
            else -> ChallengeContent(
                scenario = todaysScenario!!,
                userResponse = userResponse,
                onResponseChange = { userResponse = it },
                hasSubmitted = hasSubmitted,
                showExample = showExample,
                onSubmit = {
                    coroutineScope.launch {
                        try {
                            repository.submitResponse(todaysScenario!!.id, userResponse)
                            hasSubmitted = true
                        } catch (e: Exception) {
                            Timber.e(e, "Error submitting response")
                        }
                    }
                },
                onShowExample = { showExample = true })
        }
    }
}

@Composable
private fun ProgressHeader(userProgress: UserProgress?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(
                        value = "${progress.currentStreak}",
                        label = stringResource(R.string.day_streak)
                    )
                    StatItem(
                        value = "${progress.totalResponses}",
                        label = stringResource(R.string.responses)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.challenge_loading))
        }
    }
}

@Composable
private fun CompletedState(
    userProgress: UserProgress?,
    onNavigateToHistory: () -> Unit,
) {
    StatusCard(
        icon = R.drawable.check_circle,
        title = stringResource(R.string.challenge_completed),
        message = userProgress?.getMotivationalMessage()
            ?: stringResource(R.string.keep_practicing),
        buttonText = stringResource(R.string.view_responses),
        onButtonClick = onNavigateToHistory
    )
}

@Composable
private fun NoScenarioState(onNavigateToHistory: () -> Unit) {
    StatusCard(
        title = stringResource(R.string.no_more_challenges),
        message = stringResource(R.string.all_scenarios_completed),
        buttonText = stringResource(R.string.review_progress),
        onButtonClick = onNavigateToHistory
    )
}

@Composable
private fun StatusCard(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    icon: Int? = null,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = ImageVector.vectorResource(it),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            FilledTonalButton(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun ChallengeContent(
    scenario: EmpathyScenario,
    userResponse: String,
    onResponseChange: (String) -> Unit,
    hasSubmitted: Boolean,
    showExample: Boolean,
    onSubmit: () -> Unit,
    onShowExample: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Scenario Card
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.someone_says),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${scenario.scenarioText}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.how_would_you_respond),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Response Input or Display
        if (!hasSubmitted) {
            OutlinedTextField(
                value = userResponse,
                onValueChange = onResponseChange,
                label = { Text(stringResource(R.string.your_response)) },
                placeholder = { Text(stringResource(R.string.response_placeholder)) },
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
                Text(stringResource(R.string.submit_response))
            }
        } else {
            // Submitted Response
            ResponseCard(
                title = stringResource(R.string.your_response),
                content = userResponse,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )

            // Example Response Section
            if (!showExample) {
                FilledTonalButton(
                    onClick = onShowExample, modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.lightbulb),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.see_example))
                }
            } else {
                ResponseCard(
                    title = stringResource(R.string.example_response),
                    content = scenario.exampleResponse,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
private fun ResponseCard(
    title: String,
    content: String,
    containerColor: androidx.compose.ui.graphics.Color,
) {
    Card(colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}