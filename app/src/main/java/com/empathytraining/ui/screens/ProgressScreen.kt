package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.components.progress.NoProgressState
import com.empathytraining.ui.components.progress.ProgressContent
import com.empathytraining.ui.components.progress.LoadingState
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ProgressScreen(
    repository: EmpathyRepository,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var streakInfo by remember { mutableStateOf(Pair(0, 0)) }
    var comprehensiveStats by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var hasCompletedTodaysChallenge by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var todayResponseCount by remember { mutableIntStateOf(0) }
    var missingResources by remember { mutableStateOf<List<String>>(emptyList()) }

    val userProgress by repository.getUserProgress().collectAsState(initial = null)
    val recentResponses by repository.getRecentResponses().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        try {
            Timber.d("Loading progress screen data")

            coroutineScope.launch {
                // Load streak information
                streakInfo = repository.getStreakInfo()
                Timber.d("Loaded streak info: current=${streakInfo.first}, longest=${streakInfo.second}")

                // Load comprehensive statistics
                comprehensiveStats = repository.getComprehensiveStats()
                Timber.d("Loaded comprehensive stats: $comprehensiveStats")

                // Check if user has done today's challenge
                hasCompletedTodaysChallenge = repository.hasDoneTodaysChallenge()
                Timber.d("Has completed today's challenge: $hasCompletedTodaysChallenge")

                // Get today's response count
                todayResponseCount = repository.getTodayResponseCount()
                Timber.d("Today's response count: $todayResponseCount")

                // Validate scenario resources
                missingResources = repository.validateScenarioResources(context)
                if (missingResources.isNotEmpty()) {
                    Timber.w("Missing scenario resources: $missingResources")
                } else {
                    Timber.d("All scenario resources validated successfully")
                }

                isLoading = false
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading progress screen data")
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
        Text(
            text = stringResource(R.string.progress_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when {
            isLoading -> LoadingState()
            userProgress == null -> NoProgressState(onNavigateToChallenge)
            else -> ProgressContent(
                userProgress = userProgress!!,
                streakInfo = streakInfo,
                comprehensiveStats = comprehensiveStats,
                hasCompletedTodaysChallenge = hasCompletedTodaysChallenge,
                todayResponseCount = todayResponseCount,
                recentResponses = recentResponses,
                missingResources = missingResources,
                onNavigateToChallenge = onNavigateToChallenge,
                onUpdateDifficulty = { difficulty ->
                    coroutineScope.launch {
                        try {
                            repository.updatePreferredDifficulty(difficulty)
                            Timber.d("Updated preferred difficulty to: $difficulty")
                        } catch (e: Exception) {
                            Timber.e(e, "Error updating preferred difficulty")
                        }
                    }
                })
        }
    }
}