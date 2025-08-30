package com.empathytraining.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.empathytraining.data.models.UserResponse
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.components.history.HistoryContent
import com.empathytraining.ui.components.history.HistoryEmptyState
import com.empathytraining.ui.components.history.HistoryLoadingState
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun HistoryScreen(
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var responsesWithScenarios by remember {
        mutableStateOf<List<EmpathyRepository.UserResponseWithScenario>>(
            emptyList()
        )
    }
    var recentResponses by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var selectedDateResponses by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var comprehensiveStats by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    // Collect flows from repository
    val responsesWithScenariosFlow by repository.getUserResponsesWithScenarios(context)
        .collectAsState(initial = emptyList())
    val recentResponsesFlow by repository.getRecentResponses().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        try {
            Timber.d("Loading history screen data")
            coroutineScope.launch {
                comprehensiveStats = repository.getComprehensiveStats()
                Timber.d("Loaded comprehensive stats: $comprehensiveStats")
            }
            isLoading = false
        } catch (e: Exception) {
            Timber.e(e, "Error loading history screen data")
            isLoading = false
        }
    }

    LaunchedEffect(responsesWithScenariosFlow) {
        responsesWithScenarios = responsesWithScenariosFlow
        Timber.d("Updated responses with scenarios: ${responsesWithScenarios.size} items")
    }

    LaunchedEffect(recentResponsesFlow) {
        recentResponses = recentResponsesFlow
        Timber.d("Updated recent responses: ${recentResponses.size} items")
    }

    // Handle date selection separately
    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            try {
                Timber.d("Loading responses for selected date: $date")
                coroutineScope.launch {
                    repository.getResponsesForDate(date).collect { responses ->
                        selectedDateResponses = responses
                        Timber.d("Loaded ${responses.size} responses for date: $date")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading responses for date: $date")
                selectedDateResponses = emptyList()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.response_history),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> HistoryLoadingState()
            responsesWithScenarios.isEmpty() -> HistoryEmptyState()
            else -> HistoryContent(
                responsesWithScenarios = responsesWithScenarios,
                recentResponses = recentResponses,
                comprehensiveStats = comprehensiveStats,
                selectedDate = selectedDate,
                selectedDateResponses = selectedDateResponses
            )
        }
    }
}