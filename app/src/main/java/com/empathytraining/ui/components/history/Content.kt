package com.empathytraining.ui.components.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empathytraining.data.models.UserResponse
import com.empathytraining.data.repository.EmpathyRepository

@Composable
fun HistoryContent(
    responsesWithScenarios: List<EmpathyRepository.UserResponseWithScenario>,
    recentResponses: List<UserResponse>,
    comprehensiveStats: Map<String, Any>,
    selectedDate: String?,
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
            RecentResponsesSection(recentResponses)
        }

        // All responses with scenarios
        if (responsesWithScenarios.isNotEmpty()) {
            ResponsesWithScenariosSection(responsesWithScenarios)
        }

        // Date filter section
        selectedDate?.let { date ->
            DateFilteredSection(
                selectedDate = date, selectedDateResponses = selectedDateResponses
            )
        }
    }
}