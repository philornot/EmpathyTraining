package com.empathytraining.ui.components.challenge

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.empathytraining.R

@Composable
fun ResponseInputSection(
    userResponse: String,
    selfRating: Int?,
    onResponseChange: (String) -> Unit,
    onRatingChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ResponseTextField(
            userResponse = userResponse, onResponseChange = onResponseChange
        )

        CharacterCounter(userResponse.length)

        SelfRatingSection(
            rating = selfRating, onRatingChange = onRatingChange
        )

        SubmitButton(
            enabled = userResponse.trim().isNotEmpty(), onSubmit = onSubmit
        )
    }
}

@Composable
private fun ResponseTextField(
    userResponse: String,
    onResponseChange: (String) -> Unit,
) {
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
}

@Composable
private fun CharacterCounter(characterCount: Int) {
    Text(
        text = stringResource(R.string.character_count, characterCount),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
    )
}

@Composable
fun SelfRatingSection(
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
            RatingTitle()

            Spacer(modifier = Modifier.height(8.dp))

            RatingButtons(
                rating = rating, onRatingChange = onRatingChange
            )

            RatingDescription(rating = rating, context = context)
        }
    }
}

@Composable
private fun RatingTitle() {
    Text(
        text = "How empathetic was your response?",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun RatingButtons(
    rating: Int?,
    onRatingChange: (Int?) -> Unit,
) {
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
}

@Composable
private fun RatingDescription(rating: Int?, context: Context) {
    rating?.let { r ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = getRatingDescription(context, r),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    onSubmit: () -> Unit,
) {
    Button(
        onClick = onSubmit, enabled = enabled, modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.submit_response))
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