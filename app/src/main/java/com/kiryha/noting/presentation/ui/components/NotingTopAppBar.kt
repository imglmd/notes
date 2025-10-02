package com.kiryha.noting.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NotingTopAppBar(
    navButton: Boolean = true,
    titleText: String = "",
    modifier: Modifier = Modifier.fillMaxWidth().statusBarsPadding()
) {
    Row(modifier = Modifier.statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically) {
        if (navButton){
            IconButton(
                onClick = {
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Button Back"
                )
            }
        }
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleLarge
        )
    }

}