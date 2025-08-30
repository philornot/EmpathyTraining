package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empathytraining.data.repository.EmpathyRepository

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
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scenario display
        ScenarioCard(scenario = scenario)

        // Response input or submitted response display
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