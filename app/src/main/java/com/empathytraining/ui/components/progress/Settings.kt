package com.empathytraining.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.models.UserProgress
import timber.log.Timber

@Composable
fun DifficultySettingsCard(
    currentDifficulty: Int,
    onUpdateDifficulty: (Int) -> Unit,
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.difficulty_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(onClick = { showDifficultyDialog = true }) {
                    Text(stringResource(R.string.change))
                }
            }

            Text(
                text = stringResource(
                    R.string.current_difficulty_level, getDifficultyString(currentDifficulty)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            currentDifficulty = currentDifficulty,
            onDifficultySelected = { difficulty ->
                onUpdateDifficulty(difficulty)
                showDifficultyDialog = false
                Timber.d("User selected difficulty level: $difficulty")
            },
            onDismiss = { showDifficultyDialog = false })
    }
}

@Composable
private fun DifficultySelectionDialog(
    currentDifficulty: Int,
    onDifficultySelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(stringResource(R.string.select_difficulty_level))
    }, text = {
        Column {
            (1..5).forEach { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = level == currentDifficulty,
                        onClick = { onDifficultySelected(level) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getDifficultyString(level),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.close))
        }
    })
}

@Composable
fun ActionButton(
    userProgress: UserProgress,
    onNavigateToChallenge: () -> Unit,
) {
    FilledTonalButton(
        onClick = onNavigateToChallenge, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.psychology),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (userProgress.hasRespondedToday()) {
                stringResource(R.string.view_todays_challenge)
            } else {
                stringResource(R.string.start_todays_challenge)
            }
        )
    }
}

@Composable
private fun getDifficultyString(difficulty: Int): String {
    return when (difficulty) {
        1 -> stringResource(R.string.difficulty_very_easy)
        2 -> stringResource(R.string.difficulty_easy)
        3 -> stringResource(R.string.difficulty_medium)
        4 -> stringResource(R.string.difficulty_hard)
        5 -> stringResource(R.string.difficulty_very_hard)
        else -> stringResource(R.string.difficulty_unknown)
    }
}