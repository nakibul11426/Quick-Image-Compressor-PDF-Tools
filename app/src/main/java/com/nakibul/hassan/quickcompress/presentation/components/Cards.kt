package com.nakibul.hassan.quickcompress.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val borderModifier = if (!isDarkTheme) {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = MaterialTheme.shapes.extraLarge
        )
    } else {
        Modifier
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .then(borderModifier)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .then(borderModifier)
                .animateContentSize(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}
