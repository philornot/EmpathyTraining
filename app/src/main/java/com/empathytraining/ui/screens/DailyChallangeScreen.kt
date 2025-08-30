package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.components.challenge.ChallengeContent
import com.empathytraining.ui.components.challenge.CompletedState
import com.empathytraining.ui.components.challenge.HeaderTitle
import com.empathytraining.ui.components.challenge.LevelAndMotivationSection
import com.empathytraining.ui.components.challenge.LoadingState
import com.empathytraining.ui.components.challenge.MainStatsRow
import com.empathytraining.ui.components.challenge.NoScenarioState
import com.empathytraining.ui.components.challenge.TodayProgressSection
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
    var responseTimeStart by remember { mutableLongStateOf(0L) }
    var selfRating by remember { mutableStateOf<Int?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var showExample by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var todayResponseCount by remember { mutableIntStateOf(0) }

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
        // Progress Header
        ProgressHeader(
            userProgress = userProgress,
            todayResponseCount = todayResponseCount,
            todayResponses = todaysResponses
        )

        // Main Content based on current state
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
                    coroutineScope.launch {
                        try {
                            // Mark example as viewed in repository if needed
                            // This can be tracked at the progress level
                        } catch (e: Exception) {
                            Timber.e(e, "Error marking example viewed")
                        }
                    }
                })
        }
    }
}

@Composable
private fun ProgressHeader(
    userProgress: com.empathytraining.data.models.UserProgress?,
    todayResponseCount: Int,
    todayResponses: List<com.empathytraining.data.models.UserResponse>,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header title
            HeaderTitle()

            // User progress stats
            userProgress?.let { progress ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Level and motivation
                    LevelAndMotivationSection(userProgress = progress)

                    // Main stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MainStatsRow(
                            currentStreak = progress.currentStreak,
                            totalResponses = progress.totalResponses
                        )
                    }

                    // Today's progress
                    TodayProgressSection(
                        todayResponseCount = todayResponseCount, todayResponses = todayResponses
                    )
                }
            }
        }
    }
}