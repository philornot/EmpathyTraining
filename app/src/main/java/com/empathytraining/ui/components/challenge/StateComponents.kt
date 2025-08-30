package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserProgress

@Composable
fun LoadingState() {
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
fun CompletedState(
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
fun NoScenarioState(onNavigateToHistory: () -> Unit) {
    StatusCard(
        title = stringResource(R.string.no_more_challenges),
        message = stringResource(R.string.all_scenarios_completed),
        buttonText = stringResource(R.string.review_progress),
        onButtonClick = onNavigateToHistory
    )
}

@Composable
fun StatusCard(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    icon: Int? = null,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = androidx.compose.ui.graphics.vector.ImageVector.vectorResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}