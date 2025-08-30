package com.empathytraining.ui.components.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun SubmittedResponseSection(
    userResponse: String,
    selfRating: Int?,
    scenario: EmpathyRepository.LocalizedScenario,
    showExample: Boolean,
    onShowExample: () -> Unit,
) {
    LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // User's submitted response
        UserSubmittedResponseCard(
            userResponse = userResponse, selfRating = selfRating
        )

        // Example Response Section
        ExampleResponseSection(
            showExample = showExample,
            onShowExample = onShowExample,
            exampleText = scenario.localizedExample
        )
    }
}

@Composable
private fun UserSubmittedResponseCard(
    userResponse: String,
    selfRating: Int?,
) {
    val context = LocalContext.current

    ResponseCard(
        title = stringResource(R.string.your_response),
        content = userResponse,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        metadata = buildUserResponseMetadata(
            userResponse = userResponse, selfRating = selfRating, context = context
        )
    )
}

@Composable
private fun ExampleResponseSection(
    showExample: Boolean,
    onShowExample: () -> Unit,
    exampleText: String,
) {
    if (!showExample) {
        ShowExampleButton(onShowExample = onShowExample)
    } else {
        ExampleResponseCard(exampleText = exampleText)
    }
}

@Composable
private fun ShowExampleButton(onShowExample: () -> Unit) {
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
}

@Composable
private fun ExampleResponseCard(exampleText: String) {
    ResponseCard(
        title = stringResource(R.string.example_response),
        content = exampleText,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        metadata = listOf("Example response for reference")
    )
}

@Composable
fun ResponseCard(
    title: String,
    content: String,
    containerColor: androidx.compose.ui.graphics.Color,
    metadata: List<String> = emptyList(),
) {
    Card(colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            ResponseCardHeader(title = title)

            Spacer(modifier = Modifier.height(8.dp))

            ResponseCardContent(content = content)

            if (metadata.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ResponseCardMetadata(metadata = metadata)
            }
        }
    }
}

@Composable
private fun ResponseCardHeader(title: String) {
    Text(
        text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ResponseCardContent(content: String) {
    Text(
        text = content, style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ResponseCardMetadata(metadata: List<String>) {
    metadata.forEach { meta ->
        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun buildUserResponseMetadata(
    userResponse: String,
    selfRating: Int?,
    context: android.content.Context,
): List<String> = buildList {
    add("${userResponse.length} characters")

    selfRating?.let { rating ->
        val ratingDescription = when (rating) {
            1 -> context.getString(R.string.rating_1_desc)
            2 -> context.getString(R.string.rating_2_desc)
            3 -> context.getString(R.string.rating_3_desc)
            4 -> context.getString(R.string.rating_4_desc)
            5 -> context.getString(R.string.rating_5_desc)
            else -> ""
        }
        add("${context.getString(R.string.rating_format, rating)} - $ratingDescription")
    }

    add(
        LocalizationUtils.getQualityAssessment(
            context, userResponse.length, selfRating
        )
    )
}