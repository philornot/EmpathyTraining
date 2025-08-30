package com.empathytraining.ui.components.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.empathytraining.R

@Composable
fun HistoryStatsCard(stats: Map<String, Any>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.comprehensive_statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = (stats["totalResponses"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.total_responses)
                )
                StatItem(
                    value = (stats["activeDays"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.active_days)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = (stats["uniqueScenarios"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.unique_scenarios)
                )
                StatItem(
                    value = String.format("%.1f", stats["averageResponseLength"] as? Double ?: 0.0),
                    label = stringResource(R.string.avg_characters)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = String.format("%.1f", stats["averageSelfRating"] as? Double ?: 0.0),
                    label = stringResource(R.string.avg_rating)
                )
                StatItem(
                    value = (stats["examplesViewed"] as? Int ?: 0).toString(),
                    label = stringResource(R.string.examples_viewed)
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center
        )
    }
}