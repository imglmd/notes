package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.R
import com.kiryha.noting.presentation.components.AuthTextField
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.RegistrationScreen

@Composable
fun LoginScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }



    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Log in",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp).fillMaxSize()
        ){
            Spacer(Modifier.height(40.dp))
            Image(
                painter = painterResource(R.drawable.profile_icon),
                contentDescription = null,
                modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(20.dp))
            AuthTextField("username",username, {username = it})
            Spacer(Modifier.height(20.dp))
            AuthTextField("password", password, {password = it})
            Text(
                "forget password?",
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).padding(end = 5.dp),
                textAlign = TextAlign.Right,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )


            HorizontalButton(
                onClick = {},
                buttonText = "Log in",
                text= "Don’t  have an account? Sign up",
                onTextClick = {navController.navigate(RegistrationScreen)}
            )
        }
    }
}