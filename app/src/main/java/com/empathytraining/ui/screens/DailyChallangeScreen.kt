package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.components.ChallengeContent
import com.empathytraining.ui.components.ProgressHeader
import com.empathytraining.ui.components.StatusCard
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DailyChallengeScreen(
    repository: EmpathyRepository,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var todaysScenario by remember { mutableStateOf<EmpathyRepository.LocalizedScenario?>(null) }
    var userResponse by remember { mutableStateOf("") }
    var responseTimeStart by remember { mutableStateOf(0L) }
    var selfRating by remember { mutableStateOf<Int?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var showExample by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var todayResponseCount by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val userProgress by repository.getUserProgress().collectAsState(initial = null)
    val todaysResponses by repository.getTodaysResponses().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        try {
            todayResponseCount = repository.getTodayResponseCount()
            if (todayResponseCount < 3) { // MAX_RESPONSES_PER_DAY
                todaysScenario = repository.getTodaysChallenge(context)
                responseTimeStart = System.currentTimeMillis()
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
        // Enhanced Progress Header with today's responses
        ProgressHeader(
            userProgress = userProgress,
            todayResponseCount = todayResponseCount,
            todaysResponses = todaysResponses
        )

        when {
            isLoading -> LoadingState()
            todayResponseCount >= 3 -> CompletedState(userProgress, onNavigateToHistory)
            todaysScenario == null -> NoScenarioState(onNavigateToHistory)
            else -> ChallengeContent(
                scenario = todaysScenario!!,
                userResponse = userResponse,
                selfRating = selfRating,
                onResponseChange = { userResponse = it },
                onRatingChange = { selfRating = it },
                hasSubmitted = hasSubmitted,
                showExample = showExample,
                onSubmit = {
                    coroutineScope.launch {
                        try {
                            val responseTime =
                                ((System.currentTimeMillis() - responseTimeStart) / 1000).toInt()
                            repository.submitResponse(
                                scenarioId = todaysScenario!!.scenario.id,
                                userResponseText = userResponse,
                                responseTimeSeconds = responseTime,
                                selfRating = selfRating
                            )
                            hasSubmitted = true
                        } catch (e: Exception) {
                            Timber.e(e, "Error submitting response")
                        }
                    }
                },
                onShowExample = {
                    showExample = true
                    // Mark example as viewed in repository
                    coroutineScope.launch {
                        try {
                            // We need the response ID to mark example viewed
                            // This would need to be tracked after submission
                            // For now, we can track it at the progress level
                        } catch (e: Exception) {
                            Timber.e(e, "Error marking example viewed")
                        }
                    }
                })
        }
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
    userProgress: com.empathytraining.data.models.UserProgress?,
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