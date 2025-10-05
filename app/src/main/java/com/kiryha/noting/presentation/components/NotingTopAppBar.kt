package com.kiryha.noting.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotingTopAppBar(
    showBackButton: Boolean = false,
    showSettingsButton: Boolean = false,
    titleText: String = "",
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    var isBackClicked by remember { mutableStateOf(false) }
    var isSettingsClicked by remember { mutableStateOf(false) }


    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon ={
            if (showBackButton){
                IconButton(
                    onClick = {
                        if (!isBackClicked) {
                            isBackClicked = true
                            onBackClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = {
            if (showSettingsButton) {
                IconButton(
                    onClick = {
                        if (!isSettingsClicked) {
                            isSettingsClicked = true
                            onSettingsClick()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Settings"
                    )
                }
            }
        },
        title = {
            Text(
                titleText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },

    )
}