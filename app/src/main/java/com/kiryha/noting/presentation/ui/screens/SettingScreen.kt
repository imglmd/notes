package com.kiryha.noting.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.R
import com.kiryha.noting.presentation.ui.components.NotingTopAppBar
import com.kiryha.noting.presentation.ui.components.ProfileSection
import com.kiryha.noting.presentation.ui.components.RadioButtonGroup

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
                onBackClick = { navController.popBackStack() }
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
                onOptionSelected = { location -> saveLocation = location},
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