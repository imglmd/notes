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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.kiryha.noting.presentation.navigation.LoginScreen
import com.kiryha.noting.presentation.navigation.MainScreen
import com.kiryha.noting.presentation.viewmodel.AuthViewModel
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.presentation.viewmodel.states.AuthState
import io.ktor.util.collections.getValue
import io.ktor.util.collections.setValue

@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val formState by viewModel.formState.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var repeatPassword by remember { mutableStateOf("") }
    var repeatPasswordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Переход на главный экран после успешной регистрации
                navController.navigate(MainScreen) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (authState as AuthState.Error).message
                )
                viewModel.clearError()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetForm()
        repeatPassword = ""
        repeatPasswordError = null
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Sign In",
                showBackButton = true,
                onBackClick = {
                    navController.popBackStack()
                    viewModel.resetForm()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(10.dp))
                AuthTextField(
                    "username",
                    formState.username,
                    viewModel::onUsernameChange
                )
                Spacer(Modifier.height(10.dp))
                AuthTextField(
                    "email",
                    formState.email,
                    viewModel::onEmailChange
                )
                Spacer(Modifier.height(10.dp))
                AuthTextField(
                    "password",
                    formState.password,
                    viewModel::onPasswordChange
                )
                Spacer(Modifier.height(10.dp))
                AuthTextField(
                    "repeat password",
                    repeatPassword,
                    {   repeatPassword = it
                        repeatPasswordError = null}
                )

                HorizontalButton(
                    onClick = {
                        // Валидация повтора пароля
                        when {
                            repeatPassword.isBlank() -> {
                                repeatPasswordError = "Please repeat your password"
                            }
                            repeatPassword != formState.password -> {
                                repeatPasswordError = "Passwords don't match"
                            }
                            else -> {
                                repeatPasswordError = null
                                viewModel.signUp()
                            }
                        }
                    },
                    buttonText = "Sign up",
                    text = "Already have an account? Log in",
                    onTextClick = {
                        navController.navigate(LoginScreen) {
                            popUpTo(LoginScreen) { inclusive = true }
                        }
                    },
                )
            }
        }

    }
}