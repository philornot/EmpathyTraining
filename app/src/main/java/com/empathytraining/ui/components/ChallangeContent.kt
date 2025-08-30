package com.empathytraining.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.utils.LocalizationUtils

@Composable
fun ChallengeContent(
    scenario: EmpathyRepository.LocalizedScenario,
    userResponse: String,
    selfRating: Int?,
    onResponseChange: (String) -> Unit,
    onRatingChange: (Int?) -> Unit,
    hasSubmitted: Boolean,
    showExample: Boolean,
    onSubmit: () -> Unit,
    onShowExample: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LocalContext.current

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scenario Card with enhanced information
        ScenarioCard(scenario = scenario)

        // Response Input or Display
        if (!hasSubmitted) {
            ResponseInputSection(
                userResponse = userResponse,
                selfRating = selfRating,
                onResponseChange = onResponseChange,
                onRatingChange = onRatingChange,
                onSubmit = onSubmit
            )
        } else {
            SubmittedResponseSection(
                userResponse = userResponse,
                selfRating = selfRating,
                scenario = scenario,
                showExample = showExample,
                onShowExample = onShowExample
            )
        }
    }
}

@Composable
private fun ScenarioCard(scenario: EmpathyRepository.LocalizedScenario) {
    val context = LocalContext.current

    Card {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with category and difficulty
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
                    // Category chip
                    AssistChip(
                        onClick = { }, label = {
                        Text(
                            text = LocalizationUtils.getCategoryDisplayName(
                                context, scenario.scenario.category
                            ), style = MaterialTheme.typography.labelSmall
                        )
                    }, enabled = false
                    )

                    // Difficulty indicator
                    DifficultyIndicator(difficulty = scenario.scenario.difficultyLevel)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"${scenario.localizedText}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.how_would_you_respond),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DifficultyIndicator(difficulty: Int) {
    LocalContext.current

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
private fun ResponseInputSection(
    userResponse: String,
    selfRating: Int?,
    onResponseChange: (String) -> Unit,
    onRatingChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = userResponse,
            onValueChange = onResponseChange,
            label = { Text(stringResource(R.string.your_response)) },
            placeholder = { Text(stringResource(R.string.response_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )

        // Character count
        Text(
            text = stringResource(R.string.character_count, userResponse.length),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )

        // Self-rating section
        SelfRatingSection(
            rating = selfRating, onRatingChange = onRatingChange
        )

        Button(
            onClick = onSubmit,
            enabled = userResponse.trim().isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.submit_response))
        }
    }
}

@Composable
private fun SelfRatingSection(
    rating: Int?,
    onRatingChange: (Int?) -> Unit,
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "How empathetic was your response?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(5) { index ->
                    val ratingValue = index + 1
                    FilterChip(
                        onClick = { onRatingChange(ratingValue) },
                        label = { Text("$ratingValue") },
                        selected = rating == ratingValue,
                        leadingIcon = if (rating == ratingValue) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null)
                }
            }

            // Show rating description if selected
            rating?.let { r ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getRatingDescription(context, r),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SubmittedResponseSection(
    userResponse: String,
    selfRating: Int?,
    scenario: EmpathyRepository.LocalizedScenario,
    showExample: Boolean,
    onShowExample: () -> Unit,
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // User's submitted response
        ResponseCard(
            title = stringResource(R.string.your_response),
            content = userResponse,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            metadata = buildList {
                add("${userResponse.length} characters")
                selfRating?.let { rating ->
                    add(
                        "${stringResource(R.string.rating_format, rating)} - ${
                            getRatingDescription(
                                context,
                                rating
                            )
                        }"
                    )
                }
                add(
                    LocalizationUtils.getQualityAssessment(
                        context,
                        userResponse.length,
                        selfRating
                    )
                )
            })

        // Example Response Section
        if (!showExample) {
            FilledTonalButton(
                onClick = onShowExample, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.lightbulb),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.see_example))
            }
        } else {
            ResponseCard(
                title = stringResource(R.string.example_response),
                content = scenario.localizedExample,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                metadata = listOf("Example response for reference")
            )
        }
    }
}

@Composable
private fun ResponseCard(
    title: String,
    content: String,
    containerColor: androidx.compose.ui.graphics.Color,
    metadata: List<String> = emptyList(),
) {
    Card(colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content, style = MaterialTheme.typography.bodyMedium
            )

            if (metadata.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                metadata.forEach { meta ->
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getRatingDescription(context: Context, rating: Int): String {
    return when (rating) {
        1 -> context.getString(R.string.rating_1_desc)
        2 -> context.getString(R.string.rating_2_desc)
        3 -> context.getString(R.string.rating_3_desc)
        4 -> context.getString(R.string.rating_4_desc)
        5 -> context.getString(R.string.rating_5_desc)
        else -> ""
    }
}