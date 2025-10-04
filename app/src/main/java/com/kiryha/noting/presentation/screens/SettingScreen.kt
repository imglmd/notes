package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.components.ProfileSection
import com.kiryha.noting.presentation.components.RadioButtonGroup
import com.kiryha.noting.presentation.navigation.MainScreen

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var saveLocation by remember { mutableStateOf("local") }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "",
                showBackButton = true,
                onBackClick = { navController.navigate(MainScreen) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            ProfileSection(navController)

            Spacer(Modifier.height(40.dp))

            Text(text = "Settings", style = MaterialTheme.typography.titleLarge)

            RadioButtonGroup(

                label = "data storage",
                options = listOf("cloud", "local"),
                selectedOption = saveLocation,
                onOptionSelected = { location -> saveLocation = location },
                optionToTextStyle = { option ->
                    when (option) {
                        "cloud" -> "Cloud (Supabase)" to null
                        "local" -> "Local Device" to null
                        else -> option to null
                    }
                },

                )

        }
    }
}