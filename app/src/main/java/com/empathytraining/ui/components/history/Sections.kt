package com.empathytraining.ui.components.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserResponse
import com.empathytraining.data.repository.EmpathyRepository

@Composable
fun RecentResponsesSection(recentResponses: List<UserResponse>) {
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

@Composable
fun ResponsesWithScenariosSection(
    responsesWithScenarios: List<EmpathyRepository.UserResponseWithScenario>,
) {
    Text(
        text = stringResource(R.string.all_responses),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )

    LazyColumn(
        modifier = Modifier.height(400.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(responsesWithScenarios) { responseWithScenario ->
            ResponseWithScenarioItem(responseWithScenario)
        }
    }
}

@Composable
fun DateFilteredSection(
    selectedDate: String,
    selectedDateResponses: List<UserResponse>,
) {
    Text(
        text = stringResource(R.string.responses_for_date, selectedDate),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
    )

    if (selectedDateResponses.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
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