package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.R
import com.kiryha.noting.presentation.components.AuthTextField
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.MainScreen
import com.kiryha.noting.presentation.navigation.RegistrationScreen
import com.kiryha.noting.presentation.viewmodel.AuthViewModel
import com.kiryha.noting.presentation.viewmodel.states.AuthState

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {

    val formState by viewModel.formState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Переход на главный экран после успешного входа
                navController.navigate(MainScreen) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {
                focusRequester.requestFocus()
            }
        }
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Log in",
                showBackButton = true,
                onBackClick = {
                    navController.popBackStack()
                    viewModel.resetForm()
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ){
                Spacer(Modifier.height(40.dp))
                Image(
                    painter = painterResource(R.drawable.profile_icon),
                    contentDescription = null,
                    modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(20.dp))
                AuthTextField(
                    label = "email",
                    errorMessage = formState.emailError,
                    value = formState.email,
                    focusRequester = focusRequester,
                    onImeAction = {focusManager.moveFocus(FocusDirection.Down)},
                    onValueChange = viewModel::onEmailChange
                )
                Spacer(Modifier.height(20.dp))
                AuthTextField(
                    label = "password",
                    errorMessage = formState.passwordError,
                    value = formState.password,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        viewModel.signIn()
                        focusManager.clearFocus()
                    },
                    onValueChange = viewModel::onPasswordChange
                )
                Text(
                    "forget password?",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).padding(end = 5.dp),
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(100.dp))

                HorizontalButton(
                    onClick = {
                        viewModel.signIn()
                        focusManager.clearFocus()
                              },
                    buttonText = "Log in",
                    text= "Don't  have an account? Sign up",
                    onTextClick = { navController.navigate(RegistrationScreen) }
                )
            }
        }
    }
}