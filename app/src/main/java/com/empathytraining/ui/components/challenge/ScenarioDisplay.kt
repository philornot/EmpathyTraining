package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.utils.LocalizationUtils

@Composable
fun ScenarioHeader(
    scenario: EmpathyRepository.LocalizedScenario,
    context: android.content.Context,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.someone_says),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryChip(
                category = LocalizationUtils.getCategoryDisplayName(
                    context, scenario.scenario.category
                )
            )
            DifficultyIndicator(difficulty = scenario.scenario.difficultyLevel)
        }
    }
}

@Composable
private fun CategoryChip(category: String) {
    AssistChip(
        onClick = { }, label = {
        Text(
            text = category, style = MaterialTheme.typography.labelSmall
        )
    }, enabled = false
    )
}

@Composable
fun DifficultyIndicator(difficulty: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (index < difficulty) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }
    }
}

@Composable
fun ScenarioText(text: String) {
    Text(
        text = "\"$text\"",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun ScenarioPrompt() {
    Text(
        text = stringResource(R.string.how_would_you_respond),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}