package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserResponse

@Composable
fun TodayProgressIndicator(
    todayResponseCount: Int,
    maxResponses: Int,
    todaysResponses: List<UserResponse>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LinearProgressIndicator(
            progress = { todayResponseCount.toFloat() / maxResponses.toFloat() },
            modifier = Modifier.width(200.dp),
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "$todayResponseCount/$maxResponses " + stringResource(R.string.responses),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // Show average rating for today if available
        val avgRatingToday =
            todaysResponses.mapNotNull { it.selfRating }.average().takeIf { !it.isNaN() }

        avgRatingToday?.let { rating ->
            Text(
                text = "Today's avg: ${String.format("%.1f", rating)}/5",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun TodayProgressSection(
    todayResponseCount: Int,
    todayResponses: List<UserResponse>,
) {
    if (todayResponseCount > 0 || todayResponses.isNotEmpty()) {
        TodayProgressIndicator(
            todayResponseCount = todayResponseCount,
            maxResponses = 3,
            todaysResponses = todayResponses
        )
    }
}