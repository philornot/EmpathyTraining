package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R

@Composable
fun StatItem(
    value: String,
    label: String,
    icon: Int? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = ImageVector.vectorResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label, style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun MainStatsRow(
    currentStreak: Int,
    totalResponses: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        StatItem(
            value = "$currentStreak",
            label = stringResource(R.string.day_streak),
            icon = R.drawable.local_fire_department
        )
        StatItem(
            value = "$totalResponses",
            label = stringResource(R.string.responses),
            icon = R.drawable.chat_bubble
        )
    }
}